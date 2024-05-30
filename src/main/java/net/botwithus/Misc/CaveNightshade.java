package net.botwithus.Misc;

import net.botwithus.SnowsScript;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.ScriptConsole;

import java.util.*;

public class CaveNightshade {

    private final Random random;
    private NightShadeState nightShadeState = NightShadeState.TRAVERSING;

    public CaveNightshade(SnowsScript mainScript) {
        this.random = new Random();
    }

    public enum NightShadeState {
        TRAVERSING,
        PICKING,
        MAKEPORTER,
    }

    public static final Map<String, Integer> NightshadePicked = new HashMap<>();

    private final List<Coordinate> nightshadeCoordinates = Arrays.asList(
            new Coordinate(2532, 9461, 0),
            new Coordinate(2530, 9462, 0),
            new Coordinate(2533, 9464, 0),
            new Coordinate(2534, 9467, 0),
            new Coordinate(2530, 9468, 0),
            new Coordinate(2529, 9467, 0),
            new Coordinate(2528, 9465, 0)
    );

    public void runNightShadeLoop() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN || SnowsScript.getBotState() == SnowsScript.BotState.IDLE) {
            Execution.delay(random.nextLong(3000, 7000));
            return;
        }
        handleNightShadeState();
    }

    private void handleNightShadeState() {
        LocalPlayer player = Client.getLocalPlayer();
        switch (nightShadeState) {
            case TRAVERSING -> {
                Execution.delay(handleTraversals(player));
            }
            case MAKEPORTER -> {
                Execution.delay(makePorter());
            }
            case PICKING -> {
                Execution.delay(handlePicking(player));
            }
        }
    }

    private long handleTraversals(LocalPlayer player) {
        EntityResultSet<SceneObject> nightshade = SceneObjectQuery.newQuery().name("Cave nightshade").option("Pick").results();
        EntityResultSet<SceneObject> caveEntrance = SceneObjectQuery.newQuery().name("Cave entrance").option("Enter").results();
        Coordinate destination = new Coordinate(2524, 3070, 0);

        if (nightshade.isEmpty()) {
            ScriptConsole.println("We are traversing to the destination");
            if (Movement.traverse(NavPath.resolve(destination)) == TraverseEvent.State.FINISHED && !caveEntrance.isEmpty()) {
                ScriptConsole.println("We have arrived at the destination");
                caveEntrance.nearest().interact("Enter");
                Execution.delay(random.nextLong(5000, 7500));
                nightShadeState = NightShadeState.PICKING;
            }
        } else {
            nightShadeState = NightShadeState.PICKING;
            return random.nextLong(1500, 3000);
        }
        return random.nextLong(1500, 3000);
    }

    private long handlePicking(LocalPlayer player) {
        ComponentQuery porterQuery = ComponentQuery.newQuery(284).spriteId(51490);
        if (porterQuery.results().isEmpty()) {
            ScriptConsole.println("No porter found in equipment.");
            nightShadeState = NightShadeState.MAKEPORTER;
            return random.nextLong(1000, 3000);
        }

        EntityResultSet<SceneObject> nightshade = SceneObjectQuery.newQuery()
                .interactId(114921)
                .hidden(false)
                .option("Pick")
                .results();

        SceneObject nightshadeObject = nightshade.nearest();

        if (player.isMoving()) {
            ScriptConsole.println("Player is currently moving. Waiting...");
            return random.nextLong(1000, 3000);
        }

        if (nightshadeObject == null) {
            ScriptConsole.println("No nightshade objects found.");
            return random.nextLong(1000, 3000);
        }

        if (!nightshadeCoordinates.contains(nightshadeObject.getCoordinate())) {
            ScriptConsole.println("Nightshade object is not at a specified coordinate.");
            return random.nextLong(1000, 3000);
        }

        /*ScriptConsole.println("Picking nightshade at " + nightshadeObject.getCoordinate());*/ // debugging

        if (nightshadeObject.interact("Pick")) {
            ScriptConsole.println("Successfully interacted with nightshade.");
        } else {
            ScriptConsole.println("Failed to interact with nightshade.");
            return random.nextLong(1000, 3000);
        }

        return random.nextLong(2000, 4000);
    }

    private long makePorter() {
        if (Backpack.getQuantity("Memory shard") <= 50) {
            ScriptConsole.println("Not enough memory shards to make a porter.");
            return handleBanking();
        }

        openMemoryShardInterface();

        if (!Interfaces.isOpen(1370)) {
            return random.nextLong(1000, 1500);
        }

        if (VarManager.getVarValue(VarDomainType.PLAYER, 1170) != 51487) {
            interactWithPorter();
        }

        interactWithDialogue();

        if (!Backpack.contains("Sign of the porter VII")) {
            return random.nextLong(1000, 1500);
        }

        wearPorter();
        nightShadeState = NightShadeState.PICKING;
        return random.nextLong(1500, 2000);
    }

    private void openMemoryShardInterface() {
        Backpack.interact("Memory shard", "Open");
        ScriptConsole.println("Opening memory shard interface.");
        Execution.delayUntil(5000, () -> Interfaces.isOpen(1370));
    }

    private void interactWithPorter() {
        ScriptConsole.println("Var value is incorrect. Interacting with Porter.");
        MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, 25, 89849878);
        Execution.delay(random.nextLong(650, 800));
    }

    private void interactWithDialogue() {
        ScriptConsole.println("Interacting with dialogue.");
        MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 89784350);
        Execution.delayUntil(15000, () -> Backpack.contains("Sign of the porter VII"));
    }

    private void wearPorter() {
        ScriptConsole.println("Sign of the porter VII is in inventory. Wearing it.");
        Backpack.interact("Sign of the porter VII", "Wear");
        Execution.delay(random.nextLong(650, 800));
    }
    private long handleBanking() {
        EntityResultSet<Npc> banker = NpcQuery.newQuery().name("Banker").option("Bank").results();
        if (banker.isEmpty()) {
            ScriptConsole.println("No banker found, teleporting to Max Guild.");
            ActionBar.useAbility("Max Guild Teleport");
            Execution.delay(random.nextLong(5000, 7500));

            banker = NpcQuery.newQuery().name("Banker").option("Bank").results();
        }

        Npc bankObject = banker.nearest();
        if (bankObject.interact("Load Last Preset from")) {
            ScriptConsole.println("Interacting with banker.");
            Execution.delay(random.nextLong(4500, 5000));
        } else {
            ScriptConsole.println("Failed to interact with banker.");
            return random.nextLong(1000, 3000);
        }

        nightShadeState = NightShadeState.TRAVERSING;
        return random.nextLong(2000, 4000);
    }
}
