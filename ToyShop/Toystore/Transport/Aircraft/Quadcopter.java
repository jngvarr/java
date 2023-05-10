package Toystore.Transport.Aircraft;

import Toystore.Interfaces.RFControl;

public class Quadcopter extends Aircraft implements RFControl {
    private final int weight = 99;
    @Override
    public void rfControl() {
        System.out.println("i`m rf-controlled quad!");
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
