package result;

import model.Candidate;

import java.util.List;
import java.util.Map;

public record ProtocolStep(int round, Map<Candidate, Integer> scores, List<Candidate> eliminated, String note){ }
