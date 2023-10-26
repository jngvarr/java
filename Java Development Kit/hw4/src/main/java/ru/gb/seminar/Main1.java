package ru.gb.seminar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Main1 {
    /*
    Р’ СЂР°РјРєР°С… РІС‹РїРѕР»РЅРµРЅРёСЏ Р·Р°РґР°С‡Рё РЅРµРѕР±С…РѕРґРёРјРѕ:
    1. РЎРѕР·РґР°Р№С‚Рµ РєРѕР»Р»РµРєС†РёСЋ РјСѓР¶СЃРєРёС… Рё Р¶РµРЅСЃРєРёС… РёРјРµРЅ СЃ РїРѕРјРѕС‰СЊСЋ РёРЅС‚РµСЂС„РµР№СЃР° List
    2. РћС‚СЃРѕСЂС‚РёСЂСѓР№С‚Рµ РєРѕР»Р»РµРєС†РёСЋ РІ Р°Р»С„Р°РІРёС‚РЅРѕРј РїРѕСЂСЏРґРєРµ
    3. РћС‚СЃРѕСЂС‚РёСЂСѓР№С‚Рµ РєРѕР»Р»РµРєС†РёСЋ РїРѕ РєРѕР»РёС‡РµСЃС‚РІСѓ Р±СѓРєРІ РІ СЃР»РѕРІРµ
    4. Р Р°Р·РІРµСЂРЅРёС‚Рµ РєРѕР»Р»РµРєС†РёСЋ
     */

    public static void main(String[] args) {
        List<String> list = generateList();
        System.out.println(list);
        sortByAlphafet(list);
        System.out.println(list);
        sortByLength(list);
        System.out.println(list);
        Collections.reverse(list);
        System.out.println(list);
    }

    private static void sortByLength(List<String> list) {
        list.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.length() - o2.length();
            }
        });
    }

    private static void sortByAlphafet(List<String> list) {
        list.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
    }

    static List<String> generateList(){
        List<String> list = new ArrayList<>();
        list.add("РљРѕРЅСЃС‚Р°РЅС‚РёРЅ");
        list.add("Р’Р°СЃРёР»РёР№");
        list.add("РЎРІРµС‚Р»Р°РЅР°");
        list.add("РђРЅРЅР°");
        list.add("РРІР°РЅ");
        list.add("РЎРµРјРµРЅ");
        return list;
    }
}