package net.botwithus.Misc;

import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.player;
import static net.botwithus.Variables.Variables.random;

public class Necro {

    public static boolean handleNecro = false;

    public static long interactWithEntities() {
        if (player.isMoving() || player.getAnimationId() != -1) {
            return random.nextLong(1500, 2500);
        }

        EntityResultSet<Npc> ChangeII = NpcQuery.newQuery().name("Change II (depleted)").option("Repair").results();
        EntityResultSet<Npc> ChangeIII = NpcQuery.newQuery().name("Change III (depleted)").option("Repair").results();
        EntityResultSet<Npc> ElementalIII = NpcQuery.newQuery().name("Elemental III (depleted)").option("Repair").results();
        EntityResultSet<Npc> MultiplyIII = NpcQuery.newQuery().name("Multiply III (depleted)").option("Repair").results();
        EntityResultSet<Npc> GreaterCandle = NpcQuery.newQuery().name("Greater flaming skull (depleted)").option("Repair").results();

        if (!ChangeII.isEmpty()) log("Change II is Depleted");
        if (!ChangeIII.isEmpty()) log("Change III is Depleted");
        if (!ElementalIII.isEmpty()) log("Elemental III is Depleted");
        if (!MultiplyIII.isEmpty()) log("Multiply III is Depleted");
        if (!GreaterCandle.isEmpty()) log("Greater Candle is Depleted");

        if (player.getAnimationId() == -1 && (!ChangeII.isEmpty() || !ChangeIII.isEmpty() || !ElementalIII.isEmpty() || !MultiplyIII.isEmpty() || !GreaterCandle.isEmpty())) {

            EntityResultSet<SceneObject> Pedestal = SceneObjectQuery.newQuery().name("Pedestal (essence)").option("Replace focus").results();
            if (!Pedestal.isEmpty()) {
                log("Interacting with Pedestal");
                Pedestal.first().interact("Repair all");
                Execution.delayUntil(random.nextLong(5000, 1000), () -> player.getAnimationId() == -1 && (ChangeII.isEmpty() || ChangeIII.isEmpty() || ElementalIII.isEmpty() || MultiplyIII.isEmpty() || GreaterCandle.isEmpty()));
            }
        } else {
            EntityResultSet<SceneObject> Platform = SceneObjectQuery.newQuery().name("Platform").option("Start ritual").results();
            if (!Platform.isEmpty()) {
                log("Interacting with Platform");
                Platform.first().interact("Start ritual");
                Execution.delay(random.nextLong(5000, 10000));
            }
        }

        return random.nextLong(1500, 2500);
    }

    EntityResultSet<SceneObject> Pedestal = SceneObjectQuery.newQuery().name("Pedestal (essence)").option("Replace focus").results();
    EntityResultSet<Npc> ChangeII = NpcQuery.newQuery().name("Change II (depleted").option("Repair").results();
    EntityResultSet<Npc> ChangeIII = NpcQuery.newQuery().name("Change III (depleted").option("Repair").results();
    EntityResultSet<Npc> ElementalIII = NpcQuery.newQuery().name("Elemental III (depleted").option("Repair").results();
    EntityResultSet<Npc> MultiplyIII = NpcQuery.newQuery().name("Multiply III (depleted").option("Repair").results();
    EntityResultSet<Npc> GreaterCandle = NpcQuery.newQuery().name("Greater flaming skull").option("Repair").results();
    EntityResultSet<SceneObject> Platform = SceneObjectQuery.newQuery().name("Platform").option("Start ritual").results();

}



