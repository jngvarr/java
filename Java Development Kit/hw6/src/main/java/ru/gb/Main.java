package ru.gb;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<Integer, Boolean> remindedWins = new HashMap<>();
        Map<Integer, Boolean> notRemindedWins = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            TheMontyHallParadox game = new TheMontyHallParadox();
            game.doors.remove(game.getPlayersChoiceDoor());
            remindedWins.put(i, game.dealerChoice(game.doors));
        }
        for (int i = 0; i < 5; i++) {
            TheMontyHallParadox game = new TheMontyHallParadox();
            notRemindedWins.put(i, game.doors.get(game.getPlayersChoiceDoor()));
        }

        System.out.println("Количество выйгрышей когда игрок сменил выбор: " + winsCount(remindedWins));
        System.out.println("Количество выйгрышей когда игрок не сменил выбор: " + winsCount(notRemindedWins));
    }
    public static int winsCount(Map<Integer, Boolean> results) {
        int count = 0;
        for (Map.Entry<Integer, Boolean> result : results.entrySet()) {
            if (result.getValue()) count++;
        }
        return count;
    }
}
