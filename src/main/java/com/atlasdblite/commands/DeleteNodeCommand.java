package com.atlasdblite.commands;

import com.atlasdblite.engine.GraphEngine;

public class DeleteNodeCommand extends AbstractCommand {
    @Override
    public String getName() { return "delete-node"; }

    @Override
    public String getDescription() { return "Deletes a node and its edges. Usage: delete-node <id>"; }

    @Override
    public void execute(String[] args, GraphEngine engine) {
        if (!validateArgs(args, 1, "delete-node <id>")) return;

        boolean success = engine.deleteNode(args[1]);
        if (success) {
            printSuccess("Node " + args[1] + " and associated links deleted.");
        } else {
            printError("Node ID not found.");
        }
    }
}