package ru.gb.seminar;

import java.util.*;

public class Main2 {
    /*
    Р’ СЂР°РјРєР°С… РІС‹РїРѕР»РЅРµРЅРёСЏ Р·Р°РґР°С‡Рё РЅРµРѕР±С…РѕРґРёРјРѕ:
    1. РЎРѕР·РґР°Р№С‚Рµ РєРѕР»Р»РµРєС†РёСЋ РјСѓР¶СЃРєРёС… Рё Р¶РµРЅСЃРєРёС… РёРјРµРЅ СЃ РїРѕРјРѕС‰СЊСЋ
    РёРЅС‚РµСЂС„РµР№СЃР° List - РґРѕР±Р°РІСЊС‚Рµ РїРѕРІС‚РѕСЂСЏСЋС‰РёРµСЃСЏ Р·РЅР°С‡РµРЅРёСЏ
    2. РџРѕР»СѓС‡РёС‚Рµ СѓРЅРёРєР°Р»СЊРЅС‹Р№ СЃРїРёСЃРѕРє Set РЅР° РѕСЃРЅРѕРІР°РЅРёРё List
    3. РћРїСЂРµРґРµР»РёС‚Рµ РЅР°РёРјРµРЅСЊС€РёР№ СЌР»РµРјРµРЅС‚ (Р°Р»С„Р°РІРёС‚РЅС‹Р№ РїРѕСЂСЏРґРѕРє)
    4. РћРїСЂРµРґРµР»РёС‚Рµ РЅР°РёР±РѕР»СЊС€РёР№ СЌР»РµРјРµРЅС‚ (РїРѕ РєРѕР»РёС‡РµСЃС‚РІСѓ Р±СѓРєРІ РІ СЃР»РѕРІРµ, РЅРѕ РІ РѕР±СЂР°С‚РЅРѕРј РїРѕСЂСЏРґРєРµ)
    5. РЈРґР°Р»РёС‚Рµ РІСЃРµ СЌР»РµРјРµРЅС‚С‹ СЃРѕРґРµСЂР¶Р°С‰РёРµ Р±СѓРєРІСѓ вЂAвЂ™
     */
    public static void main(String[] args) {
        List<String> list = generateList();
        Set<String> set = new HashSet<>(list);
        System.out.println(set);
//        System.out.println(getMaxByLength(set));
        removeByChar(set);
        System.out.println(set);
    }

    static void removeByChar(Set<String> set){
        set.removeIf(s -> s.contains("Р°"));
    }

    static String getMaxByLength(Set<String> set){
        return set.stream().max(Comparator.comparingInt(String::length)).get();
    }

    static String getMinByAlphabet(Set<String> set){
        Set<String> set1 = new TreeSet<>(set);
//        System.out.println(set1.stream().max(String::compareTo));
        Iterator<String> iterator = set1.iterator();
        if (iterator.hasNext()){
            return iterator.next();
        }
        return null;
    }

    static List<String> generateList(){
        List<String> list = new ArrayList<>();
        list.add("РљРѕРЅСЃС‚Р°РЅС‚РёРЅ");
        list.add("Р’Р°СЃРёР»РёР№");
        list.add("Р’Р°СЃРёР»РёР№");
        list.add("РЎРІРµС‚Р»Р°РЅР°");
        list.add("РЎРІРµС‚Р»Р°РЅР°");
        list.add("РЎРІРµС‚Р»Р°РЅР°");
        list.add("РђРЅРЅР°");
        list.add("РђРЅРЅР°");
        list.add("РРІР°РЅ");
        list.add("РЎРµРјРµРЅ");
        return list;
    }
}