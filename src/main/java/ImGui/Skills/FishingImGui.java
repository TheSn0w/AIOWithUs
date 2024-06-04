package ImGui.Skills;

import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.script.ScriptConsole;
import ImGui.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.startTime;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.fishCaughtCount;

public class FishingImGui {


    public static void renderFishing() {
        if (isFishingActive && !showLogs) {
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
    }
}
