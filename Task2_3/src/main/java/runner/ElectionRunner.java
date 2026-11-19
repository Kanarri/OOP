package runner;

import method.VotingMethod;
import model.Election;
import result.Result;

import java.util.*;

/**
 * Прогоняет набор методов подсчёта на одном округе и собирает сравнительную таблицу.
 */
public final class ElectionRunner {
    private final List<VotingMethod> methods;

    public ElectionRunner(List<VotingMethod> methods) {
        if (methods == null || methods.isEmpty()) {
            throw new IllegalArgumentException("methods is null or empty");
        }
        for (VotingMethod m : methods) {
            if (m == null) throw new IllegalArgumentException("methods contains null");
        }
        this.methods = List.copyOf(methods);
    }

    public Map<VotingMethod, Result> runAll(Election election) {
        if (election == null) throw new IllegalArgumentException("election is null");
        Map<VotingMethod, Result> out = new LinkedHashMap<>();
        for (VotingMethod m : methods) {
            out.put(m, m.count(election));
        }
        return out;
    }

    public Map<String, String> winnersSummary(Election election) {
        Map<VotingMethod, Result> results = runAll(election);

        List<VotingMethod> sorted = new ArrayList<>(results.keySet());
        sorted.sort(Comparator.comparing(VotingMethod::name));

        Map<String, String> out = new LinkedHashMap<>();
        for (VotingMethod m : sorted) {
            out.put(m.name(), winnerText(results.get(m)));
        }
        return out;
    }

    public Map<String, Map<String, String>> runAllDistricts(Map<String, Election> byDistrict) {
        if (byDistrict == null || byDistrict.isEmpty()) {
            throw new IllegalArgumentException("byDistrict is null or empty");
        }
        Map<String, Map<String, String>> out = new LinkedHashMap<>();
        for (Map.Entry<String, Election> e : byDistrict.entrySet()) {
            out.put(e.getKey(), winnersSummary(e.getValue()));
        }
        return out;
    }

    private static String winnerText(Result r) {
        if (r.isCondorcetCycle()) return "нет победителя Кондорсе";
        if (r.hasSingleWinner()) return r.singleWinner().toString();
        return "НИЧЬЯ: " + r.winners();
    }
}