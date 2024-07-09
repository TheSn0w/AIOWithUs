package net.botwithus.Slayer;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.List;

import static net.botwithus.Combat.Combat.attackTarget;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Slayer.HandleTask.handleTask;
import static net.botwithus.Slayer.Laniakea.TeleporttoLaniakea;
import static net.botwithus.Slayer.Laniakea.skipTask;
import static net.botwithus.Slayer.NPCs.*;
import static net.botwithus.Slayer.Utilities.*;
import static net.botwithus.Slayer.WarsRetreat.bankingLogic;
import static net.botwithus.Variables.Variables.clearTargetNames;
import static net.botwithus.Variables.Variables.nearestBank;

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
        DEATHSOFFICE,
        COMBAT,
        BANK,

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
            case COMBAT:
                if (nearestBank && Backpack.isFull()) {
                    log("Backpack is full. Banking.");
                    setSlayerState(SlayerState.WARS_RETREAT);
                }
                Npc death = NpcQuery.newQuery().name("Death").results().nearest();
                if (death != null) {
                    setSlayerState(SlayerState.DEATHSOFFICE);
                }
                if (VarManager.getVarValue(VarDomainType.PLAYER, 183) == 0) {
                    slayerState = SlayerState.CHECK;
                    log("Task completed.");
                } else {
                    Execution.delay(attackTarget(player));
                }
                break;
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
            case DEATHSOFFICE:
                log("Deaths Office state.");
                interactWithDeath();
                break;
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

    private static void interactWithDeath() {
        Npc death = NpcQuery.newQuery().name("Death").results().nearest();
        if (death == null) {
            return;
        }

        log("Attempting to interact with Death.");
        Execution.delay(RandomGenerator.nextInt(3500, 5000));
        if (death.interact("Reclaim items")) {
            log("Interaction initiated. Waiting for interface 1626 to open.");
            if (Execution.delayUntil(5000, () -> Interfaces.isOpen(1626))) {
                log("Successfully opened interface 1626. Moving to reclaim confirmation.");
                Execution.delay(RandomGenerator.nextInt(3500, 5000));
                confirmReclaim(); // Proceed to confirm reclaim if successful.
            } else {
                log("Failed to open interface 1626 after interacting with Death.");
            }
        } else {
            log("Failed to initiate interaction with Death.");
        }
    }

    private static void confirmReclaim() {
        if (!Interfaces.isOpen(1626)) {
            log("Interface 1626 is not open. Cannot confirm reclaim.");
            return;
        }

        ComponentQuery query = ComponentQuery.newQuery(1626);
        List<Component> components = query.componentIndex(47).results().stream().toList();
        if (!components.isEmpty() && components.get(0).interact(1)) {
            log("Reclaim confirmation initiated. Waiting for finalization option.");
            Execution.delay(RandomGenerator.nextInt(3500, 5000));
            finalizeReclamation();
        } else {
            log("Failed to confirm reclaim with Death.");
        }
    }

    private static void finalizeReclamation() {
        if (!Interfaces.isOpen(1626)) {
            log("Interface 1626 is not open. Cannot finalize reclaim.");
            return;
        }

        ComponentQuery query = ComponentQuery.newQuery(1626);
        List<Component> components = query.componentIndex(72).results().stream().toList();
        if (!components.isEmpty() && components.get(0).interact(1)) {
            log("Reclaim finalized. Moving to post-reclaim actions.");
            Execution.delay(RandomGenerator.nextInt(3500, 5000));
            slayerState = SlayerState.WARS_RETREAT;
        } else {
            log("Failed to finalize reclaim with Death.");
        }
    }


}
