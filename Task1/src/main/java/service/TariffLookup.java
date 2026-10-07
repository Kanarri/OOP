package service;

import model.ServiceType;
import model.Tariff;

import java.time.LocalDate;

public interface TariffLookup {
    Tariff tariffForDate(ServiceType service, LocalDate date);
}