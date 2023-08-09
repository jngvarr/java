package Exceptions;

public class UnclosedResourceException extends Exception{
    public UnclosedResourceException(String msg) {
        super(msg);
    }
}
