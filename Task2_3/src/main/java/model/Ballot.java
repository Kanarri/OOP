package model;

import java.util.List;

/**
 * Бюллетень избирателя -  упорядочивание кандидатов от наиболее к наименее предпочитаемому.
 * Два бюллетеня равны, только если совпадает весь порядок.
 * Бюллетень не знает, в каком округе он подан.
 * @param ranking упорядоченный список кандидатов.
 */
public record Ballot(List<Candidate> ranking){
    public Ballot{
        if (ranking == null || ranking.isEmpty())
            throw new IllegalArgumentException("ranking can't be empty or null");
        if (ranking.stream().distinct().count() != ranking.size())
            throw new IllegalArgumentException("ranking contains duplicates");
        ranking = List.copyOf(ranking);
    }

    public int size(){
        return ranking.size();
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
