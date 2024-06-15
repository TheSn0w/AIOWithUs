package ImGui;

import net.botwithus.rs3.imgui.ImGui;

public class Theme {

    public static boolean PurpleThemeSelected = false;
    public static boolean BlueThemeSelected = false;
    public static boolean RedThemeSelected = false;
    public static boolean OrangeThemeSelected = true;
    public static boolean YellowThemeSelected = false;
    public static boolean GreenThemeSelected = false;
    public static boolean GreyThemeSelected = false;

    public static void setDefaultTheme() {
        ImGui.PushStyleVar(ImGuiStyleVar.WindowRounding, 6.0f);
        ImGui.PushStyleVar(ImGuiStyleVar.ChildRounding, 6.0f);
        ImGui.PushStyleVar(ImGuiStyleVar.FrameRounding, 3.0f);
        ImGui.PushStyleVar(ImGuiStyleVar.GrabRounding, 3.0f);
        ImGui.PushStyleVar(ImGuiStyleVar.PopupRounding, 3.0f);
        ImGui.PushStyleVar(ImGuiStyleVar.ScrollbarSize, 9.0f);
        ImGui.PushStyleVar(ImGuiStyleVar.ChildBorderSize, 2.0f);
        ImGui.PushStyleVar(ImGuiStyleVar.FramePadding, 6.0f, 2.0f);
        ImGui.PushStyleVar(ImGuiStyleVar.WindowPadding, 15.0f, 15.0f);
        ImGui.PushStyleVar(ImGuiStyleVar.WindowBorderSize, 3.0f);
        ImGui.PushStyleVar(ImGuiStyleVar.FrameBorderSize, 2.0f);

    }

    static void applyPurpleTheme() {
        setStyleColor(ImGuiCol.WindowBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.Button, 80, 0, 150, 200);
        setStyleColor(ImGuiCol.ButtonHovered, 100, 50, 180, 250);
        setStyleColor(ImGuiCol.ButtonActive, 80, 0, 150, 200);
        setStyleColor(ImGuiCol.Text, 208, 217, 209, 255);
        setStyleColor(ImGuiCol.Separator, 102, 0, 128, 255);
        setStyleColor(ImGuiCol.TitleBgActive, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.TitleBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.CheckMark, 255, 255, 255, 200);
        setStyleColor(ImGuiCol.ResizeGripHovered, 100, 0, 200, 200);
        setStyleColor(ImGuiCol.ResizeGripActive, 80, 0, 150, 255);
        setStyleColor(ImGuiCol.ResizeGrip, 80, 0, 150, 255);
        setStyleColor(ImGuiCol.SliderGrab, 80, 0, 150, 200);
        setStyleColor(ImGuiCol.SliderGrabActive, 80, 0, 150, 200);
        setStyleColor(ImGuiCol.SeparatorHovered, 100, 0, 200, 200);
        setStyleColor(ImGuiCol.Border, 255, 80, 232, 220);
        setStyleColor(ImGuiCol.BorderShadow, 132, 7, 116, 150);
        setStyleColor(ImGuiCol.ScrollbarGrab, 80, 0, 150, 255);
        setStyleColor(ImGuiCol.ScrollbarGrabHovered, 100, 0, 200, 200);
        setStyleColor(ImGuiCol.ScrollbarGrabActive, 100, 0, 200, 200);
        setStyleColor(ImGuiCol.MenuBarBg, 80, 0, 150, 100);
        setStyleColor(ImGuiCol.TabActive, 80, 0, 150, 100);
        setStyleColor(ImGuiCol.Tab, 80, 0, 150, 100);
        setStyleColor(ImGuiCol.TabHovered, 80, 0, 150, 100);
        setStyleColor(ImGuiCol.TabUnfocused, 80, 0, 150, 100);
        setStyleColor(ImGuiCol.TabUnfocusedActive, 80, 0, 150, 100);
        setStyleColor(ImGuiCol.FrameBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.PopupBg, 80, 0, 150, 200);
        setStyleColor(ImGuiCol.HeaderHovered, 0, 0, 0, 220);
        setStyleColor(ImGuiCol.HeaderActive, 0, 0, 0, 200);
        setStyleColor(ImGuiCol.Header, 0, 0, 0, 100);
        setStyleColor(ImGuiCol.FrameBgHovered, 100, 50, 180, 250);
        setStyleColor(ImGuiCol.TableHeaderBg, 100, 50, 180, 250);
    }

