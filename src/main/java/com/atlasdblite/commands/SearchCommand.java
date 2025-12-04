package com.atlasdblite.commands;

import com.atlasdblite.engine.GraphEngine;
import com.atlasdblite.models.Node;
import java.util.List;

public class SearchCommand extends AbstractCommand {
    @Override
    public String getName() { return "search"; }

    @Override
    public String getDescription() { return "Fuzzy search for nodes. Usage: search <query>"; }

    @Override
    public void execute(String[] args, GraphEngine engine) {
        if (!validateArgs(args, 1, "search <text>")) return;

        String query = args[1];
        
        // Delegate to engine.search() which handles Sharding + Indexing + List logic
        List<Node> matches = engine.search(query);

        System.out.println("--- Search Results for '" + query + "' ---");
        if (matches.isEmpty()) {
            System.out.println(" > No matches found.");
        } else {
            for (Node n : matches) {
                System.out.println(" > " + n);
            }
        }
    }
}