package model;

import java.util.*;

public final class ElectionAssembler {
    private ElectionAssembler() {}

    public static Election forDistrict(District district, List<Voter> voters) {
        if (district == null)
            throw new IllegalArgumentException("district is null");
        if (voters.isEmpty()){
            throw new IllegalArgumentException("voters list is empty");
        }
        List<Ballot> ballots = getBallots(district, voters);

        if (ballots.isEmpty())
            throw new IllegalArgumentException("ballots list is empty");

        return new Election(district.candidates(), ballots);
    }

    private static List<Ballot> getBallots(District district, List<Voter> voters) {
        Set<Candidate> candidateSet = new HashSet<>(district.candidates());
        int k = district.candidatesCount();
        List<Ballot> ballots = new ArrayList<>();

        for (Voter v : voters) {
            if (!v.district().equals(district.districtId())) {
                continue;
            }
            Ballot ballot = v.ballot();
            if (ballot == null) {
                throw new IllegalArgumentException("voter's ballot is null");
            }
            if (ballot.size() != k) {
                throw new IllegalArgumentException("voter's ballot size is wrong");
            }

            for (Candidate c: ballot.ranking()) {
                if (!candidateSet.contains(c)) {
                    throw new IllegalArgumentException("voter's ballot has candidate outside district");
                }
            }
            ballots.add(ballot);
        }
        return ballots;
    }

    public static Map<String, Election> forAllDistricts(List<District> districts,
                                                        List<Voter> voters) {
        if (districts.isEmpty())
            throw new IllegalArgumentException("districts is empty");
        if (voters.isEmpty())
            throw new IllegalArgumentException("voters is empty");

        Set<String> districtIds = new HashSet<>();
        for (District d: districts) {
            districtIds.add(d.districtId());
        }

        for (Voter v: voters) {
            if (!districtIds.contains(v.district())) {
                throw new IllegalArgumentException("voter is from wrong district");
            }
        }

        Map<String, Election> result = new LinkedHashMap<>();
        for (District d: districts) {
            result.put(d.districtId(), forDistrict(d, voters));
        }
        return result;
    }
}