package com.atlasdblite;

import com.atlasdblite.engine.GraphEngine;
import com.atlasdblite.commands.*;
import java.util.Scanner;

public class AtlasShell {
    private static final String DB_FILE = "atlas_data.json";

    public static void main(String[] args) {
        GraphEngine engine = new GraphEngine(DB_FILE);
        CommandRegistry registry = new CommandRegistry();

        // Register Commands
        registry.register(new AddNodeCommand());
        registry.register(new LinkCommand());
        registry.register(new ShowCommand());
        registry.register(new QueryCommand());

        Scanner scanner = new Scanner(System.in);
        
        printBanner();

        // REPL Loop
        while (true) {
            System.out.print("atlasdb> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;
            if (input.equalsIgnoreCase("exit")) break;
            if (input.equalsIgnoreCase("help")) {
                registry.printHelp();
                continue;
            }

            String[] tokens = input.split("\\s+");
            String commandName = tokens[0];

            Command cmd = registry.get(commandName);
            if (cmd != null) {
                cmd.execute(tokens, engine);
            } else {
                System.out.println("Unknown command. Type 'help' for list.");
            }
        }
        
        System.out.println("Goodbye.");
        scanner.close();
    }

    private static void printBanner() {
        System.out.println("    _   _   _            ____  ____  ");
        System.out.println("   / \\ | |_| | __ _ ___ |  _ \\| __ ) ");
        System.out.println("  / _ \\| __| |/ _` / __|| | | |  _ \\ ");
        System.out.println(" / ___ \\ |_| | (_| \\__ \\| |_| | |_) |");
        System.out.println("/_/   \\_\\__|_|\\__,_|___/|____/|____/ ");
        System.out.println("      CLI v1.1 | Type 'help'         ");
    }
}