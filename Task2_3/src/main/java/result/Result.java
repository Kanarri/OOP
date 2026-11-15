package result;

import model.Candidate;
import java.util.List;

/**
 * Результат подсчёта: победитель, признак цикла Кондорсе и протокол шагов.
 * Три возможных исхода различаются так:
 *
 * @param winners победители: ровно один при победе, несколько при ничьей,
 *                пусто при цикле Кондорсе; не {@code null}
 * @param condorcetCycle {@code true}, если метод Кондорсе не смог определить
 *                       победителя из-за цикла попарных предпочтений
 * @param protocol протокол подсчёта, минимум один шаг; не {@code null}, не пуст
 */
public record Result(List<Candidate> winners, boolean condorcetCycle, List<ProtocolStep> protocol){ }
