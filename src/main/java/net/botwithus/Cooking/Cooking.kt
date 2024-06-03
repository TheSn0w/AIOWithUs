package net.botwithus.Cooking

import net.botwithus.CustomLogger.log
import net.botwithus.api.game.hud.inventories.Backpack
import net.botwithus.api.game.hud.inventories.Bank
import net.botwithus.rs3.game.Client
import net.botwithus.rs3.game.hud.interfaces.Interfaces
import net.botwithus.rs3.game.minimenu.MiniMenu
import net.botwithus.rs3.game.minimenu.actions.ComponentAction
import net.botwithus.rs3.game.minimenu.actions.SelectableAction
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery
import net.botwithus.rs3.game.scene.entities.characters.player.Player
import net.botwithus.rs3.game.scene.entities.`object`.SceneObject
import net.botwithus.rs3.game.skills.Skills
import net.botwithus.rs3.script.Execution
import java.util.*

class Cooking {
    private val random: Random = Random()
    private val randomDelay = random.nextLong(1500, 3000)
    private val player = Client.getLocalPlayer()



    fun handleCooking(): Long {
        if (player?.isMoving == true || Interfaces.isOpen(1251)) {
            return randomDelay
        }

        if (Interfaces.isOpen(1370)) {
            MiniMenu.interact(ComponentAction.DIALOGUE.type, 0, -1, 89784350)
            log("[Cooking] Selecting 'Cook All'")
            return randomDelay
        }

        val rangeAndOption = findCookingRange()

        if (rangeAndOption != null) {
            val (range, option) = rangeAndOption
            if (range != null && (Backpack.containsItemByCategory(57) || Backpack.contains(53120))) {
                if (range.interact(option)) {
                    log("[Cooking] Interacting with: " + range.name)
                    return randomDelay
                }
            }
        }

        handleBankOperations(player, randomDelay)

        return randomDelay
    }

    private fun findCookingRange(): Pair<SceneObject?, String>? {
        val playerLocation = Client.getLocalPlayer()?.coordinate

        val range = SceneObjectQuery.newQuery()
            .name("Portable range")
            .results()
            .nearest()

        if (range != null && playerLocation != null && range.distanceTo(playerLocation) <= 15.0) {
            return Pair(range, "Cook")
        }

        val portableRange = SceneObjectQuery.newQuery()
            .name("Range")
            .results()
            .nearest()

        if (portableRange != null && playerLocation != null && portableRange.distanceTo(playerLocation) <= 15.0) {
            return Pair(portableRange, "Cook-at")
        }

        return null
    }


    fun cookingPotion(): Long {
        if (player?.animationId == 18000) {
            return randomDelay
        }
        if (Backpack.containsItemByCategory(69)) {
            val backpackItems = Backpack.getItems()

            if (backpackItems.isNotEmpty()) {
                val item = backpackItems.first()

                val itemName = item.name
                if (itemName != null) {

                    log("[Cooking] Interacting with item: $itemName")

                    Backpack.interact(itemName, "Drink")
                }
            }
        }
        return randomDelay
    }

    private fun handleBankOperations(player: Player?, randomDelay: Long): Long {
        if (player?.isMoving == true) {
            return randomDelay
        }

        val bankAndOption = findBankBooth()

        if (bankAndOption != null) {
            val (bank, option) = bankAndOption
            if (bank != null) {
                return handleBankInteractions(bank, option, randomDelay)
            } else {
                log("[Error] Bank not found")
            }
        }

        return randomDelay
    }

    private fun handleBankInteractions(bank: SceneObject, option: String, randomDelay: Long): Long {
        if (bank.interact("Load Last Preset from")) {
            log("[Cooking] Interacting with Bank using 'Load Last Preset'")
            Execution.delayUntil(10000) { !Backpack.containsItemByCategory(58) }

            if (Backpack.containsItemByCategory(57)) {
                return randomDelay
            } else {
                log("[Error] No food in backpack - Loading preset 9")
                Bank.loadPreset(9)
                Execution.delayUntil(10000) { Backpack.containsItemByCategory(57) }

                if (Backpack.containsItemByCategory(57)) {
                    return randomDelay
                } else {
                    log("[Error] Still no food in backpack - creating new preset")
                    return handleBankWithdrawals(Pair(bank, option), randomDelay)
                }
            }
        }
        return randomDelay
    }

    private fun handleBankWithdrawals(bankAndOption: Pair<SceneObject, String>, randomDelay: Long): Long {
        val (bank, option) = bankAndOption
        if (bank.interact(option)) {
            log("[Cooking] Interacting with Bank using $option action")
            Execution.delay(randomDelay)
            if (waitForBankToOpen()) {
                Execution.delay(randomDelay)
                handleDeposits()
                Execution.delay(randomDelay)
                handleWithdrawals()
                Execution.delay(randomDelay)
                MiniMenu.interact(ComponentAction.COMPONENT.type, 2, 1, 33882231)
                Execution.delay(randomDelay)
                return randomDelay
            }
        }
        return randomDelay
    }


    private fun findBankBooth(): Pair<SceneObject?, String>? {
        val bankBooth = SceneObjectQuery.newQuery()
            .name("Bank booth")
            .option("Bank")
            .results()
            .nearest()

        if (bankBooth != null) {
            return Pair(bankBooth, "Bank")
        }

        val bankChest = SceneObjectQuery.newQuery()
            .name("Bank chest")
            .option("Use")
            .results()
            .nearest()

        if (bankChest != null) {
            return Pair(bankChest, "Use")
        }

        return null
    }


