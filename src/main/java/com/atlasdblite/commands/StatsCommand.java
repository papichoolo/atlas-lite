package com.atlasdblite.commands;

import com.atlasdblite.engine.GraphEngine;
import java.io.File;

public class StatsCommand extends AbstractCommand {
    @Override
    public String getName() { return "stats"; }

    @Override
    public String getDescription() { return "Displays database statistics and storage info."; }

    @Override
    public void execute(String[] args, GraphEngine engine) {
        int nodes = engine.getAllNodes().size();
        int edges = engine.getAllRelations().size();
        File dbFile = new File("atlas_data.enc");
        long size = dbFile.exists() ? dbFile.length() : 0;

        System.out.println("--- AtlasDB-Lite Statistics ---");
        System.out.printf(" Nodes       : %d%n", nodes);
        System.out.printf(" Relations   : %d%n", edges);
        System.out.printf(" Storage     : %d bytes (Encrypted)%n", size);
        System.out.printf(" Encryption  : AES-256%n");
        System.out.println("-------------------------------");
    }
}