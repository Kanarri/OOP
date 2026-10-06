package model;

import java.time.YearMonth;

/**
 * Правило начисления за месяц по одной услуге.
 */
public sealed interface ChargingRule
        permits ByMeterRule, ByNormRule {

    /**
     * Рассчитать начисление за месяц.
     *
     * @param account лицевой счёт
     * @param service услуга
     * @param month   расчётный месяц
     * @return строка начисления с расшифровкой
     */
    Charge charge(Account account, ServiceType service, YearMonth month);
}