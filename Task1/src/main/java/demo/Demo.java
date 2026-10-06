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

    private static List<Tariff> buildTariffs() {
    }
}