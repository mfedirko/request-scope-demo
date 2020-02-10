package com.example.scopedemo.concurrent;

import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.*;

/*
 * This executor passes on the request attributes of the current request context to each runnable/callable it creates.
 * This allows beans with @RequestScope to be accessible within the callable/runnable tasks.
 */
public class ContextInheritingThreadPoolExecutor extends ThreadPoolExecutor {

    public static ContextInheritingThreadPoolExecutor cachedThreadPool() {
        return new ContextInheritingThreadPoolExecutor();
    }

    public ContextInheritingThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public ContextInheritingThreadPoolExecutor() {
        super(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(new ContextAwareCallable<>(task, RequestContextHolder.currentRequestAttributes()));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(new ContextAwareRunnable(task, RequestContextHolder.currentRequestAttributes()));
    }
    @Override
    public void execute(Runnable command) {
        super.execute(new ContextAwareRunnable(command, RequestContextHolder.currentRequestAttributes()));
    }
}
