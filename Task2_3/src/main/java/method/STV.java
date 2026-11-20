package method;

import model.Ballot;
import model.Candidate;
import model.Election;
import result.ProtocolStep;
import result.Result;

import java.util.*;

/**
 * Передаваемый голос: раунды исключения.
 * В каждом раунде бюллетень отдаёт голос первому живому кандидату.
 * Побеждает тот, кто набирает строго больше 50% от неисчерпанных бюллетеней
 * (бюллетень исчерпан, если все его кандидаты выбыли).
 * Иначе выбывает кандидат с наименьшим счётом (тай-брейк)
 * Протокол: по одному шагу на раунд; в {@code eliminated} — кто выбыл в этом раунде.
 */
public final class STV implements VotingMethod {

    @Override
    public String name() {
        return "Передаваемый голос";
    }

    @Override
    public Result count(Election election) {
        Set<Candidate> alive = new LinkedHashSet<>(election.candidates());
        List<ProtocolStep> protocol = new ArrayList<>();
        int round = 1;

        while (true) {
            Map<Candidate, Integer> scores = new LinkedHashMap<>();
            for (Candidate c: alive) {
                scores.put(c, 0);
            }

            int exhausted = 0;
            for (Ballot b : election.ballots()) {
                Candidate first = firstAlive(b, alive);
                if (first == null) {
                    exhausted++;
                    continue;
                }
                scores.merge(first, 1, Integer::sum);
            }
            int active = election.ballotCount() - exhausted;

            List<Candidate> majority = scores.entrySet().stream()
                    .filter(e -> e.getValue() * 2 > active)
                    .map(Map.Entry::getKey)
                    .toList();

            if (majority.size() == 1) {
                protocol.add(new ProtocolStep(round, scores, List.of(), "Большинство"));
                return new Result(majority, false, protocol);
            }

            if (alive.size() == 1) {
                protocol.add(new ProtocolStep(round, scores, List.of(), "Последний оставшийся"));
                return new Result(List.of(alive.iterator().next()), false, protocol);
            }

            Candidate loser = scores.entrySet().stream()
                    .min(Map.Entry.<Candidate, Integer>comparingByValue()
                            .thenComparing(e -> e.getKey().id()))
                    .map(Map.Entry::getKey)
                    .orElseThrow();

            protocol.add(new ProtocolStep(round, scores, List.of(loser), "Выбыл"));
            alive.remove(loser);
            round++;
        }
    }

    private Candidate firstAlive(Ballot b, Set<Candidate> alive) {
        for (Candidate c : b.ranking()) {
            if (alive.contains(c)) return c;
        }
        return null;
    }
}