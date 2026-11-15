package method;

import model.Ballot;
import model.Candidate;
import model.Election;
import result.ProtocolStep;
import result.Result;

import java.util.*;

/**
 * Относительное большинство: побеждает кандидат с наибольшим числом первых мест.
 * Протокол: один шаг со счётом первых мест по кандидатам.
 * При равенстве максимального счёта возвращается ничья (несколько победителей).
 */
public final class Plurality implements VotingMethod {
    @Override
    public String name() {
        return "Относительное большинство";
    }

    @Override
    public Result count(Election election) {
        Map<Candidate, Integer> scores = new LinkedHashMap<>();
        for (Candidate c: election.candidates()) {
            scores.put(c, 0);
        }

        for (Ballot b : election.ballots()) {
            Candidate first = b.ranking().get(0);
            scores.merge(first, 1, Integer::sum);
        }

        int max = Collections.max(scores.values());
        List<Candidate> leaders = scores.entrySet().stream()
                .filter(e -> e.getValue() == max)
                .map(Map.Entry::getKey)
                .toList();
        
        ProtocolStep step = new ProtocolStep(1, scores, List.of(), "первые места");
        return new Result(leaders, false, List.of(step));
    }
}