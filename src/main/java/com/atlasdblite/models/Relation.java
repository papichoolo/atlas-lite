package com.atlasdblite.models;

public class Relation {
    private final String sourceId;
    private final String targetId;
    private final String type;

    public Relation(String sourceId, String targetId, String type) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.type = type;
    }

    public String getSourceId() { return sourceId; }
    public String getTargetId() { return targetId; }
    public String getType() { return type; }

    @Override
    public String toString() {
        return String.format("(%s)-[:%s]->(%s)", sourceId, type, targetId);
    }
}