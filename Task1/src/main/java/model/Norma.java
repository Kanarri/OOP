package model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * норматив потребления на интервале from to.
 * @param service услуга
 * @param perPerson true на человека, false на м2
 * @param valueMilli норматив в тысячных долях единицы (0.250 -> 250)
 * @param from начало действия включая
 * @param to конец действия не включая
 */
public record Norma(ServiceType service, boolean perPerson, long valueMilli, LocalDate from, LocalDate to) {
    public Norma {
        Objects.requireNonNull(service, "услуга");
        Objects.requireNonNull(from, "от");
        Objects.requireNonNull(to, "до");
        if (valueMilli <= 0) {
            throw new IllegalArgumentException("valueMilli <= 0: " + valueMilli);
        }
        if (!to.isAfter(from)) {
            throw new IllegalArgumentException("to <= from: " + from + ".." + to);
        }
    }

    //действует ли норматив на указанную дату
    public boolean activeOn(LocalDate date) {
        return !date.isBefore(from) && date.isBefore(to);
    }
}