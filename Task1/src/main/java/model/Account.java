package model;

import java.util.Objects;

/**
 * лицевой счет
 * два счёта с одинаковым номером считаются одним и тем же счётом
 */
public final class Account {

    private final String number;
    private final Housing housing;

    public Account(String number, Housing housing) {
        this.number = Objects.requireNonNull(number, "номер");
        this.housing = Objects.requireNonNull(housing, "жилплощадь");
        if (number.isBlank()) {
            throw new IllegalArgumentException("номер пуст");
        }
    }

    public String getNumber(){
        return number;
    }
    public Housing getHousing(){
        return housing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account other)) return false;
        return number.equals(other.number);
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }

    //читаемый адрес
    @Override
    public String toString() {
        return "Аккаунт[" + number + ", " + housing.address() + "]";
    }
}