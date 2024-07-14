package net.botwithus.Slayer;

import net.botwithus.SnowsScript;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Slayer.Laniakea.skipTask;
import static net.botwithus.Slayer.Main.SlayerState.*;
import static net.botwithus.Slayer.Main.setSlayerState;
import static net.botwithus.Slayer.NPCs.*;
import static net.botwithus.Slayer.Utilities.ActivateSoulSplit;
import static net.botwithus.Variables.Variables.*;

public class HandleTask {

    private static SnowsScript snowsScript;

    public HandleTask(SnowsScript snowsScript) {
        HandleTask.snowsScript = snowsScript;
    }

    static List<String> tasksToSkip = new ArrayList<>();
    public static LinkedList<String> lastTenTasks = new LinkedList<>();

    public static void handleTask(LocalPlayer player) {
        Component component = ComponentQuery.newQuery(1639).componentIndex(11).results().first();
        if (component != null) {
            String taskText = component.getText().trim().toLowerCase();
            log("Task text retrieved: " + taskText); // Debug log

            String taskName = null;

            switch (taskText) {
                case "camel warrior":
                    taskName = "Camel warrior";
                    addTargetName(taskName);
                    addTargetName("mirage");
                    setSlayerState(CAMELWARRIORS);
                    break;
                case "creatures of the lost grove":
                    taskName = "Vinecrawler";
                    addTargetName(taskName);
                    setSlayerState(CREATURESOFTHELOSTGROVE);
                    break;
                case "risen ghosts":
                case "undead":
                    taskName = "Risen ghost";
                    addTargetName(taskName);
                    setSlayerState(RISENGHOSTS);
                    break;
                case "ganodermic creatures":
                    taskName = "Ganodermic beast";
                    addTargetName(taskName);
                    setSlayerState(GANODERMICCREATURES);
                    break;
                case "dark beasts":
                    taskName = "Dark beast";
                    addTargetName(taskName);
                    setSlayerState(DARKBEASTS);
                    break;
                case "crystal shapeshifters":
                    taskName = "Crystal Shapeshifter";
                    addTargetName(taskName);
                    setSlayerState(CRYSTALSHAPESHIFTERS);
                    break;
                case "nodon dragonkin":
                    taskName = "Nodon";
                    addTargetName(taskName);
                    setSlayerState(NODONDRAGONKIN);
                    break;
                case "soul devourers":
                    taskName = "Salawa akh";
                    addTargetName(taskName);
                    setSlayerState(SOULDEVOURERS);
                    break;
                case "dinosaurs":
                    taskName = "Venomous dinosaur";
                    addTargetName(taskName);
                    setSlayerState(DINOSAURS);
                    break;
                case "mithril dragons":
                    taskName = "Mithril dragon";
                    addTargetName(taskName);
                    setSlayerState(MITHRILDRAGONS);
                    break;
                case "demons":
                case "abyssal demons":
                    taskName = "Abyssal demon";
                    addTargetName(taskName);
                    setSlayerState(DEMONS);
                    break;
                case "ascension members":
                    taskName = "Rorarius";
                    addTargetName(taskName);
                    setSlayerState(ASCENSIONMEMBERS);
                    break;
                case "kalphite":
                    taskName = "kalphite";
                    addTargetName(taskName);
                    setSlayerState(KALPHITES);
                    break;
                case "elves":
                    taskName = "iorwerth";
                    addTargetName(taskName);
                    setSlayerState(ELVES);
                    break;
                case "shadow creatures":
                    taskName = "shadow";
                    addTargetName(taskName);
                    setSlayerState(SHADOWCREATURES);
                    break;
                case "vile blooms":
                    taskName = "Vile blooms";
                    setSlayerState(VILEBLOOMS);
                    break;
                case "ice strykewyrms":
                    taskName = "Ice strykewyrms";
                    setSlayerState(ICESTRYKEWYRMS);
                    break;
                case "lava strykewyrms":
                    taskName = "Lava strykewyrms";
                    setSlayerState(LAVASTRYKEWYRMS);
                    break;
                case "greater demons":
                    taskName = "Greater demon";
                    addTargetName(taskName);
                    setSlayerState(GREATERDEMONS);
                    break;
                case "mutated jadinkos":
                    taskName = "jadinko";
                    addTargetName(taskName);
                    setSlayerState(MUTATEDJADINKOS);
                    break;
                case "corrupted creatures":
                    taskName = "Corrupted creatures";
                    setSlayerState(CORRUPTEDCREATURES);
                    break;
                case "iron dragons":
                    taskName = "Iron dragon";
                    addTargetName(taskName);
                    setSlayerState(IRONDRAGONS);
                    break;
                case "steel dragons":
                    taskName = "Steel dragon";
                    addTargetName(taskName);
                    setSlayerState(STEELDRAGONS);
                    break;
                case "adamant dragons":
                    taskName = "Adamant dragon";
                    addTargetName(taskName);
                    setSlayerState(ADAMANTDRAGONS);
                    break;
                case "black demons":
                    taskName = "Black demon";
                    addTargetName(taskName);
                    setSlayerState(BLACKDEMONS);
                    break;
                case "kal'gerion demons":
                    taskName = "Kal'gerion demon";
                    addTargetName(taskName);
                    setSlayerState(KALGERIONDEMONS);
                    break;
                case "gargoyles":
                    taskName = "Gargoyle";
                    addTargetName(taskName);
                    setSlayerState(GARGOYLES);
                    break;
                case "chaos giants":
                    taskName = "Chaos giant";
                    addTargetName(taskName);
                    setSlayerState(CHAOSGIANTS);
                    break;
                case "airut":
                    taskName = "airut";
                    addTargetName(taskName);
                    setSlayerState(AIRUT);
                    break;
                case "dragons":
                    taskName = "Adamant dragon";
                    addTargetName(taskName);
                    setSlayerState(ADAMANTDRAGONS);
                    break;
                case "strykewyrms":
                    taskName = "Ice strykewyrms";
                    setSlayerState(ICESTRYKEWYRMS);
                    ActivateSoulSplit();
                    Execution.delay(iceStrykewyrms());
                    break;
                case "black dragons":
                    taskName = "Black dragon";
                    addTargetName(taskName);
                    setSlayerState(BLACKDRAGONS);
                    break;
                case "bats":
                    taskName = "Bats";
                    setSlayerState(BATS);
                    break;
                case "birds":
                    taskName = "Birds";
                    setSlayerState(BIRDS);
                    break;
                case "cave bugs":
                    taskName = "Cave bugs";
                    setSlayerState(CAVEBUGS);
                    break;
                case "cave slimes":
                    taskName = "Cave slimes";
                    setSlayerState(CAVESLIME);
                    break;
                case "cows":
                    taskName = "Cows";
                    setSlayerState(COWS);
                    break;
                case "frogs":
                    taskName = "Frogs";
                    setSlayerState(FROGS);
                    break;
                case "ghosts":
                    taskName = "Ghosts";
                    setSlayerState(GHOSTS);
                    break;
                case "goblins":
                    taskName = "Goblins";
                    setSlayerState(GOBLINS);
                    break;
                case "rats":
                    taskName = "Rats";
                    setSlayerState(RATS);
                    break;
                case "skeletons":
                    taskName = "Skeletons";
                    setSlayerState(SKELETONS);
                    break;
                case "spiders":
                    taskName = "Spiders";
                    setSlayerState(SPIDERS);
                    break;
                case "zombies":
                    taskName = "Zombies";
                    setSlayerState(ZOMBIES);
                    break;
                default:
                    log("Task not recognized: " + taskText); // Debug log
                    setSlayerState(CANCELTASK);
            }

            if (taskName != null) {
                lastTenTasks.addFirst(taskName);
                log("Task added: " + taskName); // Debug log
                if (lastTenTasks.size() > 10) {
                    lastTenTasks.removeLast();
                }
            }
        } else {
            log("The specified component was not found.");
        }
    }
}
