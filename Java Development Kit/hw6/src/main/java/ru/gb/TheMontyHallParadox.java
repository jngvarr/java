package ru.gb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TheMontyHallParadox {

    Random rnd = new Random();
    private boolean isTheCarBehindFirstDoor;
    private boolean isTheCarBehindSecondFirstDoor;
    private boolean isTheCarBehindThirdDoor;
    private final int playersChoiceDoor;

    public int getPlayersChoiceDoor() {
        return playersChoiceDoor;
    }


    List<Boolean> doors = new ArrayList<>();

    public TheMontyHallParadox() {
        doors.add(isTheCarBehindFirstDoor);
        doors.add(isTheCarBehindSecondFirstDoor);
        doors.add(isTheCarBehindThirdDoor);
        setCar();
        playersChoiceDoor = playersChoice();
    }

    private void setCar() {
        doors.set(rnd.nextInt(0, 2), true);
    }

    public int playersChoice() {
        return rnd.nextInt(0, 2);
    }

    public boolean dealerChoice(List<Boolean> doors) {
        int chosenElement = rnd.nextInt(0, doors.size());
        if (!doors.get(chosenElement)) {
            doors.remove(chosenElement);
            return doors.get(0);
        }
        return true;
    }


}
