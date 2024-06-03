package ImGui.Skills;

import net.botwithus.SnowsScript;
import net.botwithus.rs3.imgui.ImGui;
import ImGui.*;
import java.util.Map;

import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.fishCookedCount;

public class CookingImGui {

    public static void renderCooking() {
        if (isCookingActive && !showLogs) {
            if (tooltipsEnabled) {
                float windowWidth = 400;
                String[] texts = {
                        "Have Preset Ready",
                        "if preset does not contain food, it will load preset 9",
                        "if preset 9 will not have food",
                        "it will manually withdraw highest tier fish",
                        "and will save the preset",
                        "Will use Bank chest or Bank booth to withdraw food",
                        "Use at a Portable Range or a cooking range such as",
                        "Catherby or Forts",
                        "if doing AIO, it will load new fish once existing fish is cooked",
                        "so have the right amount of fish for the levels required +5/10%",
                        "Will use any type of Cooking potion to boost cooking level"
                };

                ImGui.PushStyleColor(ImGuiCol.Text, 255, 255, 0, 1.0f);
                for (String text : texts) {
                    float textWidth = ImGui.CalcTextSize(text).getX();
                    float centeredStartPos = (windowWidth - textWidth) / 2;

                    ImGui.SetCursorPosX(centeredStartPos);
                    ImGui.Text(text);
                }

                ImGui.PopStyleColor(1);
            }
            ImGui.SeparatorText("Cooking Options");

            int totalFishCooked = 0;
            for (int count : fishCookedCount.values()) {
                totalFishCooked += count;
            }

            long endTime = System.currentTimeMillis();
            long startTime = SnowsScript.startTime.toEpochMilli();
            double hoursElapsed = (endTime - startTime) / 1000.0 / 60.0 / 60.0;
            double averageFishPerHour = totalFishCooked / hoursElapsed;
            ImGui.Text("Average fish cooked per hour: " + (int) averageFishPerHour);

            for (Map.Entry<String, Integer> entry : fishCookedCount.entrySet()) {
                ImGui.Text("Cooked: " + entry.getKey() + " - " + entry.getValue());
            }
        }
    }
}
