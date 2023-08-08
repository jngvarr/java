package Model;

public enum PetType {
    D, // dogs - вьючные
    C, // cats - кошки
    H; // hamsters - хомяки

    public static PetType getType(String type) {
        switch (type) {
            case "кошки":
                return PetType.C;
            case "хомяки":
                return PetType.H;
            case "собаки":
                return PetType.D;
        }
        return null;
    }
}
