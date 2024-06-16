package ImGui;

import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.script.ScriptConsole;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class VersionManager {

    private static final String VERSION_FILE = "C:\\Users\\Sn0w\\OneDrive\\Desktop\\AIO\\version.properties";
    private static final String VERSION_KEY = "version";

    public static String getVersion() {
        Properties versionProps = new Properties();
        File file = new File(VERSION_FILE);


        if (file.exists() && file.canRead()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                versionProps.load(fis);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String version = versionProps.getProperty(VERSION_KEY, "1.30");
        return version;
    }
    public static void displayVersion(float windowWidth) {
        String versionText = "V" + VersionManager.getVersion();

        float versionTextWidth = ImGui.CalcTextSize(versionText).getX();
        float rightAlignedX = windowWidth - versionTextWidth - 23;

        ImGui.SetCursorPosX(rightAlignedX);
        ImGui.SetCursorPosY(20);
        ImGui.Text(versionText);
    }
}
