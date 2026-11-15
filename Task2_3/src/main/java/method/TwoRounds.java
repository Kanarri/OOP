package method;

import model.Ballot;
import model.Candidate;
import model.Election;
import result.ProtocolStep;
import result.Result;
import java.util.*;

/**
 * Два тура: если в первом туре никто не набрал абсолютного большинства
 * (строго больше 50% бюллетеней), во второй тур проходят двое лидеров
 * и победитель определяется попарным сравнением в бюллетенях.
 *
 * Протокол: один шаг, если победитель определился в первом туре;
 * два шага (первый и второй тур) — иначе.
 */
public final class TwoRounds implements VotingMethod {

    @Override
    public String name() {
        return "Два тура";
    }

    @Override
    public Result count(Election election) {
        Map<Candidate, Integer> first = firstPlaces(election);
        int total = election.ballotCount();

        List<Candidate> majority = first.entrySet().stream()
                .filter(e -> e.getValue() * 2 > total)
                .map(Map.Entry::getKey)
                .toList();

        ProtocolStep step1 = new ProtocolStep(1, first, List.of(), "Первый тур");

        if (majority.size() == 1) {
            return new Result(majority, false, List.of(step1));
        }

        List<Candidate> top2 = first.entrySet().stream()
                .sorted(Map.Entry.<Candidate, Integer>comparingByValue().reversed()
                        .thenComparing(e -> e.getKey().id()))
                .limit(2)
                .map(Map.Entry::getKey)
                .toList();

        if (top2.size() < 2) {
            return new Result(top2, false, List.of(step1));
        }

        Map<Candidate, Integer> second = new LinkedHashMap<>();
        second.put(top2.get(0), 0);
        second.put(top2.get(1), 0);

        for (Ballot b : election.ballots()) {
            Candidate preferred = b.isFirstUpperThanSecond(top2.get(0), top2.get(1))
                    ? top2.get(0)
                    : top2.get(1);
            second.merge(preferred, 1, Integer::sum);
        }

        List<Candidate> eliminated = election.candidates().stream()
                .filter(c -> !top2.contains(c))
                .toList();

        ProtocolStep step2 = new ProtocolStep(2, second, eliminated, "Второй тур");

        int max = Collections.max(second.values());
        List<Candidate> leaders = second.entrySet().stream()
                .filter(e -> e.getValue() == max)
                .map(Map.Entry::getKey)
                .toList();

        return new Result(leaders, false, List.of(step1, step2));
    }

    private Map<Candidate, Integer> firstPlaces(Election election) {
        Map<Candidate, Integer> scores = new LinkedHashMap<>();
        for (Candidate c : election.candidates()) {
            scores.put(c, 0);
        }
        for (Ballot b : election.ballots()) {
            scores.merge(b.ranking().get(0), 1, Integer::sum);
        }
        return scores;
    }
}