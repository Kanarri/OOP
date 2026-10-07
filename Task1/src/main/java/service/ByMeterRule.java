package service;

import model.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public final class ByMeterRule implements ChargingRule {

    private final List<Meter> meters;
    private final TariffLookup tariffs;
    private final ChargingRule fallback;

    public ByMeterRule(List<Meter> meters,
                       TariffLookup tariffs,
                       ChargingRule fallback) {
        this.meters = List.copyOf(meters);
        this.tariffs = tariffs;
        this.fallback = fallback;
    }

    @Override
    public Charge charge(Account account, ServiceType service, YearMonth month) {
        Meter meter = findMeter(account, service);

        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();

        Optional<MeterReadings> from = meter.readingAtOrBefore(start);
        Optional<MeterReadings> to = meter.readingAtOrBefore(end);

        if (from.isEmpty() || to.isEmpty()) {
            return fallback.charge(account, service, month);
        }

        long consumption = MeterReadings.consumptionBetween(from.get(), to.get());

        LocalDate mid = month.atDay(15);
        Tariff tariff = tariffs.tariffForDate(service, mid);

        long sum = consumption * tariff.pricePerUnitKopeyks();

        String explanation = String.format(
                "%d %s x %d,%02d руб/%s (тариф с %s)",
                consumption, service.getUnit(),
                tariff.pricePerUnitKopeyks() / 100, tariff.pricePerUnitKopeyks() % 100,
                service.getUnit(), tariff.from());

        return new Charge(service, month, sum, explanation);
    }

    private Meter findMeter(Account account, ServiceType service) {
        return meters.stream()
                .filter(m -> m.getAccount().equals(account))
                .filter(m -> m.getService() == service)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Нет счётчика: " + account.getNumber() + " / " + service));
    }
}