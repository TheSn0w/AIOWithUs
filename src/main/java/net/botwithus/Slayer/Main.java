package net.botwithus.Slayer;

import net.botwithus.Runecrafting.PlayerInfo;
import net.botwithus.Runecrafting.Runecrafting;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.login.LoginManager;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.characters.PlayerQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.characters.player.Player;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.List;

import static ImGui.Skills.CombatImGui.getTasksToSkip;
import static net.botwithus.Combat.Combat.attackTarget;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Runecrafting.Runecrafting.*;
import static net.botwithus.Runecrafting.Runecrafting.ScriptState.*;
import static net.botwithus.Slayer.HandleTask.handleTask;
import static net.botwithus.Slayer.HandleTask.tasksToSkip;
import static net.botwithus.Slayer.Jacquelyn.TeleporttoJacquelyn;
import static net.botwithus.Slayer.Laniakea.TeleporttoLaniakea;
import static net.botwithus.Slayer.Laniakea.skipTask;
import static net.botwithus.Slayer.Main.SlayerState.CANCELTASK;
import static net.botwithus.Slayer.Main.SlayerState.COMBAT;
import static net.botwithus.Slayer.NPCs.*;
import static net.botwithus.Slayer.Utilities.*;
import static net.botwithus.Slayer.Utilities.ActivateSoulSplit;
import static net.botwithus.Slayer.WarsRetreat.bankingLogic;
import static net.botwithus.Slayer.WarsRetreat.slayerPointFarming;
import static net.botwithus.TaskScheduler.bankPin;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.player;

public class Main {
    public static boolean doSlayer = false;
    public static boolean useBankPin = false;
    public static boolean hopWorldsForSlayer = false;

    public enum SlayerState {
        CHECK,
        WARS_RETREAT,
        LANIAKEA,
        JACQUELYN,
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
        CAMELWARRIORS,
        DEATHSOFFICE,
        COMBAT,
        BANK,
        BATS,
        BIRDS,
        CAVEBUGS,
        CAVESLIME,
        COWS,
        FROGS,
        GHOSTS,
        GOBLINS,
        RATS,
        SKELETONS,
        SPIDERS,
        ZOMBIES,

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

        if (Interfaces.isOpen(759)) {
            bankPin();
        }

        switch (slayerState) {
            case COMBAT:
                checkForOtherPlayersAndHopWorldSlayer();

                if (VarManager.getVarValue(VarDomainType.PLAYER, 183) == 0) {
                    if (slayerPointFarming) {
                        int varValue = VarManager.getVarValue(VarDomainType.PLAYER, 10077);
                        int lastDigit = varValue % 10;
                        if (lastDigit >= 0 && lastDigit <= 8) {
                            log("Waiting for combat to end, then Teleporting to Jacquelyn.");
                            boolean isOutOfCombat = Execution.delayUntil( random.nextLong(25000, 30000), () -> !player.inCombat());
                            if (!isOutOfCombat) {
                                log("Player is still in combat after 25-30 seconds. Moving to Wars Retreat.");
                                setSlayerState(Main.SlayerState.WARS_RETREAT);
                                return;
                            }
                            DeActivateMagicPrayer();
                            DeActivateRangedPrayer();
                            DeActivateMeleePrayer();
                            DeHandleSoulSplit();
                            lavaStrykewyrms = false;
                            iceStrykewyrms = false;
                            WarsRetreat.camelWarriors = false;
                            setSlayerState(Main.SlayerState.JACQUELYN);
                            clearTargetNames();
                        } else if (lastDigit == 9) {
                            setSlayerState(Main.SlayerState.WARS_RETREAT);
                        }
                    }
                }

                if (nearestBank && Backpack.isFull()) {
                    log("Backpack is full. Banking.");
                    setSlayerState(SlayerState.WARS_RETREAT);
                }
                Npc death = NpcQuery.newQuery().name("Death").results().nearest();
                if (death != null) {
                    setSlayerState(SlayerState.DEATHSOFFICE);
                }
                if (VarManager.getVarValue(VarDomainType.PLAYER, 183) == 0 && !slayerPointFarming) {
                    slayerState = SlayerState.CHECK;
                    log("Task completed.");
                } else {
                    attackTarget(player);
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
                clearTargetNames();
                log("Laniakea state.");
                TeleporttoLaniakea();
                break;
            case JACQUELYN:
                clearTargetNames();
                log("Jacquelyn state.");
                TeleporttoJacquelyn();
                break;
            case RETRIEVETASKINFO:
                log("Handle Task state.");
                handleTask(player);
                break;
            case CANCELTASK:
                log("Cancel Task state.");
                skipTask();
                break;
            case CAMELWARRIORS:
                log("Camel Warriors state.");
                camelWarriors(player);
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
                iceStrykewyrms();
                break;
            case LAVASTRYKEWYRMS:
                log("Lava Strykewyrms state.");
                lavaStrykewyrms();
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
            case BATS:
                log("Bats state.");
                bats(player);
                break;
            case BIRDS:
                log("Birds state.");
                birds(player);
                break;
            case CAVEBUGS:
                log("Cave Bugs state.");
                caveBugs(player);
                break;
            case CAVESLIME:
                log("Cave Slime state.");
                caveSlime(player);
                break;
            case COWS:
                log("Cows state.");
                cows(player);
                break;
            case FROGS:
                log("Frogs state.");
                frogs(player);
                break;
            case GHOSTS:
                log("Ghosts state.");
                ghosts(player);
                break;
            case GOBLINS:
                log("Goblins state.");
                goblins(player);
                break;
            case RATS:
                log("Rats state.");
                rats(player);
                break;
            case SKELETONS:
                log("Skeletons state.");
                skeletons(player);
                break;
            case SPIDERS:
                log("Spiders state.");
                spiders(player);
                break;
            case ZOMBIES:
                log("Zombies state.");
                zombies(player);
                break;
            case DEATHSOFFICE:
                log("Deaths Office state.");
                interactWithDeath();
                break;
        }
    }

