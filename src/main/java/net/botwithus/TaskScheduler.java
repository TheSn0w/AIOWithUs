package net.botwithus;

import ImGui.Stopwatch;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.login.LoginManager;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import ImGui.SnowScriptGraphics;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.ScriptController;

import java.util.HashMap;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.BotState.IDLE;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.rs3.script.ScriptController.setActive;

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
        log("[Error] Shutting down...");
        SnowsScript.setBotState(IDLE);
        LoginManager.setAutoLogin(false);
        Stopwatch.stop();
        setActive(ScriptController.getActiveScript().getName(), false);
        MiniMenu.interact(14, 1, -1, 93913156);
    }

    public static int pin1 = 0;
    public static int pin2 = 0;
    public static int pin3 = 0;
    public static int pin4 = 0;

    public static int[] bankPin = new int[4];

    public static void bankPin() {
        if (Interfaces.isOpen(759)) {
            Execution.delay(random.nextLong(1000, 2000));
            HashMap<Integer, Integer> pinComponents = new HashMap<>();
            pinComponents.put(1, 49741839);
            pinComponents.put(2, 49741844);
            pinComponents.put(3, 49741849);
            pinComponents.put(5, 49741859);
            pinComponents.put(6, 49741864);
            pinComponents.put(7, 49741869);
            pinComponents.put(8, 49741874);
            pinComponents.put(9, 49741879);
            pinComponents.put(0, 49741834);

            HashMap<Integer, Integer> dialogComponents = new HashMap<>();
            dialogComponents.put(1, 851974);
            dialogComponents.put(2, 851975);
            dialogComponents.put(3, 851976);
            dialogComponents.put(4, 851977);
            dialogComponents.put(5, 851978);
            dialogComponents.put(6, 851979);
            dialogComponents.put(7, 851980);
            dialogComponents.put(8, 851981);
            dialogComponents.put(9, 851973);

            int[] bankPin = {pin1, pin2, pin3, pin4};

            for (int i = 0; i < bankPin.length; i++) {
                int pin = bankPin[i];
                log("Interacting with pin number: " + pin); // Log the pin number
                if (i == 3) { // If it's the 4th number in the sequence
                    int dialogId = dialogComponents.get(pin);
                    dialog(0, -1, dialogId);
                } else {
                    int componentId = pinComponents.get(pin);
                    component(1, -1, componentId);
                }
                Execution.delay(random.nextLong(1000, 2000));
            }
        }
    }
}
