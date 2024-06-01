package net.botwithus.Herblore;

import net.botwithus.SnowsScript;
import net.botwithus.Variables.Variables;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.inventory.backpack;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;

import java.util.Random;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Variables.Variables.dialog;

public class Herblore {
    public SnowsScript skeletonScript; // Store a reference to SkeletonScript

    public Herblore(SnowsScript script) {
        this.skeletonScript = script;
    }

    public static HerbloreRecipe getSelectedRecipe() {
        return SharedState.selectedRecipe;
    }

    private static final Random random = new Random();

    public enum HerbloreRecipe {
        SUPREME_OVERLOADS,
        OVERLOADS,
        EXTREME_POTIONS,
        NECROMANCY_POTIONS,
        EXTREME_ATTACK,
        EXTREME_STRENGTH,
        EXTREME_DEFENCE,
        EXTREME_MAGIC,
        EXTREME_RANGING,
        EXTREME_NECROMANCY,
        SUPER_ATTACK,
        SUPER_STRENGTH,
        SUPER_DEFENCE,
        SUPER_MAGIC,
        SUPER_RANGED,
        SUPER_NECROMANCY
    }

    public static long handleHerblore(LocalPlayer player) {
        HerbloreRecipe selectedRecipe = SharedState.selectedRecipe;
        SceneObject portable = SceneObjectQuery.newQuery().name("Portable well").results().nearest();

        if (Interfaces.isOpen(1251)) {
            return random.nextLong(600, 800);
        }
        if (Interfaces.isOpen(1370)) {
            dialog(0, -1, 89784350);
            return random.nextLong(750, 1250);
        }

        switch (selectedRecipe) {
            case SUPREME_OVERLOADS:
                if (Backpack.contains("Overload (4)") && Backpack.contains("Crystal flask") && Backpack.contains("Super attack (4)") &&
                        Backpack.contains("Super strength (4)") && Backpack.contains("Super defence (4)") && Backpack.contains("Super ranging potion (4)") &&
                        Backpack.contains("Super magic potion (4)") && Backpack.contains("Super necromancy (4)")) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case OVERLOADS:
                if (Backpack.contains("Extreme attack (3)") && Backpack.contains("Extreme strength (3)") &&
                        Backpack.contains("Extreme defence (3)") && Backpack.contains("Extreme magic (3)") &&
                        Backpack.contains("Extreme ranging (3)") && Backpack.contains("Clean torstol") && Backpack.contains("Extreme necromancy (3)")) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case EXTREME_ATTACK:
                if (Backpack.contains("Super attack (3)") && Backpack.contains("Clean avantoe")) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case EXTREME_STRENGTH:
                if (Backpack.contains("Super strength (3)") && Backpack.contains("Clean dwarf weed")) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case EXTREME_DEFENCE:
                if (Backpack.contains("Super defence (3)") && Backpack.contains("Clean lantadyme")) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case EXTREME_MAGIC:
                if (Backpack.contains("Super magic potion (3)") && Backpack.contains("Ground mud runes")) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case EXTREME_RANGING:
                if (Backpack.contains("Super ranging potion (3)") && Backpack.contains("Grenwall spikes")) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case EXTREME_NECROMANCY:
                if (Backpack.contains("Super necromancy (3)") && Backpack.contains("Ground miasma rune")) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case SUPER_ATTACK:
                if (Backpack.contains("Irit potion (unf)") && Backpack.contains("Eye of newt")) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well for Super Attack.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case SUPER_STRENGTH:
                if (Backpack.contains("Kwuarm potion (unf)") && Backpack.contains("Limpwurt root")) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well for Super Strength.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case SUPER_DEFENCE:
                if (Backpack.contains("Cadantine potion (unf)") && Backpack.contains("White berries")) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well for Super Defence.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case SUPER_MAGIC:
                if (Backpack.contains("Lantadyme potion (unf)") && Backpack.contains("Potato cactus")) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well for Super Magic.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case SUPER_RANGED:
                if (Backpack.contains("Dwarf weed potion (unf)") && Backpack.contains("Wine of Zamorak")) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well for Super Ranged.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            case SUPER_NECROMANCY:
                if (Backpack.contains("Spirit weed potion (unf)") && Backpack.contains("Congealed blood")) {
                    if (portable != null && portable.interact("Mix Potions") && portable.distanceTo(player) < 5.0D) {
                        log("[Herblore] Successfully interacted with portable well for Super Necromancy.");
                        return random.nextLong(600, 800);
                    } else {
                        log("[Error] Failed to interact with portable well.");
                    }
                } else {
                    return handleBankInteraction(player);
                }
                break;
            default:
                log("[Error] Unknown recipe.");
                return random.nextLong(600, 800);
        }

        return random.nextLong(600, 800);
    }


    private static long handleBankInteraction(LocalPlayer player) {
        EntityResultSet<Npc> results = NpcQuery.newQuery().name("Banker").option("Load Last Preset from").results();
        if (!results.isEmpty()) {
            log("[Herblore] Loading last preset from banker");
            results.nearest().interact("Load Last Preset from");
            return random.nextLong(750, 1050);
        } else {
            EntityResultSet<SceneObject> chestResults = SceneObjectQuery.newQuery().name("Bank chest").option("Load Last Preset from").results();
            if (!chestResults.isEmpty()) {
                log("[Herblore] Loading last preset from bank chest");
                chestResults.nearest().interact("Load Last Preset from");
                return random.nextLong(750, 1050);
            }
        }
    return random.nextLong(750, 1050);
    }
}