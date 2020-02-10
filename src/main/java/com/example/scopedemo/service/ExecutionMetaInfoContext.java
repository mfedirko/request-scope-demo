package com.example.scopedemo.service;

import com.example.scopedemo.model.ExecutionStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Request-scoped context for showing metadata for a specific request execution.
 * Allows components to independently provide their execution steps and arranges them sequentially and by subprocess name.
 * Any thread that calls this class must be in request scope.
 * To ensure async threads are in request scope, use {@link com.example.scopedemo.concurrent.ContextInheritingThreadPoolExecutor}.
 * Additionally, this class must always be initialized in the original request thread by calling 'setOriginalRequestThread'.
 * This initialization is done automatically by {@link com.example.scopedemo.controller.MetaInfoContextInitializingFilter}
 */
@Component
@RequestScope
public class ExecutionMetaInfoContext {
    private ThreadLocal<AtomicInteger> atomicInteger = new ThreadLocal<>();

    private Logger LOG = LoggerFactory.getLogger(ExecutionMetaInfoContext.class);
    private boolean initializedMainThread;
    private ThreadLocal<String> customThreadTasksLabel = ThreadLocal.withInitial(() -> "Thread " + + Thread.currentThread().getId());
    private String description;
    public ExecutionMetaInfoContext() {
        LOG.info("Initializing execution meta info context for request");
    }
    private Map<String,List<ExecutionStep>> concurrentExecutionSteps = new ConcurrentHashMap<>();

    private boolean hasConcurrentSteps() {
        return concurrentExecutionSteps.size() > 1;
    }
    public void setRequestDescription(String description) {
        this.description = description;
    }

    public void setOriginalRequestThread() {
        if (!initializedMainThread) {
            LOG.info("Initializing the original request thread");
            initializedMainThread = true;
            this.newSubProcess("Main execution");
        } else {
            LOG.warn("Already initialized the original request thread");
        }
    }
    /*
     * Returns the step number, relative to the current thread.
     */
    private int getNextStepNumber() {
        AtomicInteger integer = atomicInteger.get();
        if (integer == null) {
            integer = new AtomicInteger();
            atomicInteger.set(integer);
        }
        return integer.incrementAndGet();
    }

    /*
     * Adds a step description to the list. Captures the current thread from which the method was called.
     */
    public void addStep(String description) {
        String key = "" + getNextStepNumber();
        String category = getCurrentThreadLabel();
        if (!concurrentExecutionSteps.containsKey(category)) {
            concurrentExecutionSteps.put(category, new ArrayList<>());
        }
        this.concurrentExecutionSteps.get(category).add(new ExecutionStep(key, description));
    }
    private String getCurrentThreadLabel() {
        return customThreadTasksLabel.get();
    }

    /*
     *  Make sure to call this  method before addStep when executing a new thread, otherwise there may be undefined
     *   behavior due to thread caching.
     */
    public void newSubProcess(String label) {
        if (!initializedMainThread) {
            LOG.warn("You should first call the 'setOriginalRequestThread' method before calling this from async threads! Not doing this may result in unexpected behavior");
        }
        LOG.info(String.format("Setting custom label '%s' for thread '%s'", label, "" + Thread.currentThread().getId()));
        atomicInteger.set(new AtomicInteger());
        this.customThreadTasksLabel.set(label);
    }

    public String getDescription() {
        return description;
    }

    /*
     * Returns the execution steps that have been set in this context.
     * The steps are first arranged based on the thread label to be able to categorize concurrent steps.
     */
    public List<ExecutionStep> getExecutionSteps() {
        if (hasConcurrentSteps()) {
            List<ExecutionStep> allStepsSortedByThread = new ArrayList<>();
            for (Map.Entry<String, List<ExecutionStep>> entry : concurrentExecutionSteps.entrySet()) {
                String label = entry.getKey();
                List<ExecutionStep> threadSteps = entry.getValue();

                for (ExecutionStep step : threadSteps) {
                    allStepsSortedByThread.add(new ExecutionStep(step.getKey(), step.getDescription(), label));
                }
            }
            return allStepsSortedByThread;
        } else if (concurrentExecutionSteps.isEmpty()) {
            return new ArrayList<>();
        } else {
            String onlyKey = concurrentExecutionSteps.keySet().iterator().next();
            return concurrentExecutionSteps.get(onlyKey);
        }
    }
}
