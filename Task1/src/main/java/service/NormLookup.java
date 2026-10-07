package service;

import model.Norma;
import model.ServiceType;

import java.time.LocalDate;

public interface NormLookup {
    Norma normFor(ServiceType service, LocalDate date);
}