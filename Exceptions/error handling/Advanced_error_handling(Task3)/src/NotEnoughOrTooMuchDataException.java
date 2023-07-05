public class NotEnoughOrTooMuchDataException extends RuntimeException {

    public NotEnoughOrTooMuchDataException(int length) {
        super("Введенных данных" + (length < 6 ? " недостаточно!" : "слишком много!"));
    }
}
