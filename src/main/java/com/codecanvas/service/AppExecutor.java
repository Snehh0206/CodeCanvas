package com.codecanvas.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutor {

    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static void submit(Runnable task) {
        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("[" + threadName + "] Task started");
            try {
                task.run();
            } catch (Exception e) {
                System.out.println("[" + threadName + "] Task FAILED: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println("[" + threadName + "] Task finished");
        });
    }

    public static void shutdown() {
        executor.shutdown();
    }
}