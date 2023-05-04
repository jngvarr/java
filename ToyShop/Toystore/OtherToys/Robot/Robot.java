package Toystore.OtherToys.Robot;

import Toystore.Toy;

public abstract class Robot extends Toy {
    private static int robotNumber;

    public Robot() {
        this.ID = super.ID + "-R" + ++robotNumber;
    }
}
