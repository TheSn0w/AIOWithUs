package net.botwithus.Variables;

import net.botwithus.Agility.Agility;
import net.botwithus.Archaeology.Archeology;
import net.botwithus.Archaeology.WorldHop;
import net.botwithus.Cooking.Cooking;
import net.botwithus.Divination.Divination;
import net.botwithus.Fishing.Fishing;
import net.botwithus.Herblore.Herblore;
import net.botwithus.Misc.*;
import net.botwithus.Runecrafting.Abyss;
import net.botwithus.Runecrafting.Runecrafting;
import net.botwithus.Runecrafting.SteamRunes;
import net.botwithus.Thieving.Thieving;
import net.botwithus.Woodcutting.Woodcutting;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.script.Execution;

import java.util.ArrayList;
import java.util.List;

import static net.botwithus.Archaeology.WorldHop.hopWorldsforArchaeology;
import static net.botwithus.Combat.ArchGlacor.handleArchGlacor;
import static net.botwithus.Combat.Combat.attackTarget;
import static net.botwithus.Combat.POD.handlePOD;
import static net.botwithus.Combat.Travel.*;
import static net.botwithus.Divination.Divination.checkAccountType;
import static net.botwithus.Divination.Divination.interactWithChronicle;
import static net.botwithus.Mining.Mining.handleSkillingMining;
import static net.botwithus.Misc.Fletching.makeDinarrow;
import static net.botwithus.Misc.GemCutter.cutGems;
import static net.botwithus.Misc.LeatherCrafter.handleLeatherCrafter;
import static net.botwithus.Misc.LeatherCrafter.interactWithLeather;
import static net.botwithus.Misc.Necro.handleNecro;
import static net.botwithus.Misc.Necro.interactWithEntities;
import static net.botwithus.Misc.Smelter.*;
import static net.botwithus.Misc.UrnMaker.craftUrns;
import static net.botwithus.Runecrafting.Abyss.useAbyssRunecrafting;
import static net.botwithus.Runecrafting.SteamRunes.useSteamRunes;
import static net.botwithus.Slayer.Main.doSlayer;
import static net.botwithus.Slayer.Main.runSlayer;
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
            if (isRunecraftingActive) {
                Runecrafting.handleRunecrafting(player);
            }
            if (isRunecraftingActive && useSoulAltar) {
                Execution.delay(Runecrafting.handleSoulAltar());
            }
            if (isRunecraftingActive && useSteamRunes) {
                SteamRunes.run();
            }
            if (isRunecraftingActive && useAbyssRunecrafting) {
                Abyss.runAbyss();
            }
        }
    }

    public static void handleMining() {
        List<String> selectedRockNames = getSelectedRockNames();
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null) {
            if (shouldTravel) {
                travelToXYZ(x, y, z);
                shouldTravel = false;
                return;
            }
            if (useHintArrow) {
                travelToLocation();
                useHintArrow = false; // Reset the flag
                return;
            }
            Execution.delay(handleSkillingMining(player, selectedRockNames));
        }
    }

    public static void handleThieving() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null) {
            Execution.delay(Thieving.handleThieving(player));
        }
    }

    public static void handleWoodcutting() {
        List<String> selectedTreeNames = getSelectedTreeNames();
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null) {
            if (shouldTravel) {
                travelToXYZ(x, y, z);
                shouldTravel = false;
                return;
            }
            if (useHintArrow) {
                travelToLocation();
                useHintArrow = false;
                return;
            }
            Execution.delay(Woodcutting.handleSkillingWoodcutting(player, selectedTreeNames));
            if (crystallise) {
                Execution.delay(Woodcutting.handleit());
            }
            if (crystalliseMahogany) {
                Execution.delay(Woodcutting.handleCrystalliseMahogany());
            }
        }
    }

    public static void handleDivination() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null) {
            if (handleOnlyChonicles) {
                List<Integer> npcTypeIds = new ArrayList<>();
                if (checkAccountType(Divination.AccountType.REGULAR)) {
                    npcTypeIds.add(18205);
                    npcTypeIds.add(18204);
                } else {
                    npcTypeIds.add(18204);
                }

                for (Integer npcTypeId : npcTypeIds) {
                    EntityResultSet<Npc> chronicles = NpcQuery.newQuery().byParentType(npcTypeId).results();
                    if (!chronicles.isEmpty()) {
                        Execution.delay(interactWithChronicle(chronicles));
                        return;
                    }
                }
                if (offerChronicles && Backpack.getQuantity("Chronicle fragment") >= random.nextInt(20, 30)) {
                    Execution.delay(Divination.convertChroniclesAtRift());
                }
            } else {
                Execution.delay(Divination.handleDivination(player));
            }
        }
    }

    public static void handleFishing() {
        LocalPlayer player = Client.getLocalPlayer();
        List<String> selectedFishingLocations = getSelectedFishingLocations();
        List<String> selectedFishingActions = getSelectedFishingActions();
        if (player != null) {
            if (shouldTravel) {
                travelToXYZ(x, y, z);
                shouldTravel = false;
                return;
            }
            if (useHintArrow) {
                travelToLocation();
                useHintArrow = false; // Reset the flag
                return;
            }
            Execution.delay(Fishing.handleFishing(player, selectedFishingLocations.get(0), selectedFishingActions.get(0)));
        }
    }

    public static boolean shouldTravel = false;

    public static void handleCombat() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null) {
            if (shouldTravel) {
                travelToXYZ(x, y, z);
                shouldTravel = false;
            } else if (useHintArrow) {
                travelToLocation();
                useHintArrow = false;
            } else if (doSlayer) {
                runSlayer();
            } else if (usePOD) {
                handlePOD();
            } else if (handleArchGlacor) {
                handleArchGlacor();
            } else {
                attackTarget(player);
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
            if (shouldTravel) {
                travelToXYZ(x, y, z);
                shouldTravel = false;
                return;
            }
            if (useHintArrow) {
                travelToLocation();
                useHintArrow = false; // Reset the flag
                return;
            }
            if (hopWorldsforArchaeology) {
                WorldHop.checkForOtherPlayersAndHopWorld();
            }
            List<String> selectedArchNames = getSelectedNames();
            Execution.delay(Archeology.findSpotAnimationAndAct(player, selectedArchNames));
        }
    }


    public static void handleMisc() {
        LocalPlayer player = Client.getLocalPlayer();

        if (player != null) {
            if (isMiscActive) {
                if (makeDinarrow && !isDissasemblerActive) {
                    Execution.delay(Fletching.fletch());
                }
                if (isportermakerActive && !isDissasemblerActive) {
                    Execution.delay(PorterMaker.makePorters());
                }
                if (isdivinechargeActive && !isDissasemblerActive) {
                    Execution.delay(PorterMaker.divineCharges());
                }
                if (isCorruptedOreActive && !isDissasemblerActive) {
                    Execution.delay(CorruptedOre.mineCorruptedOre());
                }
                if (isPlanksActive && !isDissasemblerActive) {
                    Execution.delay(Planks.handleplankmaking(player));
                }
                if (isSummoningActive && usePrifddinas && !isDissasemblerActive) {
                    Execution.delay(Summoning.makePouches(player));
                } else if (isSummoningActive && !isDissasemblerActive) {
                    Execution.delay(Summoning.interactWithObolisk(player));
                }
                if (isGemCutterActive && !isDissasemblerActive) {
                    Execution.delay(cutGems());
                }
                if (isSmeltingActive && !handleGoldBar && !handleGoldGauntlets && !isDissasemblerActive) {
                    Execution.delay(handleSmelter(player));
                }
                if (isSmeltingActive && handleGoldBar && !isDissasemblerActive) {
                    Execution.delay(smeltGold(player));
                }
                if (isSmeltingActive && handleGoldGauntlets && !isDissasemblerActive) {
                    Execution.delay(smeltGoldGauntlets(player));
                }
                if (Variables.pickCaveNightshade && !isDissasemblerActive) {
                    CaveNightshade.runNightShadeLoop();
                }
                if (isSiftSoilActive && !isDissasemblerActive) {
                    Execution.delay(SiftSoil.handleSoil(player));
                }
                if(isCrystalChestActive && !isDissasemblerActive){
                    CrystalChests.openChest();
                }
                if(isMakeUrnsActive && !isDissasemblerActive){
                    Execution.delay(craftUrns(player));
                }
                if (handleHarps && !isDissasemblerActive) {
                    Execution.delay(Harps.interactwithHarps(player));
                }
                if (handleNecro && !isDissasemblerActive) {
                    Execution.delay(interactWithEntities());
                }
                if (handleLeatherCrafter && !isDissasemblerActive) {
                    Execution.delay(interactWithLeather());
                }
                if (isDissasemblerActive) {
                    if (useDisassemble) {
                        Execution.delay(Dissasembler.Dissasemble(player));
                    }
                    if (useAlchamise) {
                        Execution.delay(Dissasembler.castHighLevelAlchemy(player));
                    }
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
