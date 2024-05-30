package net.botwithus.Misc

import net.botwithus.SnowsScript
import net.botwithus.Variables.Variables.*
import net.botwithus.api.game.hud.inventories.Backpack
import net.botwithus.rs3.game.Client
import net.botwithus.rs3.game.hud.interfaces.Interfaces
import net.botwithus.rs3.game.minimenu.MiniMenu
import net.botwithus.rs3.game.minimenu.actions.ComponentAction
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer
import net.botwithus.rs3.script.ScriptConsole
import java.util.*

class Planks {
    private val random: Random = Random()
    private val randomDelay = random.nextLong(1500, 3000)
    private val player = Client.getLocalPlayer()


    companion object {
        @JvmStatic
        fun handlePlankMaking(
            player: LocalPlayer?,
            randomDelay: Long,
            makePlanks: Boolean,
            makeRefinedPlanks: Boolean,
            makeFrames: Boolean,
            handlePlanks: () -> Long,
            handleRefinedPlanks: () -> Long,
            handleFrames: () -> Long
        ): Long {
            if (player?.isMoving == true || Interfaces.isOpen(1251)) {
                return randomDelay
            }

            if (Interfaces.isOpen(1370)) {
                MiniMenu.interact(ComponentAction.DIALOGUE.type, 0, -1, 89784350)
                ScriptConsole.println("Selecting 'Construct'")
                return randomDelay
            }
            if (makePlanks) {
                return handlePlanks()
            }
            if (makeRefinedPlanks) {
                return handleRefinedPlanks()
            }
            if (makeFrames) {
                return handleFrames()
            }
            return randomDelay
        }
    }

    fun handlePlanks(): Long {
        val bankchest = SceneObjectQuery.newQuery().name("Bank chest").option("Use").results()
        val sawmill = SceneObjectQuery.newQuery().name("Sawmill").option("Process planks").results()

        if (!sawmill.isEmpty && Backpack.containsItemByCategory(22)) {
            sawmill.first()?.interact("Process planks")
            ScriptConsole.println("Interacting with Sawmill")
            return randomDelay
        }
        if (!Backpack.containsItemByCategory(22)) {
            if (!bankchest.isEmpty) {
                bankchest.first()?.interact("Load Last Preset from")
                ScriptConsole.println("Interacting with Bank Chest")
                return randomDelay
            }
        }
        return randomDelay
    }

    fun handleRefinedPlanks(): Long {
        val bankchest = SceneObjectQuery.newQuery().name("Bank chest").option("Use").results()
        val sawmill = SceneObjectQuery.newQuery().name("Sawmill").option("Process planks").results()

        if (!sawmill.isEmpty && Backpack.getItems().any {
                it.name?.contains("plank", ignoreCase = true) == true &&
                        it.name?.contains("refined", ignoreCase = true) == false
            }) {
            sawmill.first()?.interact("Process planks")
            ScriptConsole.println("Interacting with Sawmill")
            return randomDelay
        }

        if (Backpack.getItems().none {
                it.name?.contains("plank", ignoreCase = true) == true &&
                        it.name?.contains("refined", ignoreCase = true) == false
            }) {
            if (!bankchest.isEmpty) {
                bankchest.first()?.interact("Load Last Preset from")
                ScriptConsole.println("Interacting with Bank Chest")
                return randomDelay
            }
        }

        return randomDelay
    }

    fun handleFrames(): Long {
        val bankchest = SceneObjectQuery.newQuery().name("Bank chest").option("Use").results()
        val sawmill = SceneObjectQuery.newQuery().name("Woodworking bench").option("Construct frames").results()

        if (!sawmill.isEmpty && Backpack.getItems().any { it.name?.contains("plank", ignoreCase = true) == true }) {
            sawmill.first()?.interact("Process planks")
            ScriptConsole.println("Interacting with Sawmill")
            return randomDelay
        }
        if (!Backpack.getItems().any { it.name?.contains("plank", ignoreCase = true) == true }) {
            if (!bankchest.isEmpty) {
                bankchest.first()?.interact("Load Last Preset from")
                ScriptConsole.println("Interacting with Bank Chest")
                return randomDelay
            }
        }
        return randomDelay
    }

}