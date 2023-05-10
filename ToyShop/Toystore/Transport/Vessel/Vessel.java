package Toystore.Transport.Vessel;

import Toystore.Interfaces.CanSail;
import Toystore.Toy;

public abstract class Vessel extends Toy implements CanSail {
    private static int vesselNumber;

    public Vessel() {
        this.ID = super.ID + "-V" + ++vesselNumber;
        this.weight=80;
    }
    @Override
    public void sail() {
        System.out.println("Set sail!");
    }
}
