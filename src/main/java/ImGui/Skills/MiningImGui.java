package ImGui.Skills;

import net.botwithus.Combat.ItemRemover;
import net.botwithus.rs3.imgui.ImGui;
import ImGui.*;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.imgui.NativeInteger;
import net.botwithus.rs3.imgui.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ImGui.PredefinedStrings.MiningList;
import static net.botwithus.Combat.ItemRemover.isDropActive;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Slayer.Main.useBankPin;
import static net.botwithus.TaskScheduler.*;
import static net.botwithus.TaskScheduler.pin4;
import static net.botwithus.Variables.Variables.*;

public class MiningImGui {

    public static void renderMining() {
        if (isMiningActive) {
            if (tooltipsEnabled) {
                String[] texts = {
                        "start anywhere, will move to the closest rock",
                        "will interact with Rockertunity",
                        "have ores on action bar for faster dropping",
                        "use nearest bank will use the closest bank when backpack full",
                        "will empty ore box",
                        "if you like this script, consider looking at MineWithUs",
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
            if (useBankPin) {
                if (ImGui.Begin("Bank Pin Settings", ImGuiWindowFlag.NoNav.getValue() | ImGuiWindowFlag.NoResize.getValue())) {
                    ImGui.SeparatorText("Pin Options");
                    ImGui.SetCursorPosX(15);
                    ImGui.SetItemWidth(100.0F);
                    pin1 = ImGui.InputInt("Pin 1", pin1);
                    ImGui.SetItemWidth(100.0F);
                    ImGui.SameLine();
                    pin2 = ImGui.InputInt("Pin 2", pin2);
                    ImGui.SetItemWidth(100.0F);
                    ImGui.SameLine();
                    pin3 = ImGui.InputInt("Pin 3", pin3);
                    ImGui.SetItemWidth(100.0F);
                    ImGui.SameLine();
                    pin4 = ImGui.InputInt("Pin 4", pin4);
                }
                ImGui.End();
            }
            ImGui.SeparatorText("Mining Options");
            if (ImGui.Button("Add Rock Name")) {
                addRockName(getRockName());
                log("Rock name added: " + getRockName());
                setRockName("");
            }
            ImGui.SameLine();
            ImGui.SetItemWidth(245.0F);
            Rock = ImGui.InputText("##Rock Name", getRockName());

            List<String> comboItemsList = new ArrayList<>(MiningList);
            comboItemsList.add(0, "                          Select Rock to Mine");
            String[] comboItems = comboItemsList.toArray(new String[0]);

            NativeInteger selectedItemIndex = new NativeInteger(0);
            ImGui.SetItemWidth(365.0F);

            if (ImGui.Combo("##RockType", selectedItemIndex, comboItems)) {
                int selectedIndex = selectedItemIndex.get();

                if (selectedIndex > 0 && selectedIndex < comboItems.length) {
                    String selectedName = comboItems[selectedIndex];
                    addRockName(selectedName);
                    log("Predefined Rock added: " + selectedName);
                    selectedItemIndex.set(0);
                } else {
                    log("Please select a valid rock.");
                }
            }

            if (ImGui.BeginChild("Selected Rock Names", 365, 43, true, 0)) {
                ImGui.SetCursorPosX(10.0f);
                ImGui.SetCursorPosY(10.0f);

                List<String> selectedRocks = new ArrayList<>(getSelectedRockNames());
                float itemSpacing = 10.0f;
                float lineSpacing = 10.0f;
                float buttonHeight = 20.0f;
                float windowWidth = 365.0f;

                float cursorPosX = 10.0f;
                float cursorPosY = 10.0f;

                for (String rock : selectedRocks) {
                    Vector2f textSize = ImGui.CalcTextSize(rock);
                    float buttonWidth = textSize.getX();

                    if (cursorPosX + buttonWidth > windowWidth) {
                        cursorPosX = 10.0f;
                        cursorPosY += buttonHeight + lineSpacing;
                    }

                    ImGui.SetCursorPosX(cursorPosX);
                    ImGui.SetCursorPosY(cursorPosY);

                    ImGui.PushStyleVar(ImGuiStyleVar.ButtonTextAlign, 0.5f, 0.5f);
                    ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, 1.0f, 1.0f);

                    if (ImGui.Button(rock)) {
                        removeRockName(rock);
                        log("Rock name removed: " + rock);
                    }

                    ImGui.PopStyleVar(2);
                    cursorPosX += buttonWidth + itemSpacing;
                }
                ImGui.EndChild();
            }
            /*if (useGote) {
                ImGui.SeparatorText("GOTE/Porter Options");
                ImGui.SetItemWidth(200.0F);
                if (ImGui.Combo("Type of Porter", currentPorterType, porterTypes)) {
                    int selectedIndex = currentPorterType.get();
                    if (selectedIndex >= 0 && selectedIndex < porterTypes.length) {
                        String selectedPorterType = porterTypes[selectedIndex];
                        log("Selected porter type: " + selectedPorterType);
                    } else {
                        log("Please select a valid porter type.");
                    }
                }
                ImGui.SetItemWidth(200.0F);

                if (ImGui.Combo("# of Porters to withdraw", currentQuantity, quantities)) {
                    int selectedIndex = currentQuantity.get();
                    if (selectedIndex >= 0 && selectedIndex < quantities.length) {
                        String selectedQuantity = quantities[selectedIndex];
                        log("Selected quantity: " + selectedQuantity);
                    } else {
                        log("Please select a valid quantity.");
                    }
                }
                ImGui.SeparatorText("Set Porter Withdraw Threshold from bank");
                ImGui.SetItemWidth(100.0F);
                int newThreshold = getBankingThreshold();
                newThreshold = ImGui.InputInt("##ChargeThreshold", newThreshold);
                if (newThreshold < 0) {
                    newThreshold = 0;
                } else if (newThreshold > 2000) {
                    newThreshold = 2000;
                }
                setChargeThreshold(newThreshold);
                if (ImGui.IsItemHovered()) {
                    ImGui.SetTooltip("if banking due to full inv, it will withdraw porters if porter count is below this threshold");
                }

                ImGui.SeparatorText("Set Porter Equip Charge Threshold from Inventory");
                ImGui.SetItemWidth(100.0F);
                int newEquipThreshold = getGraceChargesThreshold();
                newEquipThreshold = ImGui.InputInt("##EquipChargeThreshold", newEquipThreshold);
                if (newEquipThreshold < 0) {
                    newEquipThreshold = 0;
                } else if (newEquipThreshold > 2000) {
                    newEquipThreshold = 2000;
                }
                setEquipChargeThreshold(newEquipThreshold);
                if (ImGui.IsItemHovered()) {
                    ImGui.SetTooltip("Will equip/charge porters when the current one reaches this threshold OR will bank if none are available in backpack");
                }
            }

            ImGui.SeparatorText("Ores Mined Count");
            for (Map.Entry<String, Integer> entry : types.entrySet()) {
                String itemName = entry.getKey();
                int itemCount = entry.getValue();
                ImGui.Text(itemName + ": " + itemCount);
            }*/
        }
        if (isMiningActive && isDropActive) {
            ImGui.SeparatorText("Drop Options");
            if (ImGui.Button("Add Item") && !ItemRemover.getDroppeditems().isEmpty()) {
                ItemRemover.addDroppedItemName(ItemRemover.getDroppeditems());
                ItemRemover.setDroppednames("");
            }

            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Enter the name of the item to drop. Case-insensitive, partial names allowed.");
            }

            ImGui.SameLine();
            ImGui.SetItemWidth(273.0F);
            ItemRemover.setDroppednames(ImGui.InputText("##Itemname", ItemRemover.getDroppeditems()));

            if (!ItemRemover.getSelectedDroppedItems().isEmpty()) {
                if (ImGui.BeginTable("Items List", 2, ImGuiWindowFlag.None.getValue())) {
                    ImGui.TableNextRow();
                    ImGui.TableSetupColumn("Item Name", 0);
                    ImGui.TableSetupColumn("Action", 1);
                    ImGui.TableHeadersRow();

                    for (String itemName : new ArrayList<>(ItemRemover.getSelectedDroppedItems())) {
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
        }
    }
}
