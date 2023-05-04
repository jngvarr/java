package Toystore;

import Toystore.OtherToys.Doll.Barbie;
import Toystore.OtherToys.Doll.LOL;
import Toystore.OtherToys.Doll.MonsterHigh;
import Toystore.OtherToys.Robot.RF_Robot;
import Toystore.OtherToys.Robot.SimpleRobot;
import Toystore.OtherToys.SoftToy.Kitty;
import Toystore.OtherToys.SoftToy.Stitch;
import Toystore.OtherToys.SoftToy.TeddyBear;
import Toystore.TableGame.Chess;
import Toystore.TableGame.Constructor;
import Toystore.TableGame.Monopoly;
import Toystore.TableGame.RPG;
import Toystore.Transport.Aircraft.Helicopter;
import Toystore.Transport.Aircraft.Plane;
import Toystore.Transport.Aircraft.Quadcopter;
import Toystore.Transport.Car.RFCar;
import Toystore.Transport.Car.SpecialServiceCar;
import Toystore.Transport.Car.SpeedCar;
import Toystore.Transport.Vessel.Boat;
import Toystore.Transport.Vessel.SpeedBoat;
import Toystore.Transport.Vessel.Submarine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Store {
    public List<Toy> storeFilling() {
        List<Toy> store = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            switch (r.nextInt(21)) {
                case 0:
                    store.add(new Barbie());
                    break;
                case 1:
                    store.add(new LOL());
                    break;
                case 2:
                    store.add(new MonsterHigh());
                    break;
                case 3:
                    store.add(new RF_Robot());
                    break;
                case 4:
                    store.add(new SimpleRobot());
                    break;
                case 5:
                    store.add(new Kitty());
                    break;
                case 6:
                    store.add(new Stitch());
                    break;
                case 7:
                    store.add(new TeddyBear());
                    break;
                case 8:
                    store.add(new Chess());
                    break;
                case 9:
                    store.add(new Constructor());
                    break;
                case 10:
                    store.add(new Monopoly());
                    break;
                case 11:
                    store.add(new RPG());
                    break;
                case 12:
                    store.add(new Helicopter());
                    break;
                case 13:
                    store.add(new Plane());
                    break;
                case 14:
                    store.add(new Quadcopter());
                    break;
                case 15:
                    store.add(new Boat());
                    break;
                case 16:
                    store.add(new RFCar());
                    break;
                case 17:
                    store.add(new SpecialServiceCar());
                    break;
                case 18:
                    store.add(new SpeedCar());
                    break;
                case 19:
                    store.add(new SpeedBoat());
                    break;
                case 20:
                    store.add(new Submarine());
                    break;
            }
        }
        return store;
    }

    public List<Toy> prizeFound(int prizePercent) {
        return null;
    }
}
