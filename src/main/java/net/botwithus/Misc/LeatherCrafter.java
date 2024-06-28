package net.botwithus.Misc;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import org.jetbrains.annotations.Nullable;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.TaskScheduler.shutdown;
import static net.botwithus.Variables.Variables.player;
import static net.botwithus.Variables.Variables.random;

public class LeatherCrafter {

    public static boolean handleLeatherCrafter = false;
    public static boolean makeBody = false;
    public static boolean makeChaps = false;
    public static boolean makeVambraces = false;
    public static boolean makeShields = false;
    public static boolean tanLeather = false;

    public static long interactWithLeather() {
        if (tanLeather) {
            Execution.delay(leatherTanning());
        } else {
            if (player.isMoving() || player.getAnimationId() != -1 || Interfaces.isOpen(1251)) {
                return random.nextLong(750, 1250);
            }
            if (Interfaces.isOpen(1370)) {
                selectInterface();
                return random.nextLong(750, 1250);
            }
            Item leather = InventoryItemQuery.newQuery(93).option("Craft").results().first();
            if (leather != null) {
                int leatherCount = Backpack.getCount(leather.getName());
                if ((makeBody && leatherCount >= 3) || (makeChaps && leatherCount >= 2) || (makeVambraces && leatherCount >= 1) || (makeShields && leatherCount >= 4)) {
                    Backpack.interact(leather.getName(), "Craft");
                    log("[Success] Successfully interacted with leather.");
                    return random.nextLong(750, 1250);
                } else {
                    log("[Error] Not enough leather to craft.");
                    Execution.delay(interactwithBank());
                }
            } else {
                log("[Error] Leather not found.");
                Execution.delay(interactwithBank());
            }
        }
        return random.nextLong(750, 1250);
    }


    private static void selectInterface() {
        Component craftPouchesInterface = ComponentQuery.newQuery(1370).results().first();
        if (craftPouchesInterface == null) {
            log("[Error] Interface not found.");
            return;
        }

        int resourcesCanMake = VarManager.getVarValue(VarDomainType.PLAYER, 8847);
        if (resourcesCanMake <= 0) {
            log("[Caution] Interacting with bank.");
            return;
        }

        boolean interact = MiniMenu.interact(16, 0, -1, 89784350);
        if (interact) {
            log("[Success] Successfully interacted with Craft!");
            Execution.delay(random.nextLong(750, 1250));
        } else {
            log("[Error] Failed to create pouches.");
        }
    }

    private static long interactwithBank() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null) {

            EntityResultSet<Npc> npcResults = NpcQuery.newQuery().option("Load Last Preset from").results();
            EntityResultSet<SceneObject> objectResults = SceneObjectQuery.newQuery().option("Load Last Preset from").results();

            Npc nearestNpc = npcResults.nearest();
            SceneObject nearestObject = objectResults.nearest();

            if (nearestNpc != null && nearestObject != null) {
                if (nearestNpc.getCoordinate().distanceTo(player.getCoordinate()) <= nearestObject.getCoordinate().distanceTo(player.getCoordinate())) {
                    log("[Crafter] Loading last preset from " + nearestNpc.getName());
                    nearestNpc.interact("Load Last Preset from");
                } else {
                    log("[Crafter] Loading last preset from " + nearestObject.getName());
                    nearestObject.interact("Load Last Preset from");
                }
                return random.nextLong(750, 1050);
            } else if (nearestNpc != null) {
                log("[Crafter] Loading last preset from " + nearestNpc.getName());
                nearestNpc.interact("Load Last Preset from");
                return random.nextLong(750, 1050);
            } else if (nearestObject != null) {
                log("[Crafter] Loading last preset from " + nearestObject.getName());
                nearestObject.interact("Load Last Preset from");
                return random.nextLong(750, 1050);
            }
        }
        return random.nextLong(750, 1050);
    }

    private static long leatherTanning() {
        if (player.isMoving() || player.getAnimationId() != -1 || Interfaces.isOpen(1251)) {
            return random.nextLong(750, 1250);
        }
        if (Interfaces.isOpen(1370)) {
            selectInterface();
            return random.nextLong(750, 1050);
        }
        EntityResultSet<SceneObject> portable = SceneObjectQuery.newQuery().name("Portable crafter").option("Tan Leather").results();
        Item leather = null;
        for (Item item : Backpack.getItems()) {
            if (item.getName().contains("leather")) {
                leather = item;
                break;
            }
        }

        if (leather != null) {
            interactwithBank();
        } else {
            if (!portable.isEmpty()) {
                portable.nearest().interact("Tan Leather");
                log("[Success] Successfully interacted with portable crafter.");
            }
        }
        return random.nextLong(750, 1050);
    }


}
