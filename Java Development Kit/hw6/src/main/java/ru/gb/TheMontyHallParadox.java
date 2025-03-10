package ru.gb;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TheMontyHallParadox {

    Random rnd = new Random();
    private boolean isTheCarBehindFirstDoor;
    private boolean isTheCarBehindSecondFirstDoor;
    private boolean isTheCarBehindThirdDoor;
    private final int playersChoiceDoor;
    List<Boolean> doors = new ArrayList<>();

    public int getPlayersChoiceDoor() {
        return playersChoiceDoor;
    }



    public TheMontyHallParadox() {
        setCarBehindTheDoor();
        doors.add(isTheCarBehindFirstDoor);
        doors.add(isTheCarBehindSecondFirstDoor);
        doors.add(isTheCarBehindThirdDoor);
        playersChoiceDoor = playersChoice();
    }

    private void setCarBehindTheDoor() {
        switch (rnd.nextInt(3)) {
            case 0 -> isTheCarBehindFirstDoor = true;
            case 1 -> isTheCarBehindSecondFirstDoor = true;
            case 2 -> isTheCarBehindThirdDoor = true;
        }
    }

    public int playersChoice() {
        return rnd.nextInt(3);
    }

    public boolean dealerChoice(List<Boolean> doors) {
        int chosenElement = rnd.nextInt(doors.size());
        if (!doors.get(chosenElement)) {
            doors.remove(chosenElement);
            return doors.get(0);
        }
        return true;
    }


}
