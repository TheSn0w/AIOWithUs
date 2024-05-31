package net.botwithus.Misc;

import net.botwithus.SnowsScript;
import net.botwithus.api.game.hud.inventories.Backpack;
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
import net.botwithus.rs3.script.ScriptConsole;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static net.botwithus.Variables.Variables.useSpiritStone;

public class Summoning {
    SnowsScript script;
    private static Random random = new Random();

    public Summoning(SnowsScript script) {
        this.script = script;
    }

    public static long makePouches(LocalPlayer player) {
        EntityResultSet<SceneObject> bankChest = SceneObjectQuery.newQuery().id(92692).option("Use").results();
        EntityResultSet<SceneObject> Obolisk = SceneObjectQuery.newQuery().id(94230).option("Infuse-pouch").results();

        if (player.isMoving()) {
            return random.nextLong(1250, 2500);
        }
        if (Interfaces.isOpen(1370)) {
            MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 89784350);
            ScriptConsole.println("Selecting 'Creating Pouches'");
            return random.nextLong(1250, 1500);
        }
        if (containsPouch()) {
            backpack.interact("Attuned crystal teleport seed", "Activate");
            ScriptConsole.println("Activating 'Attuned crystal teleport seed'");
            Execution.delayUntil(5000, () -> Interfaces.isOpen(720));
            if (Interfaces.isOpen(720)) {
                MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 47185955);
                ScriptConsole.println("Teleporting to Bank'");
                Execution.delay(random.nextLong(3500, 4000));
                SceneObject bank = bankChest.nearest();
                if (bank != null) {
                    bank.interact("Load Last Preset from");
                    ScriptConsole.println("Selecting 'Load Last Preset from'");
                    return random.nextLong(1250, 1500);
                }
            }
        } else {
            backpack.interact("Attuned crystal teleport seed", "Activate");
            ScriptConsole.println("Activating 'Attuned crystal teleport seed'");
            Execution.delayUntil(5000, () -> Interfaces.isOpen(720));
            if (Interfaces.isOpen(720)) {
                MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 47185940);
                ScriptConsole.println("Teleporting to Obolisk'");
                Execution.delay(random.nextLong(3500, 4000));
                SceneObject obolisk = Obolisk.nearest();
                if (obolisk != null) {
                    obolisk.interact("Infuse-pouch");
                    ScriptConsole.println("Selecting 'Infuse-pouch'");
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


    private static final int INVENTORYID = 94;
    private static final int OBOLISK_ID = 67036;
    private static final String INFUSE_POUCH_OPTION = "Infuse-pouch";
    private static int secondaryItem = 1444;
    private static String spiritStoneName = "Spirit onyx (a)";
    private static String pouchName = "Geyser titan pouch";
    private static final int INTERFACE_ID = 1370;
    private static final int DELAY_MIN = 660;
    private static final int DELAY_MAX = 720;


    public static void setSecondaryItem(int newSecondaryItem) {
        secondaryItem = newSecondaryItem;
    }
    public static int getSecondaryItem() {
        return secondaryItem;
    }


    public static void setSpiritStoneName(String newSpiritStoneName) {
        spiritStoneName = newSpiritStoneName;
    }

    public static String getPouchName() {
        return pouchName;
    }

    public static void setPouchName(String newPouchName) {
        pouchName = newPouchName;
    }


    public static long interactWithObolisk(LocalPlayer player) {
        if (isPlayerBusy(player)) {
            return generateDelay();
        }

        if (isSpiritOnyxMissing() && useSpiritStone) {
            ScriptConsole.println("Spirit stone is missing, moving to bank");
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
            ScriptConsole.println("Failed to select 'Creating Pouches'");
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
        ScriptConsole.println("Infusing pouch with: " + itemName);
    }

    private static boolean isGeyserTitanPouchAvailable() {
        return Backpack.contains(getPouchName());
    }

    private static void infusePouchWithGeyserTitanPouch(SceneObject obolisk) {
        obolisk.interact(INFUSE_POUCH_OPTION);
        ScriptConsole.println("Infusing pouch with: " + pouchName);
    }

    private static long interactWithMagestix() {
        if (Interfaces.isOpen(1265)) {
            ScriptConsole.println("Interface is open");
            ResultSet<Item> items = InventoryItemQuery.newQuery(628).ids(secondaryItem).results();
            if (!items.isEmpty()) {
                Item item = items.first();
                int slot = item.getSlot();
                MiniMenu.interact(ComponentAction.COMPONENT.getType(), 7, slot, 82903060);
                ScriptConsole.println("Buying all: " + item.getName());
                Execution.delay(random.nextLong(550, 600));
            } else {
                MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, -1, 82903072);
                ScriptConsole.println("Selecting [Sell]");
                Execution.delay(random.nextLong(550, 600));
                ResultSet<Item> items1 = InventoryItemQuery.newQuery(93).ids(1445).results();
                if (!items1.isEmpty()) {
                    Item item = items1.first();
                    int slot = item.getSlot();
                    MiniMenu.interact(ComponentAction.COMPONENT.getType(), 6, slot, 82903060);
                }
                ScriptConsole.println("Selling all: " + secondaryItemName.get(secondaryItem));
                Execution.delay(random.nextLong(550, 600));
                ScriptConsole.println("Selecting [Buy]");
                MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, -1, 82903081);
                Execution.delay(random.nextLong(550, 600));
            }
        } else {
            EntityResultSet<Npc> magestix = NpcQuery.newQuery().name("Magestix").option("Trade").results();
            if (!magestix.isEmpty()) {
                Npc nearestMagestix = magestix.nearest();
                nearestMagestix.interact("Trade");
                ScriptConsole.println("Interacting with Magestix");
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
            ScriptConsole.println("Interacting with Bank");
        } else
        if (Movement.traverse(NavPath.resolve(bankLocation)) == TraverseEvent.State.FINISHED) {
            return random.nextLong(1250, 1500);
        }
        return random.nextLong(1250, 1500);
    }
}