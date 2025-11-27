package com.atlasdblite.commands;

import com.atlasdblite.engine.GraphEngine;
import com.atlasdblite.models.Node;

public class AddNodeCommand extends AbstractCommand {
    @Override
    public String getName() { return "add-node"; }

    @Override
    public String getDescription() { return "Creates a node. Usage: add-node <id> <label> [key:value]"; }

    @Override
    public void execute(String[] args, GraphEngine engine) {
        if (!validateArgs(args, 2, "add-node <id> <label> [prop:val]")) return;

        String id = args[1];
        String label = args[2];
        Node node = new Node(id, label);

        // Parse optional properties (format key:value)
        for (int i = 3; i < args.length; i++) {
            String[] prop = args[i].split(":");
            if (prop.length == 2) {
                node.addProperty(prop[0], prop[1]);
            }
        }

        engine.persistNode(node);
        printSuccess("Node created: " + node);
    }
}