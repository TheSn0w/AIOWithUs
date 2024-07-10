package ImGui.Skills;

import net.botwithus.SnowsScript;
import net.botwithus.rs3.imgui.ImGui;
import ImGui.*;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;

import java.util.Map;

import static net.botwithus.Slayer.Main.useBankPin;
import static net.botwithus.TaskScheduler.*;
import static net.botwithus.TaskScheduler.pin4;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.fishCookedCount;

public class CookingImGui {

    public static void renderCooking() {
        if (isCookingActive) {
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
