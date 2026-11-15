package method;

import model.Ballot;
import model.Candidate;
import model.Election;
import result.ProtocolStep;
import result.Result;

import java.util.*;

/**
 * Метод Борда: за 1-е место кандидат получает k-1 балл, за последнее — 0,
 * Протокол: один шаг с суммой баллов по кандидатам.
 * При равенстве максимальной суммы возвращается ничья.
 */
public final class Borda implements VotingMethod {

    @Override
    public String name() {
        return "Метод Борда";
    }

    @Override
    public Result count(Election election) {
        int k = election.candidateCount();

        Map<Candidate, Integer> scores = new LinkedHashMap<>();
        for (Candidate c : election.candidates()) {
            scores.put(c, 0);
        }

        for (Ballot b : election.ballots()) {
            List<Candidate> ranking = b.ranking();
            for (int i = 0; i < ranking.size(); i++) {
                int points = k - 1 - i;
                scores.merge(ranking.get(i), points, Integer::sum);
            }
        }

        int max = Collections.max(scores.values());
        List<Candidate> leaders = scores.entrySet().stream()
                .filter(e -> e.getValue() == max)
                .map(Map.Entry::getKey)
                .toList();

        ProtocolStep step = new ProtocolStep(1, scores, List.of(), "Баллы Борда");
        return new Result(leaders, false, List.of(step));
    }
}