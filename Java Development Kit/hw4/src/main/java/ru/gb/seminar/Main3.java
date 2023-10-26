package ru.gb.seminar;

import java.util.HashMap;
import java.util.Map;

public class Main3 {
    /*
    Р’ СЂР°РјРєР°С… РІС‹РїРѕР»РЅРµРЅРёСЏ Р·Р°РґР°С‡Рё РЅРµРѕР±С…РѕРґРёРјРѕ:
    1. РЎРѕР·РґР°Р№С‚Рµ С‚РµР»РµС„РѕРЅРЅС‹Р№ СЃРїСЂР°РІРѕС‡РЅРёРє СЃ РїРѕРјРѕС‰СЊСЋ Map - С‚РµР»РµС„РѕРЅ СЌС‚Рѕ РєР»СЋС‡, Р° РёРјСЏ Р·РЅР°С‡РµРЅРёРµ
    2. РќР°Р№РґРёС‚Рµ С‡РµР»РѕРІРµРєР° СЃ СЃР°РјС‹Рј РјР°Р»РµРЅСЊРєРёРј РЅРѕРјРµСЂРѕРј С‚РµР»РµС„РѕРЅР°
    3. РќР°Р№РґРёС‚Рµ РЅРѕРјРµСЂ С‚РµР»РµС„РѕРЅР° С‡РµР»РѕРІРµРєР° С‡СЊРµ РёРјСЏ СЃР°РјРѕРµ Р±РѕР»СЊС€РѕРµ РІ Р°Р»С„Р°РІРёС‚РЅРѕРј РїРѕСЂСЏРґРєРµ
    */
    public static void main(String[] args) {
        Map<String, String> phoneBook = new HashMap<>();
        phoneBook.put("123", "РљРѕРЅСЃС‚Р°РЅС‚РёРЅ");
        phoneBook.put("123123", "РњР°СЂРёСЏ");
        phoneBook.put("12311", "Р’СЏС‡РµСЃР»Р°РІ");
        phoneBook.put("12", "РљРёСЂРёР»Р»");
        phoneBook.put("911", "Р®Р»РёСЏ");

        for (Map.Entry<String, String> entry: phoneBook.entrySet()){
            String key = entry.getKey();
            String val = entry.getValue();
        }

        System.out.println(phoneBook.entrySet()
                .stream().min((e1, e2) -> e1.getKey().compareTo(e2.getKey())).get().getValue());

        System.out.println(phoneBook.entrySet()
                .stream().max((e1, e2) -> e1.getValue().compareTo(e2.getValue())).get().getKey());
    }
}