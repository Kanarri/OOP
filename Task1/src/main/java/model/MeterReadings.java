package model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * показания счетчика в конкретную дату
* @param date дата снятия показаний счетчика
* @param value колво единиц, сколько накрутил счетчик
*/
public record MeterReadings(LocalDate date, long value) {
    public MeterReadings {
        Objects.requireNonNull(date, "date");
        if (value < 0) {
            throw new IllegalArgumentException("Показание не может быть отрицательным: " + value);
        }
    }
    //расход за период
    public static long consumptionBetween(MeterReadings previous, MeterReadings current) {
        if (current.value() < previous.value()) {
            throw new InvalidReadingException(previous, current);
        }
        return current.value() - previous.value();
    }
}