    private static void checkTaskCompletion() {
        List<String> tasksToSkip = getTasksToSkip();
        log("Tasks to skip: " + tasksToSkip.size());
        if (!tasksToSkip.isEmpty()) {
            for (String task : tasksToSkip) {
                log("Task in list: " + task);
            }
        }
        Component component = ComponentQuery.newQuery(1639).componentIndex(11).results().first();
        if (component != null) {
            String taskText = component.getText().trim().toLowerCase();
            log("Component text: " + taskText);

            for (String task : tasksToSkip) {
                String taskLower = task.trim().toLowerCase();
                log("Checking against: " + taskLower);

                if (taskText.contains(taskLower) || taskLower.contains(taskText)) {
                    log("Task " + taskText + " is set to be skipped.");
                    setSlayerState(CANCELTASK);
                    return;
                }
            }
        } else {
            log("Component not found.");
        }
        if (VarManager.getVarValue(VarDomainType.PLAYER, 183) != 0) {
            slayerState = SlayerState.RETRIEVETASKINFO;
        } else {
            clearTargetNames();
            slayerState = SlayerState.WARS_RETREAT;
            log("Task completed.");
        }
    }

    public static void checkForOtherPlayersAndHopWorldSlayer() {
        if (slayerState.equals(COMBAT) && hopWorldsForSlayer) {
            if (player == null) {
                log("Local player not found.");
                return;
            }
            String localPlayerName = player.getName();
            Coordinate localPlayerLocation = player.getCoordinate();

            PlayerQuery query = PlayerQuery.newQuery();

            EntityResultSet<Player> players = query.results();

            boolean otherPlayersPresent = players.stream()
                    .filter(player -> !player.getName().equals(localPlayerName))
                    .filter(player -> {
                        Coordinate playerLocation = player.getCoordinate();
                        return playerLocation != null && localPlayerLocation.distanceTo(playerLocation) <= 25.0D;
                    })
                    .peek(player -> {
                        log("Found player within distance: " + player.getName());
                        playerInfo.add(new PlayerInfo(player.getName(), System.currentTimeMillis(), LoginManager.getWorld()));
                    })
                    .findAny()
                    .isPresent();

            if (otherPlayersPresent) {
                ActionBar.useAbility("War's Retreat Teleport");
                LocalPlayer player = Client.getLocalPlayer();
                Execution.delay(random.nextLong(6500, 7500));
                Execution.delayUntil(15000, () -> !player.inCombat());
                log("Other players found within distance. Initiating world hop.");
                int currentWorld = LoginManager.getWorld();
                int randomMembersWorldsIndex;
                do {
                    randomMembersWorldsIndex = RandomGenerator.nextInt(membersWorlds.length);
                } while (membersWorlds[randomMembersWorldsIndex] == currentWorld);
                HopWorldsSlayer(membersWorlds[randomMembersWorldsIndex]);
                log("Hopped to world: " + membersWorlds[randomMembersWorldsIndex]);
                setSlayerState(SlayerState.WARS_RETREAT);
            }
        }
    }

    public static void HopWorldsSlayer(int world) {
        if (Interfaces.isOpen(1431)) {
            log("[Slayer] Interacting with Settings Icon.");
            component(1, 7, 93782016);
            boolean hopperOpen = Execution.delayUntil(random.nextLong(5012, 9998), () -> Interfaces.isOpen(1433));
            log("Settings Menu Open: " + hopperOpen);
            Execution.delay(random.nextLong(642, 786));

            if (hopperOpen) {
                Component HopWorldsMenu = ComponentQuery.newQuery(1433).componentIndex(65).results().first();
                if (HopWorldsMenu != null) {
                    Execution.delay(random.nextLong(642, 786));
                    component(1, -1, 93913153);
                    log("[Slayer] Hop Worlds Button Clicked.");
                    boolean worldSelectOpen = Execution.delayUntil(random.nextLong(5014, 9758), () -> Interfaces.isOpen(1587));

                    if (worldSelectOpen) {
                        log("[Slayer] World Select Interface Open.");
                        Execution.delay(random.nextLong(642, 786));
                        component(2, world, 104005640);
                        log("[Slayer] Selected World: " + world);

                        if (Client.getGameState() == Client.GameState.LOGGED_IN && player != null) {
                            Execution.delay(random.nextLong(7548, 9879));
                            log("[Slayer] Resuming script.");
                        } else {
                            log("[Slayer] Failed to resume script. GameState is not LOGGED_IN or player is null.");
                        }
                    } else {
                        log("[Slayer] Failed to open World Select Interface.");
                    }
                } else {
                    log("[Slayer] Failed to find Hop Worlds Menu.");
                }
            } else {
                log("[Slayer] Failed to open hopper. Retrying...");
                HopWorldsSlayer(world);
            }
        } else {
            log("[Slayer] Interface 1431 is not open.");
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
