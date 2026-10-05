package model;

public enum TarifZone {
    DAY("день"),
    NIGHT("ночь");

    private final String zone;

    TarifZone(String zone){
        this.zone = zone;
    }

    public String getZone() {
        return zone;
    }
}
