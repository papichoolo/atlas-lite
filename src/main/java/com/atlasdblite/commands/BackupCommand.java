package com.atlasdblite.commands;

import com.atlasdblite.engine.GraphEngine;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

public class BackupCommand extends AbstractCommand {
    @Override
    public String getName() { return "backup"; }

    @Override
    public String getDescription() { return "Creates a snapshot of the database directory and key."; }

    @Override
    public void execute(String[] args, GraphEngine engine) {
        // Force save memory state to disk before backing up
        System.out.println(" ... Flushing memory to disk...");
        engine.commit();

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String backupDirName = "backup_" + timestamp;
        File backupRoot = new File(backupDirName);
        
        // Paths
        Path sourceDb = Paths.get("atlas_db");
        Path sourceKey = Paths.get("atlas.key");
        Path destDb = Paths.get(backupDirName, "atlas_db");
        Path destKey = Paths.get(backupDirName, "atlas.key");

        if (!Files.exists(sourceDb)) {
            printError("No database directory found to backup.");
            return;
        }

        try {
            // 1. Create Backup Root
            if (!backupRoot.exists()) backupRoot.mkdirs();

            // 2. Copy Key
            if (Files.exists(sourceKey)) {
                Files.copy(sourceKey, destKey, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("   + Key backed up.");
            } else {
                System.out.println("   ! Warning: No atlas.key found. Data may be unreadable.");
            }

            // 3. Copy Shards (Recursive Directory Copy)
            if (!Files.exists(destDb)) Files.createDirectories(destDb);
            
            try (Stream<Path> stream = Files.walk(sourceDb)) {
                stream.forEach(source -> {
                    Path destination = destDb.resolve(sourceDb.relativize(source));
                    try {
                        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        System.err.println("Failed to copy: " + source);
                    }
                });
            }
            
            printSuccess("Backup created successfully in: " + backupDirName);

        } catch (Exception e) {
            printError("Backup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}