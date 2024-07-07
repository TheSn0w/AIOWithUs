package net.botwithus.Misc;

import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.TaskScheduler.shutdown;
import static net.botwithus.Variables.Variables.dialog;
import static net.botwithus.Variables.Variables.random;
import static net.botwithus.inventory.backpack.backpack;

public class Fletching {
    public static boolean makeDinarrow = false;

    public static long fletch() {
        ResultSet<Item> temperedFungalShaft = InventoryItemQuery.newQuery(93).ids(53083).option("'Flight'").results();
        ResultSet<Item> dinoPropellant = InventoryItemQuery.newQuery(93).ids(53073).option("'Flight'").results();
        ResultSet<Item> sharpShell = InventoryItemQuery.newQuery(93).ids(53093).option("Tip").results();
        ResultSet<Item> headlessArrow = InventoryItemQuery.newQuery(93).ids(53033).option("Tip").results();
        EntityResultSet<SceneObject> workBench = SceneObjectQuery.newQuery().id(125717).option("Use").results();


        if (Interfaces.isOpen(1251)) {
            return random.nextLong(1500, 3000);
        }
        if (Interfaces.isOpen(1370)) {
            if (VarManager.getVarValue(VarDomainType.PLAYER, 8847) > 0) {
                dialog(0, -1, 89784350);
                log("Interacting with Dialog Option");
            } else {
                log("Option selected in Interface is 0, shutting down");
                shutdown();
            }
            return random.nextLong(1500, 3000);
        }
        if (!temperedFungalShaft.isEmpty() && !dinoPropellant.isEmpty()) {
            log("Tempered fungal shaft and Dino propellant found");
            if (!workBench.isEmpty()) {
                log("Interacting with Workbench");
                workBench.nearest().interact("Use");
            } else {
                log("Interacting with Tempered fungal shaft");
                backpack.interact("Tempered fungal shaft", "'Flight'");
            }
            return random.nextLong(1500, 3000);
        } else {
            log("No Tempered fungal shaft or Dino propellant found");
        }
        if (!headlessArrow.isEmpty() && !sharpShell.isEmpty()) {
            log("Headless dinarrow and Sharp shell found");
            if (!workBench.isEmpty()) {
                log("Interacting with Workbench");
                workBench.nearest().interact("Use");
            } else {
                log("Interacting with Headless dinarrow");
                backpack.interact("Headless dinarrow", "Tip");
            }
            return random.nextLong(1500, 300);
        } else {
            log("No Headless dinarrow or Sharp shell found");
        }
        return random.nextLong(1000, 2000);
    }

}
