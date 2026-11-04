package model;

public record DistrictId(String name) {
    public DistrictId {
        if (name == null) {
            throw new IllegalArgumentException("district id is null");
        }
    }
}
