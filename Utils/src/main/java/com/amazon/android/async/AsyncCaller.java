package com.amazon.android.async;

import android.util.Log;

import java.util.concurrent.Callable;

public class AsyncCaller<T> {
    private static final String TAG = AsyncCaller.class.getSimpleName();

    private final Callable<T> callable;

    private T result;

    public AsyncCaller(Callable<T> callable) {
        this.callable = callable;
    }

    public T getResult() {
        Thread thread = new Thread(() -> {
            try {
                result = callable.call();
            } catch (Exception e) {
                Log.e(TAG, "Unhandled exception in async callable",e);
            }
        });

        thread.setDaemon(true);
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e(TAG, "Async call was interrupted before completing",e);
        }

        return result;
    }
}
