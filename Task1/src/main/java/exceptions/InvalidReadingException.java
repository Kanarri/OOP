package exceptions;

import model.MeterReadings;

/**
 * если показание меньше предыдущего то ошибка
 */
public class InvalidReadingException extends RuntimeException {

    private final MeterReadings previous;
    private final MeterReadings current;

    public InvalidReadingException(MeterReadings previous, MeterReadings current) {
        super("Показание " + current.value() + " (" + current.date()
                + ") меньше предыдущего " + previous.value()
                + " (" + previous.date() + ")");
        this.previous = previous;
        this.current = current;
    }
    public MeterReadings getPrevious() {
        return previous;
    }
    public MeterReadings getCurrent(){
        return current;
    }
}