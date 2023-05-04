package Toystore.OtherToys.SoftToy;

import Toystore.Toy;

public abstract class SoftToy extends Toy {
    private static int softNumber;

    public SoftToy() {
        this.ID = super.ID + "-ST" + ++softNumber;
    }
}
