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

    private Charge chargeByNorm(Account account, ServiceType service, YearMonth month) {
        LocalDate mid = month.atDay(15);
        Norma norm = normFor(service, mid);
        Tariff tariff = tariffForDate(service, mid);

        Housing h = account.getHousing();

        //объём в тысячных долях единицы
        long quantityMilli;
        if (norm.perPerson()) {
            quantityMilli = (long) h.residents() * norm.valueMilli();
        } else {
            // площадь в сотых м2, норматив в тысячных
            quantityMilli = h.area() * norm.valueMilli() / 100;
        }

        // сумма в копейках = quantityMilli × цена/1000
        long sum = quantityMilli * tariff.pricePerUnitKopeyks() / 1000;

        String explanation = String.format(
                "%s × %d,%03d × %d,%02d ₽ (норматив)",
                norm.perPerson() ? h.residents() + " чел." : h.areaSquareMeters() + " м²",
                norm.valueMilli() / 1000, norm.valueMilli() % 1000,
                tariff.pricePerUnitKopeyks() / 100, tariff.pricePerUnitKopeyks() % 100
        );

        return new Charge(service, month, sum, explanation);
    }
}