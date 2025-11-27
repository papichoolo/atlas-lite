package com.atlasdblite;

import com.atlasdblite.engine.GraphEngine;
import com.atlasdblite.commands.*;
import java.util.Scanner;

public class AtlasShell {
    // Encrypted storage file
    private static final String DB_FILE = "atlas_data.enc";

    public static void main(String[] args) {
        GraphEngine engine = new GraphEngine(DB_FILE);
        CommandRegistry registry = new CommandRegistry();

        // 1. Core CRUD
        registry.register(new AddNodeCommand());
        registry.register(new UpdateNodeCommand());
        registry.register(new DeleteNodeCommand());
        registry.register(new LinkCommand());
        
        // 2. Querying
        registry.register(new ShowCommand());
        registry.register(new QueryCommand());
        registry.register(new SearchCommand()); // NEW

        // 3. Admin & Safety
        registry.register(new StatsCommand());
        registry.register(new BackupCommand()); // NEW
        registry.register(new ExportCommand());
        registry.register(new NukeCommand());

        Scanner scanner = new Scanner(System.in);
        printBanner();

        // REPL Loop
        while (true) {
            System.out.print("atlas-secure> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;
            if (input.equalsIgnoreCase("exit")) break;
            if (input.equalsIgnoreCase("clear")) {
                System.out.print("\033[H\033[2J");
                System.out.flush();
                continue;
            }
            if (input.equalsIgnoreCase("help")) {
                registry.printHelp();
                continue;
            }

            String[] tokens = input.split("\\s+");
            String commandName = tokens[0];

            Command cmd = registry.get(commandName);
            if (cmd != null) {
                try {
                    cmd.execute(tokens, engine);
                } catch (Exception e) {
                    System.out.println(" [CRASH] Command failed: " + e.getMessage());
                }
            } else {
                System.out.println(" Unknown command. Type 'help'.");
            }
        }
        System.out.println("Session closed. Data encrypted.");
        scanner.close();
    }

    private static void printBanner() {
        System.out.println("    _   _   _            ____  ____  ");
        System.out.println("   / \\ | |_| | __ _ ___ |  _ \\| __ ) ");
        System.out.println("  / _ \\| __| |/ _` / __|| | | |  _ \\ ");
        System.out.println(" / ___ \\ |_| | (_| \\__ \\| |_| | |_) |");
        System.out.println("/_/   \\_\\__|_|\\__,_|___/|____/|____/ ");
        System.out.println("      SECURE SHELL v2.1 | AES-256    ");
    }
}