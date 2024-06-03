package ImGui.Skills;

import net.botwithus.Combat.Combat;
import ImGui.*;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.NativeInteger;
import net.botwithus.rs3.script.ScriptConsole;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ImGui.PredefinedStrings.*;
import static net.botwithus.Combat.Combat.enableRadiusTracking;
import static net.botwithus.Combat.Combat.radius;
import static net.botwithus.SnowsScript.startTime;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.removeFoodName;

public class CombatImGui {

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
            Combat.setHealthThreshold(ImGui.InputInt("      Health : Prayer  ", Combat.getHealthPointsThreshold()));
            if (Combat.getHealthPointsThreshold() < 0) {
                Combat.setHealthThreshold(0);
            } else if (Combat.getHealthPointsThreshold() > 100) {
                Combat.setHealthThreshold(100);
            }

            ImGui.SameLine();
            ImGui.SetCursorPosX(spacing * 10 + checkboxWidth * 1);
            ImGui.SameLine();
            ImGui.SetItemWidth(110.0F);
            if (Combat.getPrayerPointsThreshold() < 0) {
                Combat.setPrayerPointsThreshold(0);
            } else if (Combat.getPrayerPointsThreshold() > 9900) {
                Combat.setPrayerPointsThreshold(9900);
            }

            int displayedThreshold = Combat.getPrayerPointsThreshold() / 10;

            int inputThreshold = ImGui.InputInt("", displayedThreshold);

            Combat.setPrayerPointsThreshold(inputThreshold * 10);


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

            ImGui.SetCursorPosX(spacing);
            animateDead = ImGui.Checkbox("Animate Dead", animateDead);

            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            usequickPrayers = ImGui.Checkbox("Quick Prayers", usequickPrayers);

            ImGui.SameLine();
            ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
            useScrimshaws = ImGui.Checkbox("Scrimshaws", useScrimshaws);

            ImGui.SetCursorPosX(spacing);
            enableRadiusTracking = ImGui.Checkbox("Enable Radius", enableRadiusTracking);

            if (VolleyofSouls) {
                ImGui.SetCursorPosX(spacing);
                ImGui.SetItemWidth(85.0F);
                Combat.VolleyOfSoulsThreshold = ImGui.InputInt("       Volley Stacks", Combat.VolleyOfSoulsThreshold);
                if (ImGui.IsItemHovered()) {
                    ImGui.SetTooltip("Stacks to cast at");
                }
                if (Combat.VolleyOfSoulsThreshold < 0) {
                    Combat.VolleyOfSoulsThreshold = 0;
                } else if (Combat.VolleyOfSoulsThreshold > 5) {
                    Combat.VolleyOfSoulsThreshold = 5;
                }
            }
            if (DeathGrasp) {
                ImGui.SetItemWidth(85.0F);
                Combat.NecrosisStacksThreshold = ImGui.InputInt("     Necrosis Stacks", Combat.NecrosisStacksThreshold);
                if (ImGui.IsItemHovered()) {
                    ImGui.SetTooltip("Stacks to cast at");
                }
                if (Combat.NecrosisStacksThreshold < 0) {
                    Combat.NecrosisStacksThreshold = 0;
                } else if (Combat.NecrosisStacksThreshold > 12) {
                    Combat.NecrosisStacksThreshold = 12;
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
                    ScriptConsole.println("Radius distance changed to: " + radius);
                }
                ImGui.SameLine();
                if (ImGui.Button("Set Center")) {
                    Combat.setCenterCoordinate(Client.getLocalPlayer().getCoordinate());
                }
            }
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
                    ScriptConsole.println("Predefined Enemy added: " + selectedName);
                    selectedItemIndex.set(0);
                } else {
                    ScriptConsole.println("Please select a valid enemy.");
                }
            }


            if (ImGui.BeginChild("Targets List", 360, 50, true, 0)) {
                int count = 0;
                for (String targetName : new ArrayList<>(getTargetNames())) {
                    if (count > 0 && count % 5 == 0) {
                        ImGui.Text("");
                    } else if (count > 0) {
                        ImGui.SameLine();
                    }

                    if (ImGui.Button(targetName)) {
                        removeTargetName(targetName);
                        break;
                    }

                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Click to remove this target");
                    }
                    count++;
                }
            }

            ImGui.EndChild();
        }
        if (isCombatActive && useLoot && !showLogs) {
            ImGui.SeparatorText("Loot Options");

            if (ImGui.Button("Add Item") && !Combat.getSelectedItem().isEmpty()) {
                Combat.getTargetItemNames().add(Combat.getSelectedItem());
                Combat.setSelectedItem("");
            }

            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Enter the name of the item to add to your list. Case-insensitive.");
            }
            ImGui.SameLine();
            ImGui.SetItemWidth(284.0F);
            Combat.setSelectedItem(ImGui.InputText("##Itemname", Combat.getSelectedItem()));

            List<String> comboItemsList = new ArrayList<>(LootList);
            comboItemsList.add(0, "                          Select Loot to Add");
            String[] comboItems = comboItemsList.toArray(new String[0]);

            NativeInteger selectedItemIndex = new NativeInteger(0);

            ImGui.SetItemWidth(360.0F);
            if (ImGui.Combo("##LootType", selectedItemIndex, comboItems)) {
                int selectedIndex = selectedItemIndex.get();

                if (selectedIndex > 0 && selectedIndex < comboItems.length) {
                    String selectedName = comboItems[selectedIndex];
                    Combat.getTargetItemNames().add(selectedName);
                    ScriptConsole.println("Predefined Loot added: " + selectedName);
                    selectedItemIndex.set(0);
                } else {
                    ScriptConsole.println("Please select a valid loot.");
                }
            }

            if (ImGui.BeginChild("Item List", 360, 100, true, 0)) {
                int count = 0;
                for (String itemName : new ArrayList<>(Combat.getTargetItemNames())) {
                    if (count > 0 && count % 5 == 0) {
                        ImGui.Text("");
                    } else if (count > 0) {
                        ImGui.SameLine();
                    }

                    if (ImGui.Button(itemName)) {
                        Combat.getTargetItemNames().remove(itemName);
                        break;
                    }

                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Click to remove this item");
                    }
                    count++;
                }
            }


            ImGui.EndChild();
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
                    ScriptConsole.println("Predefined Food added: " + selectedName);
                    selectedItemIndex.set(0);
                } else {
                    ScriptConsole.println("Please select a valid food.");
                }
            }

            if (ImGui.BeginChild("Food List", 355, 50, true, 0)) {
                int count = 0;
                for (String foodName : new ArrayList<>(getSelectedFoodNames())) {
                    if (count > 0 && count % 5 == 0) {
                        ImGui.Text("");
                    } else if (count > 0) {
                        ImGui.SameLine();
                    }

                    if (ImGui.Button(foodName)) {
                        removeFoodName(foodName);
                        break;
                    }

                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Click to remove this food");
                    }
                    count++;
                }
            }

            ImGui.EndChild();
        }

    }
}
