package ImGui.Skills;

import net.botwithus.rs3.imgui.ImGui;
import ImGui.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static net.botwithus.Runecrafting.Runecrafting.getRuneQuantities;
import static net.botwithus.SnowsScript.startTime;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.RingofDueling;

public class RunecraftingImGui {

    public static void renderRunecrafting() {
        if (isRunecraftingActive && !showLogs) {
            if (tooltipsEnabled) {
                String[] texts = {
                        "Select your option and it will run",
                        "Will log out if Backpack is not full after Banking",
                        "have all stuff on action bar",
                        "Have restore potions in preset if using familiar",
                        "Soul altar will only work with protean essence",
                        "if soul altar, start next to it",
                        "YOU MUST HAVE PASSING BRACELET",
                        "you have to choose a ring choice",
                        "either keep passing bracelet in backpack or wear it",
                        "unless your doing soul altar",
                        "if you like this script, consider looking at RCWithUs",
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
            ImGui.SeparatorText("Runecrafting Options");
            float totalWidth = 360.0f;
            float checkboxWidth = 100.0f;
            float numItems = 3.0f;
            float spacing = (totalWidth - (numItems * checkboxWidth)) / (numItems + 1);

            ImGui.SetCursorPosX(spacing);
            ManageFamiliar = ImGui.Checkbox("Familiar", ManageFamiliar);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Will use level 93 Beast of Burden");
            }
            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            Powerburst = ImGui.Checkbox("Powerburst", Powerburst);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Will use Powerburst of Sorcery, have on action bar");
            }
            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
            notWearingRing = ImGui.Checkbox("Backpack Bracelet", notWearingRing);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("have passing bracelet in Backpack and select this and have on actionbar");
            }

            ImGui.SetCursorPosX(spacing);
            WearingRing = ImGui.Checkbox("Wearing Bracelet", WearingRing);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("if you have equipped passing bracelet, select this");
            }
            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            RingofDueling = ImGui.Checkbox("Ring of Dueling", RingofDueling);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("if you have Ring of Dueling, select this,doesnt matter where if its in backpack or not");
            }

                        ImGui.SameLine();

                        ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
                        useGraceoftheElves = ImGui.Checkbox("Grace of the Elves", useGraceoftheElves);


            ImGui.SeparatorText("Statistics");
            displayLoopCountAndRunesPerHour(determineSelectedRuneType());
        }
    }
    private static void displayLoopCountAndRunesPerHour(String selectedRuneType) {
        int loopCount = getLoopCounter();
        ImGui.Text("Number of Runs: " + loopCount);

        Duration elapsedTime = Duration.between(startTime, Instant.now());

        float runsPerHour = calculatePerHour(elapsedTime, loopCount);
        ImGui.Text(String.format("Runs Per Hour: %.2f", runsPerHour));

        if (!selectedRuneType.equals("None")) {
            Map<String, Integer> runeQuantities = getRuneQuantities();
            Integer quantity = runeQuantities.getOrDefault(selectedRuneType, 0);

            float runesPerHour = calculatePerHour(elapsedTime, quantity);
            ImGui.Text("Rune Type: " + selectedRuneType);
            ImGui.Text("Runes Crafted: " + quantity);
            ImGui.Text(String.format("Per Hour: %.2f", runesPerHour));
        }
    }

    private static float calculatePerHour(Duration elapsed, int quantity) {
        long elapsedSeconds = elapsed.getSeconds();
        if (elapsedSeconds == 0) return 0;
        return (float) quantity / elapsedSeconds * 3600;
    }
    private static String determineSelectedRuneType() {
        if (HandleSpiritAltar) return "Spirit Runes";
        if (HandleBoneAltar) return "Bone Runes";
        if (HandleMiasmaAltar) return "Miasma Runes";
        if (HandleFleshAltar) return "Flesh Runes";
        return "None";
    }
}
