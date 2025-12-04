package com.atlasdblite.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a node (or vertex) in the graph.
 * A node has a unique ID, a label to classify it, and a map of key-value properties.
 * This class includes logic for efficient binary serialization.
 */
public class Node {
    private final String id;
    private final String label;
    // Changed from String to Object to support Lists
    private final Map<String, Object> properties;

    /**
     * Constructs a new Node.
     * @param id The unique identifier for the node.
     * @param label The type or classification of the node (e.g., "Person", "Company").
     */
    public Node(String id, String label) {
        this.id = id;
        // The 'intern()' method is used to save memory by ensuring that identical strings
        // (like common labels) are stored only once in the JVM's string pool.
        this.label = label.intern(); 
        this.properties = new HashMap<>();
    }

    /**
     * Adds or updates a property on the node.
     * @param key The property key.
     * @param value The property value (String or List).
     */
    public void addProperty(String key, Object value) {
        // Keys are also interned for memory efficiency, as they are often repeated.
        this.properties.put(key.intern(), value);
    }

    public String getId() { return id; }
    public String getLabel() { return label; }
    public Map<String, Object> getProperties() { return properties; }

    // --- Binary Serialization ---

    /**
     * Writes the node's data to a binary output stream for persistence.
     * Supports both String and List property types.
     * @param out The {@link DataOutputStream} to write to.
     * @throws IOException If an I/O error occurs.
     */
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeUTF(id);
        out.writeUTF(label);
        out.writeInt(properties.size());
        
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            out.writeUTF(entry.getKey());
            Object val = entry.getValue();
            
            if (val instanceof List) {
                out.writeByte(2); // Type 2: List
                List<?> list = (List<?>) val;
                out.writeInt(list.size());
                for (Object item : list) {
                    out.writeUTF(item.toString());
                }
            } else {
                out.writeByte(1); // Type 1: String
                out.writeUTF(val.toString());
            }
        }
    }

    /**
     * Creates a Node instance by reading data from a binary input stream.
     * Handles type reconstruction for Strings and Lists.
     * @param in The {@link DataInputStream} to read from.
     * @return A new {@link Node} instance.
     * @throws IOException If an I/O error occurs or the stream is malformed.
     */
    public static Node readFrom(DataInputStream in) throws IOException {
        String id = in.readUTF();
        String label = in.readUTF();
        Node node = new Node(id, label);
        
        int propCount = in.readInt();
        for (int i = 0; i < propCount; i++) {
            String key = in.readUTF();
            byte type = in.readByte();
            
            if (type == 2) { // List
                int listSize = in.readInt();
                List<String> list = new ArrayList<>();
                for (int j = 0; j < listSize; j++) {
                    list.add(in.readUTF());
                }
                node.addProperty(key, list);
            } else { // String (Default)
                String value = in.readUTF();
                node.addProperty(key, value);
            }
        }
        return node;
    }

    @Override
    public String toString() {
        return String.format("[ID: %s | Label: %s] %s", id, label, properties);
    }
}