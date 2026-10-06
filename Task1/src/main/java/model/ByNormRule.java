package model;

import java.time.LocalDate;
import java.time.YearMonth;

public final class ByNormRule implements ChargingRule {

    private final NormLookup norms;
    private final TariffLookup tariffs;

    public ByNormRule(NormLookup norms, TariffLookup tariffs) {
        this.norms = norms;
        this.tariffs = tariffs;
    }

    @Override
    public Charge charge(Account account, ServiceType service, YearMonth month) {
        LocalDate mid = month.atDay(15);

        Norma norm = norms.normFor(service, mid);
        Tariff tariff = tariffs.tariffForDate(service, mid);

        Housing h = account.getHousing();

        long quantityMilli;
        if (norm.perPerson()) {
            quantityMilli = (long) h.residents() * norm.valueMilli();
        } else {
            quantityMilli = h.area() * norm.valueMilli() / 100;
        }

        long sum = quantityMilli * tariff.pricePerUnitKopeyks() / 1000;

        String explanation = String.format(
                "%s x %d,%03d x %d,%02d руб (норматив)",
                norm.perPerson()
                        ? h.residents() + " чел."
                        : h.areaSquareMeters() + " м2",
                norm.valueMilli() / 1000, norm.valueMilli() % 1000,
                tariff.pricePerUnitKopeyks() / 100, tariff.pricePerUnitKopeyks() % 100);

        return new Charge(service, month, sum, explanation);
    }
}