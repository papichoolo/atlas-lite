package com.atlasdblite.commands;

import com.atlasdblite.engine.GraphEngine;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class StatsCommand extends AbstractCommand {
    @Override
    public String getName() { return "stats"; }

    @Override
    public String getDescription() { return "Displays database statistics and storage info."; }

    @Override
    public void execute(String[] args, GraphEngine engine) {
        System.out.println(" ... Calculating statistics (scanning shards)...");

        // 1. Get Logical Counts (Triggers lazy loading of all shards)
        int nodes = engine.getAllNodes().size();
        int edges = engine.getAllRelations().size();
        
        // 2. Calculate Physical Storage Size
        long totalSize = 0;
        Path dbDir = Paths.get("atlas_db");
        
        if (Files.exists(dbDir)) {
            try (Stream<Path> walk = Files.walk(dbDir)) {
                totalSize = walk.filter(p -> p.toFile().isFile())
                                .mapToLong(p -> p.toFile().length())
                                .sum();
            } catch (IOException e) {
                printError("Could not read storage directory: " + e.getMessage());
            }
        }

        // 3. Print Report
        System.out.println(" =========================================");
        System.out.println("   ATLASDB-LITE STATISTICS");
        System.out.println(" =========================================");
        System.out.println(String.format("  %-15s : %d", "Nodes", nodes));
        System.out.println(String.format("  %-15s : %d", "Relations", edges));
        System.out.println(String.format("  %-15s : %s", "Sharding", "16 Buckets"));
        System.out.println(String.format("  %-15s : %.2f KB", "Disk Usage", totalSize / 1024.0));
        System.out.println(String.format("  %-15s : %s", "Encryption", "AES-256"));
        System.out.println(String.format("  %-15s : %s", "Auto-Index", engine.isAutoIndexing() ? "ENABLED (O(1))" : "DISABLED (O(N))"));
        System.out.println(" =========================================");
    }
}