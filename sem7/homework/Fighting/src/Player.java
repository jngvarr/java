import java.util.Random;
import java.util.SortedMap;

/*Создать класс Player с полями id (long), name (String), damage (double) healthPoint (hp) (double) У класса должен быть
конструктор, который принимает только name. Идентификатор присваивается автоматически из последовательности (1, 2, ...)
Каждый класс должен уметь "бить" другого Player'а void attack(Player player) -> player1.attack(player2) Внутри метода
игрок, на котором вызван метод уменьшает здоровье игрока, который передан в метод Придумать несколько классов с разными
параметрами жизней и атаки по-умолчанию Player player = new Tank("name");
Придумать, все, что захочется и обогатить проект
Понасоздавать объектов и стравить их друг с другом */
public class Player {
    protected static Random rnd;
    protected static Long id;
    protected String name;
    protected Integer damage;
    protected Integer hp;
    protected Integer agility;
    protected Integer defence;

    static {
        Player.id = 0L;
        Player.rnd = new Random();
    }

    public Player(String name) {
        this.name = name;
    }

    public Player() {
        this(String.format("Player № %d", ++Player.id));
    }

    public String getInfo() {
        return String.format("Name: %s, Hp: %d, Power: %d, Type: %s.",
                this.name, this.hp, this.damage, this.getClass().getSimpleName());
    }

    public void Attack(Player target) {
        int damage = this.damage;
        damage = target.Defense(damage) + this.agility * rnd.nextInt(0, 2);
        System.out.format("%s hit %s on %dhp\n", this.name, target.name, damage);
        target.GetDamage(damage);
        System.out.println(target.getInfo());
    }

    public int Defense(int damage) {
        int reducedDamage = damage - this.defence - this.agility * rnd.nextInt(0, 3);
        return reducedDamage < 0 ? 0 : reducedDamage;
    }

    public void GetDamage(int damage) {
        if (this.hp - damage > 0) {
            this.hp -= damage;
        } else {
            this.hp -= damage;
            Die(this.name);
        }
    }

    public void Die(String name) {
        System.out.printf("%s died in the battle. RIP.\n", name);
    }
}

