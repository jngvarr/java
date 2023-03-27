import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
Родственные связи кошек
*/

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


        String motherName = "мама Василиса";
        String daughterName = "дочь Пушинка";
        String grandMaName = "бабушка Мурка";
        String fatherName = "папа Котофей";
        String sonName = "сын Мурчик";
        String grandPaName = "дедушка Вася";


        Cat catGrandMa = new Cat(grandMaName);
        Cat catGrandPa = new Cat(grandPaName);
        Cat catFather = new Cat(fatherName, null, catGrandPa);
        Cat catMother = new Cat(motherName, catGrandMa, null);
        Cat catSon = new Cat(sonName, catMother, catFather);
        Cat catDaughter = new Cat(daughterName, catMother, catFather);

        System.out.println(catGrandPa);
        System.out.println(catGrandMa);
        System.out.println(catFather);
        System.out.println(catMother);
        System.out.println(catSon);
        System.out.println(catDaughter);
    }

    public static class Cat {
        private String name;
        private Cat mother;
        private Cat father;
        private Cat parent;

        Cat(String name) {
            this.name = name;
        }

//        Cat(String name, Cat grand) {
//            this.name = name;
//            this.parent = grand;
//        }

        Cat(String name, Cat mother, Cat father) {
            this.name = name;
            this.mother = mother;
            this.father = father;
        }

        @Override
        public String toString() {
            if (mother == null && father == null) {
                return "The cat's name is " + name + ", no mother, no father ";
            }
            if (mother == null && father != null) {
                return "The cat's name is " + name + ", no mother, father is " + father.name;
            }
            if (mother != null && father == null) {
                return "The cat's name is " + name + ", mother is " + mother.name + " no father";
            }
            if (mother != null && father != null) {
                return "The cat's name is " + name + ", mother is " + mother.name + ", father is " + father.name;
            }
        }

    }

}