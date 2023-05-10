package Toystore.OtherToys.Robot;

import Toystore.Interfaces.RFControl;

public class RF_Robot extends Robot implements RFControl {
    private int weight = 65;

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public void rfControl() {
        System.out.println("i`m rf-controlled robot");;
    }
}
