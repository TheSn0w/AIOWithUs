package net.botwithus.Slayer;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Distance;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.Slayer.Main.doSlayer;
import static net.botwithus.Slayer.Main.setSlayerState;
import static net.botwithus.Slayer.Utilities.*;
import static net.botwithus.Slayer.Utilities.ActivateSoulSplit;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class NPCs {


    public static long lavaStrykewyrms() {
        LocalPlayer player = getLocalPlayer();
        if (player == null || (player.hasTarget() && player.getFollowing() != null || player.isMoving())) {
            return random.nextLong(400, 600);
        }
        EntityResultSet<Npc> mounds = NpcQuery.newQuery().byType(2417).option("Investigate").results();
        Npc strykewyrm = NpcQuery.newQuery().byType(20630).results().nearestTo(player);

        if (strykewyrm != null && strykewyrm.getCurrentHealth() > 0) {
            log("[Lava] Strykewyrm is being followed by the player and has health greater than 0.");
            strykewyrm.interact("Attack");
            Execution.delay(random.nextLong(1000, 2000));
            return 0;
        }

        if (!mounds.isEmpty()) {
            Npc nearestMound = mounds.nearest();
            log("[Lava] Interacting with the nearest mound.");
            nearestMound.interact("Investigate");
            if (Distance.between(nearestMound.getCoordinate(), player.getCoordinate()) > 15.0D && ActionBar.containsAbility("Surge") && ActionBar.getCooldown("Surge") == 0) {
                Execution.delay(random.nextLong(600, 700));
                log("[Lava] Used Surge: " + ActionBar.useAbility("Surge"));
                Execution.delay(random.nextLong(200, 250));
                nearestMound.interact("Investigate");
            }
            if (doSlayer) {
                lavaStrykewyrms = true;
                ActivateSoulSplit();
                setSlayerState(Main.SlayerState.COMBAT);
            }
            return random.nextLong(1000, 2000);
        } else {
            if (Movement.traverse(NavPath.resolve(new Coordinate(3033, 3823, 0))) == TraverseEvent.State.FINISHED) {
                log("[Ice] Traversed to Lava Strykewyrms location.");
                if (doSlayer) {
                    lavaStrykewyrms = true;
                    ActivateSoulSplit();
                    setSlayerState(Main.SlayerState.COMBAT);
                }
            } else {
                log("[Ice] Failed to traverse to Lava Strykewyrms location.");
            }
        }
        return 0;
    }

    public static long iceStrykewyrms() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null || (player.hasTarget() && player.getFollowing() != null || player.isMoving())) {
            return random.nextLong(400, 600);
        }

        Coordinate playerLocation = player.getCoordinate();
        Coordinate areaTopRight = new Coordinate(3076, 3816, 0);
        Coordinate areaBottomLeft = new Coordinate(3044, 3797, 0);
        EntityResultSet<Npc> mounds = NpcQuery.newQuery().byType(9462).option("Investigate").results();
        Npc strykewyrm = NpcQuery.newQuery().byType(9463).results().nearestTo(player);

        if (strykewyrm != null && strykewyrm.getCurrentHealth() > 0) {
            log("[Ice] Strykewyrm is being followed by the player and has health greater than 0.");
            strykewyrm.interact("Attack");
            Execution.delay(random.nextLong(1000, 2000));
            return 0;
        }

        if (!mounds.isEmpty()) {
            Npc nearestMound = mounds.nearest();
            log("[Ice] Interacting with the nearest mound.");
            nearestMound.interact("Investigate");
            if (Distance.between(nearestMound.getCoordinate(), player.getCoordinate()) > 15.0D && ActionBar.containsAbility("Surge") && ActionBar.getCooldown("Surge") == 0) {
                Execution.delay(random.nextLong(600, 700));
                log("[Ice] Used Surge: " + ActionBar.useAbility("Surge"));
                Execution.delay(random.nextLong(200, 250));
                nearestMound.interact("Investigate");
            }
            if (doSlayer) {
                iceStrykewyrms = true;
                ActivateSoulSplit();
                setSlayerState(Main.SlayerState.COMBAT);
            }
            return random.nextLong(1000, 2000);
        } else {
            if (playerLocation.getArea().contains(areaBottomLeft) && playerLocation.getArea().contains(areaTopRight)) {
                log("[Ice] Player is inside the area.");
                if (mounds.isEmpty()) {
                    log("[Ice] No mounds found. Waiting for mounds to appear...");
                    Execution.delayUntil(random.nextLong(3500, 6000), () -> !NpcQuery.newQuery().byType(9463).option("Investigate").results().isEmpty());
                }
            } else {
                log("[Ice] Player is not inside the area. Moving to a random walkable coordinate within the area...");
                Coordinate randomWalkableCoordinate = new Area.Rectangular(areaBottomLeft, areaTopRight).getRandomWalkableCoordinate();
                if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                    log("[Ice] Successfully moved to the area.");
                    if (doSlayer) {
                        iceStrykewyrms = true;
                        ActivateSoulSplit();
                        setSlayerState(Main.SlayerState.COMBAT);
                    }
                } else {
                    log("[Ice] Failed to move to the area.");
                }
            }
        }
        return 0;
    }

    public static void Vinecrawlers(LocalPlayer player) {
        Coordinate standstoneLocation = new Coordinate(2221, 3056, 0);
        Coordinate standstoneLocation2 = new Coordinate(1374, 5538, 0);
        Coordinate vineCrawlers = new Coordinate(1321, 5607, 0);

        Area area = createAreaAroundCoordinate(vineCrawlers);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        Npc vinecrawler = NpcQuery.newQuery().name("Vinecrawler").results().nearest();
        if (vinecrawler != null) {
            log("Vinecrawler found, proceeding to attack.");
            addTargetName("vinecrawler");
            ActivateMagicPrayer();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Vinecrawler not found, proceeding to Standstone.");
            if (Movement.traverse(NavPath.resolve(standstoneLocation)) == TraverseEvent.State.FINISHED) {
                EntityResultSet<SceneObject> standstone = SceneObjectQuery.newQuery().name("Standstone").option("Inspect").results();
                if (!standstone.isEmpty()) {
                    log("Standstone found, proceeding to inspect.");
                    standstone.nearest().interact("Inspect");
                    Execution.delayUntil(5000, () -> player.getCoordinate().equals(standstoneLocation2));
                    log("Standstone inspected, proceeding to Vinecrawler.");
                    if (player.getCoordinate().equals(standstoneLocation2)) {
                        log("Traversing to Vinecrawler location.");
                        Movement.traverse(NavPath.resolve(randomWalkableCoordinate));
                        log("Traversed to Vinecrawler location.");
                        addTargetName("vinecrawler");
                        handleMultitarget = false;
                        setSlayerState(Main.SlayerState.COMBAT);
                        ActivateMagicPrayer();
                    }
                }
            }
        }
    }

    public static void risenGhosts(LocalPlayer player) {
        Coordinate cryptEntrance = new Coordinate(3291, 3610, 0);
        Npc ghostResults = NpcQuery.newQuery().name("Risen ghost").results().nearest();

        while (ghostResults == null) {
            log("Risen ghost not found, proceeding to Crypt entrance.");
            if (Movement.traverse(NavPath.resolve(cryptEntrance)) == TraverseEvent.State.FINISHED) {
                EntityResultSet<SceneObject> cryptDoor = SceneObjectQuery.newQuery().name("Wilderness Crypt Entrance").option("Enter").results();
                if (!cryptDoor.isEmpty()) {
                    SceneObject door = cryptDoor.nearest();
                    if (door != null) {
                        log("Wilderness Crypt Entrance found, proceeding to Enter.");
                        door.interact("Enter");
                        Execution.delay(random.nextLong(6500, 7500));
                    } else {
                        log("Crypt door is null.");
                    }
                }
            }
            ghostResults = NpcQuery.newQuery().name("Risen ghost").results().nearest();

        }

        log("Risen ghost found, proceeding to attack.");
        addTargetName("ghost");
        handleMultitarget = true;
        ActivateMagicPrayer();
        setSlayerState(Main.SlayerState.COMBAT);
    }

    public static void GanodermicCreatures(LocalPlayer player) {
        Coordinate ganodermicLocation = new Coordinate(4634, 5448, 0);
        Npc ganodermicCreature = NpcQuery.newQuery().name("Ganodermic beast").results().nearest();

        Area area = createAreaAroundCoordinate(ganodermicLocation);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        if (ganodermicCreature != null) {
            log("Ganodermic beast found, proceeding to attack.");
            addTargetName("Ganodermic");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Ganodermic beast not found, proceeding to Ganodermic location.");
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                ActivateSoulSplit();
                log("Traversed to Ganodermic location.");
                addTargetName("Ganodermic");
                handleMultitarget = true;
                setSlayerState(Main.SlayerState.COMBAT);

            }
        }
    }

    public static void darkBeasts(LocalPlayer player) {
        Coordinate DungeonEntrance = new Coordinate(1685, 5288, 1);
        Coordinate delayCoordinate1 = new Coordinate(1661, 5257, 0);
        Coordinate delayCoordinate2 = new Coordinate(1641, 5268, 0);
        Coordinate delayCoordinate4 = new Coordinate(1650, 5281, 0);
        Coordinate delayCoordinate5 = new Coordinate(1651, 5281, 0);
        Coordinate delayCoordinate6 = new Coordinate(1652, 5281, 0);
        Coordinate delayCoordinate7 = new Coordinate(1651, 5280, 0);
        Npc DarkBeasts = NpcQuery.newQuery().name("Dark beast").results().nearest();


        if (DarkBeasts != null) {
            log("Dark beast found, proceeding to attack.");
            addTargetName("dark beast");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Dark beast not found, proceeding to Dungeon entrance.");
            if (Movement.traverse(NavPath.resolve(DungeonEntrance)) == TraverseEvent.State.FINISHED) {
                while (!player.getCoordinate().equals(delayCoordinate1)) {
                    EntityResultSet<SceneObject> cave = SceneObjectQuery.newQuery().name("Cave").option("Enter").results();
                    if (!cave.isEmpty()) {
                        SceneObject nearestCave = cave.nearest();
                        if (nearestCave != null) {
                            log("Cave found, proceeding to Enter.");
                            nearestCave.interact("Enter");
                        } else {
                            log("Cave not found.");
                        }
                    }
                    Execution.delay(random.nextLong(1500, 2500));
                }

                while (!player.getCoordinate().equals(delayCoordinate2)) {
                    EntityResultSet<SceneObject> Gap = SceneObjectQuery.newQuery().id(47237).option("Run-across").results();
                    if (!Gap.isEmpty()) {
                        SceneObject nearestGap = Gap.nearest();
                        if (nearestGap != null) {
                            log("Gap found, proceeding to Run-across.");
                            nearestGap.interact("Run-across");
                        } else {
                            log("Gap not found.");
                        }
                    }
                    Execution.delay(random.nextLong(4500, 7500));
                }

                Movement.walkTo(delayCoordinate7.getX(), delayCoordinate7.getY(), false);
                Execution.delayUntil(10000, () -> player.getCoordinate().equals(delayCoordinate7));
                EntityResultSet<SceneObject> barrier = SceneObjectQuery.newQuery().id(47236).option("Pass").results();
                if (!barrier.isEmpty()) {
                    SceneObject nearestBarrier = barrier.nearest();
                    if (nearestBarrier != null) {
                        log("Barrier found, proceeding to Pass.");
                        nearestBarrier.interact("Pass");
                    } else {
                        log("Barrier not found.");
                    }
                }
                Execution.delayUntil(10000, () -> player.getCoordinate().equals(delayCoordinate6) || player.getCoordinate().equals(delayCoordinate5) || player.getCoordinate().equals(delayCoordinate4));
            }

            addTargetName("dark beast");
            handleMultitarget = true;
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        }
    }


    public static void crystalShapeshifters(LocalPlayer player) {
        Coordinate worldGateCoords = new Coordinate(2367, 3358, 0);
        Coordinate crystalShapeshifterCoords = new Coordinate(4067, 6616, 0);

        Area area = createAreaAroundCoordinate(crystalShapeshifterCoords);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        Npc shapeshifterResults = NpcQuery.newQuery().name("Crystal Shapeshifter").results().nearest();

        if (shapeshifterResults != null) {
            log("Crystal Shapeshifter found, proceeding to attack.");
            addTargetName("crystal shapeshifter");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Crystal Shapeshifter not found, proceeding to World Gate.");
            if (Movement.traverse(NavPath.resolve(worldGateCoords)) == TraverseEvent.State.FINISHED) {
                log("Traversed to World Gate.");
                EntityResultSet<SceneObject> worldGate = SceneObjectQuery.newQuery().name("World Gate").option("Enter").results();
                if (!worldGate.isEmpty()) {
                    SceneObject nearestWorldGate = worldGate.nearest();
                    if (nearestWorldGate != null) {
                        log("World Gate found, proceeding to Enter.");
                        nearestWorldGate.interact("Enter");
                        Execution.delayUntil(random.nextLong(8000, 10000), () -> Interfaces.isOpen(847));
                        if (Interfaces.isOpen(847)) {
                            Execution.delay(random.nextLong(1000, 2000));
                            log("Interface is open, selecting option Yes.");
                            dialog(0, -1, 55509014);
                            Execution.delay(random.nextLong(6500, 7500));
                        }
                        log("Traversing to Crystal shapeshifter location.");
                        if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                            addTargetName("crystal shapeshifter");
                            ActivateSoulSplit();
                            handleMultitarget = false;
                            setSlayerState(Main.SlayerState.COMBAT);

                        } else {
                            log("Failed to traverse to Crystal Shapeshifter location.");
                        }
                    } else {
                        log("World Gate not found.");
                    }
                }
            }
        }
    }

    public static void nodonDragonkin(LocalPlayer player) {
        Coordinate dragonkinLocation = new Coordinate(1706, 1248, 0);
        Npc dragonkin = NpcQuery.newQuery().name("Nodon guard").results().nearest();

        Area area = createAreaAroundCoordinate(dragonkinLocation);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        if (dragonkin != null) {
            log("Nodon dragonkin found, proceeding to attack.");
            addTargetName("nodon");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Nodon dragonkin not found, proceeding to Dragonkin location.");
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Dragonkin location.");
                addTargetName("nodon");
                ActivateSoulSplit();
                handleMultitarget = false;
                setSlayerState(Main.SlayerState.COMBAT);

            }
        }
    }

    public static void soulDevourers(LocalPlayer player) {
        Coordinate soulDevourersCoords = new Coordinate(3290, 2708, 0);
        Coordinate checkpoint0 = new Coordinate(2384, 6792, 3);
        Coordinate checkpoint1 = new Coordinate(2405, 6863, 1);
        Coordinate checkpoints1 = new Coordinate(2404, 6856, 3);
        Coordinate checkpoints2 = new Coordinate(2440, 6869, 1);
        Npc salawaAkhResults = NpcQuery.newQuery().name("Salawa akh").results().nearest();


        if (salawaAkhResults != null) {
            log("Salawa akh found, proceeding to attack.");
            addTargetName("Salawa akh");
            ActivateMeleePrayer();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Salawa akh not found, proceeding to Soul Devourers location.");
            if (Movement.traverse(NavPath.resolve(soulDevourersCoords)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Soul Devourers location.");
                EntityResultSet<SceneObject> dungeonEntrance = SceneObjectQuery.newQuery().name("Dungeon entrance").option("Enter").results();
                if (Backpack.contains(40303)) {
                    if (dungeonEntrance.isEmpty()) {
                        log("Dungeon entrance not found.");
                    } else {
                        SceneObject nearestDungeonEntrance = dungeonEntrance.nearest();
                        if (nearestDungeonEntrance != null) {
                            log("Dungeon entrance found, proceeding to Enter.");
                            nearestDungeonEntrance.interact("Enter");
                            Execution.delayUntil(random.nextLong(8000, 10000), () -> player.getCoordinate().equals(checkpoint0));
                            if (player.getCoordinate().equals(checkpoint0)) {
                                if (Movement.traverse(NavPath.resolve(checkpoints1)) == TraverseEvent.State.FINISHED) {
                                    EntityResultSet<SceneObject> rope = SceneObjectQuery.newQuery().name("Rope").option("Climb down").results();
                                    if (!rope.isEmpty()) {
                                        SceneObject nearestRope = rope.nearest();
                                        if (nearestRope != null) {
                                            log("Rope found, proceeding to Climb down.");
                                            nearestRope.interact("Climb down");
                                            Execution.delayUntil(random.nextLong(8000, 10000), () -> player.getCoordinate().equals(checkpoint1));
                                            if (player.getCoordinate().equals(checkpoint1)) {
                                                if (Movement.traverse(NavPath.resolve(checkpoints2)) == TraverseEvent.State.FINISHED) {
                                                    log("Traversed to Checkpoints.");
                                                    addTargetName("Salawa akh");
                                                    handleMultitarget = true;
                                                    ActivateMeleePrayer();
                                                    setSlayerState(Main.SlayerState.COMBAT);

                                                }
                                            }
                                        } else {
                                            log("Rope not found.");
                                        }
                                    } else {
                                        log("Rope not found.");

                                    }
                                }
                                Movement.traverse(NavPath.resolve(checkpoints2));
                                log("Traversed to Checkpoints.");
                            }
                        } else {
                            log("Dungeon entrance not found.");
                        }
                    }

                } else {
                    log("We do not have any Feather of Ma'at.");
                }
            }
        }
    }


    public static void mithrilDragons(LocalPlayer player) {
        Coordinate MithrilDragonCoords = new Coordinate(1765, 5337, 1);
        Npc mithrils = NpcQuery.newQuery().name("Mithril dragon").results().nearest();

        Area area = createAreaAroundCoordinate(MithrilDragonCoords);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        if (mithrils != null) {
            log("Mithril dragon found, proceeding to attack.");
            addTargetName("mithril");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Mithril dragon not found, proceeding to Mithril dragon location.");
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Mithril dragon location.");
                addTargetName("mithril");
                handleMultitarget = false;
                ActivateSoulSplit();
                setSlayerState(Main.SlayerState.COMBAT);

            }
        }
    }

    public static void demons(LocalPlayer player) {
        Coordinate abyssalDemonCoords = new Coordinate(3230, 3654, 0);
        Npc abyssalDemonResults = NpcQuery.newQuery().name("Abyssal demon").results().nearest();

        Area area = createAreaAroundCoordinate(abyssalDemonCoords);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        if (abyssalDemonResults != null) {
            log("Abyssal demon found, proceeding to attack.");
            addTargetName("demon");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Abyssal demon not found, proceeding to Abyssal demon location.");
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Abyssal demon location.");
                addTargetName("demon");
                handleMultitarget = true;
                ActivateSoulSplit();
                setSlayerState(Main.SlayerState.COMBAT);

            }
        }
    }

    public static void ascensionMembers(LocalPlayer player) {
        Coordinate rorariusCoords = new Coordinate(1110, 598, 1);
        Npc rorarius = NpcQuery.newQuery().name("Rorarius").results().nearest();

        Area area = createAreaAroundCoordinate(rorariusCoords);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        if (rorarius != null) {
            addTargetName("rorarius");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Rorarius not found, proceeding to Rorarius location.");
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Rorarius location.");
                addTargetName("rorarius");
                handleMultitarget = true;
                ActivateSoulSplit();
                setSlayerState(Main.SlayerState.COMBAT);
            }
        }
    }

    public static void kalphites(LocalPlayer player) {
        Coordinate kalphiteCoords = new Coordinate(2995, 1624, 0);
        Npc kalphites = NpcQuery.newQuery().name("Exiled kalphite guardian").results().nearest();

        Area area = createAreaAroundCoordinate(kalphiteCoords);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        if (kalphites != null) {
            log("Kalphite guardian found, proceeding to attack.");
            addTargetName("kalphite");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Kalphite not found, proceeding to Kalphites location.");
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Kalphite guardian location.");
                addTargetName("kalphite");
                handleMultitarget = true;
                ActivateSoulSplit();
                setSlayerState(Main.SlayerState.COMBAT);

            }
        }
    }

    public static void elves(LocalPlayer player) {
        Coordinate elvesCoords = new Coordinate(2193, 3325, 1);
        Npc iorwerthElves = NpcQuery.newQuery().option("Attack").results().nearest();

        Area area = createAreaAroundCoordinate(elvesCoords);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        if (iorwerthElves != null) {
            log("Iorwerth elves found, proceeding to attack.");
            addTargetName("iorwerth");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Iorwerth elves not found, proceeding to Iorwerth elves location.");
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Iorwerth elves location.");
                addTargetName("iorwerth");
                ActivateSoulSplit();
                handleMultitarget = false;
                setSlayerState(Main.SlayerState.COMBAT);

            }
        }
    }

    public static void shadowCreatures(LocalPlayer player) {
        Coordinate shadowCreaturesEntrance = new Coordinate(2171, 3366, 1);
        Coordinate shadowCreaturesCoords = new Coordinate(2171, 3368, 1);

        Npc shadowCreatures = NpcQuery.newQuery().name("Manifest shadow").results().nearest();

        if (shadowCreatures != null) {
            log("Shadow creature found, proceeding to attack.");
            addTargetName("shadow");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Manifest shadow not found, proceeding to Manifest shadow location.");
            if (Movement.traverse(NavPath.resolve(shadowCreaturesEntrance)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Manifest shadow location.");
                EntityResultSet<SceneObject> barrier = SceneObjectQuery.newQuery().id(94185).option("Enter").results();
                if (!barrier.isEmpty()) {
                    SceneObject nearestBarrier = barrier.nearest();
                    if (nearestBarrier != null) {
                        log("Barrier found, proceeding to Enter.");
                        nearestBarrier.interact("Enter");
                        Execution.delayUntil(random.nextLong(8000, 10000), () -> player.getCoordinate().equals(shadowCreaturesCoords));
                        addTargetName("shadow");
                        handleMultitarget = false;
                        ActivateSoulSplit();
                        setSlayerState(Main.SlayerState.COMBAT);

                    } else {
                        log("Barrier not found.");
                    }
                } else {
                    log("Barrier not found.");
                }
            } else {
                log("Failed to traverse to Manifest shadow location.");

            }
        }
    }

    public static void VileBlooms(LocalPlayer player) {
        int playerSlayerLevel = Skills.SLAYER.getSkill().getLevel();

        // NPC names and their required Slayer levels
        String[] npcNames = {"Devil's snare", "Luminous snaggler", "Lampenflora", "Liverworts"};
        int[] slayerRequirements = {90, 95, 102, 110};
        Coordinate[] npcCoordinates = {
                new Coordinate(5600, 2124, 0), // Devil's snare
                new Coordinate(5284, 2387, 0), // Luminous snaggler
                new Coordinate(5617, 2262, 0),  // Lampenflora
                new Coordinate(5611, 2395, 0)  // Liverworts
        };

        // Determine the highest-level NPC that the player can attack based on Slayer level
        String targetNpcName = null;
        Coordinate targetNpcCoords = null;
        for (int i = npcNames.length - 1; i >= 0; i--) {
            if (playerSlayerLevel >= slayerRequirements[i]) {
                targetNpcName = npcNames[i];
                targetNpcCoords = npcCoordinates[i];
                break;
            }
        }

        if (targetNpcName == null) {
            log("No NPCs available for your Slayer level.");
            return;
        }

        Npc targetNpc = NpcQuery.newQuery().name(targetNpcName).results().nearest();
        if (targetNpc != null) {
            addTargetName(targetNpcName);
            switch (targetNpcName) {
                case "Devil's snare":
                    ActivateMeleePrayer();
                    break;
                case "Luminous snaggler":
                case "Lampenflora":
                    ActivateMagicPrayer();
                    break;
                case "Liverworts":
                    ActivateRangedPrayer();
                    break;
                default:
                    log("No specific prayer for " + targetNpcName);
                    break;
            }
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log(targetNpcName + " not found, proceeding to " + targetNpcName + " location.");
            if (Movement.traverse(NavPath.resolve(targetNpcCoords)) == TraverseEvent.State.FINISHED) {
                log("Traversed to " + targetNpcName + " location.");
                addTargetName(targetNpcName);
                switch (targetNpcName) {
                    case "Devil's snare":
                        ActivateMeleePrayer();
                        break;
                    case "Luminous snaggler":
                    case "Lampenflora":
                        ActivateMagicPrayer();
                        break;
                    case "Liverworts":
                        ActivateRangedPrayer();
                        break;
                    default:
                        log("No specific prayer for " + targetNpcName);
                        break;
                }
                handleMultitarget = false;
                setSlayerState(Main.SlayerState.COMBAT);
            }
        }
    }


    public static void greaterDemons(LocalPlayer player) {
        Coordinate greaterDemonCoords = new Coordinate(3160, 3685, 0);
        Npc greaterDemons = NpcQuery.newQuery().name("Greater demon").results().nearest();

        Area area = createAreaAroundCoordinate(greaterDemonCoords);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        if (greaterDemons != null) {
            log("Greater demon found, proceeding to attack.");
            addTargetName("Greater demon");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Greater demon not found, proceeding to Greater demon location.");
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Greater demon location.");
                addTargetName("Greater demon");
                ActivateSoulSplit();
                handleMultitarget = true;
                setSlayerState(Main.SlayerState.COMBAT);
            }
        }
    }

    public static void mutatedJadinkos(LocalPlayer player) {
        Coordinate mutatedJadinkosCoords = new Coordinate(3057, 9243, 0);
        Npc mutatedJadinkos = NpcQuery.newQuery().name("Mutated jadinko male").results().nearest();

        Area area = createAreaAroundCoordinate(mutatedJadinkosCoords);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        if (mutatedJadinkos != null) {
            log("Mutated jadinko found, proceeding to attack.");
            addTargetName("jadinko");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Mutated jadinko not found, proceeding to Mutated jadinko location.");
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Mutated jadinko location.");
                addTargetName("jadinko");
                ActivateSoulSplit();
                handleMultitarget = true;
                setSlayerState(Main.SlayerState.COMBAT);
            }
        }
    }

    public static void CorruptedCreatures(LocalPlayer player) {
        int playerSlayerLevel = Skills.SLAYER.getSkill().getLevel();

        if (playerSlayerLevel >= 103) {
            handleCorruptedWorker(player);
        } else if (playerSlayerLevel >= 100) {
            handleCorruptedKalphites(player);
        } else if (playerSlayerLevel >= 97) {
            handleCorruptedDustDevil(player);
        } else if (playerSlayerLevel >= 94) {
            handleCorruptedLizard(player);
        } else if (playerSlayerLevel >= 91) {
            handleCorruptedScarab(player);
        } else if (playerSlayerLevel >= 88) {
            handleCorruptedScorpion(player);
        } else {
            log("Your Slayer level is not high enough to attack any corrupted creatures.");
        }
    }

    private static void handleCorruptedScorpion(LocalPlayer player) {
        Coordinate dungeonEntranceCoords = new Coordinate(3290, 2708, 0);
        Coordinate delayedCoordinate = new Coordinate(2384, 6792, 3);
        Coordinate corruptedScorpionCoords = new Coordinate(2384, 6818, 3);
        Npc corruptedScorpion = NpcQuery.newQuery().name("Corrupted scorpion").results().nearest();

        Area area = createAreaAroundCoordinate(corruptedScorpionCoords);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        if (corruptedScorpion != null) {
            log("Corrupted scorpion found, proceeding to attack.");
            addTargetName("scorpion");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Corrupted scorpion not found, proceeding to Corrupted scorpion location.");
            if (Movement.traverse(NavPath.resolve(dungeonEntranceCoords)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Corrupted scorpion location.");
                EntityResultSet<SceneObject> dungeonEntrance = SceneObjectQuery.newQuery().name("Dungeon entrance").option("Enter").results();
                if (!dungeonEntrance.isEmpty() && Backpack.contains(40303)) {
                    SceneObject nearestDungeonEntrance = dungeonEntrance.nearest();
                    if (nearestDungeonEntrance != null) {
                        log("Dungeon entrance found, proceeding to Enter.");
                        nearestDungeonEntrance.interact("Enter");
                        Execution.delayUntil(random.nextLong(8000, 10000), () -> player.getCoordinate().equals(delayedCoordinate));
                        if (player.getCoordinate().equals(delayedCoordinate)) {
                            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                                log("Traversed to Corrupted scorpion location.");
                                addTargetName("scorpion");
                                ActivateSoulSplit();
                                handleMultitarget = true;
                                setSlayerState(Main.SlayerState.COMBAT);

                            }
                        }
                    } else {
                        log("We dont have any Feather of Ma'at.");
                    }
                }
            }
        }
    }

    private static void handleCorruptedScarab(LocalPlayer player) {
        Coordinate dungeonEntranceCoords = new Coordinate(3290, 2708, 0);
        Coordinate delayedCoordinate = new Coordinate(2384, 6792, 3);
        Coordinate corruptedScarabCoords = new Coordinate(2384, 6818, 3);
        Npc corruptedScarab = NpcQuery.newQuery().name("Corrupted scarab").results().nearest();

        Area area = createAreaAroundCoordinate(corruptedScarabCoords);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        if (corruptedScarab != null) {
            log("Corrupted scarab found, proceeding to attack.");
            addTargetName("scarab");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Corrupted scarab not found, proceeding to Corrupted scarab location.");
            if (Movement.traverse(NavPath.resolve(dungeonEntranceCoords)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Corrupted scarab location.");
                EntityResultSet<SceneObject> dungeonEntrance = SceneObjectQuery.newQuery().name("Dungeon entrance").option("Enter").results();
                if (!dungeonEntrance.isEmpty() && Backpack.contains(40303)) {
                    SceneObject nearestDungeonEntrance = dungeonEntrance.nearest();
                    if (nearestDungeonEntrance != null) {
                        log("Dungeon entrance found, proceeding to Enter.");
                        nearestDungeonEntrance.interact("Enter");
                        Execution.delayUntil(random.nextLong(8000, 10000), () -> player.getCoordinate().equals(delayedCoordinate));
                        if (player.getCoordinate().equals(delayedCoordinate)) {
                            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                                log("Traversed to Corrupted scarab location.");
                                addTargetName("scarab");
                                ActivateSoulSplit();
                                handleMultitarget = true;
                                setSlayerState(Main.SlayerState.COMBAT);

                            }
                        }
                    } else {
                        log("We dont have any Feather of Ma'at.");
                    }
                }
            }
        }
    }

    private static void handleCorruptedLizard(LocalPlayer player) {
        Coordinate dungeonEntranceCoords = new Coordinate(3290, 2708, 0);
        Coordinate delayedCoordinate = new Coordinate(2384, 6792, 3);
        Coordinate corruptedlizardCoords = new Coordinate(2403, 6841, 3);
        Npc corruptedLizard = NpcQuery.newQuery().name("Corrupted lizard").results().nearest();

        Area area = createAreaAroundCoordinate(corruptedlizardCoords);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        if (corruptedLizard != null) {
            log("Corrupted lizard found, proceeding to attack.");
            addTargetName("lizard");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Corrupted lizard not found, proceeding to Corrupted lizard location.");
            if (Movement.traverse(NavPath.resolve(dungeonEntranceCoords)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Corrupted lizard location.");
                EntityResultSet<SceneObject> dungeonEntrance = SceneObjectQuery.newQuery().name("Dungeon entrance").option("Enter").results();
                if (!dungeonEntrance.isEmpty() && Backpack.contains(40303)) {
                    SceneObject nearestDungeonEntrance = dungeonEntrance.nearest();
                    if (nearestDungeonEntrance != null) {
                        log("Dungeon entrance found, proceeding to Enter.");
                        nearestDungeonEntrance.interact("Enter");
                        Execution.delayUntil(random.nextLong(8000, 10000), () -> player.getCoordinate().equals(delayedCoordinate));
                        if (player.getCoordinate().equals(delayedCoordinate)) {
                            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                                log("Traversed to Corrupted lizard location.");
                                addTargetName("lizard");
                                handleMultitarget = true;
                                ActivateSoulSplit();
                                setSlayerState(Main.SlayerState.COMBAT);
                            }
                        }
                    } else {
                        log("We dont have any Feather of Ma'at.");
                    }
                }
            }
        }
    }

    private static void handleCorruptedDustDevil(LocalPlayer player) {
        Coordinate dungeonEntranceCoords = new Coordinate(3290, 2708, 0);
        Coordinate delayedCoordinate = new Coordinate(2384, 6792, 3);
        Coordinate ropeCoords = new Coordinate(2405, 6856, 3);
        Coordinate delayedCoordinate2 = new Coordinate(2405, 6863, 1);
        Coordinate corruptedDustDevilCoords = new Coordinate(2385, 6895, 1);
        Npc corruptedLizard = NpcQuery.newQuery().name("Corrupted dust devil").results().nearest();


        if (corruptedLizard != null) {
            log("Corrupted dust devil found, proceeding to attack.");
            addTargetName("dust");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Corrupted dust devil not found, proceeding to Corrupted dust devil location.");
            if (Movement.traverse(NavPath.resolve(dungeonEntranceCoords)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Corrupted dust devil location.");
                EntityResultSet<SceneObject> dungeonEntrance = SceneObjectQuery.newQuery().name("Dungeon entrance").option("Enter").results();
                if (!dungeonEntrance.isEmpty() && Backpack.contains(40303)) {
                    SceneObject nearestDungeonEntrance = dungeonEntrance.nearest();
                    if (nearestDungeonEntrance != null) {
                        log("Dungeon entrance found, proceeding to Enter.");
                        nearestDungeonEntrance.interact("Enter");
                        Execution.delayUntil(random.nextLong(8000, 10000), () -> player.getCoordinate().equals(delayedCoordinate));
                        if (player.getCoordinate().equals(delayedCoordinate)) {
                            if (Movement.traverse(NavPath.resolve(ropeCoords)) == TraverseEvent.State.FINISHED) {
                                EntityResultSet<SceneObject> rope = SceneObjectQuery.newQuery().name("Rope").option("Climb down").results();
                                if (!rope.isEmpty()) {
                                    SceneObject nearestRope = rope.nearest();
                                    if (nearestRope != null) {
                                        log("Rope found, proceeding to Climb down.");
                                        nearestRope.interact("Climb down");
                                        Execution.delayUntil(random.nextLong(8000, 10000), () -> player.getCoordinate().equals(delayedCoordinate2));
                                        if (player.getCoordinate().equals(delayedCoordinate2)) {
                                            if (Movement.traverse(NavPath.resolve(corruptedDustDevilCoords)) == TraverseEvent.State.FINISHED) {
                                                log("Traversed to Corrupted Dust devils location.");
                                                addTargetName("dust");
                                                handleMultitarget = true;
                                                ActivateSoulSplit();
                                                setSlayerState(Main.SlayerState.COMBAT);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    log("We dont have any Feather of Ma'at.");
                }
            }
        }
    }

    private static void handleCorruptedKalphites(LocalPlayer player) {
        Coordinate dungeonEntranceCoords = new Coordinate(3290, 2708, 0);
        Coordinate delayedCoordinate = new Coordinate(2384, 6792, 3);
        Coordinate ropeCoords = new Coordinate(2405, 6856, 3);
        Coordinate delayedCoordinate2 = new Coordinate(2405, 6863, 1);
        Coordinate corruptedKalphiteCoords = new Coordinate(2385, 6895, 1);
        Npc corruptedKalphiteMarauder = NpcQuery.newQuery().name("Corrupted kalphite marauder").results().nearest();

        Area area = createAreaAroundCoordinate(corruptedKalphiteCoords);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        if (corruptedKalphiteMarauder != null) {
            log("Corrupted kalphite marauder found, proceeding to attack.");
            addTargetName("kalphite");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Corrupted kalphite marauder not found, proceeding to Corrupted kalphite marauder location.");
            if (Movement.traverse(NavPath.resolve(dungeonEntranceCoords)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Corrupted kalphite marauder location.");
                EntityResultSet<SceneObject> dungeonEntrance = SceneObjectQuery.newQuery().name("Dungeon entrance").option("Enter").results();
                if (!dungeonEntrance.isEmpty() && Backpack.contains(40303)) {
                    SceneObject nearestDungeonEntrance = dungeonEntrance.nearest();
                    if (nearestDungeonEntrance != null) {
                        log("Dungeon entrance found, proceeding to Enter.");
                        nearestDungeonEntrance.interact("Enter");
                        Execution.delayUntil(random.nextLong(8000, 10000), () -> player.getCoordinate().equals(delayedCoordinate));
                        if (player.getCoordinate().equals(delayedCoordinate)) {
                            if (Movement.traverse(NavPath.resolve(ropeCoords)) == TraverseEvent.State.FINISHED) {
                                EntityResultSet<SceneObject> rope = SceneObjectQuery.newQuery().name("Rope").option("Climb down").results();
                                if (!rope.isEmpty()) {
                                    SceneObject nearestRope = rope.nearest();
                                    if (nearestRope != null) {
                                        log("Rope found, proceeding to Climb down.");
                                        nearestRope.interact("Climb down");
                                        Execution.delayUntil(random.nextLong(8000, 10000), () -> player.getCoordinate().equals(delayedCoordinate2));
                                        if (player.getCoordinate().equals(delayedCoordinate2)) {
                                            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                                                log("Traversed to Corrupted kalphite marauder location.");
                                                addTargetName("kalphite");
                                                handleMultitarget = true;
                                                ActivateSoulSplit();
                                                setSlayerState(Main.SlayerState.COMBAT);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    log("We dont have any Feather of Ma'at.");
                }
            }
        }
    }

    private static void handleCorruptedWorker(LocalPlayer player) {
        Coordinate dungeonEntranceCoords = new Coordinate(3290, 2708, 0);
        Coordinate delayedCoordinate = new Coordinate(2384, 6792, 3);
        Coordinate ropeCoords = new Coordinate(2405, 6856, 3);
        Coordinate delayedCoordinate2 = new Coordinate(2405, 6863, 1);
        Coordinate corruptedWorkerCoords = new Coordinate(2477, 6878, 1);
        Npc corruptedWorker = NpcQuery.newQuery().name("Corrupted worker").results().nearest();

        if (corruptedWorker != null) {
            log("Corrupted worker found, proceeding to attack.");
            addTargetName("worker");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Corrupted worker not found, proceeding to Corrupted worker location.");
            if (Movement.traverse(NavPath.resolve(dungeonEntranceCoords)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Corrupted worker location.");
                EntityResultSet<SceneObject> dungeonEntrance = SceneObjectQuery.newQuery().name("Dungeon entrance").option("Enter").results();
                if (!dungeonEntrance.isEmpty() && Backpack.contains(40303)) {
                    SceneObject nearestDungeonEntrance = dungeonEntrance.nearest();
                    if (nearestDungeonEntrance != null) {
                        log("Dungeon entrance found, proceeding to Enter.");
                        nearestDungeonEntrance.interact("Enter");
                        Execution.delayUntil(random.nextLong(8000, 10000), () -> player.getCoordinate().equals(delayedCoordinate));
                        if (player.getCoordinate().equals(delayedCoordinate)) {
                            if (Movement.traverse(NavPath.resolve(ropeCoords)) == TraverseEvent.State.FINISHED) {
                                EntityResultSet<SceneObject> rope = SceneObjectQuery.newQuery().name("Rope").option("Climb down").results();
                                if (!rope.isEmpty()) {
                                    SceneObject nearestRope = rope.nearest();
                                    if (nearestRope != null) {
                                        log("Rope found, proceeding to Climb down.");
                                        nearestRope.interact("Climb down");
                                        Execution.delayUntil(random.nextLong(8000, 10000), () -> player.getCoordinate().equals(delayedCoordinate2));
                                        if (player.getCoordinate().equals(delayedCoordinate2)) {
                                            if (Movement.traverse(NavPath.resolve(corruptedWorkerCoords)) == TraverseEvent.State.FINISHED) {
                                                log("Traversed to Corrupted worker location.");
                                                addTargetName("worker");
                                                handleMultitarget = true;
                                                ActivateSoulSplit();
                                                setSlayerState(Main.SlayerState.COMBAT);

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    log("We dont have any Feather of Ma'at.");
                }
            }
        }
    }

    public static void ironDragons(LocalPlayer player) {
        Coordinate irondragonCoords = new Coordinate(2716, 9459, 0);
        Npc ironDragon = NpcQuery.newQuery().name("Iron dragon").results().nearest();

        Area area = createAreaAroundCoordinate(irondragonCoords);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        if (ironDragon != null) {
            log("Iron dragon found, proceeding to attack.");
            addTargetName("Iron dragon");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Iron dragon not found, proceeding to Iron dragon location.");
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Iron dragon location.");
                addTargetName("Iron dragon");
                handleMultitarget = false;
                ActivateSoulSplit();
                setSlayerState(Main.SlayerState.COMBAT);
            }
        }
    }

    public static void steelDragons(LocalPlayer player) {
        Coordinate steeldragonCoords = new Coordinate(2716, 9459, 0);
        Npc steelDragon = NpcQuery.newQuery().name("Steel dragon").results().nearest();

        Area area = createAreaAroundCoordinate(steeldragonCoords);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        if (steelDragon != null) {
            log("Steel dragon found, proceeding to attack.");
            addTargetName("Steel dragon");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Steel dragon not found, proceeding to Steel dragon location.");
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Steel dragon location.");
                addTargetName("Steel dragon");
                handleMultitarget = false;
                ActivateSoulSplit();
                setSlayerState(Main.SlayerState.COMBAT);
            }
        }
    }

    public static void adamantDragons(LocalPlayer player) {
        Coordinate steeldragonCoords = new Coordinate(4512, 6034, 0);
        Npc adamantDragon = NpcQuery.newQuery().name("Adamant dragon").results().nearest();

        Area area = createAreaAroundCoordinate(steeldragonCoords);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        if (adamantDragon != null) {
            log("Adamant dragon found, proceeding to attack.");
            addTargetName("Adamant dragon");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Adamant dragon not found, proceeding to Adamant dragon location.");
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Adamant dragon location.");
                ActivateSoulSplit();
                handleMultitarget = false;
                addTargetName("Adamant dragon");
                setSlayerState(Main.SlayerState.COMBAT);
            }
        }
    }

    public static void blackDemons(LocalPlayer player) {
        Coordinate blackDemonCoords = new Coordinate(2867, 9778, 0);
        Npc blackDemon = NpcQuery.newQuery().name("Black demon").results().nearest();

        Area area = createAreaAroundCoordinate(blackDemonCoords);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        if (blackDemon != null) {
            log("Black demon found, proceeding to attack.");
            addTargetName("Black demon");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Black demon not found, proceeding to Black demon location.");
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Black demon location.");
                addTargetName("Black demon");
                handleMultitarget = true;
                ActivateSoulSplit();
                setSlayerState(Main.SlayerState.COMBAT);
            }
        }
    }

    public static void kalgerionDemons(LocalPlayer player) {
        Coordinate kalgerionDemonCoords = new Coordinate(1297, 1287, 0);
        Npc kalgerionDemon = NpcQuery.newQuery().name("Kal'gerion demon").results().nearest();

        Area area = createAreaAroundCoordinate(kalgerionDemonCoords);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        if (kalgerionDemon != null) {
            log("Kal'gerion demon found, proceeding to attack.");
            addTargetName("demon");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Kal'gerion demon not found, proceeding to Kal'gerion demon location.");
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Kal'gerion demon location.");
                addTargetName("demon");
                handleMultitarget = false;
                ActivateSoulSplit();
                setSlayerState(Main.SlayerState.COMBAT);

            }
        }
    }

    public static void gargoyles(LocalPlayer player) {
        Coordinate gargoyleCoords = new Coordinate(3441, 3565, 2);
        Npc gargoyle = NpcQuery.newQuery().name("Gargoyle").results().nearest();

        Area area = createAreaAroundCoordinate(gargoyleCoords);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);


        if (gargoyle != null) {
            log("Gargoyle found, proceeding to attack.");
            addTargetName("Gargoyle");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Gargoyle not found, proceeding to Gargoyle location.");
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Gargoyle location.");
                addTargetName("Gargoyle");
                handleMultitarget = true;
                ActivateSoulSplit();
                setSlayerState(Main.SlayerState.COMBAT);
            }
        }
    }

    public static void chaosGiants(LocalPlayer player) {
        Coordinate choasGiantsEntrance = new Coordinate(2875, 10119, 0);
        Coordinate delayedCoordinate = new Coordinate(1670, 5834, 0);
        Coordinate chaosGiantEntrance2 = new Coordinate(1715, 5884, 0);
        Npc chaosGiant = NpcQuery.newQuery().name("Chaos Giant").results().nearest();

        if (chaosGiant != null) {
            log("Chaos giant found, proceeding to attack.");
            addTargetName("Chaos Giant");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Chaos giant not found, proceeding to Chaos giant location.");
            if (Movement.traverse(NavPath.resolve(choasGiantsEntrance)) == TraverseEvent.State.FINISHED) {
                EntityResultSet<SceneObject> tunnel = SceneObjectQuery.newQuery().name("Tunnel").option("Enter").results();
                if (!tunnel.isEmpty()) {
                    SceneObject nearestTunnel = tunnel.nearest();
                    if (nearestTunnel != null) {
                        log("Tunnel found, proceeding to Enter.");
                        nearestTunnel.interact("Enter");
                        Execution.delayUntil(random.nextLong(8000, 10000), () -> player.getCoordinate().equals(delayedCoordinate));
                        if (player.getCoordinate().equals(delayedCoordinate)) {
                            if (Movement.traverse(NavPath.resolve(chaosGiantEntrance2)) == TraverseEvent.State.FINISHED) {
                                EntityResultSet<SceneObject> cave = SceneObjectQuery.newQuery().name("Cave").option("Enter").results();
                                if (!cave.isEmpty()) {
                                    SceneObject nearestCave = cave.nearest();
                                    if (nearestCave != null) {
                                        log("Cave found, proceeding to Enter.");
                                        nearestCave.interact("Enter");
                                        Execution.delay(random.nextLong(4000, 5000));
                                        log("Traversed to Chaos giant location.");
                                        addTargetName("Chaos Giant");
                                        handleMultitarget = false;
                                        ActivateSoulSplit();
                                        setSlayerState(Main.SlayerState.COMBAT);

                                    }
                                }
                            }
                        }
                    } else {
                        log("Tunnel not found.");
                    }
                } else {
                    log("Tunnel not found.");
                }
            }
        }
    }

    public static void airut(LocalPlayer player) {
        Coordinate airutCoord = new Coordinate(2274, 3617, 0);
        Npc airut = NpcQuery.newQuery().name("Airut").results().nearest();

        Area area = createAreaAroundCoordinate(airutCoord);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        if (airut != null) {
            log("Airut found, proceeding to attack.");
            addTargetName("Airut");
            ActivateSoulSplit();
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Airut not found, proceeding to Airut location.");
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Airut location.");
                addTargetName("Airut");
                handleMultitarget = true;
                ActivateSoulSplit();
                setSlayerState(Main.SlayerState.COMBAT);
            }
        }
    }

    public static void blackDragon(LocalPlayer player) {
        Coordinate blackDragonCoord = new Coordinate(2834, 9823, 0);

        Area area = createAreaAroundCoordinate(blackDragonCoord);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);

        Npc blackDragon = NpcQuery.newQuery().name("Black dragon").results().nearest();

        if (blackDragon != null) {
            log("Black dragon found, proceeding to attack.");
            ActivateSoulSplit();
            addTargetName("black");
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Black dragon not found, proceeding to Black dragon location.");
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Black dragon location.");
                addTargetName("black");
                handleMultitarget = false;
                ActivateSoulSplit();
                setSlayerState(Main.SlayerState.COMBAT);
            }
        }
    }

    public static void dinosaurs(LocalPlayer player) {
        int playerSlayerLevel = Skills.SLAYER.getSkill().getLevel();

        log("Slayer level: " + playerSlayerLevel);

        // NPC data
        class NpcData {
            final int slayerRequirement;
            final Coordinate coordinate;

            NpcData(int slayerRequirement, Coordinate coordinate) {
                this.slayerRequirement = slayerRequirement;
                this.coordinate = coordinate;
            }
        }

        Map<String, NpcData> npcDataMap = new LinkedHashMap<>();
        npcDataMap.put("Feral dinosaur", new NpcData(90, new Coordinate(5520, 2515, 0)));
        npcDataMap.put("Brutish dinosaur", new NpcData(99, new Coordinate(5527, 2544, 0)));
        npcDataMap.put("Venomous dinosaur", new NpcData(105, new Coordinate(5432, 2529, 0)));
        npcDataMap.put("Ripper dinosaur", new NpcData(114, new Coordinate(5679, 2188, 0)));

        String targetNpcName = null;
        NpcData targetNpcData = null;
        for (Map.Entry<String, NpcData> entry : npcDataMap.entrySet()) {
            if (playerSlayerLevel >= entry.getValue().slayerRequirement) {
                targetNpcName = entry.getKey();
                targetNpcData = entry.getValue();
            }
        }

        if (targetNpcName == null) {
            log("No NPCs available for your Slayer level.");
            return;
        }

        String finalTargetNpcName = targetNpcName;
        Runnable handleNpc = () -> {
            addTargetName(finalTargetNpcName);
            ActivateMeleePrayer();
            handleMultitarget = true;
            setSlayerState(Main.SlayerState.COMBAT);
        };

        Npc targetNpc = NpcQuery.newQuery().name(targetNpcName).results().nearest();
        if (targetNpc != null) {
            log(targetNpcName + " found, proceeding to attack.");
            handleNpc.run();
        } else {
            log(targetNpcName + " not found, proceeding to " + targetNpcName + " location.");
            if (Movement.traverse(NavPath.resolve(targetNpcData.coordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to " + targetNpcName + " location.");
                handleNpc.run();
            }
        }
    }

    public static long camelWarriors(LocalPlayer player) {
        Coordinate camelWarriorCoord = new Coordinate(2834, 9823, 0);

        // Search for the mirage NPCs
        Npc bloodMirage = NpcQuery.newQuery().name("Blood mirage").results().nearest();
        Npc shadowMirage = NpcQuery.newQuery().name("Shadow mirage").results().nearest();
        Npc smokeMirage = NpcQuery.newQuery().name("Smoke mirage").results().nearest();

        Npc targetNpc = null;
        int lowestHp = Integer.MAX_VALUE;

        // Determine which mirage NPC has the lowest HP
        if (bloodMirage != null && bloodMirage.getCurrentHealth() < lowestHp) {
            lowestHp = bloodMirage.getCurrentHealth();
            targetNpc = bloodMirage;
        }
        if (shadowMirage != null && shadowMirage.getCurrentHealth() < lowestHp) {
            lowestHp = shadowMirage.getCurrentHealth();
            targetNpc = shadowMirage;
        }
        if (smokeMirage != null && smokeMirage.getCurrentHealth() < lowestHp) {
            lowestHp = smokeMirage.getCurrentHealth();
            targetNpc = smokeMirage;
        }

        if (targetNpc != null) {
            if (!player.hasTarget()) {
                log(targetNpc.getName() + " with lowest HP found, proceeding to attack.");
                targetNpc.interact("Attack");
                Execution.delay(random.nextLong(1000, 1500));
            } else {
                return random.nextLong(300, 400);
            }
            return 0;
        } else {
            List<Npc> camelWarriors = NpcQuery.newQuery().name("Camel Warrior").results().stream().toList();
            if (!camelWarriors.isEmpty()) {
                for (Npc camelWarrior : camelWarriors) {
                    if (camelWarrior.getCurrentHealth() < lowestHp) {
                        lowestHp = camelWarrior.getCurrentHealth();
                        targetNpc = camelWarrior;
                    }
                }
                if (targetNpc != null && !player.hasTarget()) {
                    log("Camel Warrior with lowest HP found, proceeding to attack.");
                    targetNpc.interact("Attack");
                    Execution.delay(random.nextLong(1000, 2000));
                    if (doSlayer) {
                        ActivateSoulSplit();
                        setSlayerState(Main.SlayerState.COMBAT);
                    }
                } else {
                    return random.nextLong(300, 400);
                }
                return 0;
            } else {
                log("No NPCs found, proceeding to Camel Warrior location.");
                if (Movement.traverse(NavPath.resolve(camelWarriorCoord)) == TraverseEvent.State.FINISHED) { // TODO: Change to Camel Warrior location
                    log("Traversed to Camel Warrior location.");
                    if (doSlayer) {
                        ActivateSoulSplit();
                        handleMultitarget = false;
                        setSlayerState(Main.SlayerState.COMBAT);
                    }
                }
            }
        }
        return 0;
    }


    public static void skeletons(LocalPlayer player) {
        Coordinate skeletonCoord = new Coordinate(4024, 5530, 0);
        Npc skeleton = NpcQuery.newQuery().name("Skeleton").results().nearest();


        if (skeleton != null) {
            log("Skeleton found, proceeding to attack.");
            addTargetName("Skeleton");
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Skeleton not found, proceeding to Skeleton location.");
            Area area = createAreaAroundCoordinate(skeletonCoord);
            Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Skeleton location.");
                addTargetName("Skeleton");
                handleMultitarget = true;
                setSlayerState(Main.SlayerState.COMBAT);
            }
        }
    }

    public static void caveBugs(LocalPlayer player) {
        Coordinate caveBugCoord = new Coordinate(3181, 9557, 0);
        Npc caveBug = NpcQuery.newQuery().name("Cave bug").option("Attack").results().nearest();


        if (caveBug != null) {
            log("Cave bug found, proceeding to attack.");
            addTargetName("cave bug");
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Cave bug not found, proceeding to Cave bug location.");
            Area area = createAreaAroundCoordinate(caveBugCoord);
            Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Cave bug location.");
                addTargetName("cave bug");
                handleMultitarget = true;
                setSlayerState(Main.SlayerState.COMBAT);
            }
        }
    }

    public static void caveSlime(LocalPlayer player) {
        Coordinate caveSlimeCoord = new Coordinate(3181, 9557, 0);
        Npc caveSlime = NpcQuery.newQuery().name("Cave slime").option("Attack").results().nearest();


        if (caveSlime != null) {
            log("Cave slime found, proceeding to attack.");
            addTargetName("cave slime");
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Cave slime not found, proceeding to Cave slime location.");
            Area area = createAreaAroundCoordinate(caveSlimeCoord);
            Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Cave slime location.");
                addTargetName("cave slime");
                handleMultitarget = true;
                setSlayerState(Main.SlayerState.COMBAT);
            }
        }
    }

    public static void birds(LocalPlayer player) {
    Coordinate birdCoord = new Coordinate(3233, 3297, 0);
    Npc bird = NpcQuery.newQuery().name("Chicken").option("Attack").results().nearest();

    if (bird != null && player.getCoordinate().distanceTo(bird.getCoordinate()) <= 20) {
        log("Chicken found within 20 tiles, proceeding to attack.");
        addTargetName("Chicken");
        setSlayerState(Main.SlayerState.COMBAT);
    } else {
        log("Chicken not found or not within 20 tiles, proceeding to Chicken location.");
        Area area = createAreaAroundCoordinate(birdCoord);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);
        if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
            log("Traversed to Chicken location.");
            addTargetName("Chicken");
            handleMultitarget = true;
            setSlayerState(Main.SlayerState.COMBAT);
        }
    }
}

    public static void goblins(LocalPlayer player) {
    Coordinate goblinCoord = new Coordinate(3247, 3237, 0);
    Npc goblin = NpcQuery.newQuery().name("Goblin").option("Attack").results().nearest();


    if (goblin != null && player.getCoordinate().distanceTo(goblin.getCoordinate()) <= 20) {
        log("Goblin found within 20 tiles, proceeding to attack.");
        addTargetName("Goblin");
        setSlayerState(Main.SlayerState.COMBAT);
    } else {
        log("Goblin not found or not within 20 tiles, proceeding to Goblin location.");
        Area area = createAreaAroundCoordinate(goblinCoord);
        Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);
        if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
            log("Traversed to Goblin location.");
            addTargetName("Goblin");
            handleMultitarget = true;
            setSlayerState(Main.SlayerState.COMBAT);
        }
    }
}

    public static void spiders(LocalPlayer player) {
        Coordinate spiderCoord = new Coordinate(3181, 3180, 0);
        Npc spider = NpcQuery.newQuery().name("Giant spider").results().nearest();


        if (spider != null) {
            log("spider found, proceeding to attack.");
            addTargetName("spider");
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("spider not found, proceeding to spider location.");
            Area area = createAreaAroundCoordinate(spiderCoord);
            Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to spider location.");
                addTargetName("spider");
                handleMultitarget = true;
                setSlayerState(Main.SlayerState.COMBAT);
            }
        }
    }

    public static void cows(LocalPlayer player) {
        Coordinate cowCoord = new Coordinate(3257, 3276, 0);
        Npc cow = NpcQuery.newQuery().name("Cow").results().nearest();


        if (cow != null && player.getCoordinate().distanceTo(cow.getCoordinate()) <= 20) {
            log("Cow found within 20 tiles, proceeding to attack.");
            addTargetName("Cow");
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Cow not found or not within 20 tiles, proceeding to Cow location.");
            Area area = createAreaAroundCoordinate(cowCoord);
            Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Cow location.");
                addTargetName("Cow");
                handleMultitarget = true;
                setSlayerState(Main.SlayerState.COMBAT);
            }
        }
    }

    public static void bats(LocalPlayer player) {
        Coordinate batCoord = new Coordinate(4024, 5515, 0);
        Npc bat = NpcQuery.newQuery().name("Warped bat").results().nearest();


        if (bat != null) {
            log("Warped bat found, proceeding to attack.");
            addTargetName("Warped bat");
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Warped bat not found, proceeding to Warped bat location.");
            Area area = createAreaAroundCoordinate(batCoord);
            Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Warped bat location.");
                addTargetName("Warped bat");
                handleMultitarget = true;
                setSlayerState(Main.SlayerState.COMBAT);
            }
        }
    }

    public static void rats(LocalPlayer player) {
        Coordinate ratCoord = new Coordinate(3181, 3180, 0);
        Npc rat = NpcQuery.newQuery().name("Giant rat").results().nearest();


        if (rat != null) {
            log("Giant rat found, proceeding to attack.");
            addTargetName("Giant rat");
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Giant rat not found, proceeding to Giant rat location.");
            Area area = createAreaAroundCoordinate(ratCoord);
            Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Giant rat location.");
                addTargetName("Giant rat");
                handleMultitarget = true;
                setSlayerState(Main.SlayerState.COMBAT);
            }
        }
    }

    public static void frogs(LocalPlayer player) {
        Coordinate frogCoord = new Coordinate(3181, 3180, 0);
        Npc frog = NpcQuery.newQuery().name("Swamp frog").results().nearest();


        if (frog != null) {
            log("Swamp frog found, proceeding to attack.");
            addTargetName("Swamp frog");
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Swamp frog not found, proceeding to Swamp frog location.");
            Area area = createAreaAroundCoordinate(frogCoord);
            Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Swamp frog location.");
                addTargetName("Swamp frog");
                handleMultitarget = true;
                setSlayerState(Main.SlayerState.COMBAT);
            }
        }
    }

    public static void ghosts(LocalPlayer player) {
        Coordinate ghostCoord = new Coordinate(2902, 9849, 0);
        Npc ghost = NpcQuery.newQuery().name("Ghost").results().nearest();

        if (ghost != null) {
            log("Ghost found, proceeding to attack.");
            addTargetName("Ghost");
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Ghost not found, proceeding to Ghost location.");
            Area area = createAreaAroundCoordinate(ghostCoord);
            Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Ghost location.");
                addTargetName("Ghost");
                handleMultitarget = true;
                setSlayerState(Main.SlayerState.COMBAT);
            }
        }
    }

    public static void zombies(LocalPlayer player) {
        Coordinate center = new Coordinate(3987, 5554, 0);

        Npc spider = NpcQuery.newQuery().name("Corpse spider").results().nearest();

        if (spider != null) {
            log("Corpse spider found, proceeding to attack.");
            addTargetName("Corpse spider");
            setSlayerState(Main.SlayerState.COMBAT);
        } else {
            log("Corpse spider not found, proceeding to Corpse spider location.");
            Area area = createAreaAroundCoordinate(center);
            Coordinate randomWalkableCoordinate = getRandomWalkableCoordinateInArea(area);
            if (Movement.traverse(NavPath.resolve(randomWalkableCoordinate)) == TraverseEvent.State.FINISHED) {
                log("Traversed to Corpse spider location.");
                addTargetName("Corpse spider");
                handleMultitarget = true;
                setSlayerState(Main.SlayerState.COMBAT);
            }
        }
    }

    private static Area createAreaAroundCoordinate(Coordinate center) {
        Coordinate topLeft = new Coordinate(center.getX() - 2, center.getY() + 2, center.getZ());
        Coordinate bottomRight = new Coordinate(center.getX() + 2, center.getY() - 2, center.getZ());

        Area area = new Area.Rectangular(topLeft, bottomRight);
        log("Created area with top left coordinate: " + topLeft + " and bottom right coordinate: " + bottomRight);
        return area;
    }

    private static Coordinate getRandomWalkableCoordinateInArea(Area area) {
        Coordinate randomCoordinate = area.getRandomWalkableCoordinate();
        log("Selected random walkable coordinate: " + randomCoordinate);
        return randomCoordinate;
    }







}