    private fun waitForBankToOpen(): Boolean {
        val isBankOpen = Execution.delayUntil(30000) { Bank.isOpen() }
        if (isBankOpen) {
            log("[Cooking] Bank interface is open")
        }
        return isBankOpen
    }


    private fun handleDeposits() {
        val itemsInBackpack = Backpack.getItems()
        val itemCounts = itemsInBackpack
            .groupBy { it.name }
            .map { (name, items) -> name to items.sumOf { it.stackSize } }

        if (itemCounts.isNotEmpty()) {
            log("[Cooking] Depositing items:")
            itemCounts.forEach { (name, stackSize) ->
                log("[Cooking] - $name (Stack size: $stackSize)")
            }
            Bank.depositAll()
            Execution.delay(randomDelay)
        } else {
            log("[Error] No depositable items found in the Backpack")
        }
    }


    private fun handleWithdrawals() {
        val cookingLevel = Skills.COOKING.actualLevel
        log("[Cooking] Cooking level: $cookingLevel")

        val fishMap = mapOf(
            1 to listOf("Crayfish", "Shrimps", "Karambwanji", "Sardine", "Anchovies", "Poison Karambwan"),
            5 to listOf("Herring"),
            10 to listOf("Mackerel"),
            15 to listOf("Trout"),
            18 to listOf("Cod"),
            20 to listOf("Pike"),
            25 to listOf("Salmon"),
            28 to listOf("Slimy Eel"),
            30 to listOf("Tuna", "Karambwan"),
            35 to listOf("Rainbow Fish"),
            38 to listOf("Cave Eel"),
            40 to listOf("Lobster"),
            43 to listOf("Bass"),
            45 to listOf("Swordfish"),
            52 to listOf("Desert Sole"),
            53 to listOf("Lava Eel"),
            60 to listOf("Catfish"),
            62 to listOf("Monkfish"),
            72 to listOf("Beltfish", "Green Blubber Jellyfish"),
            80 to listOf("Shark"),
            82 to listOf("Sea Turtle"),
            84 to listOf("Great White Shark", "Great Maki"),
            88 to listOf("Cavefish"),
            91 to listOf("Manta Ray", "Fish Oil", "Great Gunkan"),
            93 to listOf("Rocktail", "Rocktail Soup"),
            94 to listOf("Arc Gumbo"),
            95 to listOf("Tiger Shark", "Blue Blubber Jellyfish"),
            96 to listOf("Wobbegong Oil", "Shark Soup"),
            99 to listOf("Sailfish", "Sailfish Soup")
        ).mapValues { (_, fishList) ->
            fishList.map { fish ->
                "Raw ${fish.lowercase(Locale.getDefault())}"
            }
        }

        val sortedFishMap = fishMap.toSortedMap(compareByDescending { it })

        val availableFish = sortedFishMap.filterKeys { it <= cookingLevel }.values.flatten().distinct()

        val category57Items = InventoryItemQuery.newQuery()
            .category(57)
            .results()

        var foodWithdrawn = false
        for (fishList in sortedFishMap.values) {
            if (foodWithdrawn) break

            for (fish in fishList) {
                if (availableFish.contains(fish)) {
                    log("[Cooking] Should be cooking: $fish")
                    for (item in category57Items) {
                        if (item.name == fish && item.stackSize > 1) {
                            log("[Cooking] Withdrawing: ${item.name} (Stack size: ${item.stackSize})")
                            Bank.withdraw(item.name, 1)
                            foodWithdrawn = true
                            break
                        }
                    }
                    if (!foodWithdrawn) {
                        log("[Error] $fish does not exist in the bank.")
                    }
                }
                if (foodWithdrawn) break
            }
        }

        if (!foodWithdrawn) {
            log("[Error] No available fish found in the bank.")
        }
    }

    fun useGrapesOnJugOfWater(): Long {
        val bankChest = SceneObjectQuery.newQuery().name("Bank chest").option("Use").results()
        if (Interfaces.isOpen(1251)) {
            return randomDelay
        }
        if (Interfaces.isOpen(1370)) {
            MiniMenu.interact(ComponentAction.DIALOGUE.type, 0, -1, 89784350)
            log("[Cooking] Selecting 'Brew'")
            return randomDelay
        }

        val grapes = InventoryItemQuery.newQuery(93)
            .name("Grapes")
            .results()
            .firstOrNull()

        if (grapes == null) {
            log("[Error] Grapes not found in inventory, Loading preset.")
            bankChest.nearest()?.interact("Load Last Preset from")
            return random.nextLong(650, 1000)
        }

        // Retrieve Jug of water from the inventory
        val jugOfWater = InventoryItemQuery.newQuery(93)
            .name("Jug of water")
            .results()
            .firstOrNull()

        if (jugOfWater == null) {
            log("[Error] Jug of water not found in inventory.")
            return random.nextLong(650, 1000)
        }

        log("[Cooking] Found Grapes and Jug of water. Preparing to use.")

        val grapesSelected = MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.type, 0, grapes.slot, 96534533)
        Execution.delay(random.nextLong(500, 750))

        if (grapesSelected) {
            log("[Cooking] Using Grapes on Jug of water...")
            val jugOfWaterSelected =
                MiniMenu.interact(SelectableAction.SELECT_COMPONENT_ITEM.type, 0, jugOfWater.slot, 96534533)
            Execution.delay(random.nextLong(500, 750))

            if (jugOfWaterSelected) {
                log("[Cooking] Grapes successfully used on Jug of water.")
            } else {
                log("[Error] Failed to use Grapes on Jug of water.")
            }
        } else {
            log("[Error] Failed to select Grapes.")
        }
        return random.nextLong(650, 1000)
    }
}
