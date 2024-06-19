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
        EntityResultSet<SceneObject> Platform2 = SceneObjectQuery.newQuery().id(127315).results();

        if (player.isMoving() || player.getAnimationId() != -1) {
            return random.nextLong(1500, 2500);
        }

        EntityResultSet<Npc> ChangeII = NpcQuery.newQuery().name("Change II (depleted)").option("Repair").results();
        EntityResultSet<Npc> ChangeIII = NpcQuery.newQuery().name("Change III (depleted)").option("Repair").results();
        EntityResultSet<Npc> ElementalIII = NpcQuery.newQuery().name("Elemental III (depleted)").option("Repair").results();
        EntityResultSet<Npc> MultiplyIII = NpcQuery.newQuery().name("Multiply III (depleted)").option("Repair").results();
        EntityResultSet<Npc> GreaterCandle = NpcQuery.newQuery().name("Greater flaming skull (depleted)").option("Repair").results();
        EntityResultSet<Npc> MultiplyII = NpcQuery.newQuery().name("Multiply II (depleted)").option("Repair").results();
        EntityResultSet<Npc> CommuneII = NpcQuery.newQuery().name("Commune II (depleted)").option("Repair").results();
        EntityResultSet<Npc> CommuneIII = NpcQuery.newQuery().name("Commune III (depleted)").option("Repair").results();

        if (!ChangeII.isEmpty()) log("Change II is Depleted");
        if (!ChangeIII.isEmpty()) log("Change III is Depleted");
        if (!ElementalIII.isEmpty()) log("Elemental III is Depleted");
        if (!MultiplyIII.isEmpty()) log("Multiply III is Depleted");
        if (!GreaterCandle.isEmpty()) log("Greater Candle is Depleted");
        if (!MultiplyII.isEmpty()) log("Multiply II is Depleted");
        if (!CommuneII.isEmpty()) log("Commune II is Depleted");
        if (!CommuneIII.isEmpty()) log("Commune III is Depleted");

        if (player.getAnimationId() == -1 && (!ChangeII.isEmpty() || !ChangeIII.isEmpty() || !ElementalIII.isEmpty() || !MultiplyIII.isEmpty() || !GreaterCandle.isEmpty() || !MultiplyII.isEmpty() || !CommuneII.isEmpty() || !CommuneIII.isEmpty())) {

            EntityResultSet<SceneObject> Pedestal = SceneObjectQuery.newQuery().option("Repair all").results();
            if (!Pedestal.isEmpty()) {
                log("Interacting with Pedestal");
                Pedestal.first().interact("Repair all");
                Execution.delay(random.nextLong(5000, 10000));
            }
        } else {
            if (!Platform2.isEmpty()) {
                log("Interacting with Platform");
                Platform2.first().interact("Start ritual");
                Execution.delay(random.nextLong(5000, 10000));
            }
        }

        return random.nextLong(1500, 2500);
    }

}



