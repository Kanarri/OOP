package model;

import java.util.List;

public record Ballot(List<Candidate> ranking){
    public Ballot{
        if (ranking == null || ranking.isEmpty())
            throw new IllegalArgumentException("ranking can't be empty or null");
        if (ranking.stream().distinct().count() != ranking().size())
            throw new IllegalArgumentException("ranking contains duplicates");
        ranking = List.copyOf(ranking);
    }

    public int size(){
        return ranking().size();
    }

    public int positionOf(Candidate candidate){
        return ranking.indexOf(candidate);
    }

    public boolean isFirstUpperThanSecond(Candidate a, Candidate b){
        int posA = ranking.indexOf(a);
        int posB = ranking.indexOf(b);
        if (posA < 0 || posB < 0)
            throw new IllegalArgumentException("one of candidates is not in ballot");
        return posA < posB;
    }
}
