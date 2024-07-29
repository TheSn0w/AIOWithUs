package ImGui.Skills;

import net.botwithus.SnowsScript;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;
import ImGui.*;

import java.util.Map;

import static net.botwithus.SnowsScript.extraPotionsMade;
import static net.botwithus.SnowsScript.startingHerbloreXP;
import static net.botwithus.Variables.Variables.*;

public class HerbloreImGui {

    public static SnowsScript script;

    public static void renderHerblore() {
        if (isHerbloreActive) {
            if (tooltipsEnabled) {
                String[] texts = {
                        "Uses `Load Last Preset from` Bank chest",
                        "Needs backpack to be full when withdrawing",
                        "Uses Portable Well",
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

            ImGui.SeparatorText("Herblore Statistics");
            int currentLevel = Skills.HERBLORE.getSkill().getActualLevel();
            int levelsGained = currentLevel - Skills.HERBLORE.getActualLevel();

            ImGui.Text("Current Herblore Level: " + currentLevel + "  (" + levelsGained + " Gained)");
            int currentXP = Skills.HERBLORE.getSkill().getExperience();
            currentLevel = Skills.HERBLORE.getSkill().getActualLevel();
            int xpForNextLevel = Skills.HERBLORE.getExperienceAt(currentLevel + 1);
            int xpTillNextLevel = xpForNextLevel - currentXP;
            ImGui.Text("XP remaining: " + xpTillNextLevel);
            displayXPGained();
            displayXpPerHour();
            String timeToLevelStr = calculateTimeTillNextLevel();
            ImGui.Text(timeToLevelStr);

            ImGui.SeparatorText("Potions Made");
            displayInventory();

            ImGui.SeparatorText("Progress Bar");

            displayXpProgressBar();
        }
    }

    private static void displayXPGained() {
        int currentXP = Skills.HERBLORE.getSkill().getExperience();
        int xpGained = currentXP - startingHerbloreXP;;
        ImGui.Text("XP Gained: " + xpGained);
    }


    private static void displayXpPerHour() {
        double hoursElapsed = Stopwatch.getElapsedTime() / (1000.0 * 60 * 60);
        int currentXP = Skills.HERBLORE.getSkill().getExperience();
        int xpGained = currentXP - startingHerbloreXP;
        double xpPerHour = hoursElapsed > 0 ? xpGained / hoursElapsed : 0;
        String formattedXPPerHour = formatXP(xpPerHour);
        ImGui.Text("XP Per Hour: " + formattedXPPerHour);
    }

    private static String formatXP(double xp) {
        if (xp >= 1_000_000) {
            return String.format("%.0fm", xp / 1_000_000);
        } else if (xp >= 1_000) {
            return String.format("%.0fk", xp / 1_000);
        } else {
            return String.format("%.0f", xp);
        }
    }


    private static void displayXpProgressBar() {
        int currentXP = Skills.HERBLORE.getSkill().getExperience();
        int currentLevel = Skills.HERBLORE.getSkill().getActualLevel();
        int xpForNextLevel = Skills.HERBLORE.getExperienceAt(currentLevel + 1);
        int xpForCurrentLevel = Skills.HERBLORE.getExperienceAt(currentLevel);


        // Calculate the total XP needed to reach the next level from the current level
        int xpToNextLevel = xpForNextLevel - xpForCurrentLevel;
        // Calculate how much XP has been gained towards the next level
        int xpGainedTowardsNextLevel = currentXP - xpForCurrentLevel;
        // Calculate the progress towards the next level as a percentage
        float progress = (float) xpGainedTowardsNextLevel / xpToNextLevel;

        float[][] colors = {
                {1.0f, 0.0f, 0.0f, 1.0f}, // 0% Red
                {1.0f, 0.4f, 0.4f, 1.0f}, // 10% Lighter Red
                {1.0f, 0.6f, 0.0f, 1.0f}, // 20% Orange
                {1.0f, 0.7f, 0.4f, 1.0f}, // 30% Lighter Orange
                {1.0f, 1.0f, 0.0f, 1.0f}, // 40% Yellow
                {0.8f, 1.0f, 0.4f, 1.0f}, // 50% Very Light Green
                {0.6f, 1.0f, 0.6f, 1.0f}, // 60% Light Green
                {0.4f, 1.0f, 0.4f, 1.0f}, // 70% Green
                {0.3f, 0.9f, 0.3f, 1.0f}, // 80% Slightly Darker Green
                {0.2f, 0.8f, 0.2f, 1.0f}, // 90% Slightly More Darker Green
                {0.1f, 0.7f, 0.1f, 1.0f}  // 100% Even Darker Green (for completion)
        };

        // Calculate current color based on progress
        int index = (int) (progress * 10);
        float blend = (progress * 10) - index; // How much to blend with the next color
        if (index >= colors.length - 1) {
            index = colors.length - 2;
            blend = 1;
        }
        float[] startColor = colors[index];
        float[] endColor = colors[index + 1];
        float[] currentColor = {
                startColor[0] + blend * (endColor[0] - startColor[0]),
                startColor[1] + blend * (endColor[1] - startColor[1]),
                startColor[2] + blend * (endColor[2] - startColor[2]),
                1.0f
        };
        ImGui.PushStyleColor(42, currentColor[0], currentColor[1], currentColor[2], currentColor[3]);

        ImGui.PushStyleColor(0, RGBToFloat(), RGBToFloat(), RGBToFloat(), 0.0f);
        ImGui.ProgressBar(String.format("%.2f%%", progress * 100), progress, 200, 15);
        ImGui.PopStyleColor(2);

    }

    private static float RGBToFloat() {
        return 0 / 255.0f;
    }

    private static String calculateTimeTillNextLevel() {
        int currentXP = Skills.HERBLORE.getSkill().getExperience();
        int currentLevel = Skills.HERBLORE.getSkill().getActualLevel();
        int xpForNextLevel = Skills.HERBLORE.getExperienceAt(currentLevel + 1);
        int xpForCurrentLevel = Skills.HERBLORE.getExperienceAt(currentLevel);
        int xpGainedTowardsNextLevel = currentXP - xpForCurrentLevel;

        long timeElapsed = Stopwatch.getElapsedTime(); // Time elapsed since tracking started in milliseconds

        if (xpGainedTowardsNextLevel > 0 && timeElapsed > 0) {
            // Calculate the XP per millisecond
            double xpPerMillisecond = xpGainedTowardsNextLevel / (double) timeElapsed;
            // Estimate the time to level up in milliseconds
            long timeToLevelMillis = (long) ((xpForNextLevel - currentXP) / xpPerMillisecond);

            // Convert milliseconds to hours, minutes, and seconds
            long timeToLevelSecs = timeToLevelMillis / 1000;
            long hours = timeToLevelSecs / 3600;
            long minutes = (timeToLevelSecs % 3600) / 60;
            long seconds = timeToLevelSecs % 60;

            return String.format("Time to level: %02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return "Time to level: calculating...";
        }
    }

    public static void displayInventory() {
        for (Map.Entry<String, Integer> entry : SnowsScript.inventoryMap.entrySet()) {
            ImGui.Text("Item Name: " + entry.getKey() + ", Quantity: " + entry.getValue());
        }

        // Display the count of extra potions made
        String category = "Extra Potions";
        int extraPotionsCount = extraPotionsMade.getOrDefault(category, 0);
        ImGui.Text(category + ": " + extraPotionsCount);
    }
}
