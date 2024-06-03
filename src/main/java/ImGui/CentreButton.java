package ImGui;

import net.botwithus.rs3.imgui.ImGui;

import static ImGui.Theme.*;
import static ImGui.Theme.setStyleColor;

public class CentreButton {

    public static void createCenteredButton(String buttonText, Runnable onClick, boolean isClicked) {
        ImGui.PushStyleVar(ImGuiStyleVar.FrameRounding, 0.0f);

        if (isClicked) {
            if (PurpleThemeSelected) {
                setStyleColor(ImGuiCol.Button, 80, 0, 150, 200);
            } else if (BlueThemeSelected) {
                setStyleColor(ImGuiCol.Button, 70, 130, 180, 200);
            } else if (RedThemeSelected) {
                setStyleColor(ImGuiCol.Button, 178, 34, 34, 200);
            } else if (OrangeThemeSelected) {
                setStyleColor(ImGuiCol.Button, 255, 140, 0, 200);
            } else if (YellowThemeSelected) {
                setStyleColor(ImGuiCol.Button, 255, 223, 0, 200);
            } else {
                setStyleColor(ImGuiCol.Button, 39, 92, 46, 255);

            }
        } else {
            ImGui.PushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
        }

        ImGui.PushStyleColor(ImGuiCol.Border, 0, 0, 0, 0);
        ImGui.PushStyleColor(ImGuiCol.BorderShadow, 0, 0, 0, 0);
        float textWidth = ImGui.CalcTextSize(buttonText).getX();
        float padding = (170 - textWidth) / 2;
        ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, padding, 2.0f);
        ImGui.SetCursorPosX(0);
        if (ImGui.Button(buttonText)) {
            onClick.run();
        }
        ImGui.PopStyleVar(2);
        ImGui.PopStyleColor(3);
    }
}
