package model;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * расчёт начислений за месяц.
 */
public final class ChargingService {
    private final List<Tariff> tariffs;
    private final List<Norma> norms;

    public ChargingService(List<Tariff> tariffs, List<Norma> norms) {
        this.tariffs = List.copyOf(tariffs);
        this.norms = List.copyOf(norms);
    }

    //тариф который действует на дату
    public Tariff tariffForDate(ServiceType service, LocalDate date) {
        return tariffs.stream()
                .filter(t -> t.service() == service)
                .filter(t -> t.activeOn(date))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Нет тарифа на " + service + " на дату " + date));
    }

    //норматив который действует на дату
    public Norma normFor(ServiceType service, LocalDate date) {
        return norms.stream()
                .filter(n -> n.service() == service)
                .filter(n -> n.activeOn(date))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Нет норматива на " + service + " на дату " + date));
    }

    /**
     * начисление за месяц по одной услуге для счёта
     *
     * @param account счёт
     * @param service услуга
     * @param month расчётный месяц
     * @return строка начисления с расшифровкой
     */
    public Charge chargeFor(Account account, ServiceType service, YearMonth month) {
        return service.isByMeter()
                ? chargeByMeter(account, service, month)
                : chargeByNorm(account, service, month);
    }
}