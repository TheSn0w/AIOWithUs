package ImGui.Skills;

import net.botwithus.Combat.Combat;
import ImGui.*;
import net.botwithus.Combat.*;
import net.botwithus.Variables.*;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.imgui.NativeInteger;
import net.botwithus.rs3.script.ScriptConsole;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static ImGui.PredefinedStrings.*;
import static ImGui.Theme.setStyleColor;
import static net.botwithus.Combat.Abilities.NecrosisStacksThreshold;
import static net.botwithus.Combat.Abilities.VolleyOfSoulsThreshold;
import static net.botwithus.Combat.Combat.*;
import static net.botwithus.Combat.ItemRemover.*;
import static net.botwithus.Combat.Notepaper.*;
import static net.botwithus.Combat.Radius.*;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.startTime;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.removeFoodName;

public class CombatImGui {

    private static String selectedItemToUseOnNotepaper = "";

    public static void renderCombat() {
        if (isCombatActive && !showLogs) {
            if (tooltipsEnabled) {
                String[] texts = {
                        "use nearest bank - will use a predefined",
                        "bank and load last preset",
                        "loot - will use loot interface to loot items",
                        "use loot all - will open loot inventory and loot everything",
                        "use POD - will use POD to train combat",
                        "Arch glacor, will use MAX GUILD and then",
                        "go to arch glacor and farm",
                        "if using arch glacor, only have 1st mechanic selected",
                        "make sure to set your target arch archglacor",
                        "radius, will set a radius around the player and stay inside",
                        "rest is self explanatory"
                };

                ImGui.PushStyleColor(ImGuiCol.Text, 255, 255, 0, 1.0f);

                for (String text : texts) {
                    float windowWidth = 400;
                    float textWidth = ImGui.CalcTextSize(text).getX();
                    float centeredStartPos = (windowWidth - textWidth) / 2;

                    ImGui.SetCursorPosX(centeredStartPos);
                    ImGui.Text(text);
                }

                ImGui.PopStyleColor(1);
            }

            ImGui.SeparatorText("Charms Obtained Count");
            List<Map<String, Integer>> allCharms = Arrays.asList(BlueCharms, CrimsonCharms, GreenCharms, GoldCharms);

            for (Map<String, Integer> charmMap : allCharms) {
                for (Map.Entry<String, Integer> entry : charmMap.entrySet()) {
                    long elapsedTime = Duration.between(startTime, Instant.now()).toMillis();
                    double elapsedHours = elapsedTime / 1000.0 / 60.0 / 60.0;

                    double charmsObtainedPerHour = entry.getValue() / elapsedHours;
                    int charmsObtainedPerHourInt = (int) charmsObtainedPerHour;

                    ImGui.Text(entry.getKey() + ": " + entry.getValue() + " - per hour: " + charmsObtainedPerHourInt);
                }
            }
            ImGui.SeparatorText("Attack Options");
            float totalWidth = 375.0f;
            float checkboxWidth = 105.0f;
            float numItems = 3.0f;
            float spacing = (totalWidth - (numItems * checkboxWidth)) / (numItems + 1);

            ImGui.SetCursorPosX(spacing);
            ImGui.SetItemWidth(110.0F);
            setHealthThreshold(ImGui.InputInt("      Health : Prayer  ", getHealthPointsThreshold()));
            if (getHealthPointsThreshold() < 0) {
                setHealthThreshold(0);
            } else if (getHealthPointsThreshold() > 100) {
                setHealthThreshold(100);
            }

            ImGui.SameLine();
            ImGui.SetCursorPosX(spacing * 10 + checkboxWidth * 1);
            ImGui.SameLine();
            ImGui.SetItemWidth(110.0F);
            if (getPrayerPointsThreshold() < 0) {
                setPrayerPointsThreshold(0);
            } else if (getPrayerPointsThreshold() > 9900) {
                setPrayerPointsThreshold(9900);
            }

            int displayedThreshold = getPrayerPointsThreshold() / 10;

            int inputThreshold = ImGui.InputInt("", displayedThreshold);

            setPrayerPointsThreshold(inputThreshold * 10);


            ImGui.SetCursorPosX(spacing);
            usePrayerPots = ImGui.Checkbox("Prayer Pots", usePrayerPots);
            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            useOverloads = ImGui.Checkbox("Overloads", useOverloads);
            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
            useAggroPots = ImGui.Checkbox("Aggro Pots", useAggroPots);

            ImGui.SetCursorPosX(spacing);
            useWeaponPoison = ImGui.Checkbox("Wep Poison", useWeaponPoison);
            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            scriptureofJas = ImGui.Checkbox("Jas Book", scriptureofJas);
            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
            scriptureofWen = ImGui.Checkbox("Wen Book", scriptureofWen);

            ImGui.SetCursorPosX(spacing);
            DeathGrasp = ImGui.Checkbox("EOF", DeathGrasp);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Do not have Finger of Death in Revo bar.");
            }
            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            SpecialAttack = ImGui.Checkbox("OmniGuard", SpecialAttack);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Have on Action Bar");
            }
            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
            VolleyofSouls = ImGui.Checkbox("Volley of Souls", VolleyofSouls);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Do not have Volley on Revo bar.");
            }
            ImGui.SetCursorPosX(spacing);
            InvokeDeath = ImGui.Checkbox("Invoke Death", InvokeDeath);

            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            SoulSplit = ImGui.Checkbox("Soul Split", SoulSplit);

            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
            KeepArmyup = ImGui.Checkbox("Army 24/7", KeepArmyup);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Have on Action Bar");
            }

            ImGui.SetCursorPosX(spacing);
            animateDead = ImGui.Checkbox("Animate Dead", animateDead);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Have on Action Bar");
            }

            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            usequickPrayers = ImGui.Checkbox("Quick Prayers", usequickPrayers);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Have Quick Prayers 1 on Action bar");
            }

            ImGui.SameLine();
            ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
            useScrimshaws = ImGui.Checkbox("Scrimshaws", useScrimshaws);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Activates and Deactivates in/out of combat");
            }

            ImGui.SetCursorPosX(spacing);
            enableRadiusTracking = ImGui.Checkbox("Enable Radius", enableRadiusTracking);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("sets a radius around current player location, and moves inside if walks out");
            }

            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            handleMultitarget = ImGui.Checkbox("Multi Target", handleMultitarget);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Attacks multiple targets when current target is below health threshold");
            }

            ImGui.SameLine();
            ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
            useVulnerabilityBombs = ImGui.Checkbox("Vuln Bombs", useVulnerabilityBombs);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Have Vulnerability Bombs on Action bar");
            }

            if (handleMultitarget) {
                ImGui.SetCursorPosX(spacing);
                ImGui.SetItemWidth(110.0F);

                int displayedHealthThreshold = (int) (getHealthThreshold() * 100);
                int inputHealthThreshold = ImGui.InputInt("target Threshold", displayedHealthThreshold);
                double newHealthThreshold = inputHealthThreshold / 100.0;


                if (newHealthThreshold != getHealthThreshold()) {
                    setHealthThreshold(newHealthThreshold);
                    ScriptConsole.println("Health threshold changed to: " + newHealthThreshold);
                }
                setHealthThreshold(inputHealthThreshold / 100.0);
                if (getHealthThreshold() < 0.0) {
                    setHealthThreshold(0.0);
                } else if (getHealthThreshold() > 1.0) {
                    setHealthThreshold(1.0);
                }
            }

            if (VolleyofSouls) {
                ImGui.SetCursorPosX(spacing);
                ImGui.SetItemWidth(85.0F);
                VolleyOfSoulsThreshold = ImGui.InputInt("       Volley Stacks", VolleyOfSoulsThreshold);
                if (ImGui.IsItemHovered()) {
                    ImGui.SetTooltip("Stacks to cast at");
                }
                if (VolleyOfSoulsThreshold < 0) {
                    VolleyOfSoulsThreshold = 0;
                } else if (VolleyOfSoulsThreshold > 5) {
                    VolleyOfSoulsThreshold = 5;
                }
            }
            if (DeathGrasp) {
                ImGui.SetItemWidth(85.0F);
                NecrosisStacksThreshold = ImGui.InputInt("     Necrosis Stacks", NecrosisStacksThreshold);
                if (ImGui.IsItemHovered()) {
                    ImGui.SetTooltip("Stacks to cast at");
                }
                if (NecrosisStacksThreshold < 0) {
                    NecrosisStacksThreshold = 0;
                } else if (NecrosisStacksThreshold > 12) {
                    NecrosisStacksThreshold = 12;
                }
            }
            if (enableRadiusTracking) {
                ImGui.SetItemWidth(85.0F);
                int newRadius = ImGui.InputInt("Radius (tiles)", radius);
                if (newRadius < 0) {
                    newRadius = 0;
                } else if (newRadius > 25) {
                    newRadius = 25;
                }
                if (newRadius != radius) {
                    radius = newRadius;
                    log("Radius distance changed to: " + radius);
                }
                ImGui.SameLine();
                if (ImGui.Button("Set Center")) {
                    setCenterCoordinate(Client.getLocalPlayer().getCoordinate());
                }
            }
            ImGui.Separator();
            ImGui.Separator();
            ImGui.Separator();
            ImGui.SeparatorText("Target Options");
            if (ImGui.Button("Add Target") && !targetName.isEmpty()) {
                addTargetName(targetName);
                addTarget(targetName);
                targetName = "";
            }
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Enter the name of the target to attack. Case-insensitive, partial names allowed.");
            }
            ImGui.SameLine();
            ImGui.SetItemWidth(273.0F);
            targetName = ImGui.InputText("##Targetname", targetName);

            List<String> comboItemsList = new ArrayList<>(CombatList);
            comboItemsList.add(0, "                          Select Enemy to Attack");
            String[] comboItems = comboItemsList.toArray(new String[0]);

            NativeInteger selectedItemIndex = new NativeInteger(0);

            ImGui.SetItemWidth(360.0f);
            if (ImGui.Combo("##EnemyType", selectedItemIndex, comboItems)) {
                int selectedIndex = selectedItemIndex.get();

                if (selectedIndex > 0 && selectedIndex < comboItems.length) {
                    String selectedName = comboItems[selectedIndex];
                    addTargetName(selectedName);
                    log("Predefined Enemy added: " + selectedName);
                    selectedItemIndex.set(0);
                } else {
                    log("Please select a valid enemy.");
                }
            }


            if (!getTargetNames().isEmpty()) {
                if (ImGui.BeginTable("Targets List", 2, ImGuiWindowFlag.None.getValue())) {
                    ImGui.TableNextRow();
                    ImGui.TableSetupColumn("Target Name", 0);
                    ImGui.TableSetupColumn("Action", 1);
                    ImGui.TableHeadersRow();

                    for (String targetName : new ArrayList<>(getTargetNames())) {
                        ImGui.TableNextRow();
                        ImGui.TableNextColumn();
                        ImGui.Text(targetName);
                        ImGui.TableNextColumn();
                        if (ImGui.Button("Remove##" + targetName)) {
                            removeTargetName(targetName);
                        }
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Click to remove this target");
                        }
                    }
                    ImGui.EndTable();
                }
            }
            ImGui.Separator();
            ImGui.Separator();
            ImGui.Separator();
        }
        if (isCombatActive && BankforFood && !showLogs) {
            ImGui.SeparatorText("Food Options");
            if (ImGui.Button("Add Food") && !getFoodName().isEmpty()) {
                addFoodName(getFoodName());
                setFoodName("");
            }

            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Enter the name of the food to add to your list. Case-insensitive.");
            }
            ImGui.SameLine();
            ImGui.SetItemWidth(272.0F);
            setFoodName(ImGui.InputText("##Foodname", getFoodName()));

            List<String> comboItemsList = new ArrayList<>(FoodList);
            comboItemsList.add(0, "                          Select Food to Add");
            String[] comboItems = comboItemsList.toArray(new String[0]);

            NativeInteger selectedItemIndex = new NativeInteger(0);

            ImGui.SetItemWidth(360.0F);
            if (ImGui.Combo("##FoodType", selectedItemIndex, comboItems)) {
                int selectedIndex = selectedItemIndex.get();

                if (selectedIndex > 0 && selectedIndex < comboItems.length) {
                    String selectedName = comboItems[selectedIndex];
                    addFoodName(selectedName);
                    log("Predefined Food added: " + selectedName);
                    selectedItemIndex.set(0);
                } else {
                    log("Please select a valid food.");
                }
            }

            if (!getSelectedFoodNames().isEmpty()) {
                if (ImGui.BeginTable("Food List", 2, ImGuiWindowFlag.None.getValue())) {
                    ImGui.TableNextRow();
                    ImGui.TableSetupColumn("Food Name", 0);
                    ImGui.TableSetupColumn("Action", 1);
                    ImGui.TableHeadersRow();

                    for (String foodName : new ArrayList<>(getSelectedFoodNames())) {
                        ImGui.TableNextRow();
                        ImGui.TableNextColumn();
                        ImGui.Text(foodName);
                        ImGui.TableNextColumn();
                        if (ImGui.Button("Remove##" + foodName)) {
                            removeFoodName(foodName);
                        }
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Click to remove this food");
                        }
                    }
                    ImGui.EndTable();
                }
            }
            ImGui.Separator();
            ImGui.Separator();
            ImGui.Separator();
        }
        if (isCombatActive && useNotepaper && !showLogs) {
            ImGui.SeparatorText("Notepaper Options");

            if (ImGui.Button("Add Notepaper") && !getNotepaperName().isEmpty()) {
                addNotepaperName(getNotepaperName());
                predefinedNotepaperNames.add(getNotepaperName());
                setNotepaperName("");
            }

            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Enter the name of the item to add to your list. Case-sensitive.");
            }

            ImGui.SameLine();
            ImGui.SetItemWidth(248.0F);

            setNotepaperName(ImGui.InputText("##Notepapername", getNotepaperName()));

            List<String> comboItemsList = new ArrayList<>();
            comboItemsList.add("Select an item...");
            for (String item : predefinedNotepaperNames) {
                if (item.toLowerCase().contains(getNotepaperName().toLowerCase())) {
                    comboItemsList.add(item);
                }
            }
            String[] comboItems = comboItemsList.toArray(new String[0]);
            NativeInteger selectedItemIndex = new NativeInteger(0);
            ImGui.SetItemWidth(361.0F);

            if (ImGui.Combo("##NotepaperType", selectedItemIndex, comboItems)) {
                int selectedIndex = selectedItemIndex.get();
                if (selectedIndex > 0 && selectedIndex < comboItems.length) {
                    String selectedName = comboItems[selectedIndex];
                    addNotepaperName(selectedName);
                    log("Predefined notepaper added: " + selectedName);
                    selectedItemIndex.set(0);
                }
            }

            if (!getSelectedNotepaperNames().isEmpty()) {
                if (ImGui.BeginTable("Notepaper List", 2, ImGuiWindowFlag.None.getValue())) {
                    ImGui.TableNextRow();

                    ImGui.TableSetupColumn("Notepaper Name", 0);
                    ImGui.TableSetupColumn("Action", 1);
                    ImGui.TableHeadersRow();

                    for (String notepaperName : new ArrayList<>(getSelectedNotepaperNames())) {
                        ImGui.TableNextRow();
                        ImGui.TableNextColumn();
                        ImGui.Text(notepaperName);
                        ImGui.TableNextColumn();
                        if (ImGui.Button("Remove##" + notepaperName)) {
                            removeNotepaperName(notepaperName);
                        }
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Click to remove this notepaper");
                        }
                    }
                    ImGui.EndTable();
                }
            }
            ImGui.Separator();
            ImGui.Separator();
            ImGui.Separator();
        }
        if (isCombatActive && useLoot && !showLogs) {
            ImGui.SeparatorText("Loot Options");

            if (ImGui.Button("Add Item") && !getSelectedItem().isEmpty()) {
                getTargetItemNames().add(getSelectedItem());
                setSelectedItem("");
            }

            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Enter the name of the item to add to your list. Case-insensitive.");
            }
            ImGui.SameLine();
            ImGui.SetItemWidth(284.0F);
            setSelectedItem(ImGui.InputText("##Itemname", getSelectedItem()));

            List<String> comboItemsList = new ArrayList<>(LootList);
            comboItemsList.add(0, "                          Select Loot to Add");
            String[] comboItems = comboItemsList.toArray(new String[0]);

            NativeInteger selectedItemIndex = new NativeInteger(0);

            ImGui.SetItemWidth(360.0F);
            if (ImGui.Combo("##LootType", selectedItemIndex, comboItems)) {
                int selectedIndex = selectedItemIndex.get();

                if (selectedIndex > 0 && selectedIndex < comboItems.length) {
                    String selectedName = comboItems[selectedIndex];
                    getTargetItemNames().add(selectedName);
                    log("Predefined Loot added: " + selectedName);
                    selectedItemIndex.set(0);
                } else {
                    log("Please select a valid loot.");
                }
            }

            if (!getTargetItemNames().isEmpty()) {
                if (ImGui.BeginTable("Item List", 2, ImGuiWindowFlag.None.getValue())) {
                    ImGui.TableNextRow();
                    ImGui.TableSetupColumn("Item Name", 0);
                    ImGui.TableSetupColumn("Action", 1);
                    ImGui.TableHeadersRow();

                    for (String itemName : new ArrayList<>(getTargetItemNames())) {
                        ImGui.TableNextRow();
                        ImGui.TableNextColumn();
                        ImGui.Text(itemName);
                        ImGui.TableNextColumn();
                        if (ImGui.Button("Remove##" + itemName)) {
                            getTargetItemNames().remove(itemName);
                        }
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Click to remove this item");
                        }
                    }
                    ImGui.EndTable();
                }
            }
            ImGui.Separator();
            ImGui.Separator();
            ImGui.Separator();
        }
        if (isCombatActive && isDropActive && !showLogs) {
            ImGui.SeparatorText("Drop Options");
            if (ImGui.Button("Add Item") && !getItemName().isEmpty()) {
                ItemRemover.addDroppedItemName(getItemName());
                setItemName("");
            }

            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Enter the name of the item to drop. Case-insensitive, partial names allowed.");
            }

            ImGui.SameLine();
            ImGui.SetItemWidth(273.0F);
            setItemName(ImGui.InputText("##DropItemname", getItemName()));

            if (!ItemRemover.getSelectedItems().isEmpty()) {
                if (ImGui.BeginTable("Dropped List", 2, ImGuiWindowFlag.None.getValue())) {
                    ImGui.TableNextRow();
                    ImGui.TableSetupColumn("Item Name", 0);
                    ImGui.TableSetupColumn("Action", 1);
                    ImGui.TableHeadersRow();

                    for (String itemName : new ArrayList<>(ItemRemover.getSelectedItems())) {
                        ImGui.TableNextRow();
                        ImGui.TableNextColumn();
                        ImGui.Text(itemName);
                        ImGui.TableNextColumn();
                        if (ImGui.Button("Remove##" + itemName)) {
                            ItemRemover.removeItemName(itemName);
                        }
                        if (ImGui.IsItemHovered()) {
                            ImGui.SetTooltip("Click to remove this item");
                        }
                    }
                    ImGui.EndTable();
                }
            }
            ImGui.Separator();
            ImGui.Separator();
            ImGui.Separator();
        }
    }
}