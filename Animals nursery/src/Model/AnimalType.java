package Model;

public enum AnimalType {
    PACK,
    HOME;

    public static AnimalType getType(String type) {
        switch (type) {
            case "1":
                return AnimalType.HOME;
            case "2":
                return AnimalType.PACK;
        }
        return null;
    }
}
