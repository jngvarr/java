package Toystore.Transport.Aircraft;

import Toystore.Interfaces.RFControl;

public class Quadcopter extends Aircraft implements RFControl {
    public static int weight = 99;
    @Override
    public void rfControl() {
        System.out.println("i`m rf-controlled quad!");
    }
}
