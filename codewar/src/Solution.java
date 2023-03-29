import java.util.ArrayList;
import java.util.List;

/*
Что за список такой?
*/

public class Solution {
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("qwerty");
        list.add("uiop");
        list.add("asdfg");
        list.add("hjkl");
        list.add("zxcvbn");
        System.out.println(list.size());
        for (String str: list){
            System.out.println(str);
        }
    }
}
