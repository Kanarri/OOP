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
        List<Norma> norms = buildNorms();
        List<Account> accounts = buildAccounts();
        List<Meter> meters = buildMeters(accounts);

        ChargingService charger = new ChargingService(tariffs, norms, meters);

        showConsumption(meters);
        showEqualsContract(accounts);
    }

    private static void showEqualsContract(List<Account> accounts) {
        System.out.println("Контракт equals/hashCode");

        Account first = accounts.get(0);
        Account sameNumber = new Account(first.getNumber(),
                new Housing("другой адрес", 1_000, 1));
        Account other = accounts.get(1);

        System.out.println("Одинаковый номер => equals = " + first.equals(sameNumber));
        System.out.println("Одинаковый номер => hashCode равны = "
                + (first.hashCode() == sameNumber.hashCode()));
        System.out.println("Разные номера => equals = " + first.equals(other));
        System.out.println();
    }

    private static void showConsumption(List<Meter> meters) {
        System.out.println("Расход между показаниями");

        Meter cold = meters.stream()
                .filter(m -> m.getService() == ServiceType.COLD_WATER)
                .findFirst()
                .orElseThrow();

        List<MeterReadings> rs = cold.getReadings();
        MeterReadings r1 = rs.get(0);
        MeterReadings r2 = rs.get(1);

        long consumption = MeterReadings.consumptionBetween(r1, r2);
        System.out.printf("  Счётчик %s: с %s по %s израсходовано %d %s%n",
                cold.getSerial(), r1.date(), r2.date(),
                consumption, cold.getService().getUnit());
        System.out.println();
    }

    //СЧЕТЧИКИ
    private static List<Meter> buildMeters(List<Account> accounts) {
        List<Meter> meters = new ArrayList<>();

        for (int i = 0; i < accounts.size(); i++) {
            Account acc = accounts.get(i);
            long base = 1000 + i * 100L;

            //холодная
            Meter cold = new Meter("CW-" + i, acc, ServiceType.COLD_WATER, null);
            cold.addReading(new MeterReadings(LocalDate.of(2025, 1, 1), base));
            cold.addReading(new MeterReadings(LocalDate.of(2025, 2, 1), base + 5 + i));
            cold.addReading(new MeterReadings(LocalDate.of(2025, 3, 1), base + 11 + i));
            meters.add(cold);

            //горячая
            Meter hot = new Meter("HW-" + i, acc, ServiceType.HOT_WATER, null);
            hot.addReading(new MeterReadings(LocalDate.of(2025, 1, 1), base));
            hot.addReading(new MeterReadings(LocalDate.of(2025, 2, 1), base + 3));
            hot.addReading(new MeterReadings(LocalDate.of(2025, 3, 1), base + 7));
            meters.add(hot);

            // электричество, дневная зона
            Meter el = new Meter("EL-" + i, acc, ServiceType.ELECTRICITY, TarifZone.DAY);
            el.addReading(new MeterReadings(LocalDate.of(2025, 1, 1), base * 10));
            el.addReading(new MeterReadings(LocalDate.of(2025, 2, 1), base * 10 + 150));
            el.addReading(new MeterReadings(LocalDate.of(2025, 3, 1), base * 10 + 320));
            meters.add(el);
        }

        return meters;
    }

    //СЧЕТА
    private static List<Account> buildAccounts() {
        List<Account> list = new ArrayList<>();
        list.add(new Account("ЛС-1", new Housing("ул. Ленина, 1, кв. 1", 5_400, 3)));
        list.add(new Account("ЛС-2", new Housing("ул. Ленина, 1, кв. 2", 7_250, 4)));
        list.add(new Account("ЛС-3", new Housing("ул. Ленина, 1, кв. 3", 3_800, 2)));
        list.add(new Account("ЛС-4", new Housing("ул. Мира, 5, кв. 10", 6_100, 1)));
        list.add(new Account("ЛС-5", new Housing("ул. Мира, 5, кв. 11", 4_500, 3)));
        return list;
    }

    //НОРМАТИВЫ
    private static List<Norma> buildNorms() {
        System.out.println("Нормативы");
        List<Norma> list = new ArrayList<>();

        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 1);

        // отопление 0,020 гкал на м2
        list.add(new Norma(ServiceType.HEATING, false, 20, from, to));
        // вывоз мусора 1,5 м3 на человека
        list.add(new Norma(ServiceType.GARBAGE, true, 1_500, from, to));

        for (Norma n : list) {
            System.out.printf("  %-12s  %-8s  %d,%03d  [%s .. %s)%n",
                    n.service(),
                    n.perPerson() ? "на чел." : "на м²",
                    n.valueMilli() / 1000, n.valueMilli() % 1000,
                    n.from(), n.to());
        }
        System.out.println();
        return list;
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