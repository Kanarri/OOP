package result;

import model.Candidate;

import java.util.List;

/**
 * Результат подсчёта: победитель(и), признак цикла Кондорсе и протокол шагов.
 * <p>
 * Три возможных исхода различаются так:
 * <ul>
 *   <li>{@code condorcetCycle == false} и {@code winners.size() == 1} — один победитель;</li>
 *   <li>{@code condorcetCycle == false} и {@code winners.size() > 1} — ничья;</li>
 *   <li>{@code condorcetCycle == true} и {@code winners} пуст — нет победителя Кондорсе
 *       (цикл попарных предпочтений).</li>
 * </ul>
 * <p>
 * Инвариант: пустой {@code winners} допустим <b>только</b> при {@code condorcetCycle == true}.
 * Иначе «пусто» было бы двусмысленным (цикл или забыли заполнить).
 * <p>
 * Иммутабельный value-объект: {@code winners} и {@code protocol} копируются
 * в компактном конструкторе.
 *
 * @param winners        победители: ровно один при победе, несколько при ничьей,
 *                       пусто при цикле Кондорсе; не {@code null}
 * @param condorcetCycle {@code true}, если метод Кондорсе не смог определить
 *                       победителя из-за цикла попарных предпочтений
 * @param protocol       протокол подсчёта, минимум один шаг; не {@code null}, не пуст
 */
public record Result(
        List<Candidate> winners,
        boolean condorcetCycle,
        List<ProtocolStep> protocol
) {
    /**
     * @throws IllegalArgumentException если {@code winners}/{@code protocol} равны {@code null},
     *                                  {@code protocol} пуст, либо нарушено соответствие
     *                                  {@code condorcetCycle} и {@code winners}
     */
    public Result {
        if (winners == null) {
            throw new IllegalArgumentException("winners is null");
        }
        if (protocol == null || protocol.isEmpty()) {
            throw new IllegalArgumentException("protocol is empty");
        }
        winners = List.copyOf(winners);
        protocol = List.copyOf(protocol);

        if (condorcetCycle && !winners.isEmpty()) {
            throw new IllegalArgumentException("condorcetCycle -> winners must be empty");
        }
        if (!condorcetCycle && winners.isEmpty()) {
            throw new IllegalArgumentException("empty winners only allowed for condorcet cycle");
        }
    }

    /**
     * @return {@code true}, если есть ровно один победитель (не цикл и не ничья)
     */
    public boolean hasSingleWinner() {
        return !condorcetCycle && winners.size() == 1;
    }

    /**
     * @return единственный победитель
     * @throws IllegalStateException если победителя нет или их несколько
     */
    public Candidate singleWinner() {
        if (!hasSingleWinner()) {
            throw new IllegalStateException("no single winner");
        }
        return winners.get(0);
    }

    /**
     * @return {@code true}, если это ничья (не цикл и несколько победителей)
     */
    public boolean isTie() {
        return !condorcetCycle && winners.size() > 1;
    }

    /**
     * @return {@code true}, если победителя Кондорсе не существует (цикл попарных предпочтений)
     */
    public boolean isCondorcetCycle() {
        return condorcetCycle;
    }
}