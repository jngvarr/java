package Toystore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class Menu {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    Map<String, String> menu = Map.of(
            "1", "Manual store filling ",
            "2", "Default store filling");

    public void menuPrint() {
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("To fill store with toys chose the action: >");
        System.out.println("-----------------------------------------------------------------------------------------");
        for (Map.Entry<String, String> pair : menu.entrySet()) {
            System.out.println(pair.getKey() + "." + pair.getValue());
        }
        System.out.print("Type \"1\" or \"2\" and press \"Enter\": > ");
    }

    public boolean manualAdd() throws IOException {
        return reader.readLine().equalsIgnoreCase("y");
    }

    public List<Toy> subMenu(List<Toy> toysList) throws IOException {
        Store store = new Store();
        String choose;
        while (!menu.containsKey(choose = reader.readLine())) {
            System.out.println("Wrong input. Try again");
            menuPrint();
        }
        if (choose.equalsIgnoreCase("1")) {
            System.out.print("Type quantity of adding toys: > ");
            int defaultToysQuantity = Integer.parseInt(reader.readLine());
            store.defaultStoreFilling(defaultToysQuantity);
            System.out.print("Done!\nDo you want to add any toys manually? (y/n) > ");
            if (manualAdd()) {
                System.out.println("-----------------------------------------------------------------------------------------");
                System.out.println("Chose toys type to add: ");
                System.out.println("-----------------------------------------------------------------------------------------");store.manualStoreFilling(toysList);}
        } else if (choose.equalsIgnoreCase("2")) {
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("Chose type of toy which yuo want to add:");
            System.out.println("-------------------------------------------------------------------------------------");
            store.manualStoreFilling(toysList);
        }
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Done!\nList of toys is ready.");
        return toysList;
    }
}
