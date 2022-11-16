package com.henyiwu.simplerxjava.core.scheduler;

import java.util.concurrent.ExecutorService;

public class NewThreadScheduler extends Scheduler {

    final ExecutorService executorService;

    public NewThreadScheduler(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public Worker createWorker() {
        return new NewThreadWorker(executorService);
    }

    static final class NewThreadWorker implements Worker {

        final ExecutorService mapper;

        public NewThreadWorker(ExecutorService mapper) {
            this.mapper = mapper;
        }

        @Override
        public void scheduler(Runnable runnable) {
            mapper.execute(runnable);
        }
    }
}
