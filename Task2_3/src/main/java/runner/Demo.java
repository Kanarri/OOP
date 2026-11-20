package runner;

import method.*;
import model.*;
import java.util.List;
import java.util.Map;

public class Demo {
    public static void main(String[] args) {
        Candidate a = new Candidate("A", "Аня");
        Candidate b = new Candidate("B", "Боря");
        Candidate c = new Candidate("C", "Вера");

        District district = new District("north", List.of(a, b, c));
        List<Voter> voters = List.of(
                new Voter("v1", "north", new Ballot(List.of(a, b, c))),
                new Voter("v2", "north", new Ballot(List.of(b, c, a))),
                new Voter("v3", "north", new Ballot(List.of(c, a, b)))
        );

        Election election = ElectionAssembler.forDistrict(district, voters);

        ElectionRunner runner = new ElectionRunner(List.of(
                new Plurality(), new TwoRounds(), new Borda(), new Condorcet(), new STV()
        ));

        Map<String, String> table = runner.winnersSummary(election);
        table.forEach((method, winner) -> System.out.println(method + " -> " + winner));
    }
}