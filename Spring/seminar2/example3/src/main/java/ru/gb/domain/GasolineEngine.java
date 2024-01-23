package ru.gb.domain;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class GasolineEngine implements iEngine{
    @Override
    public void startEngine() {
        System.out.println("Ездим на бензине");
    }
}
