package com.atlasdblite.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a directional link between two nodes with optional properties.
 * Structure: (Source) --[Type + Properties]--> (Target)
 */
public class Relation {
    private final String sourceId;
    private final String targetId;
    private final String type;
    private final Map<String, Object> properties;

    public Relation(String sourceId, String targetId, String type) {
        this(sourceId, targetId, type, new HashMap<>());
    }

    public Relation(String sourceId, String targetId, String type, Map<String, Object> properties) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.type = type.intern();
        this.properties = properties != null ? properties : new HashMap<>();
    }

    public void addProperty(String key, Object value) {
        this.properties.put(key.intern(), value);
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getType() {
        return type;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    // --- Binary Serialization ---

    public void writeTo(DataOutputStream out) throws IOException {
        out.writeUTF(sourceId);
        out.writeUTF(targetId);
        out.writeUTF(type);

        // Write Properties
        out.writeInt(properties.size());
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            out.writeUTF(entry.getKey());
            Object val = entry.getValue();

            if (val instanceof List) {
                out.writeByte(2); // List
                List<?> list = (List<?>) val;
                out.writeInt(list.size());
                for (Object item : list)
                    out.writeUTF(item.toString());
            } else {
                out.writeByte(1); // String
                out.writeUTF(val.toString());
            }
        }
    }

    public static Relation readFrom(DataInputStream in) throws IOException {
        String src = in.readUTF();
        String tgt = in.readUTF();
        String type = in.readUTF();
        Map<String, Object> props = new HashMap<>();

        int propCount = in.readInt();
        for (int i = 0; i < propCount; i++) {
            String key = in.readUTF();
            byte dataType = in.readByte();

            if (dataType == 2) {
                int listSize = in.readInt();
                List<String> list = new ArrayList<>();
                for (int j = 0; j < listSize; j++)
                    list.add(in.readUTF());
                props.put(key, list);
            } else {
                props.put(key, in.readUTF());
            }
        }
        return new Relation(src, tgt, type, props);
    }

    @Override
    public String toString() {
        if (properties.isEmpty()) {
            return String.format("(%s)-[:%s]->(%s)", sourceId, type, targetId);
        }
        return String.format("(%s)-[:%s %s]->(%s)", sourceId, type, properties, targetId);
    }
}