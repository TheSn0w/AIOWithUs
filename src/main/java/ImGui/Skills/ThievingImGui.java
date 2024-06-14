package ImGui.Skills;

import net.botwithus.rs3.imgui.ImGui;
import ImGui.*;

import java.util.Map;

import static net.botwithus.Variables.Variables.*;

public class ThievingImGui {

    private static long startTime = System.currentTimeMillis();


    public static void renderThieving() {
        if (isThievingActive && !showLogs) {
            ImGui.SeparatorText("Thieving Options");
            if (tooltipsEnabled) {
                String[] texts = {
                        "Currently does 1-5 @ Pompous Merchant",
                        "Currently does 5-42 @ Bakery Stall",
                        "Currently does 42-82 @ Crux Druid",
                        "Currently does 83-99 @ Crux Knight",
                        "Crystal Mask Support + Lightform",
                        "just have on action bar",
                        "Will Bank to Load Last Preset for food",
                        "have 4-8 pieces of food in preset to allow space",
                        "if you like this script, consider looking at Pzoots Thiever",
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
            for (Map.Entry<String, Integer> entry : materialTypes.entrySet()) {
                ImGui.Text(entry.getKey() + ": " + entry.getValue());
            }

            int totalMaterialTypes = 0;
            for (int count : materialTypes.values()) {
                totalMaterialTypes += count;
            }

            double elapsedHours = (System.currentTimeMillis() - startTime) / 1000.0 / 60.0 / 60.0;

            double materialTypesPerHour = totalMaterialTypes / elapsedHours;
            int materialTypesPerHourInt = (int) materialTypesPerHour;

            ImGui.Text("Items stolen Per Hour: " + materialTypesPerHourInt);
        }
    }
}
