package Toystore.Transport.Car;

import Toystore.Interfaces.RFControl;

public class RFCar extends Car implements RFControl {
    @Override
    public void rfControl() {
        System.out.println("i`m rf-controlled car");
    }
}