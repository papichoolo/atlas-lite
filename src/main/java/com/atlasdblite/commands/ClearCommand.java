package com.atlasdblite.commands;

import com.atlasdblite.engine.GraphEngine;

public class ClearCommand extends AbstractCommand {
    @Override
    public String getName() { return "clear"; }

    @Override
    public String getDescription() { return "Clears the terminal screen."; }

    @Override
    public void execute(String[] args, GraphEngine engine) {
        // ANSI Escape code to clear screen (works in most Unix/Linux/Mac terminals)
        // For Windows CMD, this might not work without Jansi, but works in PowerShell/GitBash
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}