package com.atlasdblite.commands;

import com.atlasdblite.engine.GraphEngine;

public class UpdateNodeCommand extends AbstractCommand {
    @Override
    public String getName() { return "update-node"; }

    @Override
    public String getDescription() { return "Updates a node property. Usage: update-node <id> <key> <value>"; }

    @Override
    public void execute(String[] args, GraphEngine engine) {
        if (!validateArgs(args, 3, "update-node <id> <key> <value>")) return;

        boolean success = engine.updateNode(args[1], args[2], args[3]);
        if (success) {
            printSuccess("Node updated successfully.");
        } else {
            printError("Node ID not found: " + args[1]);
        }
    }
}