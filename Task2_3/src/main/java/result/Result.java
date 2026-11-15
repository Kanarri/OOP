package result;

import model.Candidate;
import java.util.List;

public record Result(List<Candidate> winners, boolean condorcetCycle, List<ProtocolStep> protocol){ }
