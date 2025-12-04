package com.atlasdblite.commands;

import com.atlasdblite.engine.GraphEngine;
import com.atlasdblite.models.Node;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LinkCommand extends AbstractCommand {
    @Override
    public String getName() { return "link"; }

    @Override
    public String getDescription() { 
        return "Connects nodes. Usage: link <from> <to> <type> [key:val]..."; 
    }

    @Override
    public void execute(String[] args, GraphEngine engine) {
        if (!validateArgs(args, 3, "link <from> <to> <type> [prop:val]")) return;

        // Resolve Nodes
        System.out.println(" ... Resolving Source: '" + args[1] + "'");
        Node sourceNode = resolveNode(args[1], engine);
        if (sourceNode == null) return; 

        System.out.println(" ... Resolving Target: '" + args[2] + "'");
        Node targetNode = resolveNode(args[2], engine);
        if (targetNode == null) return; 

        String type = args[3].toUpperCase();
        Map<String, Object> props = new HashMap<>();

        // Parse Properties (Index 4 onwards)
        for (int i = 4; i < args.length; i++) {
            String rawArg = args[i];
            int splitIndex = rawArg.indexOf(":");
            if (splitIndex > 0) {
                String key = rawArg.substring(0, splitIndex);
                String value = rawArg.substring(splitIndex + 1);

                if (value.startsWith("[") && value.endsWith("]")) {
                    String content = value.substring(1, value.length() - 1);
                    List<String> listValue = Arrays.stream(content.split(","))
                            .map(String::trim)
                            .collect(Collectors.toList());
                    props.put(key, listValue);
                } else {
                    props.put(key, value);
                }
            }
        }

        try {
            engine.persistRelation(sourceNode.getId(), targetNode.getId(), type, props);
            String propStr = props.isEmpty() ? "" : " " + props;
            printSuccess("Linked: " + sourceNode.getId() + " --[" + type + propStr + "]--> " + targetNode.getId());
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }
}