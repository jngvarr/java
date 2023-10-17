package ru.gb.jdk.two.online.circles.exceptions;

public class TooMuchBallException extends RuntimeException{
    public TooMuchBallException(){
        super("Максимальное количество шаров - 15");
    }
}
