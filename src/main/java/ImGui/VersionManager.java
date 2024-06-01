package ImGui;

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

        ScriptConsole.println("Attempting to read file: " + file.getAbsolutePath());

        if (file.exists() && file.canRead()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                versionProps.load(fis);
                ScriptConsole.println("Properties loaded: " + versionProps);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ScriptConsole.println("File not found or not readable: " + file.getAbsolutePath());
        }

        String version = versionProps.getProperty(VERSION_KEY, "1.0");
        ScriptConsole.println("Version read: " + version);
        return version;
    }
}
