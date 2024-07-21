package ImGui.Skills;

import net.botwithus.Runecrafting.PlayerInfo;
import net.botwithus.Runecrafting.SteamRunes;
import net.botwithus.rs3.game.login.LoginManager;
import net.botwithus.rs3.imgui.ImGui;
import ImGui.*;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.text.SimpleDateFormat;

import static ImGui.Theme.setStyleColor;
import static net.botwithus.Runecrafting.Abyss.*;
import static net.botwithus.Runecrafting.Astral.Astralrunes;
import static net.botwithus.Runecrafting.Astral.useAstralAltar;
import static net.botwithus.Runecrafting.Runecrafting.*;
import static net.botwithus.Runecrafting.SteamRunes.useSteamRunes;
import static net.botwithus.Slayer.Main.useBankPin;
import static net.botwithus.SnowsScript.startTime;
import static net.botwithus.SnowsScript.steamRunes;
import static net.botwithus.TaskScheduler.*;
import static net.botwithus.TaskScheduler.pin4;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.RingofDueling;

public class RunecraftingImGui {

    public static void renderRunecrafting() {
        if (isRunecraftingActive) {
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

            if (useAbyssRunecrafting) {
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
                hopDuetoPlayers = ImGui.Checkbox("Hop due to players", hopDuetoPlayers);
                if (ImGui.IsItemHovered()) {
                    ImGui.SetTooltip("Will hop worlds if there are any players in the current world");
                }
                ImGui.SeparatorText("Rune to Craft");
                ImGui.SetCursorPosX(spacing);
                craftNatureRunes = ImGui.Checkbox("Nature Runes", craftNatureRunes);

                ImGui.SameLine();

                ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
                craftBloodRunes = ImGui.Checkbox("Blood Runes", craftBloodRunes);

                ImGui.SameLine();

                ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
                craftWaterRunes = ImGui.Checkbox("Water Runes", craftWaterRunes);

                ImGui.SetCursorPosX(spacing);
                craftAirRunes = ImGui.Checkbox("Air Runes", craftAirRunes);

                ImGui.SameLine();

                ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
                craftEarthRunes = ImGui.Checkbox("Earth Runes", craftEarthRunes);

                ImGui.SameLine();

                ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
                craftFireRunes = ImGui.Checkbox("Fire Runes", craftFireRunes);

                ImGui.SetCursorPosX(spacing);
                craftChaosRunes = ImGui.Checkbox("Chaos Runes", craftChaosRunes);

                ImGui.SameLine();

                ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
                craftCosmicRunes = ImGui.Checkbox("Cosmic Runes", craftCosmicRunes);

                ImGui.SameLine();

                ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
                craftDeathRunes = ImGui.Checkbox("Death Runes", craftDeathRunes);

                ImGui.SetCursorPosX(spacing);
                craftLawRunes = ImGui.Checkbox("Law Runes", craftLawRunes);

                ImGui.SameLine();

                ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
                craftMindRunes = ImGui.Checkbox("Mind Runes", craftMindRunes);

                ImGui.SeparatorText("Statistics");

                displayNatureRunesInfo();
                displayMagicalThreadsInfo();
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
            if (!useSteamRunes && !useAbyssRunecrafting && !useAstralAltar) {


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
                /*if (useWorldhop) {

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
                }*/
            }
            if (useAstralAltar) {
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

                ImGui.SeparatorText("Statistics");
                displayAstralRunesInfo();

            }
            if (useSteamRunes) {
                ImGui.SetCursorPosX(spacing);
                ManageFamiliar = ImGui.Checkbox("Use Familiar?", ManageFamiliar);
                if (ImGui.IsItemHovered()) {
                    ImGui.SetTooltip("Will use Abyssal Titan or Abyssal lurker or Abyssal parasite");
                }

                displaySteamRunesInfo();
            }
            if (!useAbyssRunecrafting && !useAstralAltar) {

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
                ImGui.Text("Current World: " + LoginManager.getWorld());

                if (ImGui.BeginTable("Player Info", 3, ImGuiWindowFlag.None.getValue())) {
                    ImGui.TableNextRow();
                    ImGui.TableSetupColumn("Player Name", 0);
                    ImGui.TableSetupColumn("Time", 1);
                    ImGui.TableSetupColumn("World", 2);
                    ImGui.TableHeadersRow();

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss"); // Use this to format the time

                    for (PlayerInfo info : playerInfo) {
                        ImGui.TableNextRow();
                        ImGui.TableNextColumn();
                        ImGui.Text(info.getName());
                        ImGui.Separator();
                        ImGui.TableNextColumn();
                        ImGui.Text(timeFormat.format(new Date(info.getTime()))); // Use the formatter here
                        ImGui.Separator();
                        ImGui.TableNextColumn();
                        ImGui.Text(String.valueOf(info.getWorld()));
                        ImGui.Separator();
                    }
                    ImGui.EndTable();
                }
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

    public static void displayAstralRunesInfo() {

        Duration elapsedTime = Duration.between(startTime, Instant.now());
        long elapsedSeconds = elapsedTime.getSeconds();
        if (elapsedSeconds == 0) return;

        ImGui.Text("Crafted Runes Info:");
        for (Map.Entry<String, Integer> entry : Astralrunes.entrySet()) {
            float runesPerHour = (float) entry.getValue() / elapsedSeconds * 3600;
            ImGui.Text(entry.getKey() + ": " + entry.getValue() + " (" + String.format("%.2f", runesPerHour) + " per hour)");
        }
        int loopCount = getLoopCounter();
        ImGui.Text("Number of Runs: " + loopCount);
        float runsPerHour = calculatePerHour(elapsedTime, loopCount);
        ImGui.Text(String.format("Runs Per Hour: %.2f", runsPerHour));
    }


    public static void displayNatureRunesInfo() {

        Duration elapsedTime = Duration.between(startTime, Instant.now());
        long elapsedSeconds = elapsedTime.getSeconds();
        if (elapsedSeconds == 0) return;

        ImGui.Text("Crafted Runes Info:");
        for (Map.Entry<String, Integer> entry : runes.entrySet()) {
            float runesPerHour = (float) entry.getValue() / elapsedSeconds * 3600;
            ImGui.Text(entry.getKey() + ": " + entry.getValue() + " (" + String.format("%.2f", runesPerHour) + " per hour)");
        }
    }

    public static void displayMagicalThreadsInfo() {
        Duration elapsedTime = Duration.between(startTime, Instant.now());
        long elapsedSeconds = elapsedTime.getSeconds();
        if (elapsedSeconds == 0) return;

        ImGui.Text("Magical Threads Info:");
        for (Map.Entry<String, Integer> entry : magicalThreads.entrySet()) {
            float threadsPerHour = (float) entry.getValue() / elapsedSeconds * 3600;
            ImGui.Text(entry.getKey() + ": " + entry.getValue() + " (" + String.format("%.2f", threadsPerHour) + " per hour)");
        }

        int loopCount = getLoopCounter();
        ImGui.Text("Number of Runs: " + loopCount);
        float runsPerHour = calculatePerHour(elapsedTime, loopCount);
        ImGui.Text(String.format("Runs Per Hour: %.2f", runsPerHour));
    }
    public static void displaySteamRunesInfo() {
        Duration elapsedTime = Duration.between(startTime, Instant.now());
        long elapsedSeconds = elapsedTime.getSeconds();
        if (elapsedSeconds == 0) return; // Prevent division by zero

        ImGui.Text("Steam Runes Info:");
        for (Map.Entry<String, Integer> entry : steamRunes.entrySet()) {
            float runesPerHour = (float) entry.getValue() / elapsedSeconds * 3600;
            ImGui.Text(entry.getKey() + ": " + entry.getValue() + " (" + String.format("%.2f", runesPerHour) + " per hour)");
        }

        // Display the quantities of FIRE, WATER, and ASTRAL runes
        ImGui.Text("FIRE Runes Left: " + SteamRunes.Rune.FIRE.getQuantity());
        ImGui.Text("WATER Runes Left: " + SteamRunes.Rune.WATER.getQuantity());
        ImGui.Text("ASTRAL Runes Left: " + SteamRunes.Rune.ASTRAL.getQuantity());

        int loopCount = getLoopCounter();
        ImGui.Text("Number of Runs: " + loopCount);
        float runsPerHour = calculatePerHour(elapsedTime, loopCount);
        ImGui.Text(String.format("Runs Per Hour: %.2f", runsPerHour));
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
