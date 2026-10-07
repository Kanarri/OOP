package model;

//единица измерения и по счетчику/нормативу считается

//true - по счетчику, false по норме
public enum ServiceType {
    GAS("м^3", true),
    HOT_WATER("м^3", true),
    COLD_WATER("м^3", true),
    ELECTRICITY("кВт*ч", true),
    HEATING("гКал", false),
    GARBAGE("чел.", false)
    ;

    private final String unit;
    private final boolean byMeter;

    ServiceType(String unit, boolean byCounter) {
        this.unit = unit;
        this.byMeter = byCounter;
    }
    public String getUnit(){
        return unit;
    }
    public boolean isByMeter(){
        return byMeter;
    }

}
