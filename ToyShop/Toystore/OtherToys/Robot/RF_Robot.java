package Toystore.OtherToys.Robot;

import Toystore.Interfaces.RFControl;

public class RF_Robot extends Robot implements RFControl {
    @Override
    public void rfControl() {
        System.out.println("i`m rf-controlled robot");;
    }
}
