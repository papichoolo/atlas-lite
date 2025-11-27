package com.atlasdblite.commands;

import com.atlasdblite.engine.GraphEngine;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackupCommand extends AbstractCommand {
    @Override
    public String getName() { return "backup"; }

    @Override
    public String getDescription() { return "Creates a snapshot of the encrypted DB."; }

    @Override
    public void execute(String[] args, GraphEngine engine) {
        File source = new File("atlas_data.enc");
        if (!source.exists()) {
            printError("No database file found to backup.");
            return;
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File dest = new File("backup_" + timestamp + ".enc");

        try {
            Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            printSuccess("Backup created: " + dest.getName());
        } catch (Exception e) {
            printError("Backup failed: " + e.getMessage());
        }
    }
}