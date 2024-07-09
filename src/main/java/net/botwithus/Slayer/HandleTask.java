package net.botwithus.Slayer;

import net.botwithus.SnowsScript;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Slayer.Laniakea.skipTask;
import static net.botwithus.Slayer.Main.SlayerState.*;
import static net.botwithus.Slayer.Main.setSlayerState;
import static net.botwithus.Slayer.NPCs.*;
import static net.botwithus.Variables.Variables.*;

public class HandleTask {

    private static SnowsScript snowsScript;

    public HandleTask(SnowsScript snowsScript) {
        HandleTask.snowsScript = snowsScript;
    }

    static void handleTask(LocalPlayer player) {
        Component component = ComponentQuery.newQuery(1639).componentIndex(11).results().first();
        if (component != null) {
            String taskText = component.getText().trim().toLowerCase();
            switch (taskText) {
                case "creatures of the lost grove":
                    addTargetName("Vinecrawler");
                    setSlayerState(CREATURESOFTHELOSTGROVE);
                    break;
                case "risen ghosts":
                    addTargetName("Risen ghost");
                    setSlayerState(RISENGHOSTS);
                    break;
                case "undead":
                    addTargetName("Risen ghost");
                    setSlayerState(RISENGHOSTS);
                    break;
                case "ganodermic creatures":
                    addTargetName("Ganodermic beast");
                    setSlayerState(GANODERMICCREATURES);
                    break;
                case "dark beasts":
                    addTargetName("Dark beast");
                    setSlayerState(DARKBEASTS);
                    break;
                case "crystal shapeshifters":
                    addTargetName("Crystal Shapeshifter");
                    setSlayerState(CRYSTALSHAPESHIFTERS);
                    break;
                case "nodon dragonkin":
                    addTargetName("Nodon");
                    setSlayerState(NODONDRAGONKIN);
                    break;
                case "soul devourers":
                    addTargetName("Salawa akh");
                    setSlayerState(SOULDEVOURERS);
                    break;
                case "dinosaurs":
                    addTargetName("Venomous dinosaur");
                    setSlayerState(DINOSAURS);
                    break;
                case "mithril dragons":
                    addTargetName("Mithril dragon");
                    setSlayerState(MITHRILDRAGONS);
                    break;
                case "demons":
                    addTargetName("Abyssal demon");
                    setSlayerState(DEMONS);
                    break;
                case "abyssal demons":
                    addTargetName("Abyssal demon");
                    setSlayerState(DEMONS);
                    break;
                case "ascension members":
                    addTargetName("Rorarius");
                    setSlayerState(ASCENSIONMEMBERS);
                    break;
                case "kalphite":
                    addTargetName("kalphite");
                    setSlayerState(KALPHITES);
                    break;
                case "elves":
                    addTargetName("iorwerth");
                    setSlayerState(ELVES);
                    break;
                case "shadow creatures":
                    addTargetName("shadow");
                    setSlayerState(SHADOWCREATURES);
                    break;
                case "vile blooms":
                    setSlayerState(VILEBLOOMS);
                    break;
                case "ice strykewyrms":
                    setSlayerState(ICESTRYKEWYRMS);
                    Execution.delay(iceStrykewyrms());
                    break;
                case "lava strykewyrms":
                    setSlayerState(LAVASTRYKEWYRMS);
                    break;
                case "greater demons":
                    addTargetName("Greater demon");
                    setSlayerState(GREATERDEMONS);
                    break;
                case "mutated jadinkos":
                    addTargetName("jadinko");
                    setSlayerState(MUTATEDJADINKOS);
                    break;
                case "corrupted creatures":
                    setSlayerState(CORRUPTEDCREATURES);
                    break;
                case "iron dragons":
                    addTargetName("Iron dragon");
                    setSlayerState(IRONDRAGONS);
                    break;
                case "steel dragons":
                    addTargetName("Steel dragon");
                    setSlayerState(STEELDRAGONS);
                    break;
                case "adamant dragons":
                    addTargetName("Adamant dragon");
                    setSlayerState(ADAMANTDRAGONS);
                    break;
                case "black demons":
                    addTargetName("Black demon");
                    setSlayerState(BLACKDEMONS);
                    break;
                case "kal'gerion demons":
                    addTargetName("demon");
                    setSlayerState(KALGERIONDEMONS);
                    break;
                case "gargoyles":
                    addTargetName("Gargoyle");
                    setSlayerState(GARGOYLES);
                    break;
                case "chaos giants":
                    addTargetName("giant");
                    setSlayerState(CHAOSGIANTS);
                    break;
                case "airut":
                    addTargetName("airut");
                    setSlayerState(AIRUT);
                    break;
                case "dragons":
                    addTargetName("Adamant dragon");
                    setSlayerState(ADAMANTDRAGONS);
                    break;
                default:
                case "black dragons":
                    addTargetName("Black dragon");
                    setSlayerState(BLACKDRAGONS);
                    log("Task not recognized.");
                    setSlayerState(CANCELTASK);
            }
        } else {
            log("The specified component was not found.");
        }
    }
}
