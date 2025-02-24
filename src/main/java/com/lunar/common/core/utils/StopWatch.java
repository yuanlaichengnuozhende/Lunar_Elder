package com.lunar.common.core.utils;

import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 秒表封装
 * <p>
 * 优化prettyPrint方法
 *
 * <br> 此工具用于存储一组任务的耗时时间，并一次性打印对比。
 *
 * <br> 比如：我们可以记录多段代码耗时时间，然后一次性打印（StopWatch提供了一个prettyString()函数用于按照指定格式打印出耗时）
 * <p>
 * <p>
 * org.springframework.util.StopWatch
 * <p>
 * 此工具来自：https://github.com/spring-projects/spring-framework/blob/master/spring-core/src/main/java/org/springframework/util/StopWatch.java
 * <p>
 * <p>
 * <p> * 使用方法如下： *
 * <pre>
 * StopWatch stopWatch = new StopWatch("任务名称");
 *
 * // 任务1
 * stopWatch.start("任务一");
 * Thread.sleep(1000);
 * stopWatch.stop();
 *
 * // 任务2
 * stopWatch.start("任务一");
 * Thread.sleep(2000);
 * stopWatch.stop();
 *
 * // 打印出耗时
 * Console.log(stopWatch.prettyPrint());
 *
 * @author szx
 * @date 2022/07/13 17:55
 */
public class StopWatch {

    /**
     * Identifier of this {@code StopWatch}.
     * <p>Handy when we have output from multiple stop watches and need to
     * distinguish between them in log or console output.
     */
    private final String id;

    private boolean keepTaskList = true;

    private final List<TaskInfo> taskList = new ArrayList<>(1);

    /** Start time of the current task. */
    private long startTimeNanos;

    /** Name of the current task. */
    @Nullable
    private String currentTaskName;

    @Nullable
    private TaskInfo lastTaskInfo;

    private int taskCount;

    /** Total running time. */
    private long totalTimeNanos;

    /**
     * Construct a new {@code StopWatch}.
     * <p>Does not start any task.
     */
    public StopWatch() {
        this("");
    }

    /**
     * Construct a new {@code StopWatch} with the given ID.
     * <p>The ID is handy when we have output from multiple stop watches and need
     * to distinguish between them.
     * <p>Does not start any task.
     *
     * @param id identifier for this stop watch
     */
    public StopWatch(String id) {
        this.id = id;
    }

    /**
     * Get the ID of this {@code StopWatch}, as specified on construction.
     *
     * @return the ID (empty String by default)
     * @see #StopWatch(String)
     * @since 4.2.2
     */
    public String getId() {
        return this.id;
    }

    /**
     * Configure whether the {@link org.springframework.util.StopWatch.TaskInfo} array is built over time.
     * <p>Set this to {@code false} when using a {@code StopWatch} for millions
     * of intervals; otherwise, the {@code TaskInfo} structure will consume excessive memory.
     * <p>Default is {@code true}.
     */
    public void setKeepTaskList(boolean keepTaskList) {
        this.keepTaskList = keepTaskList;
    }

    /**
     * Start an unnamed task.
     * <p>The results are undefined if {@link #stop()} or timing methods are
     * called without invoking this method first.
     *
     * @see #start(String)
     * @see #stop()
     */
    public void start() throws IllegalStateException {
        start("");
    }

    /**
     * Start a named task.
     * <p>The results are undefined if {@link #stop()} or timing methods are
     * called without invoking this method first.
     *
     * @param taskName the name of the task to start
     * @see #start()
     * @see #stop()
     */
    public void start(String taskName) throws IllegalStateException {
        if (this.currentTaskName != null) {
            throw new IllegalStateException("Can't start StopWatch: it's already running");
        }
        this.currentTaskName = taskName;
        this.startTimeNanos = System.nanoTime();
    }

    /**
     * Stop the current task.
     * <p>The results are undefined if timing methods are called without invoking
     * at least one pair of {@code start()} / {@code stop()} methods.
     *
     * @see #start()
     * @see #start(String)
     */
    public void stop() throws IllegalStateException {
        if (this.currentTaskName == null) {
            throw new IllegalStateException("Can't stop StopWatch: it's not running");
        }
        long lastTime = System.nanoTime() - this.startTimeNanos;
        this.totalTimeNanos += lastTime;
        this.lastTaskInfo = new TaskInfo(this.currentTaskName, lastTime);
        if (this.keepTaskList) {
            this.taskList.add(this.lastTaskInfo);
        }
        ++this.taskCount;
        this.currentTaskName = null;
    }

    /**
     * Determine whether this {@code StopWatch} is currently running.
     *
     * @see #currentTaskName()
     */
    public boolean isRunning() {
        return (this.currentTaskName != null);
    }

    /**
     * Get the name of the currently running task, if any.
     *
     * @see #isRunning()
     * @since 4.2.2
     */
    @Nullable
    public String currentTaskName() {
        return this.currentTaskName;
    }

    /**
     * Get the time taken by the last task in nanoseconds.
     *
     * @see #getLastTaskTimeMillis()
     * @since 5.2
     */
    public long getLastTaskTimeNanos() throws IllegalStateException {
        if (this.lastTaskInfo == null) {
            throw new IllegalStateException("No tasks run: can't get last task interval");
        }
        return this.lastTaskInfo.getTimeNanos();
    }

    /**
     * Get the time taken by the last task in milliseconds.
     *
     * @see #getLastTaskTimeNanos()
     */
    public long getLastTaskTimeMillis() throws IllegalStateException {
        if (this.lastTaskInfo == null) {
            throw new IllegalStateException("No tasks run: can't get last task interval");
        }
        return this.lastTaskInfo.getTimeMillis();
    }

