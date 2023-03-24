package ru.gb.lesson6.i;

public class InterfaceSegregationDemo {

    public static void main(String[] args) {

    }

    interface UserCreateService {
        void createUser();
    }

    interface UserUpdateService {
        void updateUser();
    }

    interface UserDeleteService {
        void deleteUser();
    }

    static class UserService implements UserCreateService, UserUpdateService, UserDeleteService {

        public void createUser() {

        }

        public void updateUser() {

        }

        public void deleteUser() {

        }

    }

}
