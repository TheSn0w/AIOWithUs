package ImGui.Skills;

import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;
import ImGui.*;

import java.util.Map;

import static net.botwithus.SnowsScript.startingThievingLevel;
import static net.botwithus.SnowsScript.startingThievingXP;
import static net.botwithus.Variables.Variables.*;

public class ThievingImGui {

    private static long startTime = System.currentTimeMillis();
    public static boolean thievingAuto = false;
    public static boolean doPompousMerchants = false;
    public static boolean doBakeryStall = false;
    public static boolean doCruxDruids = false;
    public static boolean doCruxknights = false;
    public static boolean enableLightform = false;
    public static boolean enableCrystalMask = false;
    public static boolean enableEatFood = false;


    public static void renderThieving() {
        if (isThievingActive) {
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
            float totalWidth = 375.0f;
            float checkboxWidth = 105.0f;
            float numItems = 3.0f;
            float spacing = (totalWidth - (numItems * checkboxWidth)) / (numItems + 1);
            ImGui.SetCursorPosX(spacing);
            thievingAuto = ImGui.Checkbox("Thieving Auto", thievingAuto);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Will automatically thieve based on your current level");
            }

            ImGui.SetCursorPosX(spacing);
            doPompousMerchants = ImGui.Checkbox("Merchant", doPompousMerchants);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Will do Pompous Merchants in Taverly");
            }
            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            doBakeryStall = ImGui.Checkbox("Bakery Stall", doBakeryStall);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Will do Bakery Stalls in Lumbridge");
            }
            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
            doCruxDruids = ImGui.Checkbox("Crux Druids", doCruxDruids);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Will do Crux Druids in Hets Oasis");
            }

            ImGui.SetCursorPosX(spacing);
            doCruxknights = ImGui.Checkbox("Crux Knights", doCruxknights);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Will do Crux Knights in Hets Oasis");
            }

            ImGui.SeparatorText("Thieving Support");

            ImGui.SetCursorPosX(spacing);
            enableLightform = ImGui.Checkbox("Lightform", enableLightform);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Have Lightform on your action bar");
            }
            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 2 + checkboxWidth);
            enableCrystalMask = ImGui.Checkbox("Crystal Mask", enableCrystalMask);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Have Crystal Mask on your action bar");
            }
            ImGui.SameLine();

            ImGui.SetCursorPosX(spacing * 3 + checkboxWidth * 2);
            enableEatFood = ImGui.Checkbox("Eat Food", enableEatFood);
            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Will eat food if health is low, and then bank when no food is left");
            }

            ImGui.SeparatorText("Thieving Loot");

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

            ImGui.SeparatorText("Thieving Statistics");
            int startingLevel = startingThievingLevel;
            int levelsGained = Skills.THIEVING.getActualLevel() - startingLevel;

            ImGui.Text("Current Thieving Level: " + Skills.THIEVING.getSkill().getActualLevel() + "  (" + levelsGained + " Gained)");
            int currentXP = Skills.THIEVING.getSkill().getExperience();
            int xpForNextLevel = Skills.THIEVING.getExperienceAt(Skills.THIEVING.getSkill().getActualLevel() + 1);
            int xpTillNextLevel = xpForNextLevel - currentXP;
            ImGui.Text("XP remaining: " + xpTillNextLevel);
            displayXPGained();
            displayXpPerHour();
            String timeToLevelStr = calculateTimeTillNextLevel();
            ImGui.Text(timeToLevelStr);

            ImGui.SeparatorText("Progress Bar");

            displayXpProgressBar();
        }
    }

    private static void displayXPGained() {
        int currentXP = Skills.THIEVING.getSkill().getExperience();
        int xpGained = currentXP - startingThievingXP;
        ;
        ImGui.Text("XP Gained: " + xpGained);
    }


    private static void displayXpPerHour() {
        double hoursElapsed = Stopwatch.getElapsedTime() / (1000.0 * 60 * 60);
        int currentXP = Skills.THIEVING.getSkill().getExperience();
        int xpGained = currentXP - startingThievingXP;
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
        int currentXP = Skills.THIEVING.getSkill().getExperience();
        int currentLevel = Skills.THIEVING.getSkill().getActualLevel();
        int xpForNextLevel = Skills.THIEVING.getExperienceAt(currentLevel + 1);
        int xpForCurrentLevel = Skills.THIEVING.getExperienceAt(currentLevel);


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
        int currentXP = Skills.THIEVING.getSkill().getExperience();
        int currentLevel = Skills.THIEVING.getSkill().getActualLevel();
        int xpForNextLevel = Skills.THIEVING.getExperienceAt(currentLevel + 1);
        int xpForCurrentLevel = Skills.THIEVING.getExperienceAt(currentLevel);
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
}
