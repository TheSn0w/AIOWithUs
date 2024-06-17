package ImGui.Skills;

import net.botwithus.Runecrafting.PlayerInfo;
import net.botwithus.rs3.imgui.ImGui;
import ImGui.*;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static ImGui.Theme.setStyleColor;
import static net.botwithus.Runecrafting.Runecrafting.*;
import static net.botwithus.Runecrafting.Runecrafting.player;
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
                ImGui.SetTooltip("Will use Powerburst of Sorcery");
            }


            ImGui.SetCursorPosX(spacing);
            notWearingRing = ImGui.Checkbox("Passing bracelet in Backpack?", notWearingRing);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Have passing bracelet in Backpack and select this");
            }

            ImGui.SetCursorPosX(spacing);
            WearingRing = ImGui.Checkbox("Passing bracelet is Equipped?", WearingRing);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("If you have equipped passing bracelet, select this");
            }

            ImGui.SetCursorPosX(spacing);
            RingofDueling = ImGui.Checkbox("RoD", RingofDueling);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("if you have Ring of Dueling, select this,doesnt matter equipped or backpack");
            }

            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            useGraceoftheElves = ImGui.Checkbox("Gote", useGraceoftheElves);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Have Grace of the Elves Equipped and `Deep sea fishing hub` Teleport selected");
            }

            ImGui.SeparatorText("World Hopping - Experimental");
            ImGui.SetCursorPosX(spacing);
            useWorldhop = ImGui.Checkbox("World Hop", useWorldhop);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Will hop worlds randomly between the set interval");
            }
            ImGui.SameLine();
            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            hopDuetoPlayers = ImGui.Checkbox("Hop due to players", hopDuetoPlayers);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Will hop worlds if there are any players in the current world");
            }
            if (useWorldhop) {

                long timeRemaining = nextWorldHopTime - System.currentTimeMillis();
                if (timeRemaining > 0) {
                    String remainingTimeFormatted = formatTimeRemaining(timeRemaining);
                    ImGui.Text("Next hop in: " + remainingTimeFormatted);
                } else {
                    ImGui.Text("Ready to hop worlds...");
                }
                ImGui.Text("World Hop Settings:");
                ImGui.SetItemWidth(100.0f);
                minHopIntervalMinutes = ImGui.InputInt("Min Hop Interval (Minutes)", minHopIntervalMinutes);
                if (minHopIntervalMinutes < 1) {
                    minHopIntervalMinutes = 1;
                } else if (minHopIntervalMinutes > maxHopIntervalMinutes) {
                    minHopIntervalMinutes = maxHopIntervalMinutes;
                }
                if (ImGui.IsItemHovered()) {
                    ImGui.SetTooltip("Set the minimum interval for hopping worlds. The script will select a random time between these two values for each hop.");
                }
                ImGui.SetItemWidth(100.0f);
                maxHopIntervalMinutes = ImGui.InputInt("Max Hop Interval (Minutes)", maxHopIntervalMinutes);
                if (maxHopIntervalMinutes < minHopIntervalMinutes) {
                    maxHopIntervalMinutes = minHopIntervalMinutes;
                } else if (maxHopIntervalMinutes > 300) {
                    maxHopIntervalMinutes = 300;
                }

                if (ImGui.IsItemHovered()) {
                    ImGui.SetTooltip("Set the maximum interval for hopping worlds. The script will select a random time between these two values for each hop.");
                }
            }

            ImGui.SeparatorText("Statistics");
            displayLoopCountAndRunesPerHour(determineSelectedRuneType());

            ImGui.SeparatorText("Warnings");

            String[] texts = {
                    "WARNING",
                    "RUNECRAFTING IS VERY RISKY",
                    "USE AT YOUR OWN RISK",
                    "DO NOT BOT LONG PERIODS OF TIME",
                    "PEOPLE WILL REPORT YOU",
            };

            setStyleColor(ImGuiCol.Text, 255, 0, 0, 255);

            for (String text : texts) {
                float windowWidth = 400;
                float textWidth = ImGui.CalcTextSize(text).getX();
                float centeredStartPos = (windowWidth - textWidth) / 2;

                ImGui.SetCursorPosX(centeredStartPos);
                ImGui.Text(text);
            }

            ImGui.PopStyleColor(1);

            ImGui.SeparatorText("Players Encountered");

            // Begin a table with the specified number of columns
            if (ImGui.BeginTable("Player Info", 3, ImGuiWindowFlag.None.getValue())) {
                ImGui.TableNextRow();
                ImGui.TableSetupColumn("Player Name", 0);
                ImGui.TableSetupColumn("Time", 1);
                ImGui.TableSetupColumn("World", 2);
                ImGui.TableHeadersRow();

                for (PlayerInfo info : playerInfo) {
                    ImGui.TableNextRow();
                    ImGui.TableNextColumn();
                    ImGui.Text(info.getName());
                    ImGui.Separator();
                    ImGui.TableNextColumn();
                    ImGui.Text(new Date(info.getTime()).toString());
                    ImGui.Separator();
                    ImGui.TableNextColumn();
                    ImGui.Text(String.valueOf(info.getWorld()));
                    ImGui.Separator();
                }
                ImGui.EndTable();
            }
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
    private static String formatTimeRemaining(long millis) {
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
