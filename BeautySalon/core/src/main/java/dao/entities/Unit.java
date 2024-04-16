package dao.entities;

import lombok.*;

@RequiredArgsConstructor
@Getter
public enum Unit {

    PIECE("Штука"),

    ML("Миллилитр"),

    GR("Грамм"),

    BOX("Коробка"),

    PACK("Упаковка"),

    PAIR("Пара"),

    BOTTLE("Бутылка");

    private final String name;


    }
