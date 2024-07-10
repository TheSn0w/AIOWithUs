package ImGui.Skills;

import net.botwithus.Combat.ItemRemover;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import ImGui.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.botwithus.Combat.ItemRemover.isDropActive;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.Slayer.Main.useBankPin;
import static net.botwithus.SnowsScript.startTime;
import static net.botwithus.TaskScheduler.*;
import static net.botwithus.TaskScheduler.pin4;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.fishCaughtCount;

public class FishingImGui {


    public static void renderFishing() {
        if (isFishingActive) {
            if (tooltipsEnabled) {
                String[] texts = {
                        "start anywhere, will move to the closest spot",
                        "have fish on action bar for faster dropping",
                        "use nearest bank will use the closest bank when backpack full",
                        "will only load last preset, so make sure you have items on preset",
                        "if you like this script, consider looking at FishWithUs",
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
            ImGui.SeparatorText("Fishing Options");
            if (ImGui.Button("Add Fishing Action")) {
                String actionInput = getFishingAction();
                if (actionInput != null && !actionInput.trim().isEmpty()) {
                    addFishingAction(actionInput);
                    log("Fishing action added: " + actionInput);
                    setFishingAction("");
                } else {
                    log("Invalid fishing action.");
                }
            }
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("I.E Lure, or Net");
            }
            ImGui.SameLine();

            ImGui.SetItemWidth(227.0F);
            ImGui.PushStyleVar(ImGuiStyleVar.FrameBorderSize, 2.0f);
            String actionInput = ImGui.InputText("##Fishing Action", getFishingAction());
            ImGui.PopStyleVar(1);
            setFishingAction(actionInput);
            if (ImGui.Button("Add Fishing Location")) {
                String locationInput = getFishingLocation();
                if (locationInput != null && !locationInput.trim().isEmpty()) {
                    addFishingLocation(locationInput);
                    log("Fishing location added: " + locationInput);
                    setFishingLocation("");
                } else {
                    log("Invalid fishing location.");
                }
            }
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("I.E Fishing spot, or Fishing ID");
            }
            ImGui.SameLine();
            ImGui.SetItemWidth(214.0F);
            String locationInput = ImGui.InputText("##Fishing Location", getFishingLocation());
            setFishingLocation(locationInput);

            if (ImGui.BeginTable("Selected Fishing", 3, ImGuiWindowFlag.None.getValue())) {
                ImGui.TableNextRow();
                ImGui.TableSetupColumn("Fishing Action", 0);
                ImGui.TableSetupColumn("Fishing Location", 1);
                ImGui.TableSetupColumn("Action", 2);
                ImGui.TableHeadersRow();

                List<String> selectedActions = new ArrayList<>(getSelectedFishingActions());
                List<String> selectedLocations = new ArrayList<>(getSelectedFishingLocations());

                for (int i = 0; i < Math.max(selectedActions.size(), selectedLocations.size()); i++) {
                    ImGui.TableNextRow();
                    ImGui.TableNextColumn();
                    ImGui.Text(i < selectedActions.size() ? selectedActions.get(i) : "");
                    ImGui.TableNextColumn();
                    ImGui.Text(i < selectedLocations.size() ? selectedLocations.get(i) : "");
                    ImGui.TableNextColumn();
                    if (i < selectedActions.size() && ImGui.Button("Remove Action##" + selectedActions.get(i))) {
                        removeFishingAction(selectedActions.get(i));
                        log("Fishing action removed: " + selectedActions.get(i));
                    }
                    if (i < selectedLocations.size() && ImGui.Button("Remove Location##" + selectedLocations.get(i))) {
                        removeFishingLocation(selectedLocations.get(i));
                        log("Fishing location removed: " + selectedLocations.get(i));
                    }
                }
                ImGui.EndTable();
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
            }*/

            /*ImGui.Text("Selected Fishing Actions:");*/

            ImGui.SeparatorText("Fish Caught Count");
            for (Map.Entry<String, Integer> entry : fishCaughtCount.entrySet()) {
                ImGui.Text(entry.getKey() + ": " + entry.getValue());
            }

            int totalFishCaught = 0;
            for (int count : fishCaughtCount.values()) {
                totalFishCaught += count;
            }

            long elapsedTime = Duration.between(startTime, Instant.now()).toMillis();
            double elapsedHours = elapsedTime / 1000.0 / 60.0 / 60.0;

            double fishCaughtPerHour = totalFishCaught / elapsedHours;
            int fishCaughtPerHourInt = (int) fishCaughtPerHour;

            ImGui.Text("Fish Caught Per Hour: " + fishCaughtPerHourInt);
        }
        if (isFishingActive && isDropActive && !showLogs) {
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
            ImGui.Separator();
            ImGui.Separator();
            ImGui.Separator();
        }
    }
}
