package ImGui.Skills;

import net.botwithus.Herblore.SharedState;
import net.botwithus.rs3.imgui.ImGui;
import ImGui.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ImGui.PredefinedStrings.categorizedRecipes;
import static ImGui.PredefinedStrings.stringToHerbloreRecipe;
import static net.botwithus.CustomLogger.log;
import static net.botwithus.SnowsScript.startTime;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.Potions;

public class HerbloreImGui {

    public static void renderHerblore() {
        if (isHerbloreActive && !showLogs) {
            if (tooltipsEnabled) {
                String[] texts = {
                        "Uses `Load Last Preset from` Bank chest",
                        "Uses Portable Well",
                        "You dont need a Portable Well if Making Bombs",
                        "if you like this script, consider looking at HerbloreWithUs",
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


                            /*String[] recipeNames = Arrays.stream(Herblore.HerbloreRecipe.values())
                                    .map(Enum::name)
                                    .toArray(String[]::new);*/ //will display CASE NAME

            List<String> categoryList = new ArrayList<>(categorizedRecipes.keySet());
            categoryList.add(0, "Select Herblore Category");
            String[] categoryNames = categoryList.toArray(new String[0]);

            ImGui.SetItemWidth(372.0F);
            if (ImGui.Combo("##HerbloreCategory", currentCategoryIndex, categoryNames)) {
                int selectedIndex = currentCategoryIndex.get();
                if (selectedIndex > 0 && selectedIndex < categoryNames.length) {
                    String selectedCategoryName = categoryNames[selectedIndex];
                    selectedCategory = selectedCategoryName;
                    log("Herblore Category selected: " + selectedCategoryName);
                } else if (selectedIndex != 0) {
                    log("Please select a valid Herblore Category.");
                }
            }

            if (selectedCategory != null && !selectedCategory.equals("Select Herblore Category")) {
                List<String> recipeList = new ArrayList<>(categorizedRecipes.get(selectedCategory));
                recipeList.add(0, "Select Herblore Recipe");
                String[] recipeNames = recipeList.toArray(new String[0]);

                ImGui.SetItemWidth(372.0F);
                if (ImGui.Combo("##HerbloreRecipe", currentRecipeIndex, recipeNames)) {
                    int selectedIndex = currentRecipeIndex.get();
                    if (selectedIndex > 0 && selectedIndex < recipeNames.length) {
                        String selectedRecipeName = recipeNames[selectedIndex];
                        SharedState.selectedRecipe = stringToHerbloreRecipe(selectedRecipeName);
                        log("Herblore Recipe selected: " + selectedRecipeName);
                    } else if (selectedIndex != 0) {
                        log("Please select a valid Herblore Recipe.");
                    }
                }
            }


            ImGui.SeparatorText("Potions Made Count");
            for (Map.Entry<String, Integer> entry : Potions.entrySet()) {
                ImGui.Text(entry.getKey() + ": " + entry.getValue());
            }

            int totalPotionsMade = 0;
            for (int count : Potions.values()) {
                totalPotionsMade += count;
            }

            long elapsedTime = Duration.between(startTime, Instant.now()).toMillis();
            double elapsedHours = elapsedTime / 1000.0 / 60.0 / 60.0;

            double potionsMadePerHour = totalPotionsMade / elapsedHours;
            int potionsMadePerHourInt = (int) potionsMadePerHour;

            ImGui.Text("Potions Made Per Hour: " + potionsMadePerHourInt);
        }
    }
}
