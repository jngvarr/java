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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Store {
    List<Toy> store = new ArrayList<>();
    Random r = new Random();
    static int raffleNumber = 0;
    static int prizeFundPercent = 20;

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

    public Map<String, Integer> toyNumberByTitle() {
        Map<String, Integer> map = new HashMap<>();
        map.put("barbie", 0);
        map.put("lol", 1);
        map.put("monsterhigh", 2);
        map.put("rfrobot", 3);
        map.put("simplerobot", 4);
        map.put("kitty", 5);
        map.put("stitch", 6);
        map.put("teddybear", 7);
        map.put("chess", 8);
        map.put("constructor", 9);
        map.put("monopoly", 10);
        map.put("rpg", 11);
        map.put("helicopter", 12);
        map.put("plane", 13);
        map.put("quadcopter", 14);
        map.put("boat", 15);
        map.put("rfcar", 16);
        map.put("specialservicecar", 17);
        map.put("speedcar", 18);
        map.put("speedboat", 19);
        map.put("submarine", 20);
        return map;
    }

    public List<Toy> manualStoreFilling() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean end = false;
        String qq;
        Map toyDict = toyNumberByTitle();
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Chose type of toy to add: ");
        System.out.println("-------------------------------------------------------------------------");
        printTitles(toyDict);
        System.out.println("-------------------------------------------------------------------------");

        while (!end) {
            System.out.println("(For END type \"exit\")");
            System.out.print("Type toys type to add: > ");
            qq = reader.readLine();
            if (!qq.equalsIgnoreCase("exit") && toyDict.containsKey(qq.toLowerCase())) {
                System.out.print("Type quantity toys to add: > ");
                int quantityToAdd = Integer.parseInt(reader.readLine());
                for (int i = 0; i < quantityToAdd; i++) {
                    filler((Integer) toyDict.get(qq.toLowerCase()));
                }
            } else if (qq.equalsIgnoreCase("exit")) end = true;
            else if (!toyDict.containsKey(qq.toLowerCase()) && !qq.equalsIgnoreCase("exit"))
                System.out.println("Wrong input");
        }
        return store;
    }

    public void printToysSet(List<Toy> list) {
        Set<String> set = new LinkedHashSet<>();
        for (Toy t : list) {
            set.add(t.title);
        }
//        for (int i = 0; i < set.size(); i++) {
//            System.out.println(i + ". " + set.iterator());
//        }
        for (Iterator<String> iter = set.iterator(); iter.hasNext(); ) {
            System.out.println(iter.next());
        }
    }

    public void printTitles(Map<String, Integer> map) {
        for (String key : map.keySet()) {
            System.out.println(key.substring(0, 1).toUpperCase() + key.substring(1));
        }
    }

    public List<String[]> prizeFundForming(int percent, List<String[]> list, Map<String, Integer> map) {
        List<String[]> tempList = new ArrayList<>();
        List<String[]> resultList = new ArrayList<>();
        for (Map.Entry<String, Integer> key : map.entrySet()) {
            tempList.clear();
            for (String[] s : list) {
                if (key.getKey().equals(s[2])) {
                    tempList.add(s);
                }
            }
            for (int i = 0; i < Math.round(tempList.size() * percent / 100.0); i++) {
                String[] toyToPrizeFund = tempList.get(r.nextInt(tempList.size()));
                tempList.remove(toyToPrizeFund);
                resultList.add(toyToPrizeFund);
            }
        }
        resultList.sort(new Model().stringArrComparator);
        return resultList;
    }

    public List<String[]> setWeight(List<String[]> list) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean end = false;
        String newWeight = "";
        Map toyDict = toyNumberByTitle();
        String toyClass;
        list.remove(0);

        new Model().printWeightFromList(list);
        while (!end) {
            System.out.println("(For END type \"exit\")");
            System.out.print("Type toys type to change weight: > ");
            toyClass = reader.readLine();
            if (!toyClass.equalsIgnoreCase("exit") && toyDict.containsKey(toyClass.toLowerCase())) {
                System.out.print("Type new value of weight: > ");
                newWeight = reader.readLine();
            } else if (toyClass.equalsIgnoreCase("exit")) end = true;
            else if (!toyDict.containsKey(toyClass.toLowerCase()) && !toyClass.equalsIgnoreCase("exit"))
                System.out.println("Wrong input");
            for (String[] s : list) {
                if (s[2].equalsIgnoreCase(toyClass)) s[3] = newWeight;
            }
        }
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Toys weights: ");
        System.out.println("-------------------------------------------------------------------------");
        new Model().printWeightFromList(list);
        return list;
    }

    public List<List<String[]>> raffle(List<String[]> prizeFundlist, List<String[]> wholeStore) {
        List<String[]> prizes = new ArrayList<>();
        List<String[]> listCopy = new ArrayList<>(prizeFundlist);
        for (String[] string : listCopy) {
            int rand = r.nextInt(100);
            if (rand > Integer.parseInt(string[3])) {
                prizeFundlist.remove(string);
                prizes.add(string);
                wholeStore.remove(string);
            }
        }
        wholeStore.remove(0);
        return List.of(prizes, wholeStore);
    }
}