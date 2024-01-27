package com.example.sem3HomeTask.services;

import com.example.sem3HomeTask.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final DataProcessingService dataProcessingService;
    private final UserService userService;
    private final NotificationService notificationService;

    public DataProcessingService getDataProcessingService() {
        return dataProcessingService;
    }


    public RegistrationService(DataProcessingService dataProcessingService, UserService userService,
                               NotificationService notificationService) {
        this.dataProcessingService = dataProcessingService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    /**
     *  Создание пользователя с заданными параметрами,
     *  добавление пользователя в репозиторий
     *  вывод в консоль сообщения о создании,
     *
     * @param name Имя пользователя
     * @param age Возраст пользователя
     * @param eMail адрес электронной почты пользователя
     */
    public void processRegistration(String name, int age, String eMail) {
        User user = userService.createUser(name, age, eMail);
        dataProcessingService.addUser(user);
//        notificationService.notifyUser(user); в методе с UserService.createUser уже есть уведомление о создании юзера
    }
}
