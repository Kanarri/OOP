package result;

import model.Candidate;

import java.util.List;
import java.util.Map;

/**
 * Один шаг протокола подсчёта: номер тура/раунда, счёт по кандидатам,
 * кто выбыл на этом шаге, и короткое пояснение.
 * Число шагов зависит от метода:
 *
 * плюралити, Борд, Кондорсе — один шаг
 * два тура — до двух шагов (первый и второй тур)
 * STV — столько шагов, сколько раундов исключения потребовалось.
 *
 * @param round номер тура/раунда, начиная с {@code 1}
 * @param scores счёт по кандидатам на этом шаге; не {@code null},
 *               ключи — кандидаты, значения — целочисленный счёт
 *               (голоса, баллы Борда, победы в парах и т.п.)
 * @param eliminated кандидаты, выбывшие на этом шаге; может быть пустым,
 *                   но не {@code null} (в конструкторе заменяется на пустой список)
 * @param note короткое пояснение шага (например, {@code "first places"},
 *             {@code "second round"}, {@code "eliminated"}); может быть пустым,
 *             но не {@code null} (в конструкторе заменяется на {@code ""})
 */
public record ProtocolStep(
        int round,
        Map<Candidate, Integer> scores,
        List<Candidate> eliminated,
        String note
) {
    public ProtocolStep {
        if (round < 1) {
            throw new IllegalArgumentException("round must be >= 1");
        }
        if (scores == null) {
            throw new IllegalArgumentException("scores is null");
        }
        scores = Map.copyOf(scores);
        eliminated = eliminated == null ? List.of() : List.copyOf(eliminated);
        note = note == null ? "" : note;
    }
}