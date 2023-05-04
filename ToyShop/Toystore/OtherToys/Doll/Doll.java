package Toystore.OtherToys.Doll;

import Toystore.Toy;

public abstract class Doll extends Toy {
    private static int dollNumber;

    public Doll() {
        this.ID = super.ID + "-D" + ++dollNumber;
    }
}