    static void applyBlueTheme() {
        setStyleColor(ImGuiCol.WindowBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.Button, 70, 130, 180, 200);
        setStyleColor(ImGuiCol.ButtonActive, 70, 130, 180, 200);
        setStyleColor(ImGuiCol.ButtonHovered, 70, 130, 180, 255);
        setStyleColor(ImGuiCol.Text, 240, 248, 255, 255);
        setStyleColor(ImGuiCol.Separator, 70, 130, 180, 255);
        setStyleColor(ImGuiCol.TitleBgActive, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.TitleBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.CheckMark, 70, 130, 180, 255);
        setStyleColor(ImGuiCol.ResizeGripHovered, 100, 149, 237, 200);
        setStyleColor(ImGuiCol.ResizeGripActive, 70, 130, 180, 255);
        setStyleColor(ImGuiCol.ResizeGrip, 70, 130, 180, 255);
        setStyleColor(ImGuiCol.SliderGrab, 70, 130, 180, 200);
        setStyleColor(ImGuiCol.SliderGrabActive, 70, 130, 180, 200);
        setStyleColor(ImGuiCol.SeparatorHovered, 100, 149, 237, 200);
        setStyleColor(ImGuiCol.Border, 70, 130, 180, 200);
        setStyleColor(ImGuiCol.BorderShadow, 70, 130, 180, 200);
        setStyleColor(ImGuiCol.ScrollbarGrab, 70, 130, 180, 255);
        setStyleColor(ImGuiCol.ScrollbarGrabHovered, 100, 149, 237, 200);
        setStyleColor(ImGuiCol.ScrollbarGrabActive, 100, 149, 237, 200);
        setStyleColor(ImGuiCol.MenuBarBg, 70, 130, 180, 255);
        setStyleColor(ImGuiCol.TabActive, 240, 248, 255, 200);
        setStyleColor(ImGuiCol.Tab, 70, 130, 180, 100);
        setStyleColor(ImGuiCol.TabHovered, 240, 248, 255, 200);
        setStyleColor(ImGuiCol.TabUnfocused, 70, 130, 180, 100);
        setStyleColor(ImGuiCol.TabUnfocusedActive, 70, 130, 180, 255);
        setStyleColor(ImGuiCol.HeaderHovered, 70, 130, 180, 200);
        setStyleColor(ImGuiCol.HeaderActive, 70, 130, 180, 255);
        setStyleColor(ImGuiCol.Header, 0, 0, 0, 255);
        setStyleColor(ImGuiCol.FrameBgHovered, 70, 130, 180, 255);
        setStyleColor(ImGuiCol.TableHeaderBg, 70, 130, 180, 255);
    }

    static void applyRedTheme() {
        setStyleColor(ImGuiCol.WindowBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.Button, 178, 34, 34, 200);
        setStyleColor(ImGuiCol.ButtonHovered, 220, 20, 60, 250);
        setStyleColor(ImGuiCol.ButtonActive, 178, 34, 34, 200);
        setStyleColor(ImGuiCol.Text, 255, 250, 250, 255);
        setStyleColor(ImGuiCol.Separator, 178, 34, 34, 255);
        setStyleColor(ImGuiCol.TitleBgActive, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.TitleBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.CheckMark, 255, 250, 250, 200);
        setStyleColor(ImGuiCol.ResizeGripHovered, 220, 20, 60, 200);
        setStyleColor(ImGuiCol.ResizeGripActive, 178, 34, 34, 255);
        setStyleColor(ImGuiCol.ResizeGrip, 178, 34, 34, 255);
        setStyleColor(ImGuiCol.SliderGrab, 178, 34, 34, 200);
        setStyleColor(ImGuiCol.SliderGrabActive, 178, 34, 34, 200);
        setStyleColor(ImGuiCol.SeparatorHovered, 220, 20, 60, 200);
        setStyleColor(ImGuiCol.Border, 178, 34, 34, 220);
        setStyleColor(ImGuiCol.BorderShadow, 139, 0, 0, 150);
        setStyleColor(ImGuiCol.ScrollbarGrab, 178, 34, 34, 255);
        setStyleColor(ImGuiCol.ScrollbarGrabHovered, 220, 20, 60, 200);
        setStyleColor(ImGuiCol.ScrollbarGrabActive, 220, 20, 60, 200);
        setStyleColor(ImGuiCol.MenuBarBg, 178, 34, 34, 100);
        setStyleColor(ImGuiCol.TabActive, 178, 34, 34, 100);
        setStyleColor(ImGuiCol.Tab, 178, 34, 34, 100);
        setStyleColor(ImGuiCol.TabHovered, 178, 34, 34, 100);
        setStyleColor(ImGuiCol.TabUnfocused, 178, 34, 34, 100);
        setStyleColor(ImGuiCol.TabUnfocusedActive, 178, 34, 34, 100);
        setStyleColor(ImGuiCol.FrameBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.PopupBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.HeaderHovered, 220, 20, 60, 250);
        setStyleColor(ImGuiCol.HeaderActive, 220, 20, 60, 250);
        setStyleColor(ImGuiCol.Header, 220, 20, 60, 250);
        setStyleColor(ImGuiCol.FrameBgHovered, 220, 20, 60, 150);
        setStyleColor(ImGuiCol.TableHeaderBg, 220, 20, 60, 250);
    }

