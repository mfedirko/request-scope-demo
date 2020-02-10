package com.example.scopedemo.service;

import com.example.scopedemo.model.ExecutionStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionMetaInfoContextTest {
    private ExecutionMetaInfoContext context;
    @BeforeEach
    void setup() {
        context = new ExecutionMetaInfoContext();
    }


    @Test
    void noStepsAdded() {
        List<ExecutionStep> steps = context.getExecutionSteps();
        assertTrue(steps.isEmpty());
    }
    @Test
    void addStepsStoresStepsInOrder() {
        context.addStep("My step");
        context.addStep("Another step");
        context.addStep("step 3");

        List<ExecutionStep> steps = context.getExecutionSteps();
        assertEquals(3, steps.size());
        assertEquals("My step", steps.get(0).getDescription());
        assertEquals("1", steps.get(0).getKey());
        assertNull(steps.get(0).getCategory());

        assertEquals("Another step", steps.get(1).getDescription());
        assertEquals("2", steps.get(1).getKey());
        assertNull(steps.get(1).getCategory());

        assertEquals("step 3", steps.get(2).getDescription());
        assertEquals("3", steps.get(2).getKey());
        assertNull(steps.get(2).getCategory());
    }

    @Test
    void addStepsConcurrentlyAddsCategories() throws InterruptedException {
        Thread t2 = new Thread(() -> {
            context.newSubProcess("ABC Subprocess");
            context.addStep("Some step");
            context.addStep("step!");
        });

        Thread t1 = new Thread(() -> {
           context.newSubProcess("DEF subproces");
            context.addStep("Calibrating the dhjgdfj");
            context.addStep("Decrypting the fdhjfhjdhjdgf");
            context.addStep("Prepare for ehjfewhjfhjew");
        });

        Thread t13 = new Thread(() -> {
            context.newSubProcess("GHI subprocess");
            context.addStep("Manipulating the aafefefefe");
            context.addStep("Polarizing the f4gfgfwf");
        });

        t1.start();
        t2.start();
        t13.start();

        t1.join();
        t2.join();
        t13.join();

        List<ExecutionStep> steps = context.getExecutionSteps();
        assertEquals(7, steps.size());

        List<ExecutionStep> abcSteps = steps.stream().filter(s -> s.getCategory().equals("ABC Subprocess")).collect(Collectors.toList());
        List<ExecutionStep> defSteps = steps.stream().filter(s -> s.getCategory().equals("DEF subproces")).collect(Collectors.toList());
        List<ExecutionStep> ghiSteps = steps.stream().filter(s -> s.getCategory().equals("GHI subprocess")).collect(Collectors.toList());

        assertEquals(2, abcSteps.size());
        assertEquals("1", abcSteps.get(0).getKey());
        assertEquals("Some step", abcSteps.get(0).getDescription());
        assertEquals("2", abcSteps.get(1).getKey());
        assertEquals("step!", abcSteps.get(1).getDescription());

        assertEquals(3, defSteps.size());
        assertEquals("1", defSteps.get(0).getKey());
        assertEquals("Calibrating the dhjgdfj", defSteps.get(0).getDescription());
        assertEquals("2", abcSteps.get(1).getKey());
        assertEquals("Decrypting the fdhjfhjdhjdgf", defSteps.get(1).getDescription());
        assertEquals("3", defSteps.get(2).getKey());
        assertEquals("Prepare for ehjfewhjfhjew", defSteps.get(2).getDescription());

        assertEquals(2, ghiSteps.size());
        assertEquals("1", ghiSteps.get(0).getKey());
        assertEquals("Manipulating the aafefefefe", ghiSteps.get(0).getDescription());
        assertEquals("2", ghiSteps.get(1).getKey());
        assertEquals("Polarizing the f4gfgfwf", ghiSteps.get(1).getDescription());
    }

    @Test
    void addStepsConcurrentStressTest() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Callable<Void>> callables = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            callables.add(new CustomConcurrentTask(i));
        }
        executorService.invokeAll(callables);
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);

        List<ExecutionStep> executionSteps = context.getExecutionSteps();
        System.out.println(executionSteps.size());
        for (int i = 0; i < 1000; i++) {
            final String value = "My Next Thread: " + i;
            List<ExecutionStep> stepsForThread = executionSteps.stream().filter(s -> s.getCategory().equals(value)).collect(Collectors.toList());
            assertEquals(3, stepsForThread.size());
            assertEquals("1", stepsForThread.get(0).getKey());
            assertEquals("2", stepsForThread.get(1).getKey());
            assertEquals("3", stepsForThread.get(2).getKey());
        }
    }

    @Test
    void addStepsFromNestedMethods() {
        new MyFirstClass().doIt();
        List<ExecutionStep> steps = context.getExecutionSteps();
        assertEquals(4, steps.size());
        assertEquals("My step is starting", steps.get(0).getDescription());
        assertEquals("A third step is doing it!",steps.get(1).getDescription());
        assertEquals("Another step is doing it",steps.get(2).getDescription());
        assertEquals("My step is done",steps.get(3).getDescription());
    }

    class CustomConcurrentTask implements Callable<Void> {
        private int i;
        CustomConcurrentTask(int i) {
            this.i = i;
        }
        @Override
        public Void call() throws Exception {
            context.newSubProcess("My Next Thread: " + i);
            context.addStep("Another one");
            context.addStep("bites the");
            context.addStep("dust");
            return null;
        }
    }

    class MyFirstClass {
        void doIt() {
            context.addStep("My step is starting");
            new  MyOtherClass().doIt();
            context.addStep("My step is done");
        }
    }

    class MyOtherClass {
        void doIt() {
            System.out.println("OK");
            new  MyThirdClass().doIt();
            context.addStep("Another step is doing it");
            System.out.println("step");
        }
    }

    class MyThirdClass {
        void doIt() {
            System.out.println("Doing");
            context.addStep("A third step is doing it!");
        }
    }
}