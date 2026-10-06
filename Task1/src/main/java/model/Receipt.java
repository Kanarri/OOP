package model;

import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

/**
 * квитанция за месяц по лицевому счёту
 *
 * @param accountNumber номер лицевого счёта
 * @param month расчётный месяц
 * @param charges строки начислений
 * @param totalKopecks итоговая сумма в копейках
 */
public record Receipt(String accountNumber, YearMonth month, List<Charge> charges, long totalKopecks) {

    public Receipt {
        Objects.requireNonNull(accountNumber, "номер счета");
        Objects.requireNonNull(month, "месяц");
        Objects.requireNonNull(charges, "цена");
        charges = List.copyOf(charges);
        if (totalKopecks < 0) {
            throw new IllegalArgumentException("total < 0: " + totalKopecks);
        }
    }

    /**
     * квитанция по счету и месяцу
     *
     * @param account счёт
     * @param month месяц
     * @param services список услуг
     * @param charger сервис расчёта
     * @return квитанция с расшифровкой
     */
    public static Receipt of(Account account, YearMonth month, List<ServiceType> services, ChargingService charger) {
        List<Charge> lines = services.stream()
                .map(s -> charger.chargeFor(account, s, month))
                .toList();
        long total = lines.stream().mapToLong(Charge::sumKopeyks).sum();
        return new Receipt(account.getNumber(), month, lines, total);
    }

    //расшифровка квитанции
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append("Квитанция за ").append(month).append('\n');
        sb.append("Лицевой счёт: ").append(accountNumber).append('\n');
        for (Charge c : charges) {
            sb.append(String.format("%-12s %8d,%02d ₽   %s%n",
                    c.service(), c.sumKopeyks() / 100, c.sumKopeyks() % 100,
                    c.explanation()));
        }
        sb.append(String.format("ИТОГО: %d,%02d ₽%n", totalKopecks / 100, totalKopecks % 100));
        return sb.toString();
    }
}