    static void applyOrangeTheme() {
        setStyleColor(ImGuiCol.WindowBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.Button, 255, 140, 0, 200);
        setStyleColor(ImGuiCol.ButtonHovered, 255, 165, 0, 250);
        setStyleColor(ImGuiCol.ButtonActive, 255, 140, 0, 200);
        setStyleColor(ImGuiCol.Text, 255, 255, 240, 255);
        setStyleColor(ImGuiCol.Separator, 255, 140, 0, 255);
        setStyleColor(ImGuiCol.TitleBgActive, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.TitleBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.CheckMark, 255, 255, 240, 200);
        setStyleColor(ImGuiCol.ResizeGripHovered, 255, 165, 0, 200);
        setStyleColor(ImGuiCol.ResizeGripActive, 255, 140, 0, 255);
        setStyleColor(ImGuiCol.ResizeGrip, 255, 140, 0, 255);
        setStyleColor(ImGuiCol.SliderGrab, 255, 140, 0, 200);
        setStyleColor(ImGuiCol.SliderGrabActive, 255, 140, 0, 200);
        setStyleColor(ImGuiCol.SeparatorHovered, 255, 165, 0, 200);
        setStyleColor(ImGuiCol.Border, 255, 140, 0, 220);
        setStyleColor(ImGuiCol.BorderShadow, 139, 69, 19, 150);
        setStyleColor(ImGuiCol.ScrollbarGrab, 255, 140, 0, 255);
        setStyleColor(ImGuiCol.ScrollbarGrabHovered, 255, 165, 0, 200);
        setStyleColor(ImGuiCol.ScrollbarGrabActive, 255, 165, 0, 200);
        setStyleColor(ImGuiCol.MenuBarBg, 255, 140, 0, 100);
        setStyleColor(ImGuiCol.TabActive, 255, 140, 0, 100);
        setStyleColor(ImGuiCol.Tab, 255, 140, 0, 100);
        setStyleColor(ImGuiCol.TabHovered, 255, 140, 0, 100);
        setStyleColor(ImGuiCol.TabUnfocused, 255, 140, 0, 100);
        setStyleColor(ImGuiCol.TabUnfocusedActive, 255, 140, 0, 100);
        setStyleColor(ImGuiCol.FrameBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.PopupBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.HeaderHovered, 255, 165, 0, 250);
        setStyleColor(ImGuiCol.HeaderActive, 255, 165, 0, 250);
        setStyleColor(ImGuiCol.Header, 255, 165, 0, 250);
        setStyleColor(ImGuiCol.FrameBgHovered, 255, 165, 0, 250);
        setStyleColor(ImGuiCol.TableHeaderBg, 255, 165, 0, 250);
    }

