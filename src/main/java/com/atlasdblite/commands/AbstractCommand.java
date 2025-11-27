package com.atlasdblite.commands;

/**
 * Base class for all commands.
 * Implements common validation logic.
 */
public abstract class AbstractCommand implements Command {
    
    // Helper method for inheritance: Validate Argument Count
    protected boolean validateArgs(String[] args, int expected, String usage) {
        // args[0] is the command name itself, so we check length - 1
        if (args.length - 1 < expected) {
            System.out.println(" > Error: Invalid arguments.");
            System.out.println(" > Usage: " + usage);
            return false;
        }
        return true;
    }

    protected void printSuccess(String message) {
        System.out.println(" [OK] " + message);
    }
    
    protected void printError(String message) {
        System.out.println(" [ERR] " + message);
    }
}