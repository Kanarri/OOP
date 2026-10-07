package model;

import exceptions.InvalidReadingException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * счётчик привязан к лицевому счёту и услуге хранит историю показаний
 */
public final class Meter {

    private final String serial;
    private final Account account;
    private final ServiceType service;
    private final TarifZone zone;
    private final List<MeterReadings> readings = new ArrayList<>();

    public Meter(String serial, Account account, ServiceType service, TarifZone zone) {
        this.serial = Objects.requireNonNull(serial, "серийный номер");
        this.account = Objects.requireNonNull(account, "лицевой счёт");
        this.service = Objects.requireNonNull(service, "тип услуги");
        this.zone = zone;
    }

    public String getSerial() {
        return serial;
    }
    public Account getAccount(){
        return account;
    }
    public ServiceType getService(){
        return service;
    }
    public TarifZone getZone(){
        return zone;
    }

    //нкопия истории показаний
    public List<MeterReadings> getReadings() {
        return List.copyOf(readings);
    }

    //добавить показание + проверка что оно не меньше предыдущего
    public void addReading(MeterReadings reading) {
        Objects.requireNonNull(reading, "показание");
        lastReading().ifPresent(prev -> {
            if (reading.value() < prev.value()) {
                throw new InvalidReadingException(prev, reading);
            }
        });
        readings.add(reading);
    }

    // последнее по дате показание
    public Optional<MeterReadings> lastReading() {
        return readings.stream()
                .max(Comparator.comparing(MeterReadings::date));
    }

    //показание ближайшее к дате слева (не позже указанной)
    public Optional<MeterReadings> readingAtOrBefore(LocalDate date) {
        return readings.stream()
                .filter(r -> !r.date().isAfter(date))
                .max(Comparator.comparing(MeterReadings::date));
    }
}