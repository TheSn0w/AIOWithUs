package net.botwithus.Misc;

import net.botwithus.SnowsScript;
import net.botwithus.TaskScheduler;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;



public class PorterMaker {
    public static boolean MakePorterVII = false;
    public static boolean MakePorterVI = false;
    public static boolean MakePorterV = false;
    public static boolean MakePorterIV = false;
    public static boolean MakePorterIII = false;
    public static boolean MakePorterII = false;
    public static boolean MakePorterI = false;

    public static TaskScheduler taskScheduler = new TaskScheduler(0, "");
    public static int userTaskCount = 100; // Default value




    public static long makePorters() {
        if (Interfaces.isOpen(1251)) {
            return random.nextLong(1250, 2500);
        }
        if (Interfaces.isOpen(1370)) {
            dialog(0, -1, 89784350);
            log("[Porter Maker] Selecting 'Weave'");
            return random.nextLong(1250, 1500);
        }
        if (MakePorterVII) {
            porterVII();
            taskScheduler.decreaseTaskCount("Porter VII");
        } else if (MakePorterVI) {
            porterVI();
            taskScheduler.decreaseTaskCount("Porter VI");
        } else if (MakePorterV) {
            porterV();
            taskScheduler.decreaseTaskCount("Porter V");
        } else if (MakePorterIV) {
            porterIV();
            taskScheduler.decreaseTaskCount("Porter IV");
        } else if (MakePorterIII) {
            porterIII();
            taskScheduler.decreaseTaskCount("Porter III");
        } else if (MakePorterII) {
            porterII();
            taskScheduler.decreaseTaskCount("Porter II");
        } else if (MakePorterI) {
            porterI();
            taskScheduler.decreaseTaskCount("Porter I");
        }
        return random.nextLong(750, 1050);
    }

