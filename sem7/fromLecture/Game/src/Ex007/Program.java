package Ex007;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Program {
    public static void main(String[] args) {

        // #region ex1 Demo

        // BaseHero hero3 = new Magician();
        // System.out.println(hero3.getInfo());

        // BaseHero hero4 = new Priest();
        // System.out.println(hero4.getInfo());

        // #endregion

        // #region ex2 Attack
        // System.out.println("------");
        // System.out.println(hero3.getInfo());
        // System.out.println(hero4.getInfo());

        // hero3.Attack(hero4);

        // hero4.Attack(hero3);
        // System.out.println(hero3.getInfo());
        // System.out.println(hero4.getInfo());

        // #endregion

        // #region Teams

        int teamCount = 10;
        Random rand = new Random();
        int magicianCount = 0;
        int priestCount = 0;
        int warriorCount = 0;


        List<BaseHero> teams = new ArrayList<>();
        for (int i = 0; i < teamCount; i++) {
            switch (rand.nextInt(3)) {
                case 0:
                    teams.add(new Priest());
                    priestCount++;
                    break;
                case 1:
                    teams.add(new Magician());
                    magicianCount++;
                    break;
                case 2:
                    teams.add(new Warrior());
                    warriorCount++;
            }
            System.out.println(teams.get(i).getInfo());
        }
        System.out.println();
        System.out.printf("magicalCount: %d priestCount: %d warrior: %d \n\n\n", magicianCount, priestCount, warriorCount);

        for (int i = 0; i < teams.size(); i++) {
//            teams.get(i).GetDamage(teams.get(BaseHero.r.nextInt(1, teams.size())).Attack());
            BaseHero hero1 = teams.get(i);
            BaseHero hero2 = teams.get(BaseHero.r.nextInt(1, teams.size()));
            System.out.printf("%s get damage from %s", hero1.name, hero2.name);
            hero1.GetDamage(hero2.Attack());
            System.out.println();
            System.out.println(teams.get(i).getInfo());
        }
        teams.get(2).GetHealed(teams.get(1).Heal(teams.get(2)));
        System.out.println(teams.get(2).getInfo());

        // attack

        // #endregion

        // todo добавить ещё один класс и
        // реализовать возможность лечения героев
    }
}