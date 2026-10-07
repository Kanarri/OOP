package tests;

import model.*;
import org.junit.jupiter.api.Test;
import service.ChargingService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChargingServiceTest {

    private static final LocalDate FROM = LocalDate.of(2025, 1, 1);
    private static final LocalDate TO   = LocalDate.of(2026, 1, 1);

    private Account account() {
        return new Account("ЛС-001", new Housing("адрес", 5_000, 3));
    }

    @Test
    void chargeFor_byMeter_computesConsumptionTimesPrice() {
        Account acc = account();

        Meter meter = new Meter("CW-1", acc, ServiceType.COLD_WATER, null);
        meter.addReading(new MeterReadings(LocalDate.of(2025, 1, 1), 100));
        meter.addReading(new MeterReadings(LocalDate.of(2025, 2, 1), 110));

        List<Tariff> tariffs = List.of(new Tariff(
                ServiceType.COLD_WATER, null, FROM, TO, 45_00));
        List<Norma> norms = List.of();

        ChargingService service = new ChargingService(tariffs, norms, List.of(meter));
        Charge charge = service.chargeFor(acc, ServiceType.COLD_WATER, YearMonth.of(2025, 2));

        // расход 10, цена 45.00 руб = 4500 коп
        assertEquals(10 * 4500L, charge.sumKopeyks());
    }

    @Test
    void chargeFor_byNorm_computesNormTimesPrice() {
        Account acc = account();  // 3 человека

        List<Tariff> tariffs = List.of(new Tariff(
                ServiceType.GARBAGE, null, FROM, TO, 80_00));
        // норматив 1.5 м³ на человека → valueMilli = 1500
        List<Norma> norms = List.of(new Norma(
                ServiceType.GARBAGE, true, 1500, FROM, TO));

        ChargingService service = new ChargingService(tariffs, norms, List.of());
        Charge charge = service.chargeFor(acc, ServiceType.GARBAGE, YearMonth.of(2025, 2));
        assertEquals(36_000L, charge.sumKopeyks());
    }

    @Test
    void tariffForDate_noMatchingTariff_throws() {
        Account acc = account();
        ChargingService service = new ChargingService(List.of(), List.of(), List.of());

        assertThrows(IllegalStateException.class,
                () -> service.tariffForDate(ServiceType.COLD_WATER, LocalDate.of(2025, 3, 1)));
    }

    @Test
    void chargeFor_meterWithoutReadings_fallsBackToNorm() {
        Account acc = account();

        Meter meter = new Meter("CW-1", acc, ServiceType.COLD_WATER, null);
        // показаний нет

        List<Tariff> tariffs = List.of(new Tariff(
                ServiceType.COLD_WATER, null, FROM, TO, 45_00));
        // норматив 2.0 м³ на человека
        List<Norma> norms = List.of(new Norma(
                ServiceType.COLD_WATER, true, 2000, FROM, TO));

        ChargingService service = new ChargingService(tariffs, norms, List.of(meter));
        Charge charge = service.chargeFor(acc, ServiceType.COLD_WATER, YearMonth.of(2025, 2));

        // 3 чел × 2000 = 6000, × 4500 / 1000 = 27000 коп
        assertEquals(27_000L, charge.sumKopeyks());
    }
}