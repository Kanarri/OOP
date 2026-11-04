package model;

import java.util.HashSet;
import java.util.List;

public record District(String districtId, List<Candidate> candidates) {

    public District {
        if (districtId == null) {
            throw new IllegalArgumentException("id is null");
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