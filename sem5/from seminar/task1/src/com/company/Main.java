package com.company;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        System.out.println(isIsomorphic("title", "sirle"));
        System.out.println(isIsomorphic("title", "title"));
    }

    private static boolean isIsomorphic(String firstStr, String secondStr) {
        if (firstStr.length() == secondStr.length()) {
            Map<Character, Character> symbol = new HashMap<Character, Character>();
            for (int i = 0; i < firstStr.length(); i++) {
                if (!symbol.containsKey(firstStr.charAt(i))) {
                    symbol.put(firstStr.charAt(i), secondStr.charAt(i));
                } else if (!symbol.get(firstStr.charAt(i)).equals(secondStr.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }
}