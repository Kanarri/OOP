package model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * тариф на услугу действующий в интервале {@code [from, to)}.
 * @param service вид услуги
 * @param zone тарифная зона день/ночь
 * @param from дата начала действия включительно
 * @param to дата окончания не включительно
 * @param pricePerUnitKopeyks цена за единицу услуги в копейках
 */

public record Tariff(ServiceType service,
                     TarifZone zone,
                     LocalDate from,
                     LocalDate to,
                     long pricePerUnitKopeyks) {

    public Tariff {
        Objects.requireNonNull(service);
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        if (!to.isAfter(from)) {
            throw new IllegalArgumentException("to должно быть после from: " + from + ".." + to);
        }
        if (pricePerUnitKopeyks < 0) {
            throw new IllegalArgumentException("цена < 0: " + pricePerUnitKopeyks);
        }
    }

    // действует ли тариф на данную дату
    public boolean activeOn(LocalDate date) {
        return !date.isBefore(from) && date.isBefore(to);
    }
}