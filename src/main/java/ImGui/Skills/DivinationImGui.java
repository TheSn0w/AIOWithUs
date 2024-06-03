package ImGui.Skills;

import net.botwithus.rs3.imgui.ImGui;
import ImGui.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static net.botwithus.Divination.Divination.count;
import static net.botwithus.SnowsScript.startTime;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.energy;

public class DivinationImGui {

    public static void renderDivination() {
        if (isDivinationActive && !showLogs) {
            if (tooltipsEnabled) {
                String[] texts = {
                        "1-99 AIO, will move from spot to spot automatically",
                        "have required quests complete, it follows wiki guide",
                        "if using familiar, have pouches/restore potions in preset",
                        "will bank at prif if out of pouches/restore potions",
                        "will use Load Last Preset from, make sure preset is set",
                        "if you like this script, consider looking at DivWithUs",
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
            ImGui.SeparatorText("Chronicles Captured Count");
            for (Map.Entry<String, Integer> entry : chroniclesCaughtCount.entrySet()) {
                ImGui.Text(entry.getKey() + ": " + entry.getValue());
            }

            int totalChroniclesCaptured = 0;
            for (int count : chroniclesCaughtCount.values()) {
                totalChroniclesCaptured += count;
            }

            long elapsedTime = Duration.between(startTime, Instant.now()).toMillis();
            double elapsedHours = elapsedTime / 1000.0 / 60.0 / 60.0;

            double chroniclesCapturedPerHour = totalChroniclesCaptured / elapsedHours;
            int chroniclesCapturedPerHourInt = (int) chroniclesCapturedPerHour;

            ImGui.Text("Chronicles Captured Per Hour: " + chroniclesCapturedPerHourInt);

            ImGui.SeparatorText("Energy Gathered Count");
            for (Map.Entry<String, Integer> entry : energy.entrySet()) {
                ImGui.Text(entry.getKey() + ": " + entry.getValue());
            }

            int totalEnergyGathered = 0;
            for (int count : energy.values()) {
                totalEnergyGathered += count;
            }

            double energyGatheredPerHour = totalEnergyGathered / elapsedHours;
            int energyGatheredPerHourInt = (int) energyGatheredPerHour;

            ImGui.Text("Energy Gathered Per Hour: " + energyGatheredPerHourInt);

            ImGui.SeparatorText("Divine Charges");
            ImGui.Text("Divine Charges Created: " + count);

            double countPerHour = count / elapsedHours;
            int countPerHourInt = (int) countPerHour;

            ImGui.Text("Per Hour: " + countPerHourInt);
        }
    }
}
