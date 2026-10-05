package model;

import java.util.Objects;

/**
 * квартира
 *
 * @param address адрес
 * @param area площадь в м^2 * 100 потому что площадь может быть в сотых метра
 * @param residents число жителей
 */
public record Housing(String address, long area, int residents) {

    public Housing {
        Objects.requireNonNull(address, "адрес");
        if (area <= 0) {
            throw new IllegalArgumentException("площадь <= 0: " + area);
        }
        if (residents < 0) {
            throw new IllegalArgumentException("жители < 0: " + residents);
        }
    }

    // площадь в м^2 с запятой чисто для печати
    public double areaSquareMeters() {
        return area / 100.0;
    }
}