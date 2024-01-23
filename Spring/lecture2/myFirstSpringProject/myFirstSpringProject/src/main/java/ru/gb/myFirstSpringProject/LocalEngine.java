package ru.gb.myFirstSpringProject;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Primary
@Profile("local")
public class LocalEngine implements Engine{
    public LocalEngine() {
        System.out.println("Engine started on my notebook!");
    }

    public void go() {
        System.out.println("Let`s go, slowly!");
    }
}
