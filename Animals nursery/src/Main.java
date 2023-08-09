import Controller.Counter;
import Model.*;
import View.UserMenu;
import Controller.Counter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws Exception {
        new UserMenu().start();

    }
}