package model;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class ChargingService implements TariffLookup, NormLookup {

    private final List<Tariff> tariffs;
    private final List<Norma> norms;
    private final Map<ServiceType, ChargingRule> rules = new EnumMap<>(ServiceType.class);

    public ChargingService(List<Tariff> tariffs,
                           List<Norma> norms,
                           List<Meter> meters) {
        this.tariffs = List.copyOf(tariffs);
        this.norms = List.copyOf(norms);

        var byNorm  = new ByNormRule(this, this);
        var byMeter = new ByMeterRule(meters, this, byNorm);

        for (ServiceType s : ServiceType.values()) {
            rules.put(s, s.isByMeter() ? byMeter : byNorm);
        }
    }

    @Override
    public Tariff tariffForDate(ServiceType service, LocalDate date) {
        return tariffs.stream()
                .filter(t -> t.service() == service)
                .filter(t -> t.activeOn(date))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Нет тарифа на " + service + " на дату " + date));
    }

    @Override
    public Norma normFor(ServiceType service, LocalDate date) {
        return norms.stream()
                .filter(n -> n.service() == service)
                .filter(n -> n.activeOn(date))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Нет норматива на " + service + " на дату " + date));
    }

    public Charge chargeFor(Account account, ServiceType service, YearMonth month) {
        return rules.get(service).charge(account, service, month);
    }
}