    /**
     * Get the name of the last task.
     */
    public String getLastTaskName() throws IllegalStateException {
        if (this.lastTaskInfo == null) {
            throw new IllegalStateException("No tasks run: can't get last task name");
        }
        return this.lastTaskInfo.getTaskName();
    }

    /**
     * Get the last task as a {@link org.springframework.util.StopWatch.TaskInfo} object.
     */
    public TaskInfo getLastTaskInfo() throws IllegalStateException {
        if (this.lastTaskInfo == null) {
            throw new IllegalStateException("No tasks run: can't get last task info");
        }
        return this.lastTaskInfo;
    }

    /**
     * Get the total time in nanoseconds for all tasks.
     *
     * @see #getTotalTimeMillis()
     * @see #getTotalTimeSeconds()
     * @since 5.2
     */
    public long getTotalTimeNanos() {
        return this.totalTimeNanos;
    }

    /**
     * Get the total time in milliseconds for all tasks.
     *
     * @see #getTotalTimeNanos()
     * @see #getTotalTimeSeconds()
     */
    public long getTotalTimeMillis() {
        return nanosToMillis(this.totalTimeNanos);
    }

    /**
     * Get the total time in seconds for all tasks.
     *
     * @see #getTotalTimeNanos()
     * @see #getTotalTimeMillis()
     */
    public double getTotalTimeSeconds() {
        return nanosToSeconds(this.totalTimeNanos);
    }

    /**
     * 格式化seconds
     *
     * @return
     */
    public String getTotalSeconds() {
        return NumUtil.formatClearZero(2, getTotalTimeSeconds());
    }

    /**
     * Get the number of tasks timed.
     */
    public int getTaskCount() {
        return this.taskCount;
    }

    /**
     * Get an array of the data for tasks performed.
     */
    public TaskInfo[] getTaskInfo() {
        if (!this.keepTaskList) {
            throw new UnsupportedOperationException("Task info is not being kept!");
        }
        return this.taskList.toArray(new TaskInfo[0]);
    }

    /**
     * Get a short description of the total running time.
     */
    public String shortSummary() {
        return "StopWatch '" + getId() + "': running time = " + getTotalTimeNanos() + " ns";
    }

    /**
     * 改为ms展示
     * <p>
     * Generate a string with a table describing all tasks performed.
     * <p>For custom reporting, call {@link #getTaskInfo()} and use the task info
     * directly.
     */
    public String prettyPrint() {
        String summary = "StopWatch '" + getId() + "': running time = " + getTotalTimeMillis() + " ms";
        StringBuilder sb = new StringBuilder(summary);
        sb.append('\n');
        if (!this.keepTaskList) {
            sb.append("No task info kept");
        } else {
            sb.append("---------------------------------\n");
            sb.append("    ms      %  Task name\n");
            sb.append("---------------------------------\n");
//            NumberFormat nf = NumberFormat.getNumberInstance();
//            nf.setMinimumIntegerDigits(9);
//            nf.setGroupingUsed(false);
//            NumberFormat pf = NumberFormat.getPercentInstance();
//            pf.setMinimumIntegerDigits(3);
//            pf.setGroupingUsed(false);
            for (TaskInfo task : getTaskInfo()) {
//                sb.append(nf.format(task.getTimeNanos())).append("  ");
//                sb.append(pf.format((double) task.getTimeNanos() / getTotalTimeNanos())).append("  ");
                // 耗时ms，左侧补空格
                String time = StringUtils.leftPad(String.valueOf(task.getTimeMillis()), 6, ' ');
                sb.append(time).append("  ");
                // 百分比，左侧补空格
                double percent = (double) task.getTimeNanos() * 100 / getTotalTimeNanos();
                String percentStr = StringUtils.leftPad(NumUtil.formatClearZero(1, percent) + "%", 5, ' ');
                sb.append(percentStr).append("  ");

                sb.append(task.getTaskName()).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Generate an informative string describing all tasks performed
     * <p>For custom reporting, call {@link #getTaskInfo()} and use the task info
     * directly.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(shortSummary());
        if (this.keepTaskList) {
            for (TaskInfo task : getTaskInfo()) {
                sb.append("; [").append(task.getTaskName()).append("] took ").append(task.getTimeNanos()).append(" ns");
                long percent = Math.round(100.0 * task.getTimeNanos() / getTotalTimeNanos());
                sb.append(" = ").append(percent).append('%');
            }
        } else {
            sb.append("; no task info kept");
        }
        return sb.toString();
    }

    private static long nanosToMillis(long duration) {
        return TimeUnit.NANOSECONDS.toMillis(duration);
    }

    private static double nanosToSeconds(long duration) {
        return duration / 1_000_000_000.0;
    }

    /**
     * Nested class to hold data about one task executed within the {@code StopWatch}.
     */
    public static final class TaskInfo {

        private final String taskName;

        private final long timeNanos;

        TaskInfo(String taskName, long timeNanos) {
            this.taskName = taskName;
            this.timeNanos = timeNanos;
        }

        /**
         * Get the name of this task.
         */
        public String getTaskName() {
            return this.taskName;
        }

        /**
         * Get the time in nanoseconds this task took.
         *
         * @see #getTimeMillis()
         * @see #getTimeSeconds()
         * @since 5.2
         */
        public long getTimeNanos() {
            return this.timeNanos;
        }

        /**
         * Get the time in milliseconds this task took.
         *
         * @see #getTimeNanos()
         * @see #getTimeSeconds()
         */
        public long getTimeMillis() {
            return nanosToMillis(this.timeNanos);
        }

        /**
         * Get the time in seconds this task took.
         *
         * @see #getTimeMillis()
         * @see #getTimeNanos()
         */
        public double getTimeSeconds() {
            return nanosToSeconds(this.timeNanos);
        }

    }

}
