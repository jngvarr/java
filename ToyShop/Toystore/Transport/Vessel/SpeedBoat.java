package Toystore.Transport.Vessel;

import Toystore.Interfaces.RFControl;
import Toystore.Interfaces.Speed;

public class SpeedBoat extends Vessel implements RFControl, Speed {
    private int weight = 85;
    @Override
    public void rfControl() {
        System.out.println("i`m rf-controlled speedboat");
    }

    @Override
    public void speed() {
        System.out.println("i`m speedy rf-controlled boat");

    }
}


