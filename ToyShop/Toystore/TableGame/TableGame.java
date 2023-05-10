package Toystore.TableGame;

import Toystore.Toy;

public abstract class TableGame extends Toy {

    private static int tgNumber;

    public TableGame() {
        this.ID = super.ID + "-TG" + ++tgNumber;
        this.weight=35;
    }
}
