package ru.gb.myFirstSpringProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class Car {
    @Autowired
    Engine engine;

    public void start(){
        engine.go();
    }

    //Лучше всего внедрять зависимости (создавать объекты) через конструктор
    // Это один вариант
//    public Car(Engine engine) {
//        this.engine = engine;
//        engine.go();
//
//
// второй вариант внедрения зависимосте через setter(конструктор пустой)
    //public Car() {
//    }
//    public void setEngine(Engine engine) {
//        this.engine = engine;
//        engine.go();
//    }

    // третий вариант внедрения - через поле, добавлением аннотации @Autowired
}
