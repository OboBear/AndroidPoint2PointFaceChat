package com.obo.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by obo on 2017/8/11.
 * Email:obo.lin@dingtone.me
 */
public final class ThreadPool {
    private ExecutorService cachedThreadPool;

    private static class ThreadPoolHolder {
        private static ThreadPool INSTANCE = new ThreadPool();
    }

    public static ThreadPool getInstance() {
        return ThreadPoolHolder.INSTANCE;
    }

    private ThreadPool() {
        cachedThreadPool = Executors.newCachedThreadPool();
    }

    public void exec(Runnable runnable) {
        cachedThreadPool.execute(runnable);
    }

}
