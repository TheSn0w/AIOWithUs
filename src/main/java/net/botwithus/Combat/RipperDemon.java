package net.botwithus.Combat;

import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.script.Execution;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.random;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class RipperDemon {
    static LocalPlayer player = getLocalPlayer();
    static Coordinate evadeCoordinate = null;

    public static long engageRipperDemon() {
        EntityResultSet<Npc> rippers = NpcQuery.newQuery().name("Ripper Demon").option("Attack").results();
        if (rippers.isEmpty()) {
            log("[Info] No Ripper Demons found.");
            return random.nextLong(100, 200);
        }

        Npc ripper = rippers.nearest();
        while (ripper != null) {
            log("[Info] Attacking Ripper Demon...");
            Execution.delay(attackRipper(ripper));

            if (ripper.getCurrentHealth() < 15000) {
                log("[Info] Ripper Demon's health is less than 10,000. Preparing to evade...");
                evadeCoordinate = calculateEvadeCoordinate(ripper);
            }

            if (ripper.getCurrentHealth() < 4000 || ripper.getAnimationId() == 27775) {
                log("[Info] Avoiding special attack...");
                avoidSpecialAttack(ripper);
            }

            ripper = NpcQuery.newQuery().name("Ripper demon").option("Attack").results().nearest();
        }

        return random.nextLong(1500, 2500);
    }

    private static long attackRipper(Npc ripper) {
        if (player.getFollowing() == null && !player.hasTarget()) {
            log("[Info] Player is not following any NPC and does not have a target. Interacting with Ripper Demon...");
            ripper.interact("Attack");
        }
        return random.nextLong(100, 200);
    }

    private static void avoidSpecialAttack(Npc ripper) {
        if (ripper.getCurrentHealth() < 4000 || ripper.getAnimationId() == 27775) {
            log("[Info] Ripper Demon's health is less than 4,000 or animation ID is 27775. Evading...");
            if (evadeCoordinate != null && evadeCoordinate.isReachable()) {
                log("[Info] Evade coordinate is reachable. Moving to evade coordinate...");
                Movement.walkTo(evadeCoordinate.getX(), evadeCoordinate.getY(), true);
            }
        }
    }

    private static Coordinate calculateEvadeCoordinate(Npc ripper) {
        Coordinate npcCoord = ripper.getCoordinate();
        int[][] directions = {{0, 3}, {3, 0}, {0, -3}, {-3, 0}};
        for (int[] dir : directions) {
            Coordinate targetCoord = new Coordinate(npcCoord.getX() + dir[0], npcCoord.getY() + dir[1], npcCoord.getZ());
            if (targetCoord.isReachable()) {
                return targetCoord;
            }
        }
        return null;
    }
}
