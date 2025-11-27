package com.atlasdblite.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.atlasdblite.models.Node;
import com.atlasdblite.models.Relation;
import com.atlasdblite.security.CryptoManager;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class GraphEngine {
    private Map<String, Node> nodeIndex = new HashMap<>();
    private List<Relation> relationStore = new ArrayList<>();
    
    private final String storagePath;
    private final Gson gson;
    private final CryptoManager crypto;

    public GraphEngine(String storagePath) {
        this.storagePath = storagePath; // Expecting a .enc file path
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.crypto = new CryptoManager();
        initializeStorage();
    }

    // --- Write Operations (CRUD) ---

    public void persistNode(Node node) {
        nodeIndex.put(node.getId(), node);
        commit();
    }

    public boolean updateNode(String id, String key, String value) {
        Node node = nodeIndex.get(id);
        if (node == null) return false;
        
        node.addProperty(key, value);
        commit();
        return true;
    }

    public boolean deleteNode(String id) {
        if (!nodeIndex.containsKey(id)) return false;
        
        // 1. Remove the node
        nodeIndex.remove(id);
        
        // 2. Cascade delete: Remove all relations involving this node to prevent orphans
        relationStore.removeIf(r -> r.getSourceId().equals(id) || r.getTargetId().equals(id));
        
        commit();
        return true;
    }

    public void persistRelation(String fromId, String toId, String type) {
        if (!nodeIndex.containsKey(fromId) || !nodeIndex.containsKey(toId)) {
            throw new IllegalArgumentException("Error: Source or Target node does not exist.");
        }
        relationStore.add(new Relation(fromId, toId, type));
        commit();
    }

    public void wipeDatabase() {
        nodeIndex.clear();
        relationStore.clear();
        commit();
    }

    // --- Read Operations (Querying) ---

    public Node getNode(String id) {
        return nodeIndex.get(id);
    }

    public Collection<Node> getAllNodes() {
        return nodeIndex.values();
    }

    public List<Relation> getAllRelations() {
        return relationStore;
    }

    public List<Node> traverse(String fromId, String relationType) {
        return relationStore.stream()
                .filter(r -> r.getSourceId().equals(fromId) && r.getType().equalsIgnoreCase(relationType))
                .map(r -> nodeIndex.get(r.getTargetId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // --- Encrypted Storage Layer ---

    private void commit() {
        try {
            StorageSchema schema = new StorageSchema(nodeIndex, relationStore);
            String rawJson = gson.toJson(schema);
            
            // Encrypt before writing to disk
            String encryptedData = crypto.encrypt(rawJson); 
            Files.write(Paths.get(storagePath), encryptedData.getBytes());
        } catch (Exception e) {
            System.err.println("Critical IO Error during save: " + e.getMessage());
        }
    }

    private void initializeStorage() {
        if (!Files.exists(Paths.get(storagePath))) return;
        
        try {
            byte[] encryptedBytes = Files.readAllBytes(Paths.get(storagePath));
            String encryptedData = new String(encryptedBytes);
            
            // Decrypt after reading from disk
            String rawJson = crypto.decrypt(encryptedData); 
            
            StorageSchema schema = gson.fromJson(rawJson, StorageSchema.class);
            if (schema != null) {
                if (schema.nodes != null) this.nodeIndex = schema.nodes;
                if (schema.relations != null) this.relationStore = schema.relations;
            }
        } catch (Exception e) {
            System.err.println("Could not load database. Key mismatch or corrupt file.");
        }
    }

    // Internal Schema for JSON Serialization
    private static class StorageSchema {
        Map<String, Node> nodes;
        List<Relation> relations;
        StorageSchema(Map<String, Node> n, List<Relation> r) { this.nodes = n; this.relations = r; }
    }
}