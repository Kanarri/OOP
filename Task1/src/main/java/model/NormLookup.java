package model;

import java.time.LocalDate;

public interface NormLookup {
    Norma normFor(ServiceType service, LocalDate date);
}