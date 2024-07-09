package net.botwithus.Slayer;

import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
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


import static net.botwithus.CustomLogger.log;
import static net.botwithus.Slayer.Main.setSlayerState;
import static net.botwithus.Variables.Variables.random;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class WarsRetreat {

    public static void bankingLogic() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null)
            if (player.getCoordinate().getRegionId() != 13214) {
                Execution.delay(useWarsRetreat(player));
            } else {
                Execution.delay(handleCampfire(player));
            }
        Execution.delay(RandomGenerator.nextInt(1000, 2000));
        log("We're idle!");
        Execution.delay(RandomGenerator.nextInt(1000, 2000));
    }

    private static long useWarsRetreat(LocalPlayer player) {
        if (player != null) {
            log("Used Wars Retreat: " + ActionBar.useAbility("War's Retreat Teleport"));
            Execution.delay(RandomGenerator.nextInt(5000, 6000));
            Execution.delay(handleCampfire(player));
        }
        return random.nextLong(1500, 3000);
    }

    private static long handleCampfire(LocalPlayer player) {
        if (player != null) {
            EntityResultSet<SceneObject> Campfire = SceneObjectQuery.newQuery().name("Campfire").option("Warm hands").results();

            if (!Campfire.isEmpty()) {
                SceneObject campfire = Campfire.nearest();
                if (campfire != null) {
                    campfire.interact("Warm hands");
                    log("Warming hands!");
                    Execution.delayUntil(10000, () -> {
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
                    Execution.delay(RandomGenerator.nextInt(3000, 5000));

                    boolean healthFull = Execution.delayUntil(15000, () -> player.getCurrentHealth() == player.getMaximumHealth());
                    if (healthFull) {
                        log("Player health is now full.");
                    } else {
                        log("Timed out waiting for player health to be full.");
                    }
                } else {
                    log("Bank chest is not found.");
                    return random.nextLong(1500, 3000);
                }
                if( VarManager.getVarValue(VarDomainType.PLAYER, 183) == 0) {
                    setSlayerState(Main.SlayerState.LANIAKEA);
                } else {
                    setSlayerState(Main.SlayerState.RETRIEVETASKINFO);
                }
            }
        }
        return random.nextLong(1500, 3000);
    }
}