    static void applyYellowTheme() {
        setStyleColor(ImGuiCol.WindowBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.Button, 255, 223, 0, 200);
        setStyleColor(ImGuiCol.ButtonHovered, 255, 255, 0, 200);
        setStyleColor(ImGuiCol.ButtonActive, 255, 223, 0, 200);
        setStyleColor(ImGuiCol.Text, 255, 255, 255, 230);
        setStyleColor(ImGuiCol.Separator, 255, 223, 0, 255);
        setStyleColor(ImGuiCol.TitleBgActive, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.TitleBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.CheckMark, 255, 255, 255, 230);
        setStyleColor(ImGuiCol.ResizeGripHovered, 255, 255, 0, 200);
        setStyleColor(ImGuiCol.ResizeGripActive, 255, 223, 0, 255);
        setStyleColor(ImGuiCol.ResizeGrip, 255, 223, 0, 255);
        setStyleColor(ImGuiCol.SliderGrab, 255, 223, 0, 200);
        setStyleColor(ImGuiCol.SliderGrabActive, 255, 223, 0, 200);
        setStyleColor(ImGuiCol.SeparatorHovered, 255, 255, 0, 200);
        setStyleColor(ImGuiCol.Border, 255, 223, 0, 220);
        setStyleColor(ImGuiCol.BorderShadow, 139, 139, 0, 150);
        setStyleColor(ImGuiCol.ScrollbarGrab, 255, 223, 0, 255);
        setStyleColor(ImGuiCol.ScrollbarGrabHovered, 255, 255, 0, 200);
        setStyleColor(ImGuiCol.ScrollbarGrabActive, 255, 255, 0, 200);
        setStyleColor(ImGuiCol.MenuBarBg, 255, 223, 0, 100);
        setStyleColor(ImGuiCol.TabActive, 255, 223, 0, 100);
        setStyleColor(ImGuiCol.Tab, 255, 223, 0, 100);
        setStyleColor(ImGuiCol.TabHovered, 255, 223, 0, 100);
        setStyleColor(ImGuiCol.TabUnfocused, 255, 223, 0, 100);
        setStyleColor(ImGuiCol.TabUnfocusedActive, 255, 223, 0, 100);
        setStyleColor(ImGuiCol.FrameBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.PopupBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.HeaderHovered, 139, 139, 0, 150);
        setStyleColor(ImGuiCol.HeaderActive, 0, 0, 0, 200);
        setStyleColor(ImGuiCol.Header, 0, 0, 0, 100);
        setStyleColor(ImGuiCol.FrameBgHovered, 139, 139, 0, 150);
        setStyleColor(ImGuiCol.TableHeaderBg, 255, 255, 0, 200);
    }

    static void applyGreenTheme() {
        setStyleColor(ImGuiCol.WindowBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.Button, 39, 92, 46, 255);
        setStyleColor(ImGuiCol.ButtonActive, 39, 92, 46, 255);
        setStyleColor(ImGuiCol.ButtonHovered, 91, 102, 91, 250);
        setStyleColor(ImGuiCol.Text, 208, 217, 209, 255);
        setStyleColor(ImGuiCol.Separator, 39, 92, 46, 255);
        setStyleColor(ImGuiCol.TitleBgActive, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.TitleBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.CheckMark, 35, 219, 60, 200);
        setStyleColor(ImGuiCol.ResizeGripHovered, 35, 219, 60, 200);
        setStyleColor(ImGuiCol.ResizeGripActive, 39, 92, 46, 255);
        setStyleColor(ImGuiCol.ResizeGrip, 39, 92, 46, 255);
        setStyleColor(ImGuiCol.SliderGrab, 39, 92, 46, 200);
        setStyleColor(ImGuiCol.SliderGrabActive, 39, 92, 46, 200);
        setStyleColor(ImGuiCol.SeparatorHovered, 35, 219, 60, 200);
        setStyleColor(ImGuiCol.Border, 39, 92, 46, 255);
        setStyleColor(ImGuiCol.BorderShadow, 39, 92, 46, 255);
        setStyleColor(ImGuiCol.ScrollbarGrab, 39, 92, 46, 255);
        setStyleColor(ImGuiCol.ScrollbarGrabHovered, 35, 219, 60, 200);
        setStyleColor(ImGuiCol.ScrollbarGrabActive, 35, 219, 60, 200);
        setStyleColor(ImGuiCol.MenuBarBg, 39, 92, 46, 100);
        setStyleColor(ImGuiCol.TabActive, 39, 92, 46, 100);
        setStyleColor(ImGuiCol.Tab, 39, 92, 46, 100);
        setStyleColor(ImGuiCol.TabHovered, 39, 92, 46, 100);
        setStyleColor(ImGuiCol.TabUnfocused, 39, 92, 46, 100);
        setStyleColor(ImGuiCol.TabUnfocusedActive, 39, 92, 46, 100);
        setStyleColor(ImGuiCol.FrameBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.PopupBg, 0, 0, 0, 230);
        setStyleColor(ImGuiCol.HeaderHovered, 91, 102, 91, 250);
        setStyleColor(ImGuiCol.HeaderActive, 32, 77, 30, 100);
        setStyleColor(ImGuiCol.Header, 32, 77, 30, 100);
        setStyleColor(ImGuiCol.FrameBgHovered, 91, 102, 91, 250);
        setStyleColor(ImGuiCol.TableHeaderBg, 39, 92, 46, 255);
    }

    public static void setStyleColor(int colorEnum, int r, int g, int b, int a) {
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));
        a = Math.max(0, Math.min(255, a));

        float floatColorR = r / 255.0f;
        float floatColorG = g / 255.0f;
        float floatColorB = b / 255.0f;
        float floatColorA = a / 255.0f;

        ImGui.PushStyleColor(colorEnum, floatColorR, floatColorG, floatColorB, floatColorA);
    }
}
