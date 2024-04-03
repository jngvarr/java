package exceptions;

public class NeededObjectNotFound extends RuntimeException {
    public NeededObjectNotFound(String message){
        super(message);
    }
}
