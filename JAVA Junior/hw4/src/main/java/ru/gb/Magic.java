package ru.gb;

import javax.persistence.*;

@Entity
@Table(name = "test.magic")
public class Magic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "название")
    private String name;
    @Column(name = "повреждение")
    private int damage;
    @Column(name = "броня")
    private int def;


    public Magic(String name, int damage, int def, int attBonus) {
        this.name = name;
        this.damage = damage;
        this.def = def;
        this.attBonus = attBonus;
    }

    @Column(name = "атака")
    int attBonus;

    public Magic() { // обязательно нужно создать пустой конструктор
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public void setAttBonus(int attBonus) {
        this.attBonus = attBonus;
    }

    @Override
    public String toString() {
        return "Magic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", damage=" + damage +
                ", def=" + def +
                ", attBonus=" + attBonus +
                '}';
    }
}
