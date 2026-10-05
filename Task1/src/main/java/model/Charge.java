package model;

import java.util.Objects;

/**
 *начисление: услуга, период, сумма и расшифровка.
 *
 * @param service вид услуги
 * @param period расчётный месяц
 * @param sumKopeyks сумма в копейках
 * @param explanation расшифровка (объём, тариф, норматив)
 */
public record Charge(ServiceType service,
                     java.time.YearMonth period,
                     long sumKopeyks,
                     String explanation) {

    public Charge {
        Objects.requireNonNull(service, "услуга");
        Objects.requireNonNull(period, "период");
        Objects.requireNonNull(explanation, "информация");
        if (sumKopeyks < 0) {
            throw new IllegalArgumentException("цена < 0");
        }
    }
}