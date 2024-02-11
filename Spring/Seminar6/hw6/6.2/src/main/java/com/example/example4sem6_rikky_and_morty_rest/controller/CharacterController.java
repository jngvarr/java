package com.example.example4sem6_rikky_and_morty_rest.controller;

import com.example.example4sem6_rikky_and_morty_rest.domain.Characters;
import com.example.example4sem6_rikky_and_morty_rest.domain.Result;
import com.example.example4sem6_rikky_and_morty_rest.service.ServiceApi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

/**
 * Контроллер для обработки запросов, связанных с персонажами.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/character")
public class CharacterController {

    private final ServiceApi serviceApi;

    /**
     * Возвращает страницу со списком персонажей.
     * @param page Номер страницы (необязательный параметр)
     * @param model Модель данных
     * @return Название страницы
     */
    @GetMapping()
    public String getCharacters(@RequestParam(value = "page", required = false) String page, Model model) {
        Characters allCharacters;
        if (StringUtils.isEmpty(page)) {
            allCharacters = serviceApi.getAllCharacters();
        } else {
            allCharacters = serviceApi.getCharactersWithPage(page);
        }
        model.addAttribute("characters", allCharacters.getResults());

        String prevUrl = allCharacters.getInfo().getPrev();
        String nextUrl = allCharacters.getInfo().getNext();

        if (prevUrl != null) {
            String prevPage = UriComponentsBuilder.fromUriString(prevUrl).build().getQueryParams().get("page").get(0);
            model.addAttribute("prevPage", prevPage);
        }

        if (nextUrl != null) {
            String nextPage = UriComponentsBuilder.fromUriString(nextUrl).build().getQueryParams().get("page").get(0);
            model.addAttribute("nextPage", nextPage);
        }

        return "characters";
    }

    /**
     * Возвращает страницу с подробной информацией о персонаже.
     * @param id Идентификатор персонажа
     * @param model Модель данных
     * @return Название страницы
     */
    @GetMapping("/{id}")
    public String getCharacterDetails(@PathVariable Integer id, Model model) {
        Result character = serviceApi.getCharacterById(id);
        model.addAttribute("character", character);

        int page = serviceApi.getPageNumberById(id);
        model.addAttribute("page", page);

        return "characterDetails";
    }

    /**
     * Возвращает страницу с формой фильтрации персонажей.
     * @param model Модель данных
     * @return Название страницы
     */
    @GetMapping("/filter")
    public String showFilterPage(Model model) {
        // Добавляем возможность выбора предустановленных значений для фильтрации
        model.addAttribute("statusOptions", Arrays.asList("alive", "dead", "unknown"));
        model.addAttribute("genderOptions", Arrays.asList("female", "male", "genderless", "unknown"));

        return "filter";
    }

    /**
     * Фильтрует персонажей с учетом выбранных параметров и возвращает страницу с результатами.
     * @param name Имя персонажа (опциональный параметр)
     * @param status Статус персонажа (опциональный параметр)
     * @param species Вид персонажа (опциональный параметр)
     * @param type Тип персонажа (опциональный параметр)
     * @param gender Пол персонажа (опциональный параметр)
     * @param model Модель данных
     * @return Название страницы
     */
    @GetMapping("/filter/result")
    public String filterCharacters(@RequestParam(name = "name", required = false) String name,
                                   @RequestParam(name = "status", required = false) String status,
                                   @RequestParam(name = "species", required = false) String species,
                                   @RequestParam(name = "type", required = false) String type,
                                   @RequestParam(name = "gender", required = false) String gender,
                                   Model model) {
        // Выполняем запрос на фильтрацию с учетом выбранных параметров
        Characters filteredCharacters = serviceApi.getFilteredCharacters(name, status, species, type, gender);

        // Передаем отфильтрованных персонажей в модель
        model.addAttribute("characters", filteredCharacters.getResults());

        return "characters";
    }



}
