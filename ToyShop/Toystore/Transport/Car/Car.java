package Toystore.Transport.Car;

import Toystore.Interfaces.CanDrive;
import Toystore.Toy;


public abstract class Car extends Toy implements CanDrive {
    private static int carNumber;

    public Car() {
        this.ID = super.ID + "-C" + ++carNumber;
        this.weight=60;
    }

    @Override
    public void drive() {
        System.out.println("drive");
    }
    public int getCarNumber(){
        return carNumber;
    }
}
