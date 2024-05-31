package net.botwithus;

import net.botwithus.rs3.game.login.LoginManager;
import net.botwithus.rs3.game.minimenu.MiniMenu;

import static net.botwithus.SnowsScript.BotState.IDLE;

public class TaskScheduler {
    public int amountToDisassemble;
    public String itemToDisassemble;
    private int amountDisassembled = 0;

    public TaskScheduler(int amountToDisassemble, String itemToDisassemble) {
        this.amountToDisassemble = amountToDisassemble;
        this.itemToDisassemble = itemToDisassemble;
    }

    public boolean isComplete() {
        return amountDisassembled >= amountToDisassemble;
    }

    public void incrementAmountDisassembled() {
        amountDisassembled++;
    }

    public int getAmountDisassembled() {
        return amountDisassembled;
    }
    public String getItemToDisassemble() {
        return itemToDisassemble;
    }


    public static void shutdown() {
        SnowsScript.setBotState(IDLE);
        LoginManager.setAutoLogin(false);
        MiniMenu.interact(14, 1, -1, 93913156);
    }
}
