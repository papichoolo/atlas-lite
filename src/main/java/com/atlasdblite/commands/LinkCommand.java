package com.atlasdblite.commands;

import com.atlasdblite.engine.GraphEngine;

public class LinkCommand extends AbstractCommand {
    @Override
    public String getName() { return "link"; }

    @Override
    public String getDescription() { return "Connects two nodes. Usage: link <from_id> <to_id> <type>"; }

    @Override
    public void execute(String[] args, GraphEngine engine) {
        if (!validateArgs(args, 3, "link <from_id> <to_id> <type>")) return;

        try {
            engine.persistRelation(args[1], args[2], args[3].toUpperCase());
            printSuccess("Linked " + args[1] + " to " + args[2] + " via " + args[3].toUpperCase());
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }
}