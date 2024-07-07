package ImGui.Skills;

import net.botwithus.Runecrafting.Abyss;
import net.botwithus.Runecrafting.SteamRunes;
import net.botwithus.Variables.GlobalState;
import net.botwithus.rs3.imgui.ImGui;
import ImGui.*;
import java.time.Duration;
import java.time.Instant;

import static ImGui.VersionManager.displayVersion;
import static net.botwithus.Misc.CaveNightshade.getNightShadeState;
import static net.botwithus.Misc.UrnMaker.getUrnState;
import static net.botwithus.Runecrafting.Abyss.useAbyssRunecrafting;
import static net.botwithus.Runecrafting.Runecrafting.getCurrentState;
import static net.botwithus.Runecrafting.SteamRunes.useSteamRunes;
import static net.botwithus.SnowsScript.getBotState;
import static net.botwithus.SnowsScript.startTime;
import static net.botwithus.Variables.Variables.*;
import static net.botwithus.Variables.Variables.totalElapsedTime;

public class BottomChild {

    public static void renderBottom() {
        int noScrollbarFlag = 0x00000008;
        int noScrollWithMouseFlag = 0x00000010;
        int combinedFlags = noScrollbarFlag | noScrollWithMouseFlag;

        if (ImGui.BeginChild("Child1", 580, 60, true, combinedFlags)) {
            ImGui.SetCursorPosY(12);
            ImGui.SetCursorPosX(10);

            String botState;
            if (isRunecraftingActive && !useSteamRunes && !useAbyssRunecrafting) {
                botState = String.valueOf(getCurrentState());
            } else if (useSteamRunes) {
                botState = String.valueOf(SteamRunes.getCurrentState());
            } else if (pickCaveNightshade) {
                botState = String.valueOf(getNightShadeState());
            } else if (isHerbloreActive) {
                botState = String.valueOf(getSelectedRecipe());
            } else if (isMakeUrnsActive) {
                botState = String.valueOf(getUrnState());
            } else if (useAbyssRunecrafting) {
                botState = String.valueOf(Abyss.getCurrentState());
            } else {
                botState = String.valueOf(getBotState());
            }

            ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, 20, 10);
            ImGui.PushStyleVar(ImGuiStyleVar.FrameRounding, 5);
            ImGui.PushStyleColor(ImGuiCol.Button,0, 0, 0, 0);
            ImGui.PushStyleColor(ImGuiCol.Text, 1, 1, 1, 1);
            ImGui.PushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);


            float buttonWidth = 580.0f - 2 * 20;

            ImGui.SetItemWidth(buttonWidth);

            ImGui.PushStyleColor(ImGuiCol.Border, 0, 0, 0, 0);
            ImGui.PushStyleColor(ImGuiCol.BorderShadow, 0, 0, 0, 0);



            if (ImGui.Button("Enable Tooltips")) {
                tooltipsEnabled = !tooltipsEnabled;
            }

            ImGui.PopStyleColor(2);

            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Enable or disable tooltips in the Options tab");
            }

            ImGui.SameLine();

            ImGui.PushStyleColor(ImGuiCol.Border, 0, 0, 0, 0);
            ImGui.PushStyleColor(ImGuiCol.BorderShadow, 0, 0, 0, 0);

            if (ImGui.Button("Logs")) {
                showLogs = !showLogs;
            }

            ImGui.PopStyleColor(2);

            if (ImGui.IsItemHovered()) {
                ImGui.SetTooltip("Show Console Logs");
            }

            ImGui.PopStyleVar(2);
            float windowWidth = 577.0f;
            float textWidth = ImGui.CalcTextSize("" + botState).getX();
            float centeredX = (windowWidth - textWidth) / 2;

            ImGui.SetCursorPosX(0);
            ImGui.SetCursorPosY(10);
            ImGui.SetCursorPosX(centeredX);
            ImGui.Text("" + botState);

            long elapsedTime = ScriptisOn ? Duration.between(startTime, Instant.now()).getSeconds() + totalElapsedTime : totalElapsedTime;
            String elapsedTimeText = String.format("%02d:%02d:%02d", elapsedTime / 3600, (elapsedTime % 3600) / 60, elapsedTime % 60);
            textWidth = ImGui.CalcTextSize(elapsedTimeText).getX();
            centeredX = (windowWidth - textWidth) / 2;


            ImGui.SetCursorPosX(0);
            ImGui.SetCursorPosY(27);
            ImGui.SetCursorPosX(centeredX);
            ImGui.Text(elapsedTimeText);

            ImGui.SetCursorPosX(440);
            ImGui.SetCursorPosY(20);

            ImGui.Text("Ticks: " + GlobalState.currentTickCount);

            displayVersion(577.0f);
            ImGui.PopStyleVar(2);
            ImGui.PopStyleColor(3);
            ImGui.EndChild();
        }
    }
}
