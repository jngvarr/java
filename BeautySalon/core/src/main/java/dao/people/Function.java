package dao.people;

import lombok.Getter;

@Getter
public enum Function {
    ADMIN,
    HAIRDRESSER,
    NAILMASTER,
    CLEANING;
}

// @Getter
//public enum Function {
//    ADMIN("Администратор"),
//    HAIRDRESSER("Парикмахер"),
//    NAILMASTER("Ногтевой мастер"),
//    CLEANING("Уборщица");
//
//    private final String function;
//
//    Function(String function) {
//        this.function = function;
//    }

//
//}
