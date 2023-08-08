package Model;

public enum PackAnimalType {
    H, // horses - лошади
    C, // camels - верблюды
    D; // donkeys - ослы

    public static PackAnimalType getType(String type) {
        switch (type) {
            case "верблюды":
                return PackAnimalType.C;
            case "лошади":
                return PackAnimalType.H;
            case "ослы":
                return PackAnimalType.D;
        }
        return null;
    }
}
