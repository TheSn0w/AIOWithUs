package net.botwithus.Combat;

import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NPCs {
    private static final List<List<String>> npcTableData = new ArrayList<>();
    private static final long SCAN_DELAY_MS = 5000; // Delay between scans in milliseconds
    private static long lastScanTime = 0; // Time of the last scan

    public static void updateNpcTableData(LocalPlayer player) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastScanTime >= SCAN_DELAY_MS) {
            List<Npc> npcs = NpcQuery.newQuery()
                    .health(100, 1_000_000)
                    .option("Attack")
                    .results()
                    .stream().toList();

            npcTableData.clear();

            Set<String> uniqueNpcNames = new HashSet<>();

            for (Npc npc : npcs) {
                uniqueNpcNames.add(npc.getName());
            }

            for (String npcName : uniqueNpcNames) {
                List<String> row = new ArrayList<>();
                row.add(npcName);
                npcTableData.add(row);
            }

            lastScanTime = currentTime;
        }
    }

    public static List<List<String>> getNpcTableData() {
        return npcTableData;
    }
}