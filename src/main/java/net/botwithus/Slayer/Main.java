package net.botwithus.Slayer;

import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.vars.VarManager;

import static net.botwithus.Combat.Combat.attackTarget;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Slayer.HandleTask.handleTask;
import static net.botwithus.Slayer.Laniakea.TeleporttoLaniakea;
import static net.botwithus.Slayer.Laniakea.skipTask;
import static net.botwithus.Slayer.NPCs.*;
import static net.botwithus.Slayer.Utilities.*;
import static net.botwithus.Slayer.WarsRetreat.bankingLogic;
import static net.botwithus.Variables.Variables.clearTargetNames;

public class Main {
    public static boolean doSlayer = false;

    public enum SlayerState {
        CHECK,
        WARS_RETREAT,
        LANIAKEA,
        RETRIEVETASKINFO,
        CANCELTASK,
        LOGOUT,
        CREATURESOFTHELOSTGROVE,
        RISENGHOSTS,
        GANODERMICCREATURES,
        DARKBEASTS,
        CRYSTALSHAPESHIFTERS,
        NODONDRAGONKIN,
        SOULDEVOURERS,
        DINOSAURS,
        MITHRILDRAGONS,
        DEMONS,
        ASCENSIONMEMBERS,
        KALPHITES,
        ELVES,
        SHADOWCREATURES,
        VILEBLOOMS,
        ICESTRYKEWYRMS,
        LAVASTRYKEWYRMS,
        GREATERDEMONS,
        MUTATEDJADINKOS,
        CORRUPTEDCREATURES,
        IRONDRAGONS,
        STEELDRAGONS,
        ADAMANTDRAGONS,
        BLACKDEMONS,
        KALGERIONDEMONS,
        GARGOYLES,
        CHAOSGIANTS,
        AIRUT,
        BLACKDRAGONS,
        COMBAT,

    }

    public static SlayerState slayerState = SlayerState.CHECK;


    public static void setSlayerState(SlayerState state) {
        slayerState = state;
    }
    public static SlayerState getSlayerState() {
        return slayerState;
    }


    public static void runSlayer() {
        LocalPlayer player = Client.getLocalPlayer();

        switch (slayerState) {
            case CHECK:
                log("Checking Task");
                checkTaskCompletion();
                break;
            case WARS_RETREAT:
                log("Wars Retreat state.");
                bankingLogic();
                break;
            case LANIAKEA:
                log("Laniakea state.");
                TeleporttoLaniakea();
                break;
            case RETRIEVETASKINFO:
                log("Handle Task state.");
                handleTask(player);
                break;
            case CANCELTASK:
                log("Cancel Task state.");
                skipTask();
                break;
            case CREATURESOFTHELOSTGROVE:
                log("Creatures of the Lost Grove state.");
                Vinecrawlers(player);
                break;
            case RISENGHOSTS:
                log("Risen Ghosts state.");
                risenGhosts(player);
                break;
            case GANODERMICCREATURES:
                log("Ganodermic Creatures state.");
                GanodermicCreatures(player);
                break;
            case DARKBEASTS:
                log("Dark Beasts state.");
                darkBeasts(player);
                break;
            case CRYSTALSHAPESHIFTERS:
                log("Crystal Shapeshifters state.");
                crystalShapeshifters(player);
                break;
            case NODONDRAGONKIN:
                log("Nodon Dragonkin state.");
                nodonDragonkin(player);
                break;
            case SOULDEVOURERS:
                log("Soul Devourers state.");
                soulDevourers(player);
                break;
            case DINOSAURS:
                log("Dinosaurs state.");
                dinosaurs(player);
                break;
            case MITHRILDRAGONS:
                log("Mithril Dragons state.");
                mithrilDragons(player);
                break;
            case DEMONS:
                log("Demons state.");
                demons(player);
                break;
            case ASCENSIONMEMBERS:
                log("Ascension Members state.");
                ascensionMembers(player);
                break;
            case KALPHITES:
                log("Kalphites state.");
                kalphites(player);
                break;
            case ELVES:
                log("Elves state.");
                elves(player);
                break;
            case SHADOWCREATURES:
                log("Shadow Creatures state.");
                shadowCreatures(player);
                break;
            case VILEBLOOMS:
                log("Vile Blooms state.");
                VileBlooms(player);
                break;
            case ICESTRYKEWYRMS:
                log("Ice Strykewyrms state.");
                break;
            case LAVASTRYKEWYRMS:
                log("Lava Strykewyrms state.");
                break;
            case GREATERDEMONS:
                log("Greater Demons state.");
                greaterDemons(player);
                break;
            case MUTATEDJADINKOS:
                log("Mutated Jadinkos state.");
                mutatedJadinkos(player);
                break;
            case CORRUPTEDCREATURES:
                log("Corrupted Creatures state.");
                CorruptedCreatures(player);
                break;
            case IRONDRAGONS:
                log("Iron Dragons state.");
                ironDragons(player);
                break;
            case STEELDRAGONS:
                log("Steel Dragons state.");
                steelDragons(player);
                break;
            case ADAMANTDRAGONS:
                log("Adamant Dragons state.");
                adamantDragons(player);
                break;
            case BLACKDEMONS:
                log("Black Demons state.");
                blackDemons(player);
                break;
            case KALGERIONDEMONS:
                log("Kal'gerion Demons state.");
                kalgerionDemons(player);
                break;
            case GARGOYLES:
                log("Gargoyles state.");
                gargoyles(player);
                break;
            case CHAOSGIANTS:
                log("Chaos Giants state.");
                chaosGiants(player);
                break;
            case AIRUT:
                log("Airut state.");
                airut(player);
                break;
            case BLACKDRAGONS:
                log("Black Dragons state.");
                blackDragon(player);
                break;
            case COMBAT:
                if (VarManager.getVarValue(VarDomainType.PLAYER, 183) == 0) {
                    slayerState = SlayerState.CHECK;
                    log("Task completed.");
                } else {
                    attackTarget(player);
                }
        }
    }

    private static void checkTaskCompletion() {
        if (VarManager.getVarValue(VarDomainType.PLAYER, 183) != 0) {
            slayerState = SlayerState.RETRIEVETASKINFO;
        } else {
            clearTargetNames();
            slayerState = SlayerState.WARS_RETREAT;
            log("Task completed.");
        }
    }


}
