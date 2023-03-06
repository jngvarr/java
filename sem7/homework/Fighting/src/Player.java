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
    protected Double damage;
    protected Double hp;

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
        return String.format("Name: %s, Hp: %.2f, Power: %.2f, Type: %s.",
                this.name, this.hp, this.damage, this.getClass().getSimpleName());
    }
    public void Attack(Player target) {
        int damage = Player.rnd.nextInt();
        target.GetDamage(damage);
    }
    public void GetDamage(int damage) {
        if (this.hp - damage > 0) {
            this.hp -= damage;
        } else {
            Die(this.name);
        }
    }
    public void Die(String name){
        System.out.printf("%s died in the battle. RIP.", name);
    }
}

