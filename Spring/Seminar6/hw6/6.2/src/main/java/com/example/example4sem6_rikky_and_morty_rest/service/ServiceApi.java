package com.example.example4sem6_rikky_and_morty_rest.service;

import com.example.example4sem6_rikky_and_morty_rest.domain.Characters;
import com.example.example4sem6_rikky_and_morty_rest.domain.Result;

/**
 * Интерфейс, предоставляющий методы для работы с API сервисом "Rick and Morty".
 */
public interface ServiceApi {

    public Characters getAllCharacters();

    public Result getCharacterById(Integer id);

    public Characters getCharactersWithPage(String page);

    public int getPageNumberById(int id);

    public Characters getFilteredCharacters(String name, String status, String species, String type, String gender);

}
