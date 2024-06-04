package net.botwithus;

import net.botwithus.rs3.game.login.LoginManager;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import ImGui.SnowScriptGraphics;

import java.util.HashMap;

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
        SnowScriptGraphics.setScriptStatus(false);
        LoginManager.setAutoLogin(false);
        MiniMenu.interact(14, 1, -1, 93913156);
    }

    private static HashMap<String, Integer> tasks = new HashMap<>();

    public static void addTask(String task, int count) {
        tasks.put(task, count);
    }

    public static boolean isTaskComplete(String task) {
        return tasks.getOrDefault(task, 0) <= 0;
    }

    public static void decreaseTaskCount(String task) {
        tasks.put(task, tasks.getOrDefault(task, 0) - 1);
    }

    public static int getTaskCount(String task) {
        return tasks.getOrDefault(task, 0);
    }

    public static HashMap<String, Integer> getTasks() {
        return tasks;
    }
}
