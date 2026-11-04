package model;

public record Voter(String id, DistrictId district, Ballot ballot) {
    public Voter {
        if (id == null) {
            throw new IllegalArgumentException("voter id is null");
        }
        if (district == null) {
            throw new IllegalArgumentException("voter district is null");
        }
        if (ballot == null) {
            throw new IllegalArgumentException("voter ballot is null");
        }
    }
}