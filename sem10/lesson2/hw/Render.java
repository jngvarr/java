package ru.gb.lesson4.lesson2.hw;

public class Render {

    public void render(Object object) {
        if (object instanceof HasHealthPoint hhp) {
//            HasHealthPoint hhp = (HasHealthPoint) object;

            int current = hhp.getCurrentHealthPoint();
            int max = hhp.getMaxHealthPoint();
            double percent = current * 1.0 / max;


            // показывает процент текущего здоровья от максимума
        }

        if (object instanceof Tirendess) {
            // показывает процент уровня усталости от максимума
        }
    }

}
