package tests;

import model.ServiceType;
import model.Tariff;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TariffTest {

    private Tariff tariff() {
        return new Tariff(ServiceType.COLD_WATER, null,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 7, 1),
                45_00);
    }

    @Test
    void activeOn_fromInclusive_true() {
        assertTrue(tariff().activeOn(LocalDate.of(2025, 1, 1)));
    }

    @Test
    void activeOn_toExclusive_false() {
        assertFalse(tariff().activeOn(LocalDate.of(2025, 7, 1)));
    }

    @Test
    void activeOn_beforeFrom_false() {
        assertFalse(tariff().activeOn(LocalDate.of(2024, 12, 31)));
    }

    @Test
    void constructor_toNotAfterFrom_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Tariff(ServiceType.COLD_WATER, null,
                        LocalDate.of(2025, 7, 1),
                        LocalDate.of(2025, 1, 1),
                        45_00));
    }
}