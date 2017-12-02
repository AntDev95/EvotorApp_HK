package ru.evotor.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

public class Application extends android.app.Application {

    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public static volatile Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        handler = new Handler(context.getMainLooper());
    }

    public static void runOnUIThread(Runnable runnable) {
        handler.post(runnable);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        handler.postDelayed(runnable, delay);
    }
}
