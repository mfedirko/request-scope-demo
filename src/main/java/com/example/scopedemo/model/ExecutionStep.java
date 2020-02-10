package com.example.scopedemo.model;

public class ExecutionStep {
    private final String key;
    private final String description;
    private final String category;
    public ExecutionStep(String key, String description) {
        this.key = key;
        this.description = description;
        this.category = null;
    }
    public ExecutionStep(String key, String description, String category) {
        this.key = key;
        this.description = description;
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "ExecutionStep{" +
                "key='" + key + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}

