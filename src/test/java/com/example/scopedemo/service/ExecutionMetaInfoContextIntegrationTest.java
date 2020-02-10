package com.example.scopedemo.service;

import com.example.scopedemo.ScopeDemoApplication;
import com.example.scopedemo.concurrent.ContextInheritingThreadPoolExecutor;
import com.example.scopedemo.model.ExecutionStep;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = ScopeDemoApplication.class)
@AutoConfigureMockMvc
class ExecutionMetaInfoContextIntegrationTest {
    @Autowired
    private ExecutionMetaInfoContext context;

    @Autowired
    private WebApplicationContext wac;


    @Test
    void addSteps() {
        context.addStep("Test");
        context.addStep("Another test step");
        List<ExecutionStep> steps = context.getExecutionSteps();
        assertEquals(2,steps.size());
        assertEquals("Test",steps.get(0).getDescription());
        assertEquals("Another test step",steps.get(1).getDescription());
    }

    @Test
    void addStepsConcurrent() throws InterruptedException {
//        context.setRequestDescription("Something");
        context.setOriginalRequestThread();
        ContextInheritingThreadPoolExecutor executor = new ContextInheritingThreadPoolExecutor();
        executor.submit(() -> {
            context.newSubProcess("my subprocess1");
            context.addStep("Parallel step 1");
            context.addStep("Parallel step two");
        });
        executor.submit(() -> {
            context.newSubProcess("Second Subprocess");
            context.addStep("Exeucuting the process");
            context.addStep("Completed");
            context.addStep("OK");
        });
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);


        List<ExecutionStep> steps = context.getExecutionSteps();
        System.out.println(steps);

        assertEquals(5,steps.size());
        assertEquals(2, steps.stream().filter(s -> s.getCategory().equals("my subprocess1")).count());
        assertEquals(3, steps.stream().filter(s -> s.getCategory().equals("Second Subprocess")).count());
    }
}
