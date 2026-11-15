package service;

import model.*;

import java.time.YearMonth;

/**
 * Правило начисления за месяц по одной услуге.
 */
public sealed interface ChargingRule
        permits ByMeterRule, ByNormRule {
    Charge charge(Account account, ServiceType service, YearMonth month);
}