package net.botwithus

import net.botwithus.internal.scripts.ScriptDefinition
import net.botwithus.rs3.game.Area
import net.botwithus.rs3.game.Client
import net.botwithus.rs3.game.Coordinate
import net.botwithus.rs3.game.Distance
import net.botwithus.rs3.game.movement.Movement
import net.botwithus.rs3.game.movement.NavPath
import net.botwithus.rs3.game.movement.TraverseEvent
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer
import net.botwithus.rs3.script.Execution
import net.botwithus.rs3.script.LoopingScript
import net.botwithus.rs3.script.ScriptConsole
import net.botwithus.rs3.script.config.ScriptConfig
import java.util.*

class Banking {
    data class Bank(val name: String, val area: Area.Rectangular, val bankType: String, val actions: List<String>) {
        val coordinate: Coordinate
            get() {
                TODO()
            }
    }

    val VarrackWest = Bank(
        "Varrack West",
        Area.Rectangular(Coordinate(3188, 3445, 0), Coordinate(3182, 3435, 0)),
        "Bank booth",
        listOf("Bank", "Load Last Preset from")
    )
    val VarrockEast = Bank(
        "Varrock East",
        Area.Rectangular(Coordinate(3250, 3420, 0), Coordinate(3257, 3423, 0)),
        "Bank booth",
        listOf("Bank", "Load Last Preset from")
    )
    val GrandExchange = Bank(
        "Grand Exchange",
        Area.Rectangular(Coordinate(3156, 3485, 0), Coordinate(3172, 3500, 0)),
        "Banker",
        listOf("Bank", "Load Last Preset from")
    )
    val Canafis = Bank(
        "Canafis",
        Area.Rectangular(Coordinate(3510, 3478, 0), Coordinate(3512, 3483, 0)),
        "Bank booth",
        listOf("Bank", "Load Last Preset from")
    )
    val AlKharid = Bank(
        "Al Kharid",
        Area.Rectangular(Coordinate(3268, 3167, 0), Coordinate(3272, 3168, 0)),
        "Bank booth",
        listOf("Bank", "Load Last Preset from")
    )
    val Lumbridge = Bank(
        "Lumbridge",
        Area.Rectangular(Coordinate(3213, 3255, 0), Coordinate(3217, 3259, 0)),
        "Bank chest",
        listOf("Use", "Load Last Preset from")
    )
    val Draynor = Bank(
        "Draynor",
        Area.Rectangular(Coordinate(3092, 3240, 0), Coordinate(3095, 3246, 0)),
        "Bank booth",
        listOf("Bank", "Load Last Preset from")
    )
    val FaladorEast = Bank(
        "Falador East",
        Area.Rectangular(Coordinate(3009, 3355, 0), Coordinate(3018, 3358, 0)),
        "Bank booth",
        listOf("Bank", "Load Last Preset from")
    )
    val SmithingGuild = Bank(
        "Smithing Guild",
        Area.Rectangular(Coordinate(3058, 3337, 0), Coordinate(3060, 3342, 0)),
        "Bank chest",
        listOf("Use", "Load Last Preset from")
    )
    val FaladorWest = Bank(
        "Falador West",
        Area.Rectangular(Coordinate(2943, 3368, 0), Coordinate(2947, 3370, 0)),
        "Bank booth",
        listOf("Bank", "Load Last Preset from")
    )
    val Burthorpe = Bank(
        "Burthorpe",
        Area.Rectangular(Coordinate(2890, 3534, 0), Coordinate(2886, 3538, 0)),
        "Bank booth",
        listOf("Bank", "Load Last Preset from")
    )
    val Taverly = Bank(
        "Taverly",
        Area.Rectangular(Coordinate(2875, 3415, 0), Coordinate(2877, 2419, 0)),
        "Counter",
        listOf("Bank", "Load Last Preset from")
    )
    val Catherby = Bank(
        "Catherby",
        Area.Rectangular(Coordinate(2795, 3437, 0), Coordinate(2796, 3442, 0)),
        "Bank booth",
        listOf("Bank", "Load Last Preset from")
    )
    val Seers = Bank(
        "Seers",
        Area.Rectangular(Coordinate(2722, 3491, 0), Coordinate(2728, 3493, 0)),
        "Bank booth",
        listOf("Bank", "Load Last Preset from")
    )
    val ArdougneSouth = Bank(
        "Ardougne South",
        Area.Rectangular(Coordinate(2652, 3280, 0), Coordinate(2655, 3286, 0)),
        "Bank booth",
        listOf("Bank", "Load Last Preset from")
    )
    val ArdougneNorth = Bank(
        "Ardougne North",
        Area.Rectangular(Coordinate(2621, 3332, 0), Coordinate(2612, 3335, 0)),
        "Bank booth",
        listOf("Bank", "Load Last Preset from")
    )
    val Yanille = Bank(
        "Yanille",
        Area.Rectangular(Coordinate(2610, 3088, 0), Coordinate(2613, 3095, 0)),
        "Bank booth",
        listOf("Bank", "Load Last Preset from")
    )
    val Ooglog = Bank(
        "Ooglog",
        Area.Rectangular(Coordinate(2556, 2836, 0), Coordinate(2558, 2840, 0)),
        "Bank booth",
        listOf("Bank", "Load Last Preset from")
    )
    val CityofUm = Bank(
        "City of Um",
        Area.Rectangular(Coordinate(1146, 1801, 1), Coordinate(1149, 1806, 1)),
        "Bank chest",
        listOf("Use", "Load Last Preset from")
    )
    val PrifCenter = Bank(
        "Prifddinas Center",
        Area.Rectangular(Coordinate(2203, 3366, 1), Coordinate(2207, 3370, 1)),
        "Banker",
        listOf("Bank", "Load Last Preset from")
    )
    val PrifEast = Bank(
        "Prifddinas East",
        Area.Rectangular(Coordinate(2230, 3309, 1), Coordinate(2233, 3312, 1)),
        "Bank chest",
        listOf("Use", "Load Last Preset from")
    )
    val WarsRetreat = Bank(
        "Wars Retreat",
        Area.Rectangular(Coordinate(3296, 10129, 0), Coordinate(2200, 3360, 1)),
        "Bank chest",
        listOf("Use", "Load Last Preset from")
    )
    val Anachronia = Bank(
        "Anachronia",
        Area.Rectangular(Coordinate(3700, 10000, 0), Coordinate(3702, 10002, 0)),
        "Bank chest",
        listOf("Bank", "Load Last Preset from")
    )

