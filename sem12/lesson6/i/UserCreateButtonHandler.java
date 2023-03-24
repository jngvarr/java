package ru.gb.lesson6.i;

public class UserCreateButtonHandler {

    private InterfaceSegregationDemo.UserCreateService userService;

    public UserCreateButtonHandler(InterfaceSegregationDemo.UserCreateService userService) {
        this.userService = userService;
    }

    public void handleClick() {
        // срабатывает, когда кто-то на форме нажимает кнопку "SAVE USER"
        userService.createUser();
    }

}
