package com.example.scopedemo.model;

import com.example.scopedemo.service.ExecutionMetaInfoContext;

import java.util.List;

/*
 * Represents meta information for the execution of some request. This is meant to give the user more understanding of
 *  the underlying workings of the application.
 * It is constructed from the ExecutionMetaInfoContext which contains details from each component/step
 *  of the request execution flow.
 */
public class MetaInfo {
    private String description;
    private List<ExecutionStep> executionSteps;

    public MetaInfo(ExecutionMetaInfoContext context) {
        this.description = context.getDescription();
        this.executionSteps = context.getExecutionSteps();
    }
    public String getDescription() {
        return description;
    }

    public List<ExecutionStep> getExecutionSteps() {
        return executionSteps;
    }

}
