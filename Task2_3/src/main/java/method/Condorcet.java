package method;

import model.Ballot;
import model.Candidate;
import model.Election;
import result.ProtocolStep;
import result.Result;

import java.util.*;

/**
 * Метод Кондорсе: побеждает кандидат, который в попарном сравнении бьёт каждого другого кандидата.
 * Если такого кандидата нет (цикл попарных предпочтений - парадокс Кондорсе),
 * возвращается {@code Result} с {@code condorcetCycle == true} и пустым списком
 * победителей. Это валидный исход, а не ошибка
 *
 * Протокол: один шаг, в {@code scores} — число побед в парах у каждого кандидата.
 */
public final class Condorcet implements VotingMethod {

    @Override
    public String name() {
        return "Метод Кондорсе";
    }

    @Override
    public Result count(Election election) {
        List<Candidate> cs = election.candidates();
        int n = cs.size();

        int[][] wins = new int[n][n];
        for (Ballot b : election.ballots()) {
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (b.isFirstUpperThanSecond(cs.get(i), cs.get(j))) {
                        wins[i][j]++;
                    } else {
                        wins[j][i]++;
                    }
                }
            }
        }

        Map<Candidate, Integer> pairwiseWins = new LinkedHashMap<>();
        for (int i = 0; i < n; i++) {
            int w = 0;
            for (int j = 0; j < n; j++) {
                if (i != j && wins[i][j] > wins[j][i]) {
                    w++;
                }
            }
            pairwiseWins.put(cs.get(i), w);
        }

        List<Candidate> winners = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            boolean beatsAll = true;
            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                if (wins[i][j] <= wins[j][i]) {
                    beatsAll = false;
                    break;
                }
            }
            if (beatsAll) {
                winners.add(cs.get(i));
            }
        }

        ProtocolStep step = new ProtocolStep(1, pairwiseWins, List.of(), "pairwise wins");

        if (!winners.isEmpty()) {
            return new Result(winners, false, List.of(step));
        }
        return new Result(List.of(), true, List.of(step));
    }
}