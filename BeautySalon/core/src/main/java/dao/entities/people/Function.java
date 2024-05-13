package dao.entities.people;

import lombok.Getter;

//@Getter
//public enum Function {
//    ADMIN,
//    HAIRDRESSER,
//    NAILMASTER,
//    CLEANING;
//}

@Getter
public enum Function {
    MANAGER("Администратор"),
    HAIRDRESSER("Парикмахер"),
    NAILMASTER("Ногтевой мастер"),
    CLEANING("Уборщица"),
    ADMIN ("Системный администратор");


    private final String function;

    Function(String function) {
        this.function = function;
    }


}