    private val banks = listOf(
        VarrackWest,
        VarrockEast,
        GrandExchange,
        Canafis,
        AlKharid,
        Lumbridge,
        Draynor,
        FaladorEast,
        SmithingGuild,
        FaladorWest,
        Burthorpe,
        Taverly,
        Catherby,
        Seers,
        ArdougneSouth,
        ArdougneNorth,
        Yanille,
        Ooglog,
        CityofUm,
        PrifCenter,
        PrifEast,
        WarsRetreat,
        Anachronia
    )

    fun findNearestBank(playerCoord: Coordinate): Bank? {
        ScriptConsole.println("Finding the nearest bank.")
        return banks.minByOrNull { bank ->
            bank.area.centroid?.distanceTo(playerCoord) ?: Double.MAX_VALUE
        }
    }

    fun navigateToNearestBank(player: LocalPlayer, nearestBank: Bank) {
        ScriptConsole.println("Attempting to walk to the ${nearestBank.name}.")

        val result = Movement.traverse(NavPath.resolve(nearestBank.area.randomWalkableCoordinate))
        if (result == TraverseEvent.State.NO_PATH) {
            ScriptConsole.println("Failed to find path to the ${nearestBank.name}.")
        } else {
            ScriptConsole.println("Successfully walked to the ${nearestBank.name}.")
            if (nearestBank.area.contains(player.coordinate)) {
                return
            }
        }
    }


    fun interactWithBank(nearestBank: Bank) {
        val results = NpcQuery.newQuery().name(nearestBank.bankType).option(nearestBank.actions.first()).results()

        if (!results.isEmpty) {
            val bankNpc = results.nearest()

            if (bankNpc?.options?.contains("Bank") == true) {
                bankNpc.interact("Bank")
            } else if (bankNpc?.options?.contains("Use") == true) {
                bankNpc.interact("Use")
            } else {
                println("No valid options found for the bank.")
            }
        }
    }
}