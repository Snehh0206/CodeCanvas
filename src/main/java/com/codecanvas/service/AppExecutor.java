package com.codecanvas.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutor {

    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static void submit(Runnable task) {
        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("[" + threadName + "] Task started");
            task.run();
            System.out.println("[" + threadName + "] Task finished");
        });
    }

    public static void shutdown() {
        executor.shutdown();
    }
}