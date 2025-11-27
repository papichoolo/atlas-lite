package com.atlasdblite.commands;

import com.atlasdblite.engine.GraphEngine;
import com.atlasdblite.models.Node;
import java.util.List;

public class QueryCommand extends AbstractCommand {
    @Override
    public String getName() { return "query"; }

    @Override
    public String getDescription() { return "Finds related nodes. Usage: query <id> <relation_type>"; }

    @Override
    public void execute(String[] args, GraphEngine engine) {
        if (!validateArgs(args, 2, "query <id> <relation_type>")) return;

        String startId = args[1];
        String relType = args[2];
        
        List<Node> results = engine.traverse(startId, relType);
        
        System.out.println("Search Results (" + startId + " -[:" + relType + "]-> ? )");
        if (results.isEmpty()) {
            System.out.println(" > No matching nodes found.");
        } else {
            for (Node n : results) {
                System.out.println(" > Found: " + n);
            }
        }
    }
}