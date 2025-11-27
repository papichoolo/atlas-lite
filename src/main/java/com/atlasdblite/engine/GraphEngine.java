package com.atlasdblite.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.atlasdblite.models.Node;
import com.atlasdblite.models.Relation;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class GraphEngine {
    private Map<String, Node> nodeIndex = new HashMap<>();
    private List<Relation> relationStore = new ArrayList<>();
    private final String storagePath;
    private final Gson gson;

    public GraphEngine(String storagePath) {
        this.storagePath = storagePath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        initializeStorage();
    }

    public void persistNode(Node node) {
        nodeIndex.put(node.getId(), node);
        commit();
    }

    public void persistRelation(String fromId, String toId, String type) {
        if (!nodeIndex.containsKey(fromId) || !nodeIndex.containsKey(toId)) {
            throw new IllegalArgumentException("Error: Source or Target node does not exist.");
        }
        relationStore.add(new Relation(fromId, toId, type));
        commit();
    }

    public Collection<Node> getAllNodes() {
        return nodeIndex.values();
    }

    public List<Node> traverse(String fromId, String relationType) {
        return relationStore.stream()
                .filter(r -> r.getSourceId().equals(fromId) && r.getType().equalsIgnoreCase(relationType))
                .map(r -> nodeIndex.get(r.getTargetId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void commit() {
        try (Writer writer = new FileWriter(storagePath)) {
            StorageSchema schema = new StorageSchema(nodeIndex, relationStore);
            gson.toJson(schema, writer);
        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
        }
    }

    private void initializeStorage() {
        if (!Files.exists(Paths.get(storagePath))) return;
        try (Reader reader = new FileReader(storagePath)) {
            StorageSchema schema = gson.fromJson(reader, StorageSchema.class);
            if (schema != null) {
                if (schema.nodes != null) this.nodeIndex = schema.nodes;
                if (schema.relations != null) this.relationStore = schema.relations;
            }
        } catch (IOException e) {
            System.err.println("Could not load database: " + e.getMessage());
        }
    }

    private static class StorageSchema {
        Map<String, Node> nodes;
        List<Relation> relations;
        StorageSchema(Map<String, Node> n, List<Relation> r) { this.nodes = n; this.relations = r; }
    }
}