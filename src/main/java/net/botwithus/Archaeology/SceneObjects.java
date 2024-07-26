package net.botwithus.Archaeology;

import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SceneObjects {
    private static final List<List<String>> sceneObjectTableData = new ArrayList<>();
    private static final long SCAN_DELAY_MS = 5000; // Delay between scans in milliseconds
    private static long lastScanTime = 0; // Time of the last scan

    public static void updateSceneObjectTableData() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastScanTime >= SCAN_DELAY_MS) {
            List<SceneObject> sceneObjects = SceneObjectQuery.newQuery()
                    .option("Excavate")
                    .results()
                    .stream().toList();

            sceneObjectTableData.clear();

            Set<String> uniqueSceneObjectNames = new HashSet<>();

            for (SceneObject sceneObject : sceneObjects) {
                uniqueSceneObjectNames.add(sceneObject.getName());
            }

            for (String sceneObjectName : uniqueSceneObjectNames) {
                List<String> row = new ArrayList<>();
                row.add(sceneObjectName);
                sceneObjectTableData.add(row);
            }

            lastScanTime = currentTime;
        }
    }

    public static List<List<String>> getSceneObjectTableData() {
        return sceneObjectTableData;
    }
}