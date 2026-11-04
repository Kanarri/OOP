package model;

import java.util.HashSet;
import java.util.List;

public record District(DistrictId id, List<Candidate> candidates) {

    public District {
        if (id == null) {
            throw new IllegalArgumentException("district id is null");
        }
        if (candidates == null) {
            throw new IllegalArgumentException("district candidates is null");
        }
        candidates = List.copyOf(candidates);
        if (new HashSet<>(candidates).size() != candidates.size()) {
            throw new IllegalArgumentException("duplicate candidates in district");
        }
    }

    public int candidatesCount() {
        return candidates.size();
    }
}