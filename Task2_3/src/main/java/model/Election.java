package model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record Election(List<Candidate> candidates, List<Ballot> ballots) {
    public Election {
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalArgumentException("candidates is null or empty");
        }
        if (ballots == null || ballots.isEmpty()) {
            throw new IllegalArgumentException("ballots is null or empty");
        }
        candidates = List.copyOf(candidates);
        ballots = List.copyOf(ballots);

        if (new HashSet<>(candidates).size() != candidates.size()) {
            throw new IllegalArgumentException("list of candidates has duplicates");
        }

        Set<Candidate> candidateSet = new HashSet<>(candidates);
        int k = candidates.size();

        for (Ballot b : ballots) {
            if (b.size() != k) {
                throw new IllegalArgumentException("ballot must contain all candidates");
            }
            for (Candidate c : b.ranking()) {
                if (!candidateSet.contains(c)) {
                    throw new IllegalArgumentException("ballot contains unknown candidate: " + c);
                }
            }
        }
    }

    public int candidateCount() {
        return candidates.size();
    }

    public int ballotCount() {
        return ballots.size();
    }
}