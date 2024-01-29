package ru.gb.hw4.services;

import org.springframework.stereotype.Service;
import ru.gb.hw4.model.User;

import java.util.ArrayList;
import java.util.List;
@Service
public class UserService {
    private List<User> users = new ArrayList<>();

    public List<User> getUsers(){
        return users;
    }

    public void addUser(User user){
        users.add(user);
    }


}
