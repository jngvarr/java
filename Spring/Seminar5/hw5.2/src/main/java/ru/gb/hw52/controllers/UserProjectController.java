package ru.gb.hw52.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.gb.hw52.model.Project;
import ru.gb.hw52.services.UserProjectService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping
@RequiredArgsConstructor
/**
 * Контроллер обработки запросов по работе с базами данных
 */
public class UserProjectController {
    private UserProjectService userProjectService;

    /**
     * Запрос на получение списка пользователей, связанных с определенным проектом
     *
     * @param projectId уникальный идентификатор проекта
     * @return список пользователей
     */
    @GetMapping("/users")
    public ResponseEntity<List> getUsersByProjectId(Long projectId) {
        List<Project> users = null;
        try {
            users = userProjectService.getProjectsByUserId(projectId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList());
        } catch (RuntimeException e) {
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
    }

    /**
     * Запрос на получение списка проектов, связанных с определенным пользователем
     *
     * @param userId уникальный идентификатор пользователя
     * @return список проектов
     */
    @GetMapping("/projects")
    public ResponseEntity<List> getProjectsByUserId(Long userId) {
        List<Project> projects = null;
        try {
            projects = userProjectService.getProjectsByUserId(userId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList());
        } catch (RuntimeException e) {
            return new ResponseEntity<>(projects, HttpStatus.OK);
        }
    }

    /**
     * Запрос для добавления пользователя к проекту
     *
     * @param userId    уникальный идентификатор пользователя
     * @param projectId уникальный идентификатор проекта
     * @return
     */
    @PostMapping("/{userId}/{projectId}")
    public ResponseEntity addUserToProject(@PathVariable("userId") Long userId, @PathVariable("projectId") Long projectId) {
        return new ResponseEntity<>(userProjectService.addUserToProject(userId, projectId), HttpStatus.OK);
    }

        /**
         * метод, обрабатывающий POST-запрос для удаления пользователя из проекта
         *
         * @param userId уникальный идентификатор, удаляемого пользователя
         * @param projectId уникальный идентификатор проекта
         * @return ResponseEntity с соответствующим HTTP-статусом.
         */
        public ResponseEntity removeUserFromProject (Long userId, Long projectId){
            userProjectService.removeUserFromProject(userId, projectId);
            return ResponseEntity.ok().build();
        }
    }
