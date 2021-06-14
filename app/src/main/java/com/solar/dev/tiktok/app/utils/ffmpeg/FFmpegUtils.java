package com.solar.dev.tiktok.app.utils.ffmpeg;

import android.os.AsyncTask;

import com.arthenica.mobileffmpeg.util.AsyncSingleFFmpegExecuteTask;
import com.arthenica.mobileffmpeg.util.SingleExecuteCallback;

public class FFmpegUtils {
    public static String REGEX = "&~lhd~&";

    /**
     * <p>Starts a new asynchronous FFmpeg operation with arguments provided.
     *
     * @param executeCallback callback function to receive result of this execution
     * @param arguments       FFmpeg command options/arguments
     */
//    public static void executeAsync(final SingleExecuteCallback executeCallback, final String arguments) {
//        final AsyncSingleFFmpegExecuteTask asyncCommandTask = new AsyncSingleFFmpegExecuteTask("",executeCallback);
//        asyncCommandTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, arguments.split(REGEX));
//    }
    public static void executeAsync(final SingleExecuteCallback executeCallback, final String arguments, String command) {
        final AsyncSingleFFmpegExecuteTask asyncCommandTask = new AsyncSingleFFmpegExecuteTask(command, executeCallback);
        asyncCommandTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, arguments.split(REGEX));
    }

    public static void excute(SingleExecuteCallback executeCallback, String command) {
        final AsyncSingleFFmpegExecuteTask asyncCommandTask = new AsyncSingleFFmpegExecuteTask(command, executeCallback);
        asyncCommandTask.execute();
    }

    /**
     * <p>Starts a new asynchronous FFmpeg operation with command provided.
     *
     * @param singleExecuteCallback callback function to receive result of this execution
     * @param command               FFmpeg command
     */
    public static void executeAsync(final SingleExecuteCallback singleExecuteCallback, final String command) {
        final AsyncSingleFFmpegExecuteTask asyncCommandTask = new AsyncSingleFFmpegExecuteTask(command, singleExecuteCallback);
        asyncCommandTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
