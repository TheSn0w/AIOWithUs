package net.botwithus.Variables;

import net.botwithus.Agility.Agility;
import net.botwithus.Archaeology.Archeology;
import net.botwithus.Cooking.Cooking;
import net.botwithus.Divination.Divination;
import net.botwithus.Fishing.Fishing;
import net.botwithus.Herblore.Herblore;
import net.botwithus.Misc.*;
import net.botwithus.Runecrafting.Runecrafting;
import net.botwithus.Thieving.Thieving;
import net.botwithus.Woodcutting.Woodcutting;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.script.Execution;

import java.util.List;

import static net.botwithus.Combat.ArchGlacor.handleArchGlacor;
import static net.botwithus.Combat.Combat.attackTarget;
import static net.botwithus.Combat.Loot.LootEverything;
import static net.botwithus.Combat.POD.handlePOD;
import static net.botwithus.Combat.Radius.enableRadiusTracking;
import static net.botwithus.Combat.Radius.ensureWithinRadius;
import static net.botwithus.Mining.Mining.handleSkillingMining;
import static net.botwithus.Misc.UrnMaker.craftUrns;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.isMakeUrnsActive;

public class Runnables {

    public static void handleHerblore() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null) {
            Execution.delay(Herblore.handleHerblore(player));
        }
    }
    public static void handleRunecrafting() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null) {
            if (isRunecraftingActive && !soulAltar) {
                Runecrafting.handleRunecrafting(player);
            }
            if (isRunecraftingActive && soulAltar) {
                Execution.delay(Runecrafting.handleEdgevillebanking());
            }
        }
    }

    public static  void handleMining() {
        List<String> selectedRockNames = getSelectedRockNames();
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null) {
            Execution.delay(handleSkillingMining(player, selectedRockNames));
        }
    }

    public static  void handleThieving() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null) {
            Execution.delay(Thieving.handleThieving(player));
        }
    }

    public static  void handleWoodcutting() {
        List<String> selectedTreeNames = getSelectedTreeNames();
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null) {
            Execution.delay(Woodcutting.handleSkillingWoodcutting(player, selectedTreeNames));
            if (crystallise) {
                Execution.delay(Woodcutting.handleit());
            }
            if (crystalliseMahogany) {
                Execution.delay(Woodcutting.handleCrystalliseMahogany());
            }
        }
    }

    public static  void handleDivination() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null) {
            Execution.delay(Divination.handleDivination(player));
        }
    }

    public static  void handleFishing() {
        LocalPlayer player = Client.getLocalPlayer();
        List<String> selectedFishingLocations = getSelectedFishingLocations();
        List<String> selectedFishingActions = getSelectedFishingActions();
        if (player != null) {
            Execution.delay(Fishing.handleFishing(player, selectedFishingLocations.get(0), selectedFishingActions.get(0)));
        }
    }

    public static  void handleCombat() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null) {
            if (enableRadiusTracking) {
                Execution.delay(ensureWithinRadius(player));
            }
            if (usePOD) {
                handlePOD();
            }
            if (!usePOD && !handleArchGlacor) {
                Execution.delay(attackTarget(player));
            }
            if (handleArchGlacor) {
                Execution.delay(handleArchGlacor());
            }
            if (interactWithLootAll) {
                LootEverything();
            }
        }
    }

    public static  void handleCooking() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null) {
            if (makeWines) {
                Cooking cooking = new Cooking();
                Execution.delay(cooking.useGrapesOnJugOfWater());
            } else {
                Cooking cooking = new Cooking();
                Execution.delay(cooking.handleCooking());
            }
        }
    }

    public static  void handleArcheology() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null) {
            List<String> selectedArchNames = getSelectedNames();
            Execution.delay(Archeology.findSpotAnimationAndAct(player, selectedArchNames));
        }
    }


    public static void handleMisc() {
        LocalPlayer player = Client.getLocalPlayer();

        if (player != null) {
            if (isMiscActive) {
                if (isportermakerActive) {
                    Execution.delay(PorterMaker.makePorters());
                }
                if (isdivinechargeActive) {
                    Execution.delay(PorterMaker.divineCharges());
                }
                if (isCorruptedOreActive) {
                    Execution.delay(CorruptedOre.mineCorruptedOre());
                }
                if (isPlanksActive) {
                    Execution.delay(Planks.handleplankmaking(player));
                }
                if (isSummoningActive && usePrifddinas) {
                    Execution.delay(Summoning.makePouches(player));
                } else if (isSummoningActive) {
                    Execution.delay(Summoning.interactWithObolisk(player));
                }
                if (isDissasemblerActive) {
                    if (useDisassemble) {
                        Execution.delay(Dissasembler.Dissasemble(player));
                    }
                    if (useAlchamise) {
                        Execution.delay(Dissasembler.castHighLevelAlchemy(player));
                    }
                }
                if (isGemCutterActive) {
                    Execution.delay(GemCutter.cutGems());
                }
                if (isSmeltingActive) {
                    Execution.delay(Smelter.handleSmelter(player));
                }
                if (Variables.pickCaveNightshade) {
                    CaveNightshade.runNightShadeLoop();
                }
                if (isSiftSoilActive) {
                    Execution.delay(SiftSoil.handleSoil(player));
                }
                if(isCrystalChestActive){
                    CrystalChests.openChest();
                }
                if(isMakeUrnsActive){
                    Execution.delay(craftUrns(player));
                }
            }
        }
    }
    public static  void handleSkillingAgility() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null) {
            Execution.delay(Agility.handleSkillingAgility(player));
        }
    }
}
