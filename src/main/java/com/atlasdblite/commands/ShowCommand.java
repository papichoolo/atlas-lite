package com.atlasdblite.commands;

import com.atlasdblite.engine.GraphEngine;
import com.atlasdblite.models.Node;

public class ShowCommand extends AbstractCommand {
    @Override
    public String getName() { return "show"; }

    @Override
    public String getDescription() { return "Lists all nodes in the database."; }

    @Override
    public void execute(String[] args, GraphEngine engine) {
        System.out.println("--- Current Nodes ---");
        for (Node n : engine.getAllNodes()) {
            System.out.println(n);
        }
        System.out.println("---------------------");
    }
}