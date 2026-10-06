package model;

import java.time.LocalDate;

public interface TariffLookup {
    Tariff tariffForDate(ServiceType service, LocalDate date);
}