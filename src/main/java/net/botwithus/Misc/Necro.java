package net.botwithus.Misc;

import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.minimenu.actions.NPCAction;
import net.botwithus.rs3.game.queries.builders.animations.SpotAnimationQuery;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.animation.SpotAnimation;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.ArrayList;
import java.util.List;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.player;
import static net.botwithus.Variables.Variables.random;

public class Necro {

    public static boolean handleNecro = false;
    public static boolean enableDisturbances = true;

    public static long interactWithEntities() {
        if (enableDisturbances) {
            Execution.delay(defile());
            Execution.delay(shamblingHorror());
            Execution.delay(glyths());
            Execution.delay(soulStorm());
            Execution.delay(sparklingglyth());
            Execution.delay(ghost());
        }

        EntityResultSet<SceneObject> Platform2 = SceneObjectQuery.newQuery().id(127315).results();

        if (player.isMoving() || player.getAnimationId() != -1) {
            return random.nextLong(random.nextLong(750,1250));
        }

        EntityResultSet<Npc> allEntities = NpcQuery.newQuery().option("Repair").results();
        List<String> depletedNpcs = new ArrayList<>();

        for (Npc npc : allEntities) {
            if (npc.getName().contains("depleted")) {
                depletedNpcs.add(npc.getName());
            }
        }

        if (!depletedNpcs.isEmpty()) {
            log("Depleted Glyths: " + String.join(", ", depletedNpcs));
        }

        if (VarManager.getVarValue(VarDomainType.PLAYER, 10937) == 0 && player.getAnimationId() == -1 && !depletedNpcs.isEmpty()) {
            EntityResultSet<SceneObject> Pedestal = SceneObjectQuery.newQuery().option("Repair all").results();
            if (!Pedestal.isEmpty()) {
                log("Interacting with Pedestal");
                Pedestal.first().interact("Repair all");
                Execution.delayUntil(random.nextLong(8000, 10000), () -> player.getAnimationId() == -1);
            }
        } else {
            if (!Platform2.isEmpty() && VarManager.getVarValue(VarDomainType.PLAYER, 10937) == 0) {
                log("Starting Ritual");
                Platform2.first().interact("Start ritual");
                Execution.delay(random.nextLong(1500, 3000));
            } else {
                EntityResultSet<SceneObject> pedestal = SceneObjectQuery.newQuery().id(127316).option("Continue ritual").results();
                if (!pedestal.isEmpty()) {
                    log("Continuing Ritual");
                    pedestal.first().interact("Continue ritual");
                    Execution.delayUntil(random.nextLong(8000, 10000), () -> !player.isMoving() && player.getAnimationId() != -1);
                }
            }
        }

        return random.nextLong(random.nextLong(750,1250));
    }

    private static long glyths() {
        List<Integer> npcTypeIds = new ArrayList<>();
        npcTypeIds.add(30495); // Glyth 1
        npcTypeIds.add(30496); // Glyth 2
        npcTypeIds.add(30497); // Glyth 3

        boolean interactedWithAllGlyths = false;

        for (Integer npcTypeId : npcTypeIds) {
            EntityResultSet<Npc> entities = NpcQuery.newQuery().byParentType(npcTypeId).results();

            if (!entities.isEmpty()) {
                Execution.delay(random.nextLong(200, 400));
                log("Interacting with Glyth");
                entities.first().interact("Deactivate");


                while (!entities.isEmpty()) {
                    entities = NpcQuery.newQuery().byParentType(npcTypeId).results();
                    Execution.delay(random.nextLong(50, 100));

                }

                if (npcTypeId.equals(30497) && entities.isEmpty()) {
                    interactedWithAllGlyths = true;
                }
            }
        }

        if (interactedWithAllGlyths) {
            EntityResultSet<SceneObject> pedestal = SceneObjectQuery.newQuery().id(127316).option("Continue ritual").results();
            if (!pedestal.isEmpty()) {
                log("Continuing Ritual");
                pedestal.first().interact("Continue ritual");
                Execution.delayUntil(random.nextLong(8000, 10000), () -> !player.isMoving() && player.getAnimationId() != -1);
            }
        }

        return 0;
    }

    private static long ghost() {
        int npcTypeId = 30493; // Ghost

        EntityResultSet<Npc> entities = NpcQuery.newQuery().byParentType(npcTypeId).results();

        while (!entities.isEmpty()) {
            Execution.delay(random.nextLong(500, 1250));
            log("Interacting with Ghost");
            entities.nearest().interact("Dismiss");

            Execution.delayUntil(random.nextLong(8000, 10000), () -> NpcQuery.newQuery().byParentType(npcTypeId).animation(-1).results().isEmpty());
            Execution.delay(random.nextLong(900, 1500));
            entities = NpcQuery.newQuery().byParentType(npcTypeId).results();

            if (entities.nearest().getAnimationId() != -1) {
                EntityResultSet<SceneObject> pedestal = SceneObjectQuery.newQuery().id(127316).option("Continue ritual").results();
                if (!pedestal.isEmpty()) {
                    log("Continuing Ritual");
                    pedestal.first().interact("Continue ritual");
                    Execution.delayUntil(random.nextLong(8000, 10000), () -> !player.isMoving() && player.getAnimationId() != -1);
                    Execution.delay(random.nextLong(3000, 5000));
                }
            }

            if (entities.nearest().getAnimationId() != -1) {
                break;
            }
        }

        return 0;
    }


