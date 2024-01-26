package com.example.sem3HomeTask.services;

import com.example.sem3HomeTask.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {


    public DataProcessingService getDataProcessingService() {
        return dataProcessingService;
    }

    private DataProcessingService dataProcessingService;
    private UserService userService;
    private NotificationService notificationService;

    public RegistrationService(DataProcessingService dataProcessingService, UserService userService,
                               NotificationService notificationService) {
        this.dataProcessingService = dataProcessingService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    /**
     *  Создание пользователя с заданными параметрами,
     *  вывод в консоль сообщения о создании,
     *  добавление пользователя в репозиторий
     *
     * @param name Имя пользователя
     * @param age Возраст пользователя
     * @param eMail адрес электронной почты пользователя
     */
    public void processRegistration(String name, int age, String eMail) {
        User user = userService.createUser(name, age, eMail);
        dataProcessingService.addUserToList(user);
    }
}
