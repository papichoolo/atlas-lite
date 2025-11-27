package com.atlasdblite.commands;

import com.atlasdblite.engine.GraphEngine;

public interface Command {
    String getName();
    String getDescription();
    void execute(String[] args, GraphEngine engine);
}