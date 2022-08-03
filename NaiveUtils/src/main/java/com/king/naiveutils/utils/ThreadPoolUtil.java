package com.king.naiveutils.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池
 * @author NaiveKing
 * @date 2022/6/14
 */
public class ThreadPoolUtil {

    //线程池核心线程数
    private static final int CORE_POOL_SIZE = 5;
    //线程池最大线程数
    private static final int MAX_POOL_SIZE = 20;
    //额外线程空状态生存时间
    private static final int KEEP_ALIVE_TIME = 10000;
    //阻塞队列。当核心线程都被占用，且阻塞队列已满的情况下，才会开启额外线程。
    private static final BlockingQueue workQueue = new ArrayBlockingQueue(10);
    //线程池
    private static final ThreadPoolExecutor threadPool;
    //计时线程池
    private static final ScheduledThreadPoolExecutor scheduledThreadPool;

    private ThreadPoolUtil() {
    }

    //线程工厂
    private static ThreadFactory threadFactory = new ThreadFactory() {
        private final AtomicInteger integer = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "myThreadPool thread:" + integer.getAndIncrement());
        }
    };

    static {
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, workQueue, threadFactory);

        scheduledThreadPool = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE, threadFactory);
    }

    public static void execute(Runnable runnable) {
        threadPool.execute(runnable);
    }

    public static void execute(FutureTask futureTask) {
        threadPool.execute(futureTask);
    }

    public static void cancel(FutureTask futureTask) {
        futureTask.cancel(true);
    }

    /**
     * 延时任务
     *
     * @param command 任务
     * @param delay   延时时间
     * @param unit    时间单位
     */
    public static void scheduled(Runnable command, long delay, TimeUnit unit) {
        scheduledThreadPool.schedule(command, delay, unit);
    }

    /**
     * 定期定时执行任务（任务开始时按固定时间执行下一次任务）
     *
     * @param command      任务
     * @param initialDelay 开始时间
     * @param period       间隔时间
     * @param unit         时间单位
     */
    public static void scheduledAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        scheduledThreadPool.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    /**
     * 定期延时执行任务（任务结束时开始定时执行下一次任务）
     *
     * @param command      任务
     * @param initialDelay 开始时间
     * @param delay        延时时间
     * @param unit         时间单位
     */
    public static void scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        scheduledThreadPool.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }
}
