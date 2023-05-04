package Toystore.Transport.Aircraft;

import Toystore.Interfaces.CanFly;
import Toystore.Toy;

public abstract class Aircraft extends Toy implements CanFly {
    private static int airCraftNumber;

    public Aircraft() {
        this.ID = super.ID + "-A" + ++airCraftNumber;
    }
    @Override
    public void fly() {
        System.out.println("i wish i could fly");
    }
}
