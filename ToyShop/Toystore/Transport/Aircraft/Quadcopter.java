package Toystore.Transport.Aircraft;

import Toystore.Interfaces.RFControl;

public class Quadcopter extends Aircraft implements RFControl {
    @Override
    public void rfControl() {
        System.out.println("i`m rf-controlled quad!");
    }
}
