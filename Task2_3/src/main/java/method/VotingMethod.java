package method;

import model.Election;
import result.Result;

/**
 * Метод подсчёта голосов.
 */
public interface VotingMethod {
    String name();
    Result count(Election election);
}