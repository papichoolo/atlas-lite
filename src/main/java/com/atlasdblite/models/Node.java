package com.atlasdblite.models;

import java.util.HashMap;
import java.util.Map;

public class Node {
    private final String id;
    private final String label;
    private final Map<String, String> properties;

    public Node(String id, String label) {
        this.id = id;
        this.label = label;
        this.properties = new HashMap<>();
    }

    public void addProperty(String key, String value) {
        this.properties.put(key, value);
    }

    public String getId() { return id; }
    public String getLabel() { return label; }
    public String getProperty(String key) { return properties.get(key); }
    public Map<String, String> getProperties() { return properties; }

    @Override
    public String toString() {
        return String.format("(%s:%s) %s", id, label, properties);
    }
}