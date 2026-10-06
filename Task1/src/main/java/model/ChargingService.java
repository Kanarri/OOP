package model;

import model.Account;
import model.Charge;
import model.Housing;
import model.Meter;
import model.MeterReadings;
import model.Norma;
import model.ServiceType;
import model.Tariff;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * Расчёт начислений за месяц по лицевому счёту.
 *
 * <p>Правило округления: целочисленное деление, округление вниз (в пользу
 * потребителя). Все суммы — в копейках.
 */
public final class ChargingService {

    private final List<Tariff> tariffs;
    private final List<Norma> norms;
    private final List<Meter> meters;

    public ChargingService(List<Tariff> tariffs, List<Norma> norms, List<Meter> meters) {
        this.tariffs = List.copyOf(tariffs);
        this.norms   = List.copyOf(norms);
        this.meters  = List.copyOf(meters);
    }

    // тариф действующий на указанную дату
    public Tariff tariffForDate(ServiceType service, LocalDate date) {
        return tariffs.stream()
                .filter(t -> t.service() == service)
                .filter(t -> t.activeOn(date))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Нет тарифа на " + service + " на дату " + date));
    }

    // норматив на указанную дату
    public Norma normFor(ServiceType service, LocalDate date) {
        return norms.stream()
                .filter(n -> n.service() == service)
                .filter(n -> n.activeOn(date))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Нет норматива на " + service + " на дату " + date));
    }

    /**
     * Начисление за месяц по одной услуге для указанного счёта.
     *
     * @param account счёт
     * @param service услуга
     * @param month рассчётный месяц
     * @return строка начисления с расшифровкой
     */
    public Charge chargeFor(Account account, ServiceType service, YearMonth month) {
        return service.isByMeter()
                ? chargeByMeter(account, service, month)
                : chargeByNorm(account, service, month);
    }

    private Charge chargeByMeter(Account account, ServiceType service, YearMonth month) {
        Meter meter = findMeter(account, service);
        LocalDate start = month.atDay(1);
        LocalDate end   = month.atEndOfMonth();

        Optional<MeterReadings> from = meter.readingAtOrBefore(start);
        Optional<MeterReadings> to   = meter.readingAtOrBefore(end);

        if (from.isEmpty() || to.isEmpty()) {
            return chargeByNorm(account, service, month);
        }

        long consumption = MeterReadings.consumptionBetween(from.get(), to.get());

        LocalDate mid = month.atDay(15);
        Tariff tariff = tariffForDate(service, mid);

        long sum = consumption * tariff.pricePerUnitKopeyks();

        String explanation = String.format(
                "%d %s × %d,%02d ₽/%s (тариф с %s)",
                consumption, service.getUnit(),
                tariff.pricePerUnitKopeyks() / 100, tariff.pricePerUnitKopeyks() % 100,
                service.getUnit(), tariff.from()
        );

        return new Charge(service, month, sum, explanation);
    }

    private Charge chargeByNorm(Account account, ServiceType service, YearMonth month) {
        LocalDate mid = month.atDay(15);
        Norma norm = normFor(service, mid);
        Tariff tariff = tariffForDate(service, mid);

        Housing h = account.getHousing();

        // объём в тысячных
        long quantityMilli;
        if (norm.perPerson()) {
            quantityMilli = (long) h.residents() * norm.valueMilli();
        } else {
            //площадь в сотых м2 норматив в тысячных
            quantityMilli = h.area() * norm.valueMilli() / 100;
        }

        // сумма в копейках = quantityMilli * цена / 1000, округление вниз
        long sum = quantityMilli * tariff.pricePerUnitKopeyks() / 1000;

        String explanation = String.format(
                "%s × %d,%03d × %d,%02d ₽ (норматив)",
                norm.perPerson()
                        ? h.residents() + " чел."
                        : h.areaSquareMeters() + " м²",
                norm.valueMilli() / 1000, norm.valueMilli() % 1000,
                tariff.pricePerUnitKopeyks() / 100, tariff.pricePerUnitKopeyks() % 100
        );

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