    private static long sparklingglyth() {
        List<Integer> npcTypeIds = new ArrayList<>();
        npcTypeIds.add(30492); // Sparkling Glyth

        for (Integer npcTypeId : npcTypeIds) {
            EntityResultSet<Npc> entities = NpcQuery.newQuery().byType(npcTypeId).results();

            while (!entities.isEmpty() && !player.isMoving()) {
                Execution.delay(random.nextLong(500, 1250));
                log("Interacting with Sparkling Glyth");
                entities.first().interact("Restore");
                Execution.delayUntil(random.nextLong(7500, 10000), () -> NpcQuery.newQuery().byType(npcTypeId).results().isEmpty());
                entities = NpcQuery.newQuery().byType(npcTypeId).results();

                if (entities.isEmpty()) {
                    break;
                }
            }
        }

        return 0;
    }

    private static long soulStorm() {
        List<Integer> npcTypeIds = new ArrayList<>();
        npcTypeIds.add(30498); // Soul Storm
        npcTypeIds.add(30499); // Soul Storm

        for (Integer npcTypeId : npcTypeIds) {
            EntityResultSet<Npc> entities = NpcQuery.newQuery().byType(npcTypeId).results();

            while (!entities.isEmpty() && !player.isMoving()) {
                Execution.delay(random.nextLong(500, 1250));
                log("Interacting with Soul Storm");
                entities.first().interact("Dissipate");
                Execution.delay(random.nextLong(1000, 2000));
                entities = NpcQuery.newQuery().byType(npcTypeId).results();

                if (entities.isEmpty()) {
                    break;
                }
            }
        }

        return 0;
    }

    private static long shamblingHorror() {
        int npcTypeId = 30494; // Shambling Horror
        EntityResultSet<Npc> entities = NpcQuery.newQuery().byParentType(npcTypeId).results();

        if (!entities.isEmpty()) {
            Execution.delay(random.nextLong(1025, 1500));
            Npc shambingHorrorNpc = entities.first();

            log("Interacting with Shambling Horror");
            shambingHorrorNpc.interact("Sever link");
            Execution.delay(random.nextLong(1025, 1250));

            Npc glow = getGlow();
            while (glow == null) {
                log("Glow not found. Interacting with Shambling Horror again.");
                shambingHorrorNpc.interact("Sever link");
                Execution.delay(random.nextLong(1024, 1540));
                glow = getGlow();
            }

            log("Found Glow. Interacting with Glow");
            boolean action;
            String name = glow.getName();
            if (name != null && name.contains("depleted")) {
                action = glow.interact(NPCAction.NPC3);
            } else {
                action = glow.interact(NPCAction.NPC1);
            }
            if (action) {
                log("Interaction with Glow complete.");
                Execution.delay(random.nextLong(600, 1250));
                EntityResultSet<SceneObject> pedestal = SceneObjectQuery.newQuery().id(127316).option("Continue ritual").results();
                if (!pedestal.isEmpty()) {
                    log("Continuing Ritual");
                    pedestal.first().interact("Continue ritual");
                    Execution.delay(random.nextLong(3000, 5000));
                }
            } else {
                log("Failed to interact with Glow.");
            }
        }
        return 0;
    }

    private static Npc getGlow() {
        Npc glow = NpcQuery.newQuery()
                .spotAnimation(7977)
                .results()
                .first();
        if (glow == null) {
            glow = NpcQuery.newQuery()
                    .spotAnimation(6861)
                    .results()
                    .first();
        }
        return glow;
    }
    private static long defile() {
        EntityResultSet<Npc> results = NpcQuery.newQuery().byType(30500).results();
        if (!results.isEmpty()) {
            Npc defileNpc = results.first();
            Execution.delay(random.nextLong(500, 1000));
            log("Siphoning the Defile");
            defileNpc.interact("Siphon");
            Execution.delayUntil(random.nextLong(10000, 15000), () -> !player.isMoving());

            boolean hasInteracted = false;
            while (!results.isEmpty()) {
                Npc light = getLight();
                if (light != null && !hasInteracted) {
                    Execution.delay(random.nextLong(400, 600));
                    log("Purple Smoke appeared, interacting with Defile again");
                    defileNpc.interact("Siphon");
                    hasInteracted = true;
                } else if (light == null) {
                    hasInteracted = false;
                }
                results = NpcQuery.newQuery().byType(30500).results();
                if (results.isEmpty()) {
                    break;
                }
            }
        }
        return 0;
    }

    private static Npc getLight() {
        return NpcQuery.newQuery()
                .spotAnimation(7930)
                .results()
                .first();
    }
}



