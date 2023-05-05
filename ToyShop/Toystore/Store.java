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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Store {
    List<Toy> store = new ArrayList<>();
    Map<String, Integer> map = new HashMap();
    Random r = new Random();

    public List<Toy> defaultStoreFilling(int quantity) {
        for (int i = 0; i < quantity; i++) {
            this.filler(r.nextInt(21));
        }
        return store;
    }

    public void filler(int type) {
        switch (type) {
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

    public int toyNumber(String str) {
        Map<String, Integer> map = Map.of(
                "barbie", 0,
                "lol", 1,
                "monsterhigh", 2,
                "rfrobot", 3,
                "simplerobot", 4,
                "kitty", 5,
                "stitch", 6,
                "teddybear", 7,
                "chess", 8,
                "constructor", 9
        );
    }
        switch(str.toLowerCase())


                "plane", 13,
                "quadcopter", 14,
                "boat", 15,
                "rfcar", 16,
                "specialservicecar", 17,
                "speedcar", 18,
                "speedboat", 19,
                "submarine", 20

//        public List<Toy> prizeFound ( int prizePercent){
//            return null;
//        }


    public List<Toy> manualStoreFilling(List<Toy> list) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        printToysSet(list);
        boolean end = false;
        String qq;
        while (!end) {
            System.out.println("(For END type \"exit\")");
            System.out.print("\nType toys type to add: > ");
            qq = reader.readLine();
            if (!qq.equals("exit")) {
                System.out.print("Type quantity toys to add: > ");
                int quantityToAdd = Integer.parseInt(reader.readLine());
                for (int i = 0; i < quantityToAdd; i++) {
                    filler(this.toyNumber(qq));
                }
            } else end = true;
        }
        return store;
    }

    public void printToysSet(List<Toy> list) {
        Set<String> set = new LinkedHashSet<>();
        for (Toy t : list) {
            set.add(t.title);
        }
        for (int i = 0; i < set.size(); i++) {
            System.out.println(i + ". " + set.iterator());
        }
        for (Iterator<String> iter = set.iterator(); iter.hasNext(); ) {
            System.out.println(iter.next());
        }
    }
}
