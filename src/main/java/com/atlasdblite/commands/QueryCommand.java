package com.atlasdblite.commands;

import com.atlasdblite.engine.GraphEngine;
import com.atlasdblite.models.Node;
import java.util.List;

public class QueryCommand extends AbstractCommand {
    @Override
    public String getName() { return "query"; }

    @Override
    public String getDescription() { return "Finds related nodes. Usage: query <search_term> <relation_type>"; }

    @Override
    public void execute(String[] args, GraphEngine engine) {
        if (!validateArgs(args, 2, "query <search_term> <relation_type>")) return;

        String searchTerm = args[1];
        String relType = args[2];

        // 1. Resolve the Start Node (Handle Fuzzy Search / Menu)
        System.out.println(" ... Resolving Start Node: '" + searchTerm + "'");
        Node startNode = resolveNode(searchTerm, engine);
        
        if (startNode == null) {
            // resolveNode prints its own errors (cancelled/not found)
            return;
        }

        // 2. Run Traversal using the resolved ID
        List<Node> results = engine.traverse(startNode.getId(), relType);
        
        // 3. Display Results
        System.out.println("\n [QUERY RESULT] " + startNode.getLabel() + " (" + startNode.getId() + ")");
        System.out.println("      |");
        System.out.println("      +---[:" + relType.toUpperCase() + "]---> ?");
        System.out.println("      |");
        
        if (results.isEmpty()) {
            System.out.println("      x (No connections found)");
        } else {
            for (Node n : results) {
                System.out.println("      +--> " + n.toString());
            }
        }
        System.out.println();
    }
}