package net.botwithus.Archaeology;

import net.botwithus.Runecrafting.PlayerInfo;
import net.botwithus.Runecrafting.Runecrafting;
import net.botwithus.SnowsScript;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.login.LoginManager;
import net.botwithus.rs3.game.queries.builders.characters.PlayerQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.characters.player.Player;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.ArrayList;
import java.util.List;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.getBotState;
import static net.botwithus.Variables.Variables.component;
import static net.botwithus.Variables.Variables.random;

public class WorldHop {
    public static boolean hopWorldsforArchaeology = false;

    public static void checkForOtherPlayersAndHopWorld() {
        if (hopWorldsforArchaeology) {
            if (getBotState() == SnowsScript.BotState.SKILLING) {
                Player localPlayer = Client.getLocalPlayer();
                if (localPlayer == null) {
                    log("Local player not found.");
                    return;
                }
                String localPlayerName = localPlayer.getName();
                Coordinate localPlayerLocation = localPlayer.getCoordinate();

                PlayerQuery query = PlayerQuery.newQuery();

                EntityResultSet<Player> players = query.results();

                boolean otherPlayersPresent = players.stream()
                        .filter(player -> !player.getName().equals(localPlayerName))
                        .filter(player -> {
                            Coordinate playerLocation = player.getCoordinate();
                            return playerLocation != null && localPlayerLocation.distanceTo(playerLocation) <= 8.0D;
                        })
                        .peek(player -> {
                            log("Found player within distance: " + player.getName());
                        })
                        .findAny()
                        .isPresent();

                if (otherPlayersPresent) {
                    log("Other players found within distance. Initiating world hop.");
                    int currentWorld = LoginManager.getWorld();
                    int randomMembersWorldsIndex;
                    do {
                        randomMembersWorldsIndex = RandomGenerator.nextInt(membersWorlds.length);
                    } while (membersWorlds[randomMembersWorldsIndex] == currentWorld);
                    HopWorlds(membersWorlds[randomMembersWorldsIndex]);
                    log("Hopped to world: " + membersWorlds[randomMembersWorldsIndex]);
                }
            }
        }
    }
    private static void HopWorlds(int world) {
        LocalPlayer player = Client.getLocalPlayer();
        if (Interfaces.isOpen(1431)) {
            log("[Runecrafting] Interacting with Settings Icon.");
            component(1, 7, 93782016);
            boolean hopperOpen = Execution.delayUntil(random.nextLong(5012, 9998), () -> Interfaces.isOpen(1433));
            log("Settings Menu Open: " + hopperOpen);
            Execution.delay(random.nextLong(642, 786));

            if (hopperOpen) {
                Component HopWorldsMenu = ComponentQuery.newQuery(1433).componentIndex(65).results().first();
                if (HopWorldsMenu != null) {
                    Execution.delay(random.nextLong(642, 786));
                    component(1, -1, 93913153);
                    log("[Runecrafting] Hop Worlds Button Clicked.");
                    boolean worldSelectOpen = Execution.delayUntil(random.nextLong(5014, 9758), () -> Interfaces.isOpen(1587));

                    if (worldSelectOpen) {
                        log("[Runecrafting] World Select Interface Open.");
                        Execution.delay(random.nextLong(642, 786));
                        component(2, world, 104005640);
                        log("[Runecrafting] Selected World: " + world);

                        if (Client.getGameState() == Client.GameState.LOGGED_IN && player != null) {
                            Execution.delay(random.nextLong(7548, 9879));
                            log("[Runecrafting] Resuming script.");
                        } else {
                            log("[Runecrafting] Failed to resume script. GameState is not LOGGED_IN or player is null.");
                        }
                    } else {
                        log("[Runecrafting] Failed to open World Select Interface.");
                    }
                } else {
                    log("[Runecrafting] Failed to find Hop Worlds Menu.");
                }
            } else {
                log("[Runecrafting] Failed to open hopper. Retrying...");
                HopWorlds(world);
            }
        } else {
            log("[Runecrafting] Interface 1431 is not open.");
        }
    }

    private static final int[] membersWorlds = new int[]{
            1, 2, 4, 5, 6, 9, 10, 12, 14, 15,
            16, 21, 22, 23, 24, 25, 26, 27, 28, 31,
            32, 35, 36, 37, 39, 40, 42, 44, 45, 46,
            49, 50, 51, 53, 54, 56, 58, 59, 60,
            62, 63, 64, 65, 66, 67, 68, 69, 70, 71,
            72, 73, 74, 76, 77, 78, 79, 82, 83,
            85, 87, 88, 89, 91, 92, 97, 98, 99, 100, 103, 104, 105, 106, 116, 117, 119,
            123, 124, 134, 138, 139, 140, 252};
}
