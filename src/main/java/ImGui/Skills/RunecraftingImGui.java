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
                        "Will log out if Backpack does not contain Impure Essence",
                        "YOU MUST HAVE PASSING BRACELET",
                        "Must choose GOTE or Castle Wars",
                        "If you are using familiar option",
                        "You do not need familiar and Super restore in preset",
                        "but you do need them in bank",
                        "Soul altar will only work with protean essence",
                        "if soul altar, start next to it",
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
            ManageFamiliar = ImGui.Checkbox("Use Familiar?", ManageFamiliar);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Will use Abyssal Titan or Abyssal lurker or Abyssal parasite");
            }
            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            Powerburst = ImGui.Checkbox("Use Powerburst", Powerburst);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Will use Powerburst of Sorcery, must have on action bar");
            }


            ImGui.SetCursorPosX(spacing);
            notWearingRing = ImGui.Checkbox("Passing bracelet in Backpack?", notWearingRing);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("have passing bracelet in Backpack and select this and have on actionbar");
            }

            ImGui.SetCursorPosX(spacing);
            WearingRing = ImGui.Checkbox("Passing bracelet is Equipped?", WearingRing);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("if you have equipped passing bracelet, select this");
            }

            ImGui.SetCursorPosX(spacing);
            RingofDueling = ImGui.Checkbox("RoD", RingofDueling);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("if you have Ring of Dueling, select this,doesnt matter where if its in backpack or not");
            }

            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            useGraceoftheElves = ImGui.Checkbox("Gote", useGraceoftheElves);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Have Grace of the Elves Equipped and `Deep sea fishing hub` Teleport selected");
            }

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