    private static long handleBanking() {
        EntityResultSet<Npc> results = NpcQuery.newQuery().name("Banker").option("Load Last Preset from").results();
        if (!results.isEmpty()) {
            log("[Porter Maker] Loading last preset from banker");
            results.nearest().interact("Load Last Preset from");
            return random.nextLong(750, 1050);
        } else {
            EntityResultSet<SceneObject> chestResults = SceneObjectQuery.newQuery().name("Bank chest").option("Load Last Preset from").results();
            if (!chestResults.isEmpty()) {
                log("[Porter Maker] Loading last preset from bank chest");
                chestResults.nearest().interact("Load Last Preset from");
                return random.nextLong(750, 1050);
            } else {
                log("[Error] Bank chest not found.");
            }
        }
        return random.nextLong(750, 1050);
    }
    public static void porterVII() {
        if (Skills.DIVINATION.getActualLevel() == 99) {
            if (Backpack.getQuantity("Incandescent energy") >= 120 && Backpack.contains("Dragonstone necklace")) {
                backpack.interact("Incandescent energy", "Weave");
                log("[Porter Maker] Weaving Porter VII");
            } else {
                if (Backpack.getQuantity("Incandescent energy") < 120) {
                    log("[Error] Less than 50 Incandescent energy in the backpack.");
                }
                if (!Backpack.contains("Dragonstone necklace")) {
                    log("[Error] Dragonstone necklace not found in the backpack.");
                    Execution.delay(handleBanking());
                }
            }
        } else {
            log("[Error] Divination level is not 99.");
        }
    }
    public static void porterVI() {
        if (Skills.DIVINATION.getActualLevel() >= 94) {
            if (Backpack.getQuantity("Luminous energy") >= 80 && Backpack.contains("Diamond necklace")) {
                backpack.interact("Luminous energy", "Weave");
                log("[Porter Maker] Weaving Porter VI");
            } else {
                if (Backpack.getQuantity("Luminous energy") < 80) {
                    log("[Error] Less than 30 Luminous energy in the backpack.");
                }
                if (!Backpack.contains("Diamond necklace")) {
                    log("[Error] Diamond necklace not found in the backpack.");
                    Execution.delay(handleBanking());
                }
            }
        } else {
            log("[Error] Divination level is not 94.");
        }
    }
    public static void porterV() {
        if (Skills.DIVINATION.getActualLevel() >= 88) {
            if (Backpack.getQuantity("Radiant energy") >= 60 && Backpack.contains("Ruby necklace")) {
                backpack.interact("Radiant energy", "Weave");
                log("[Porter Maker] Weaving Porter V");
            } else {
                if (Backpack.getQuantity("Radiant energy") < 60) {
                    log("[Error] Less than 25 Radiant energy in the backpack.");
                }
                if (!Backpack.contains("Ruby necklace")) {
                    log("[Error] Ruby necklace not found in the backpack.");
                    Execution.delay(handleBanking());
                }
            }
        } else {
            log("[Error] Divination level is not 88.");
        }
    }
    public static void porterIV() {
        if (Skills.DIVINATION.getActualLevel() >= 68) {
            if (Backpack.getQuantity("Vibrant energy") >= 45 && Backpack.contains("Emerald necklace")) {
                backpack.interact("Vibrant energy", "Weave");
                log("[Porter Maker] Weaving Porter IV");
            } else {
                if (Backpack.getQuantity("Vibrant energy") < 45) {
                    log("[Error] Less than 20 Vibrant energy in the backpack.");
                }
                if (!Backpack.contains("Emerald necklace")) {
                    log("[Error] Emerald necklace not found in the backpack.");
                    Execution.delay(handleBanking());
                }
            }
        } else {
            log("[Error] Divination level is not 68.");
        }
    }
    public static void porterIII() {
        if (Skills.DIVINATION.getActualLevel() >= 48) {
            if (Backpack.getQuantity("Sparkling energy") >= 40 && Backpack.contains("Emerald necklace")) {
                backpack.interact("Sparkling energy", "Weave");
                log("[Porter Maker] Weaving Porter III");
            } else {
                if (Backpack.getQuantity("Sparkling energy") < 40) {
                    log("[Error] Less than 15 Sparkling energy in the backpack.");
                }
                if (!Backpack.contains("Emerald necklace")) {
                    log("[Error] Emerald necklace not found in the backpack.");
                    Execution.delay(handleBanking());
                }
            }
        } else {
            log("[Error] Divination level is not 48.");
        }
    }
    public static void porterII() {
        if (Skills.DIVINATION.getActualLevel() >= 28) {
            if (Backpack.getQuantity("Bright energy") >= 35 && Backpack.contains("Sapphire necklace")) {
                backpack.interact("Bright energy", "Weave");
                log("[Porter Maker] Weaving Porter II");
            } else {
                if (Backpack.getQuantity("Bright energy") < 35) {
                    log("[Error] Less than 15 Bright energy in the backpack.");
                }
                if (!Backpack.contains("Sapphire necklace")) {
                    log("[Error] Sapphire necklace not found in the backpack.");
                    Execution.delay(handleBanking());
                }
            }
        } else {
            log("[Error] Divination level is not 28.");
        }
    }
    public static void porterI() {
        if (Skills.DIVINATION.getActualLevel() >= 6) {
            if (Backpack.getQuantity("Pale energy") >= 30 && Backpack.contains("Sapphire necklace")) {
                backpack.interact("Pale energy", "Weave");
                log("[Porter Maker] Weaving Porter I");
            } else {
                if (Backpack.getQuantity("Pale energy") < 30) {
                    log("[Error] Less than 30 Pale energy in the backpack.");
                }
                if (!Backpack.contains("Sapphire necklace")) {
                    log("[Error] Sapphire necklace not found in the backpack.");
                    Execution.delay(handleBanking());
                }
            }
        } else {
            log("[Error] Divination level is not 6.");
        }
    }

    public static long divineCharges() {
        if (Interfaces.isOpen(1251)) {
            return random.nextLong(1250, 2500);
        }
        if (Interfaces.isOpen(1370)) {
            MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 89784350);
            log("[Porter Maker] Selecting 'Weave'");
            return random.nextLong(1250, 1500);
        }
        if (Backpack.contains("Incandescent energy")) {
            backpack.interact("Incandescent energy", "Weave");
            log("[Porter Maker] Weaving Incandescent energy");
        }
        return random.nextLong(750, 1050);
    }
}
