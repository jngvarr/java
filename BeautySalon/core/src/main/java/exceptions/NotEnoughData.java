package exceptions;

public class NotEnoughData extends RuntimeException {
    public NotEnoughData(String message) {
        super(message);
    }

}
