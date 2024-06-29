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
        log("Time Remaining: " + VarManager.getVarValue(VarDomainType.PLAYER, 10937) + " seconds.");

        if (enableDisturbances) {
            Execution.delay(shamblingHorror());
            Execution.delay(glyths());
            Execution.delay(soulStorm());
            Execution.delay(sparklingglyth());
            Execution.delay(ghost());
        }

        EntityResultSet<SceneObject> Platform2 = SceneObjectQuery.newQuery().id(127315).results();

        if (player.isMoving() || player.getAnimationId() != -1) {
            return random.nextLong(1500, 2500);
        }

        EntityResultSet<Npc> ChangeI = NpcQuery.newQuery().name("Change I (depleted)").option("Repair").results();
        EntityResultSet<Npc> ChangeII = NpcQuery.newQuery().name("Change II (depleted)").option("Repair").results();
        EntityResultSet<Npc> ChangeIII = NpcQuery.newQuery().name("Change III (depleted)").option("Repair").results();
        EntityResultSet<Npc> ElementalI = NpcQuery.newQuery().name("Elemental I (depleted)").option("Repair").results();
        EntityResultSet<Npc> ElementalII = NpcQuery.newQuery().name("Elemental II (depleted)").option("Repair").results();
        EntityResultSet<Npc> ElementalIII = NpcQuery.newQuery().name("Elemental III (depleted)").option("Repair").results();
        EntityResultSet<Npc> CommuneI = NpcQuery.newQuery().name("Commune I (depleted)").option("Repair").results();
        EntityResultSet<Npc> CommuneII = NpcQuery.newQuery().name("Commune II (depleted)").option("Repair").results();
        EntityResultSet<Npc> CommuneIII = NpcQuery.newQuery().name("Commune III (depleted)").option("Repair").results();
        EntityResultSet<Npc> ReagentI = NpcQuery.newQuery().name("Reagent I (depleted)").option("Repair").results();
        EntityResultSet<Npc> ReagentII = NpcQuery.newQuery().name("Reagent II (depleted)").option("Repair").results();
        EntityResultSet<Npc> ReagentIII = NpcQuery.newQuery().name("Reagent III (depleted)").option("Repair").results();
        EntityResultSet<Npc> BasicCandle = NpcQuery.newQuery().name("Basic ritual candle (depleted)").option("Repair").results();
        EntityResultSet<Npc> RegularCandle = NpcQuery.newQuery().name("Regular ritual candle (depleted)").option("Repair").results();
        EntityResultSet<Npc> GreaterCandle = NpcQuery.newQuery().name("Greater ritual candle (depleted)").option("Repair").results();
        EntityResultSet<Npc> GreaterSkull = NpcQuery.newQuery().name("Greater flaming skull (depleted)").option("Repair").results();
        EntityResultSet<Npc> MultiplyI = NpcQuery.newQuery().name("Multiply I (depleted)").option("Repair").results();
        EntityResultSet<Npc> MultiplyII = NpcQuery.newQuery().name("Multiply II (depleted)").option("Repair").results();
        EntityResultSet<Npc> MultiplyIII = NpcQuery.newQuery().name("Multiply III (depleted)").option("Repair").results();
        EntityResultSet<Npc> SpeedI = NpcQuery.newQuery().name("Speed I (depleted)").option("Repair").results();
        EntityResultSet<Npc> SpeedII = NpcQuery.newQuery().name("Speed II (depleted)").option("Repair").results();
        EntityResultSet<Npc> SpeedIII = NpcQuery.newQuery().name("Speed III (depleted)").option("Repair").results();
        EntityResultSet<Npc> AttractionI = NpcQuery.newQuery().name("Attraction I (depleted)").option("Repair").results();
        EntityResultSet<Npc> AttractionII = NpcQuery.newQuery().name("Attraction II (depleted)").option("Repair").results();
        EntityResultSet<Npc> AttractionIII = NpcQuery.newQuery().name("Attraction III (depleted)").option("Repair").results();
        EntityResultSet<Npc> ProtectionI = NpcQuery.newQuery().name("Protection I (depleted)").option("Repair").results();
        EntityResultSet<Npc> ProtectionII = NpcQuery.newQuery().name("Protection II (depleted)").option("Repair").results();
        EntityResultSet<Npc> ProtectionIII = NpcQuery.newQuery().name("Protection III (depleted)").option("Repair").results();

        if (!ChangeI.isEmpty()) log("Change I is Depleted");
        if (!ElementalI.isEmpty()) log("Elemental I is Depleted");
        if (!ElementalII.isEmpty()) log("Elemental II is Depleted");
        if (!MultiplyI.isEmpty()) log("Multiply I is Depleted");
        if (!CommuneI.isEmpty()) log("Commune I is Depleted");
        if (!ReagentI.isEmpty()) log("Reagent I is Depleted");
        if (!ReagentII.isEmpty()) log("Reagent II is Depleted");
        if (!ReagentIII.isEmpty()) log("Reagent III is Depleted");
        if (!BasicCandle.isEmpty()) log("Basic Candle is Depleted");
        if (!RegularCandle.isEmpty()) log("Regular Candle is Depleted");
        if (!ChangeII.isEmpty()) log("Change II is Depleted");
        if (!ChangeIII.isEmpty()) log("Change III is Depleted");
        if (!ElementalIII.isEmpty()) log("Elemental III is Depleted");
        if (!MultiplyIII.isEmpty()) log("Multiply III is Depleted");
        if (!GreaterSkull.isEmpty()) log("Greater Candle is Depleted");
        if (!MultiplyII.isEmpty()) log("Multiply II is Depleted");
        if (!CommuneII.isEmpty()) log("Commune II is Depleted");
        if (!CommuneIII.isEmpty()) log("Commune III is Depleted");
        if (!SpeedI.isEmpty()) log("Speed I is Depleted");
        if (!SpeedII.isEmpty()) log("Speed II is Depleted");
        if (!SpeedIII.isEmpty()) log("Speed III is Depleted");
        if (!AttractionI.isEmpty()) log("Attraction I is Depleted");
        if (!AttractionII.isEmpty()) log("Attraction II is Depleted");
        if (!AttractionIII.isEmpty()) log("Attraction III is Depleted");
        if (!ProtectionI.isEmpty()) log("Protection I is Depleted");
        if (!ProtectionII.isEmpty()) log("Protection II is Depleted");
        if (!ProtectionIII.isEmpty()) log("Protection III is Depleted");

        if (VarManager.getVarValue(VarDomainType.PLAYER, 10937) == 0 && player.getAnimationId() == -1 && (!ChangeI.isEmpty() || !ChangeII.isEmpty() || !ChangeIII.isEmpty() || !ElementalI.isEmpty() || !ElementalII.isEmpty() || !ElementalIII.isEmpty() || !MultiplyI.isEmpty() || !MultiplyII.isEmpty() || !MultiplyIII.isEmpty() || !CommuneI.isEmpty() || !CommuneII.isEmpty() || !CommuneIII.isEmpty() || !ReagentI.isEmpty() || !ReagentII.isEmpty() || !ReagentIII.isEmpty() || !BasicCandle.isEmpty() || !RegularCandle.isEmpty() || !GreaterCandle.isEmpty() || !GreaterSkull.isEmpty() || !SpeedI.isEmpty() || !SpeedII.isEmpty() || !SpeedIII.isEmpty() || !AttractionI.isEmpty() || !AttractionII.isEmpty() || !AttractionIII.isEmpty() || !ProtectionI.isEmpty() || !ProtectionII.isEmpty() || !ProtectionIII.isEmpty())) {

            EntityResultSet<SceneObject> Pedestal = SceneObjectQuery.newQuery().option("Repair all").results();
            if (!Pedestal.isEmpty()) {
                log("Interacting with Pedestal");
                Pedestal.first().interact("Repair all");
                Execution.delayUntil(random.nextLong(8000, 10000), () -> player.getAnimationId() == -1 && (ChangeI.isEmpty() || ChangeII.isEmpty() || ChangeIII.isEmpty() || ElementalI.isEmpty() || ElementalII.isEmpty() || ElementalIII.isEmpty() || MultiplyI.isEmpty() || MultiplyII.isEmpty() || MultiplyIII.isEmpty() || CommuneI.isEmpty() || CommuneII.isEmpty() || CommuneIII.isEmpty() || ReagentI.isEmpty() || ReagentII.isEmpty() || ReagentIII.isEmpty() || BasicCandle.isEmpty() || RegularCandle.isEmpty() || GreaterCandle.isEmpty() || GreaterSkull.isEmpty() || SpeedI.isEmpty() || SpeedII.isEmpty() || SpeedIII.isEmpty() || AttractionI.isEmpty() || AttractionII.isEmpty() || AttractionIII.isEmpty() || ProtectionI.isEmpty() || ProtectionII.isEmpty() || ProtectionIII.isEmpty()));
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

        return random.nextLong(750, 1500);
    }

    private static long glyths() {
        List<Integer> npcTypeIds = new ArrayList<>();
        npcTypeIds.add(30495); // Glyth 1
        npcTypeIds.add(30496); // Glyth 2
        npcTypeIds.add(30497); // Glyth 3

        for (Integer npcTypeId : npcTypeIds) {
            EntityResultSet<Npc> entities = NpcQuery.newQuery().byParentType(npcTypeId).results();

            while (!entities.isEmpty()) {
                log("Interacting with Glyth: " + npcTypeId);
                entities.first().interact("Deactivate");
                Execution.delay(random.nextLong(750, 1200));
                entities = NpcQuery.newQuery().byParentType(npcTypeId).results();

                if (npcTypeId.equals(30497) && entities.isEmpty()) {
                    EntityResultSet<SceneObject> pedestal = SceneObjectQuery.newQuery().id(127316).option("Continue ritual").results();
                    if (!pedestal.isEmpty()) {
                        log("Continuing Ritual");
                        pedestal.first().interact("Continue ritual");
                        Execution.delayUntil(random.nextLong(8000, 10000), () -> !player.isMoving() && player.getAnimationId() != -1);
                    }
                    break;
                }
            }
        }
        return 0;
    }
    private static long ghost() {
        int npcTypeId = 30493; // Ghost

        EntityResultSet<Npc> entities = NpcQuery.newQuery().byParentType(npcTypeId).results();

        while (!entities.isEmpty() && !player.isMoving()) {
            log("Interacting with Ghost");
            entities.nearest().interact("Dismiss");
            Execution.delayUntil(random.nextLong(8000, 10000), () -> !player.isMoving());
            entities = NpcQuery.newQuery().byParentType(npcTypeId).results();

            if (entities.isEmpty() || !player.isMoving()) {
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
                log("Interacting with Sparkling Glyth: " + npcTypeId);
                entities.first().interact("Restore");
                Execution.delay(random.nextLong(500, 600));
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
                log("Interacting with Soul Storm: " + npcTypeId);
                log("ID: " + entities.first().getType());
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
}



