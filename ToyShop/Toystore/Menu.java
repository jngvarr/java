package Toystore;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;


public class Menu {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    Store store = new Store();
    boolean end = false;
    Map<String, String> mainMenu = Map.of(
            "2", "Manual toy store filling.",
            "1", "Default toy store filling.");

    public void welcome() {
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Chose the action:");
        System.out.println("-------------------------------------------------------------------------");
    }

    public void printMenu(Map<String, String> menu) {
        for (Map.Entry<String, String> pair : menu.entrySet()) {
            System.out.println(pair.getKey() + "." + pair.getValue());
        }
        System.out.print("> ");
    }

    public int toyQuantityToAdd() throws IOException {
        System.out.println("-------------------------------------------------------------------------");
        System.out.print("Input quantity toys to add: > ");
        return Integer.parseInt(reader.readLine());
    }

    public boolean manualAdd() throws IOException {
        System.out.print("Done!\nDo you want to add any toys manually? (y/n): ");
        return reader.readLine().equalsIgnoreCase("y");
    }

    public void startRaffle() throws IOException {
        System.out.println("Let`s start the raffle. ");
        System.out.println("-------------------------------------------------------------------------");
    }

    public List<Toy> subMenu(List<Toy> toysList) throws IOException {
        Store store = new Store();
        String mainMenuChose;
        while (!mainMenu.containsKey(mainMenuChose = reader.readLine())) {
            System.out.println("-------------------------------------------------------------------------");
            System.out.println("Wrong input, try again:");
            printMenu(mainMenu);
        }
        if (mainMenuChose.equalsIgnoreCase("1")) {
            toysList = store.defaultStoreFilling(toyQuantityToAdd());
            if (manualAdd()) toysList = store.manualStoreFilling();
        } else if (mainMenuChose.equalsIgnoreCase("2")) {
            toysList = store.manualStoreFilling();
        }
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("The list of toys in the store is ready:");
        System.out.println("-------------------------------------------------------------------------");
        return toysList;
    }

    Map<String, String> raffleMenu = Map.of(
            "1", "Print the prize pool.",
            "2", "Print out the remaining toys.",
            "3", "Set prize pool percentage (default value: 20).", /* + Store.prizeFundPercent + ")."*/
            "4", "Set toys weight (the able to be not drawn).",
            "5", "Raffle the prizes."
    );

    public void raffleMenu() throws IOException {
        boolean endRaffle = false;
        String raffleMenuChose;
        Model model = new Model();
        List<String[]> toysStorelist = model.readDataFromFile(System.getProperty("user.dir")
                + "\\toysList.csv");                // Список всех игрушек в магазине
        Map<String, Integer> mapToys = model.listToMapTitle(toysStorelist);
        model.printToysByTitle(mapToys);                                  // Количество призов по видам
        startRaffle();// Сколько каких игрушек в магазине
        System.out.println("Chose the action: \n(Type \"exit\" to STOP)");
        System.out.println("-------------------------------------------------------------------------");
        List<String[]> prizeFundList = store.prizeFundForming(Store.prizeFundPercent, toysStorelist, mapToys);   // Список всех призовых игрушек
        model.writeDataToFile(prizeFundList, System.getProperty("user.dir") + "\\prizeFund" +
                ++Store.raffleNumber + "raffle.csv");
        printMenu(raffleMenu);
        while (!endRaffle) {
            while (!raffleMenu.containsKey(raffleMenuChose = reader.readLine()) || raffleMenuChose.equalsIgnoreCase("exit")) {
                if (raffleMenuChose.equalsIgnoreCase("exit")) {
                    endRaffle = true;
                    break;
                } else {
                    System.out.println("-------------------------------------------------------------------------");
                    System.out.println("Wrong input, try again:");
                    printMenu(raffleMenu);

                }
                switch (raffleMenuChose) {
                    case "1": {
                        System.out.println("-------------------------------------------------------------------------");
                        System.out.println("Prize pool: ");
                        System.out.println("-------------------------------------------------------------------------");
                        model.printToysByTitle(model.listToMapTitle(prizeFundList));
                        break;
                    }
                    case "2": {
                        System.out.println("-------------------------------------------------------------------------");
                        System.out.println("Quantity of toys in the store: ");
                        System.out.println("-------------------------------------------------------------------------");
                        File f = new File(System.getProperty("user.dir") + "\\storeRemains.csv");
                        if (f.exists() && !f.isDirectory()) {
                            model.printToysByTitle(model.listToMapTitle(model.readDataFromFile(
                                    System.getProperty("user.dir") + "\\storeRemains.csv")));
                        } else model.printToysByTitle(mapToys);
//                        System.out.println("-------------------------------------------------------------------------");
                        break;
                    }
                    case "3":
                        System.out.print("Set prize pool percentage: > ");
                        Store.prizeFundPercent = Integer.parseInt(reader.readLine());
                        System.out.println("-------------------------------------------------------------------------");
                        prizeFundList = store.prizeFundForming(Store.prizeFundPercent, toysStorelist, mapToys);   // Обновленный список всех призовых игрушек с учетом новой процентовки
                        model.writeDataToFile(prizeFundList, System.getProperty("user.dir") + "\\prizeFund"
                                + Store.raffleNumber + "raffle.csv");
                        break;
                    case "4":
                        System.out.println("-------------------------------------------------------------------------");
                        System.out.println("Toys weights: ");
                        System.out.println("-------------------------------------------------------------------------");
                        store.setWeight(toysStorelist);
                        System.out.println("-------------------------------------------------------------------------");
                        break;
                    case "5": {
                        System.out.println("-------------------------------------------------------------------------");
                        System.out.println("Prizes of " + Store.raffleNumber + " raffle: ");
                        System.out.println("-------------------------------------------------------------------------");
                        List<List<String[]>> lists = store.raffle(prizeFundList, toysStorelist);
                        model.writeDataToFile(lists.get(0), System.getProperty("user.dir") + "\\prizesOf" +
                                Store.raffleNumber++ + "raffle.csv");
                        model.printToysByTitle(model.listToMapTitle(prizeFundList));
                        model.writeDataToFile(lists.get(1), System.getProperty("user.dir") + "\\storeRemains.csv");
                        prizeFundList = store.prizeFundForming(Store.prizeFundPercent, lists.get(1), mapToys);
                        model.writeDataToFile(prizeFundList, System.getProperty("user.dir") + "\\prizeFund"
                                + Store.raffleNumber + "raffle.csv");
                        break;
                    }
                }
                System.out.println("Chose the action:");
                System.out.println("-------------------------------------------------------------------------");
                printMenu(raffleMenu);
            }
        }
    }
}


