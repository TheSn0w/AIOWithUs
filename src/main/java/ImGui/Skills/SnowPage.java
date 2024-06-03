package ImGui.Skills;

import net.botwithus.rs3.imgui.ImGui;

import java.awt.*;
import java.net.URI;

public class SnowPage {

    public static void renderSnow() {
        String[] snowTexts = {
                " SSSS         N   N       OOO       W      W",
                "S             NN  N      O   O      W      W",
                " SSS          N N N      O   O      W      W",
                "    S         N  NN      O   O      W  W  W",
                "    S         N   N      O   O      W  W  W",
                "S   S         N   N      O   O      W  W  W",
                " SSS          N   N       OOO        W W W "
        };

        for (String text : snowTexts) {
            float windowWidth = 400;
            float textWidth = ImGui.CalcTextSize(text).getX();
            float centeredX = (windowWidth - textWidth) / 2;
            ImGui.SetCursorPosX(centeredX);
            ImGui.Text(text);
            ImGui.Spacing(5, 5);
        }
        ImGui.SetCursorPosY(220);

        String[] newLines = {
                "Welcome to the AIO Script",
                "Please select a skill to start",
                "if you like the script please",
                "consider leaving a review",
                "if you have any bugs or issues",
                "please report them on the forum",
                "please note, these are just the barebones of some scripts",
                "that can be found on the marketplace",
        };

        for (String line : newLines) {
            float windowWidth = 400;
            float textWidth = ImGui.CalcTextSize(line).getX();
            float centeredX = (windowWidth - textWidth) / 2;
            ImGui.SetCursorPosX(centeredX);
            ImGui.Text(line);
        }

        String discordLink = "https://discordapp.com/channels/973830420858810378/1192140405689548891";
        float buttonWidth1 = ImGui.CalcTextSize("Discord Support").getX();
        float centeredButtonX1 = (362 - buttonWidth1) / 3;
        ImGui.SetCursorPosX(centeredButtonX1);
        if (ImGui.Button("Discord Support")) {
            try {
                Desktop.getDesktop().browse(new URI(discordLink));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ImGui.IsItemHovered()) {
            ImGui.SetTooltip("Click here to join the Discord server for support");
        }

        ImGui.SameLine();

        String reviewLink = "https://discord.com/channels/973830420858810378/1116465231141544038";
        float centeredButtonX2 = centeredButtonX1 + buttonWidth1 + 20;
        ImGui.SetCursorPosX(centeredButtonX2);
        if (ImGui.Button("Write a Review")) {
            try {
                Desktop.getDesktop().browse(new URI(reviewLink));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ImGui.IsItemHovered()) {
            ImGui.SetTooltip("Click here to write a review on Discord");
        }
    }
}

