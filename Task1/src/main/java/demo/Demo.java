package demo;

import model.Account;
import model.ChargingService;
import model.Housing;
import model.InvalidReadingException;
import model.Meter;
import model.MeterReadings;
import model.Norma;
import model.Receipt;
import model.ServiceType;
import model.TarifZone;
import model.Tariff;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Консольная демонстрация модели расчёта коммунальных начислений.
 */
public final class Demo {

    private Demo() {}

    public static void main(String[] args) {
        System.out.println("Показания счётчиков и начисления\n");

        List<Tariff> tariffs = buildTariffs();
        List<Norma> norms   = buildNorms();
        List<Account> accounts = buildAccounts();
        List<Meter> meters = buildMeters(accounts);

        ChargingService charger = new ChargingService(tariffs, norms, meters);
    }

    private static List<Meter> buildMeters(List<Account> accounts) {
    }

    private static List<Account> buildAccounts() {
    }

    private static List<Norma> buildNorms() {
    }

    // ТАРИФЫ
    private static List<Tariff> buildTariffs() {
        System.out.println("Тарифы");
        List<Tariff> list = new ArrayList<>();

        LocalDate h1From = LocalDate.of(2025, 1, 1);
        LocalDate h1To = LocalDate.of(2025, 7, 1);
        LocalDate h2From = LocalDate.of(2025, 7, 1);
        LocalDate h2To = LocalDate.of(2026, 1, 1);

        //первое полугодие 2025
        list.add(new Tariff(ServiceType.COLD_WATER, null, h1From, h1To, 45_00));
        list.add(new Tariff(ServiceType.HOT_WATER, null, h1From, h1To, 180_00));
        list.add(new Tariff(ServiceType.GAS, null, h1From, h1To, 7_50));
        list.add(new Tariff(ServiceType.ELECTRICITY, TarifZone.DAY, h1From, h1To, 5_50));
        list.add(new Tariff(ServiceType.ELECTRICITY, TarifZone.NIGHT, h1From, h1To, 2_50));
        list.add(new Tariff(ServiceType.HEATING,null, h1From, h1To, 1500_00));
        list.add(new Tariff(ServiceType.GARBAGE,null, h1From, h1To, 80_00));

        // второе полугодие 2025
        list.add(new Tariff(ServiceType.COLD_WATER, null, h2From, h2To, 48_00));
        list.add(new Tariff(ServiceType.HOT_WATER,  null, h2From, h2To, 192_00));
        list.add(new Tariff(ServiceType.GAS, null, h2From, h2To, 8_00));
        list.add(new Tariff(ServiceType.ELECTRICITY, TarifZone.DAY,   h2From, h2To, 6_00));
        list.add(new Tariff(ServiceType.ELECTRICITY, TarifZone.NIGHT, h2From, h2To, 2_80));
        list.add(new Tariff(ServiceType.HEATING, null, h2From, h2To, 1600_00));
        list.add(new Tariff(ServiceType.GARBAGE, null, h2From, h2To, 85_00));

        for (Tariff t : list) {
            System.out.printf("  %-12s %-5s  %d,%02d руб  [%s .. %s)%n",
                    t.service(),
                    t.zone() == null ? "—" : t.zone().getZone(),
                    t.pricePerUnitKopeyks() / 100,
                    t.pricePerUnitKopeyks() % 100,
                    t.from(), t.to());
        }
        System.out.println();
        return list;
    }

}