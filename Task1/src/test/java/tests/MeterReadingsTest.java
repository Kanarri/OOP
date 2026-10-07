package tests;

import model.MeterReadings;
import org.junit.jupiter.api.Test;
import exceptions.InvalidReadingException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MeterReadingsTest {

    @Test
    void consumption_normal_returnsDifference() {
        MeterReadings r1 = new MeterReadings(LocalDate.of(2025, 1, 1), 100);
        MeterReadings r2 = new MeterReadings(LocalDate.of(2025, 2, 1), 150);
        assertEquals(50, MeterReadings.consumptionBetween(r1, r2));
    }

    @Test
    void consumption_equalValues_returnsZero() {
        MeterReadings r1 = new MeterReadings(LocalDate.of(2025, 1, 1), 100);
        MeterReadings r2 = new MeterReadings(LocalDate.of(2025, 2, 1), 100);
        assertEquals(0, MeterReadings.consumptionBetween(r1, r2));
    }

    @Test
    void consumption_lessThanPrevious_throws() {
        MeterReadings r1 = new MeterReadings(LocalDate.of(2025, 1, 1), 150);
        MeterReadings r2 = new MeterReadings(LocalDate.of(2025, 2, 1), 100);
        assertThrows(InvalidReadingException.class,
                () -> MeterReadings.consumptionBetween(r1, r2));
    }

    @Test
    void constructor_negativeValue_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new MeterReadings(LocalDate.of(2025, 1, 1), -1));
    }
}