package com.atlasdblite.commands;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();

    public void register(Command cmd) {
        commands.put(cmd.getName(), cmd);
    }

    public Command get(String name) {
        return commands.get(name);
    }

    public void printHelp() {
        System.out.println("\nAvailable Commands:");
        System.out.printf("%-15s %s%n", "COMMAND", "DESCRIPTION");
        System.out.println("------------------------------------------------");
        commands.values().forEach(cmd -> 
            System.out.printf("%-15s %s%n", cmd.getName(), cmd.getDescription())
        );
        System.out.println("------------------------------------------------");
    }
}