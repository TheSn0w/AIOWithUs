package net.botwithus.Slayer;

import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;


import static ImGui.Skills.CombatImGui.selectedSlayerMasterIndex;
import static ImGui.Skills.CombatImGui.slayerMasters;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Slayer.Main.setSlayerState;
import static net.botwithus.Slayer.Utilities.*;
import static net.botwithus.Slayer.Utilities.DeHandleSoulSplit;
import static net.botwithus.TaskScheduler.bankPin;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class WarsRetreat {

    public static boolean slayerPointFarming = false;
    public static boolean camelWarriors = false;

    public static void bankingLogic() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null)
            if (player.getCoordinate().getRegionId() != 13214) {
                Execution.delay(useWarsRetreat(player));
            } else {
                Execution.delay(handleCampfire(player));
            }
        log("We're idle!");
    }

    private static long useWarsRetreat(LocalPlayer player) {
        if (player != null) {
            log("Used Wars Retreat: " + ActionBar.useAbility("War's Retreat Teleport"));
            Execution.delay(RandomGenerator.nextInt(5000, 6000));
            DeActivateMagicPrayer();
            DeActivateRangedPrayer();
            DeActivateMeleePrayer();
            DeHandleSoulSplit();
            clearTargetNames();
            lavaStrykewyrms = false;
            iceStrykewyrms = false;
            camelWarriors = false;
            Execution.delay(handleCampfire(player));
        }
        return random.nextLong(1500, 3000);
    }

    private static long handleCampfire(LocalPlayer player) {
        if (player != null) {
            EntityResultSet<SceneObject> Campfire = SceneObjectQuery.newQuery().name("Campfire").option("Warm hands").results();

            if (!Campfire.isEmpty() && ComponentQuery.newQuery(284).spriteId(10931).results().isEmpty()) {
                SceneObject campfire = Campfire.nearest();
                if (campfire != null) {
                    campfire.interact("Warm hands");
                    log("Warming hands!");
                    Execution.delayUntil(random.nextLong(10000, 15000), () -> {
                        ResultSet<Component> results = ComponentQuery.newQuery(284).spriteId(10931).results();
                        return !results.isEmpty();
                    });
                    ResultSet<Component> results = ComponentQuery.newQuery(284).spriteId(10931).results();
                    if (!results.isEmpty()) {
                        log("Campfire buff is now active!");
                        Execution.delay(handlePraying(player));
                    } else {
                        log("Timed out waiting for campfire buff to be active.");
                        return random.nextLong(1500, 3000);
                    }
                } else {
                    log("Campfire is not found.");
                    Execution.delay(handlePraying(player));
                }
            } else {
                log("Campfire buff is already active!");
                Execution.delay(handlePraying(player));
            }
        }
        return random.nextLong(1500, 3000);
    }

    private static long handlePraying(LocalPlayer player) {
        if (player != null) {
            EntityResultSet<SceneObject> altarOfWarResults = SceneObjectQuery.newQuery().name("Altar of War").results();

            if (player.getPrayerPoints() < Skills.PRAYER.getLevel() * 100) {
                if (!altarOfWarResults.isEmpty()) {
                    SceneObject altar = altarOfWarResults.nearest();
                    if (altar != null && altar.interact("Pray")) {
                        log("Praying at Altar of War!");
                        Execution.delayUntil(random.nextLong(10000, 15000), () -> getLocalPlayer().getPrayerPoints() == Skills.PRAYER.getLevel() * 100);
                        if (player.getPrayerPoints() == Skills.PRAYER.getLevel() * 100) {
                            log("Prayer points are now full.");
                            Execution.delay(handleBank(player));
                        } else {
                            log("Timed out waiting for prayer points to be full.");
                            return random.nextLong(1500, 3000);
                        }
                    }
                } else {
                    log("Altar of War is not found.");
                    Execution.delay(handleBank(player));
                }
            } else {
                log("Prayer points are already full.");
                Execution.delay(handleBank(player));
            }
        }
        return random.nextLong(1500, 3000);
    }



    private static long handleBank(LocalPlayer player) {
        if (player != null) {
            EntityResultSet<SceneObject> BankChest = SceneObjectQuery.newQuery().name("Bank chest").results();
            if (!BankChest.isEmpty()) {
                SceneObject bank = BankChest.nearest();
                if (bank != null) {
                    bank.interact("Load Last Preset from");
                    log("Loading preset!");
                    Execution.delayUntil(random.nextLong(5000, 10000), () -> player.getCoordinate().equals(new Coordinate(3299, 10131, 0)));
                    if (Interfaces.isOpen(759)) {
                        bankPin();
                    }

                    if (VarManager.getVarValue(VarDomainType.PLAYER, 183) == 0) {
                        String selectedMaster = slayerMasters[selectedSlayerMasterIndex.get()];
                        if (slayerPointFarming) {
                            int varValue = VarManager.getVarValue(VarDomainType.PLAYER, 10077);
                            int lastDigit = varValue % 10;
                            if (lastDigit >= 0 && lastDigit <= 8) {
                                setSlayerState(Main.SlayerState.JACQUELYN);
                            } else if (lastDigit == 9) {
                                setSlayerState(Main.SlayerState.valueOf(selectedMaster.toUpperCase()));
                            }
                        } else {
                            switch (selectedMaster) {
                                case "Jacquelyn":
                                    setSlayerState(Main.SlayerState.JACQUELYN);
                                    break;
                                case "Mazcha":
                                    setSlayerState(Main.SlayerState.MAZCHNA);
                                    break;
                                case "Kuradal":
                                    setSlayerState(Main.SlayerState.KURADAL);
                                    break;
                                case "Laniakea":
                                    setSlayerState(Main.SlayerState.LANIAKEA);
                                    break;
                                case "Mandrith":
                                    setSlayerState(Main.SlayerState.MANDRITH);
                                    break;
                                default:
                                    log("Invalid slayer master selected.");
                                    break;
                            }
                        }
                    } else {
                        setSlayerState(Main.SlayerState.RETRIEVETASKINFO);
                    }
                }
            }
        }
        return random.nextLong(1500, 3000);
    }


}
