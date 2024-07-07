package ImGui.Skills;

import ImGui.*;
import net.botwithus.Combat.*;
import net.botwithus.Variables.Runnables;
import net.botwithus.Variables.Variables;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.imgui.NativeInteger;
import net.botwithus.rs3.script.ScriptConsole;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static ImGui.PredefinedStrings.*;
import static net.botwithus.Combat.Combat.*;
import static net.botwithus.Combat.CombatManager.*;
import static net.botwithus.Combat.Familiar.*;
import static net.botwithus.Combat.ItemRemover.*;
import static net.botwithus.Combat.NPCs.getNpcTableData;
import static net.botwithus.Combat.Notepaper.*;
import static net.botwithus.Combat.Potions.*;
import static net.botwithus.Combat.Radius.*;
import static net.botwithus.Combat.Travel.*;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.*;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.removeFoodName;

public class CombatImGui {

    public static boolean showCheckboxesWindow = false;
    public static boolean showNearbyNPCS = false;
    public static boolean showAllLoot = false;


    public static void renderCombat() {
        if (isCombatActive) {
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
            float windowWidth = 400;
            String buttonText = "Show Options in New Window?";
            float textWidth = ImGui.CalcTextSize(buttonText).getX();
            float padding = (windowWidth - textWidth) / 2;
            ImGui.PushStyleColor(ImGuiCol.Border, 0, 0, 0, 0);
            ImGui.PushStyleColor(ImGuiCol.BorderShadow, 0, 0, 0, 0);
            ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, padding, 2.0f);
            ImGui.SetCursorPosX(padding);
            ImGui.SetCursorPosX(0);
            if (ImGui.Button(buttonText)) {
                showCheckboxesWindow = !showCheckboxesWindow;
            }
            float windowWidth1 = 400;
            String buttonText1 = "Show Nearby NPCs?";
            float textWidth1 = ImGui.CalcTextSize(buttonText1).getX();
            float padding1 = (windowWidth1 - textWidth1) / 2;
            ImGui.PushStyleColor(ImGuiCol.Border, 0, 0, 0, 0);
            ImGui.PushStyleColor(ImGuiCol.BorderShadow, 0, 0, 0, 0);
            ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, padding1, 2.0f);
            ImGui.SetCursorPosX(padding1);
            ImGui.SetCursorPosX(0);
            if (ImGui.Button(buttonText1)) {
                showNearbyNPCS = !showNearbyNPCS;
            }
            float windowWidth2 = 400;
            String buttonText2 = "Show All Looted Items?";
            float textWidth2 = ImGui.CalcTextSize(buttonText2).getX();
            float padding2 = (windowWidth2 - textWidth2) / 2;
            ImGui.PushStyleColor(ImGuiCol.Border, 0, 0, 0, 0);
            ImGui.PushStyleColor(ImGuiCol.BorderShadow, 0, 0, 0, 0);
            ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, padding2, 2.0f);
            ImGui.SetCursorPosX(padding2);
            ImGui.SetCursorPosX(0);
            if (ImGui.Button(buttonText2)) {
                showAllLoot = !showAllLoot;
            }
            ImGui.PopStyleVar(3);
            ImGui.PopStyleColor(6);
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
            useWeaponSpecialAttack = ImGui.Checkbox("EOF", useWeaponSpecialAttack);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Do not have Finger of Death in Revo bar.");
            }
            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            useEssenceofFinality = ImGui.Checkbox("OmniGuard", useEssenceofFinality);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Have on Action Bar");
            }
            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
            useVolleyofSouls = ImGui.Checkbox("Volley of Souls", useVolleyofSouls);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Do not have Volley on Revo bar.");
            }
            ImGui.SetCursorPosX(spacing);
            useInvokeDeath = ImGui.Checkbox("Invoke Death", useInvokeDeath);

            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            SoulSplit = ImGui.Checkbox("Soul Split", SoulSplit);

            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
            useConjureUndeadArmy = ImGui.Checkbox("Army 24/7", useConjureUndeadArmy);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Have on Action Bar");
            }

            ImGui.SetCursorPosX(spacing);
            Variables.useAnimateDead = ImGui.Checkbox("Animate Dead", Variables.useAnimateDead);
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
                ImGui.SetTooltip("sets a radius around current player location, will walk back if accidentally moved out of, Reload if disabled!");
            }

            ImGui.SameLine();
            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            useDarkness = ImGui.Checkbox("Darkness", useDarkness);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Have Darkness on Action bar");
            }

            ImGui.SameLine();
            ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
            useVulnerabilityBomb = ImGui.Checkbox("Vuln Bombs", useVulnerabilityBomb);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Have Vulnerability Bombs on Action bar");
            }

            ImGui.SetCursorPosX(spacing);
            useThreadsofFate = ImGui.Checkbox("Threads of Fate", useThreadsofFate);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Have Threads of Fate on Action bar, will use on CD");
            }
            ImGui.SameLine();
            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            useDwarfcannon = ImGui.Checkbox("Dwarf Cannon", useDwarfcannon);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Will ONLY do Dwarven Siege Engine");
            }
            ImGui.SameLine();
            ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
            useElvenRitual = ImGui.Checkbox("Elven Ritual", useElvenRitual);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("will use from backpack when prayer points are below threshold");
            }
            ImGui.SetCursorPosX(spacing);
            useExcalibur = ImGui.Checkbox("Excalibur", useExcalibur);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("will use excalibur when health below threshold");
            }
            ImGui.SameLine();
            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            useDemonSlayer = ImGui.Checkbox("Demon Slayer", useDemonSlayer);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Will use on CD");
            }
            ImGui.SameLine();
            ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
            useDragonSlayer = ImGui.Checkbox("Dragon Slayer", useDragonSlayer);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Will use on CD");
            }
            ImGui.SetCursorPosX(spacing);
            useUndeadSlayer = ImGui.Checkbox("Undead Slayer", useUndeadSlayer);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Will use on CD");
            }
            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            usePowderOfProtection = ImGui.Checkbox("Protection", usePowderOfProtection);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Powder of Protection, Will use on CD");
            }
            ImGui.SameLine();
            ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
            usePowderOfPenance = ImGui.Checkbox("Penance", usePowderOfPenance);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Powder of Penance, Will use on CD");
            }
            ImGui.SetCursorPosX(spacing);
            useKwuarmSticks = ImGui.Checkbox("Kwuarm Stick", useKwuarmSticks);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Have atleast 10, will overload and boost to 30 and then keep topped up");
            }
            ImGui.SameLine();
            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            useIritSticks = ImGui.Checkbox("Irit Stick", useIritSticks);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Have atleast 10, will overload and boost to 30 and then keep topped up");
            }
            ImGui.SameLine();
            ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
            useLantadymeSticks = ImGui.Checkbox("Lantadyme Stick", useLantadymeSticks);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Have atleast 10, will overload and boost to 30 and then keep topped up");
            }

            ImGui.SetCursorPosX(spacing);
            handleMultitarget = ImGui.Checkbox("Multi Target", handleMultitarget);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Attacks multiple targets when current target is below health threshold");
            }

            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            useFamiliarForCombat = ImGui.Checkbox("Familiar", useFamiliarForCombat);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Will summon familiar if pouch in inventory and has summoning points, if low summoning points, will try drink a restore potion, if none available will bank if Bank is enabled");
            }

            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);

            useFamiliarScrolls = ImGui.Checkbox("Familiar Scrolls", useFamiliarScrolls);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Will use scrolls on familiar if available, make sure your familiar tab is open");
            }

            ImGui.SeparatorText("Checkbox Configs");


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

            if (useVolleyofSouls) {
                ImGui.SetCursorPosX(spacing);
                ImGui.SetItemWidth(85.0F);
                VolleyOfSoulsThreshold = ImGui.InputInt("Volley Stacks", VolleyOfSoulsThreshold);
                if (ImGui.IsItemHovered()) {
                    ImGui.SetTooltip("Stacks to cast at");
                }
                if (VolleyOfSoulsThreshold < 0) {
                    VolleyOfSoulsThreshold = 0;
                } else if (VolleyOfSoulsThreshold > 5) {
                    VolleyOfSoulsThreshold = 5;
                }
            }
            if (useWeaponSpecialAttack) {
                ImGui.SetItemWidth(85.0F);
                NecrosisStacksThreshold = ImGui.InputInt("Necrosis Stacks", NecrosisStacksThreshold);
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
            if (showNearbyNPCS) {
                if (ImGui.Begin("NPCs Nearby", ImGuiWindowFlag.NoNav.getValue() | ImGuiWindowFlag.NoResize.getValue())) {
                    ImGui.SetWindowSize((float) 600, (float) 225);
                    ImGui.SeparatorText("Target Options");
                    List<List<String>> tableData = getNpcTableData();

                    ImGui.SetItemWidth(600);

                    if (ImGui.ListBoxHeader("", 569, 0)) {
                        ImGui.Columns(1, "NPCs names", true);
                        for (int i = 0; i < tableData.size(); i++) {
                            List<String> row = tableData.get(i);
                            String npcName = row.get(0);

                            String npcIdentifier = npcName + "##" + i;

                            // Check if the npcName is already in the CombatList
                            if (!CombatList.contains(npcName)) {
                                ImGui.Selectable(npcIdentifier, false, 0);
                                if (ImGui.IsItemClicked(ImGui.MouseButton.LEFT_BUTTON)) {
                                    CombatList.add(npcName); // Add the npcName to the CombatList
                                    addTargetName(npcName);
                                    ScriptConsole.println("Added " + npcName + " to combat list.");
                                }
                            }

                            ImGui.NextColumn();
                        }
                        ImGui.Columns(1, "Column", false);
                        ImGui.ListBoxFooter();
                    }

                    ImGui.End();
                }
            }
            if (showAllLoot) {
                if (ImGui.Begin("Looted Items", ImGuiWindowFlag.NoNav.getValue() | ImGuiWindowFlag.NoResize.getValue())) {
                    ImGui.SetWindowSize((float) 600, (float) 225);
                    ImGui.SeparatorText("Items Looted");

                    ImGui.SetItemWidth(600);

                    if (ImGui.ListBoxHeader("", 569, 0)) {
                        ImGui.Columns(3, "Looted Items", true);
                        ImGui.Text("Items Looted"); // Header for the first column
                        ImGui.NextColumn();
                        ImGui.Text("Amount"); // Header for the second column
                        ImGui.NextColumn();
                        ImGui.Text("Items Per Hour"); // Header for the third column
                        ImGui.NextColumn();
                        ImGui.Separator();
                        Duration elapsedTime = Duration.between(startTime, Instant.now());
                        long elapsedSeconds = elapsedTime.getSeconds();
                        for (Map.Entry<String, Integer> entry : lootedItems.entrySet()) {
                            ImGui.Text(entry.getKey()); // Item name
                            ImGui.NextColumn();
                            ImGui.Text(String.valueOf(entry.getValue())); // Item count
                            ImGui.NextColumn();
                            // Calculate items per hour
                            float itemsPerHour = elapsedSeconds > 0 ? (float) entry.getValue() / elapsedSeconds * 3600 : 0;
                            ImGui.Text(String.valueOf((int)itemsPerHour)); // Items per hour
                            ImGui.NextColumn();
                            ImGui.Separator(); // Separator for each line
                        }
                        ImGui.Columns(1, "Column", false);
                        ImGui.ListBoxFooter();
                    }

                    ImGui.End();
                }
            }
            if (useTraveltoLocation) {
                ImGui.SeparatorText("Travel Options");
                ImGui.SetCursorPosX(spacing);
                ImGui.SetItemWidth(115.0F);
                x = ImGui.InputInt("X", x);
                ImGui.SetItemWidth(115.0F);
                y = ImGui.InputInt("Y", y);
                ImGui.SetItemWidth(115.0F);
                z = ImGui.InputInt("Z", z);
                if (ImGui.Button("Travel")) {
                    Runnables.shouldTravel = true;
                }
            }
            if (showCheckboxesWindow) {
                if (ImGui.Begin("Combat Settings", ImGuiWindowFlag.NoNav.getValue() | ImGuiWindowFlag.NoResize.getValue())) {
                    ImGui.SetWindowSize((float) 400, (float) 510);
                    ImGui.SeparatorText("Target Options");
                    if (ImGui.Button("Add Target") && !targetName.isEmpty()) {
                        addTargetName(targetName);
                        CombatList.add(targetName);
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
                    comboItemsList.add(0, "                          Saved Enemy Names");
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
                                ImGui.Separator();
                                ImGui.TableNextColumn();
                                ImGui.Text(targetName);
                                ImGui.Separator();
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
                }
                if (isCombatActive && BankforFood) {
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
                                ImGui.Separator();
                                ImGui.TableNextColumn();
                                ImGui.Text(foodName);
                                ImGui.Separator();
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
                }
                if (isCombatActive && useNotepaper) {
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
                                ImGui.Separator();
                                ImGui.TableNextColumn();
                                ImGui.Text(notepaperName);
                                ImGui.Separator();
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
                }
                if (isCombatActive && isDropActive) {
                    ImGui.SeparatorText("Drop Options");

                    // Input text for item name
                    ImGui.SetItemWidth(273.0F);
                    droppednames = ImGui.InputText("##DropItemname", droppednames);
                    ImGui.SameLine();

                    // Button to add item
                    if (ImGui.Button("Add Item") && !droppednames.isEmpty()) {
                        ItemRemover.addDroppedItemName(droppednames);
                        droppednames = "";
                    }

                    // Tooltip for the input field
                    if (ImGui.IsItemHovered()) {
                        ImGui.SetTooltip("Enter the name of the item to drop. Case-insensitive, partial names allowed.");
                    }

                    // Display selected items in a table
                    if (!ItemRemover.getSelectedDroppedItems().isEmpty()) {
                        if (ImGui.BeginTable("Dropped List", 2, ImGuiWindowFlag.None.getValue())) {
                            ImGui.TableNextRow();

                            ImGui.TableSetupColumn("Item Name", 0);
                            ImGui.TableSetupColumn("Action", 1);
                            ImGui.TableHeadersRow();

                            for (String itemName : new ArrayList<>(ItemRemover.getSelectedDroppedItems())) {
                                ImGui.TableNextRow();
                                ImGui.Separator();
                                ImGui.TableNextColumn();
                                ImGui.Text(itemName);
                                ImGui.Separator();
                                ImGui.TableNextColumn();

                                // Button to remove item
                                if (ImGui.Button("Remove##" + itemName)) {
                                    ItemRemover.removeItemName(itemName);
                                }
                                // Tooltip for the remove button
                                if (ImGui.IsItemHovered()) {
                                    ImGui.SetTooltip("Click to remove this item");
                                }
                            }
                            ImGui.EndTable();
                        }
                    }
                }
                if (isCombatActive && useCustomLoot) {
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
                                ImGui.Separator();
                                ImGui.TableNextColumn();
                                ImGui.Text(itemName);
                                ImGui.Separator();
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
                }
                ImGui.End();
            }
        }
    }
}