package net.botwithus.Misc;

import net.botwithus.SnowsScript;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Equipment;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.*;

public class Summoning {

    public static long makePouches(LocalPlayer player) {
        EntityResultSet<SceneObject> bankChest = SceneObjectQuery.newQuery().id(92692).option("Use").results();
        EntityResultSet<SceneObject> Obolisk = SceneObjectQuery.newQuery().id(94230).option("Infuse-pouch").results();

        if (player.isMoving()) {
            return random.nextLong(1250, 2500);
        }
        if (Interfaces.isOpen(1370)) {
            log("[Summoning] Interface 1370 is open.");
            MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 89784350);
            log("[Summoning] Selecting 'Creating Pouches'");
            return random.nextLong(1250, 1500);
        }
        if (containsPouch()) {
            if (Backpack.contains("Attuned crystal teleport seed")) {
                backpack.interact("Attuned crystal teleport seed", "Activate");
                log("[Summoning] Activating 'Attuned crystal teleport seed' from backpack");
            } else {
                if (Equipment.contains("Attuned crystal teleport seed")) {
                    Equipment.interact(Equipment.Slot.POCKET, "Activate");
                    log("[Summoning] Activating 'Attuned crystal teleport seed' from equipment");
                }
            }
            Execution.delayUntil(5000, () -> Interfaces.isOpen(720));
            if (Interfaces.isOpen(720)) {
                dialog(0, -1, 47185955);
                log("[Summoning] Teleporting to Bank'");
                Execution.delay(random.nextLong(3500, 4000));
                SceneObject bank = bankChest.nearest();
                if (bank != null) {
                    log("[Summoning] Found Bank chest.");
                    bank.interact("Load Last Preset from");
                    log("[Summoning] Selecting 'Load Last Preset from'");
                    return random.nextLong(1250, 1500);
                }
            }
        } else {
            log("[Summoning] Going to obolisk.");
            if (Backpack.contains("Attuned crystal teleport seed")) {
                backpack.interact("Attuned crystal teleport seed", "Activate");
                log("[Summoning] Activating 'Attuned crystal teleport seed from backpack'");
            } else {
                if (Equipment.contains("Attuned crystal teleport seed")) {
                    Equipment.interact(Equipment.Slot.POCKET, "Activate");
                    log("[Summoning] Activating 'Attuned crystal teleport seed from equipment'");
                }
            }
            Execution.delayUntil(5000, () -> Interfaces.isOpen(720));
            if (Interfaces.isOpen(720)) {
                log("[Summoning] Interface 720 is open.");
                MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 47185940);
                log("[Summoning] Teleported to Obolisk'");
                Execution.delay(random.nextLong(3500, 4000));
                SceneObject obolisk = Obolisk.nearest();
                if (obolisk != null) {
                    obolisk.interact("Infuse-pouch");
                    log("[Summoning] Selecting 'Infuse-pouch'");
                    return random.nextLong(1250, 1500);
                }
            }
        }
        return random.nextLong(1250, 1500);
    }

    public static boolean containsPouch() {
        for (Item item : Backpack.getItems()) {
            String itemName = item.getName();
            if (itemName.contains("pouch") && !itemName.equals("pouch")) {
                return true;
            }
        }
        return false;
    }




    public static long interactWithObolisk(LocalPlayer player) {
        if (isPlayerBusy(player)) {
            return generateDelay();
        }

        if (isSpiritOnyxMissing() && useSpiritStone) {
            log("[Error] Spirit stone is missing, moving to bank");
            return bankforPreset();
        }

        if (isInterfaceOpen()) {
            selectCreatingPouches();
        }

        if (isOboliskMissing()) {
            moveToObolisk();
            return generateDelay();
        }

        SceneObject nearestObolisk = getNearestObolisk();

        if (isWaterTalismanAvailable()) {
            infusePouchWithWaterTalisman(nearestObolisk);
            return generateDelay();
        }

        if (isGeyserTitanPouchAvailable()) {
            infusePouchWithGeyserTitanPouch(nearestObolisk);
            return generateDelay();
        }

        Execution.delay(interactWithMagestix());
        return generateDelay();
    }

    private static boolean isPlayerBusy(LocalPlayer player) {
        return player.isMoving();
    }

    private static long generateDelay() {
        return random.nextLong(DELAY_MIN, DELAY_MAX);
    }

    private static boolean isSpiritOnyxMissing() {
        ResultSet<Item> spirit = InventoryItemQuery.newQuery(INVENTORYID).name(spiritStoneName).results();
        return spirit.isEmpty();
    }

    private static boolean isInterfaceOpen() {
        return Interfaces.isOpen(INTERFACE_ID);
    }

    private static void selectCreatingPouches() {
        if (MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 89784350)) {
            Execution.delay(random.nextLong(2200, 2600));
        }
        else {
            log("[Error] Failed to select 'Creating Pouches'");
        }
    }

    private static boolean isOboliskMissing() {
        EntityResultSet<SceneObject> obolisk = SceneObjectQuery.newQuery().id(OBOLISK_ID).option(INFUSE_POUCH_OPTION).results();
        return obolisk.isEmpty();
    }

    private static void moveToObolisk() {
        Coordinate oboliskLocation = new Coordinate(2931, 3448, 0);
        Movement.traverse(NavPath.resolve(oboliskLocation));
    }

    private static SceneObject getNearestObolisk() {
        EntityResultSet<SceneObject> obolisk = SceneObjectQuery.newQuery().id(OBOLISK_ID).option(INFUSE_POUCH_OPTION).results();
        return obolisk.nearest();
    }

    private static boolean isWaterTalismanAvailable() {
        return Backpack.contains(secondaryItem);
    }

    private static final Map<Integer, String> secondaryItemName = new HashMap<>();
    static {
        secondaryItemName.put(1444, "Water talisman");
    }
    private static void infusePouchWithWaterTalisman(SceneObject obolisk) {
        obolisk.interact(INFUSE_POUCH_OPTION);
        String itemName = secondaryItemName.get(secondaryItem);
        log("[Summoning] Infusing pouch with: " + itemName);
    }

    private static boolean isGeyserTitanPouchAvailable() {
        return Backpack.contains(getPouchName());
    }

    private static void infusePouchWithGeyserTitanPouch(SceneObject obolisk) {
        obolisk.interact(INFUSE_POUCH_OPTION);
        log("[Summoning] Infusing pouch with: " + pouchName);
    }

    private static long interactWithMagestix() {
        if (Interfaces.isOpen(1265)) {
            log("[Summoning] Interface is open");
            ResultSet<Item> items = InventoryItemQuery.newQuery(628).ids(secondaryItem).results();
            if (!items.isEmpty()) {
                Item item = items.first();
                int slot = item.getSlot();
                MiniMenu.interact(ComponentAction.COMPONENT.getType(), 7, slot, 82903060);
                log("[Summoning] Buying all: " + item.getName());
                Execution.delay(random.nextLong(550, 600));
            } else {
                MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, -1, 82903072);
                log("[Summoning] Selecting [Sell]");
                Execution.delay(random.nextLong(550, 600));
                ResultSet<Item> items1 = InventoryItemQuery.newQuery(93).ids(1445).results();
                if (!items1.isEmpty()) {
                    Item item = items1.first();
                    int slot = item.getSlot();
                    MiniMenu.interact(ComponentAction.COMPONENT.getType(), 6, slot, 82903060);
                }
                log("[Summoning] Selling all: " + secondaryItemName.get(secondaryItem));
                Execution.delay(random.nextLong(550, 600));
                log("[Summoning] Selecting [Buy]");
                MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, -1, 82903081);
                Execution.delay(random.nextLong(550, 600));
            }
        } else {
            EntityResultSet<Npc> magestix = NpcQuery.newQuery().name("Magestix").option("Trade").results();
            if (!magestix.isEmpty()) {
                Npc nearestMagestix = magestix.nearest();
                nearestMagestix.interact("Trade");
                log("[Summoning] Interacting with Magestix");
            }
        }
        return 0;
    }


    public static long bankforPreset() {
        Coordinate bankLocation = new Coordinate(2875, 3417, 0);
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().name("Counter").option("Bank").results();
        if (!results.isEmpty()) {
            SceneObject bank = results.nearest();
            bank.interact("Load Last Preset from");
            log("[Summoning] Interacting with Bank");
        } else
        if (Movement.traverse(NavPath.resolve(bankLocation)) == TraverseEvent.State.FINISHED) {
            return random.nextLong(1250, 1500);
        }
        return random.nextLong(1250, 1500);
    }
}