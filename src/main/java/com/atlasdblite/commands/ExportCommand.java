package com.atlasdblite.commands;

import com.atlasdblite.engine.GraphEngine;
import com.atlasdblite.models.Node;
import com.atlasdblite.models.Relation;
import java.io.FileWriter;
import java.io.IOException;

public class ExportCommand extends AbstractCommand {
    @Override
    public String getName() { return "export"; }

    @Override
    public String getDescription() { return "Exports graph to DOT format. Usage: export <filename.dot>"; }

    @Override
    public void execute(String[] args, GraphEngine engine) {
        if (!validateArgs(args, 1, "export <filename.dot>")) return;

        String filename = args[1];
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write("digraph G {\n");
            
            // Write Nodes
            for (Node n : engine.getAllNodes()) {
                fw.write(String.format("  \"%s\" [label=\"%s:%s\"];\n", 
                    n.getId(), n.getId(), n.getLabel()));
            }

            // Write Edges
            for (Relation r : engine.getAllRelations()) {
                fw.write(String.format("  \"%s\" -> \"%s\" [label=\"%s\"];\n", 
                    r.getSourceId(), r.getTargetId(), r.getType()));
            }

            fw.write("}\n");
            printSuccess("Exported graph to " + filename);
        } catch (IOException e) {
            printError("Export failed: " + e.getMessage());
        }
    }
}