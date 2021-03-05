package com.myfirst.threaddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final static String VALUE = "VALUE";
    private final static int WHAT = 3543;

    enum ThreadType {Simple, Handler, AsyncTask}

    private TextView tvTimerValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTimerValue = findViewById(R.id.tv_timer_value);

        //todo add UI logic

    }

    public void onStartClick(View view) {
    }

    public void onPauseClick(View view) {
    }

    public void onStopClick(View view) {
    }

    private void startTimer(ThreadType type) {
        switch (type) {
            case Simple:
                startTimer(getSimpleThread());
                break;
            case Handler:
                startTimer(getHandlerThread());
                break;
            case AsyncTask:
                startAsyncTask();
                break;
        }
    }

    private void pauseTimer(ThreadType type) {
        switch (type) {
            case Simple:
                pauseTimer(getSimpleThread());
                break;
            case Handler:
                pauseTimer(getHandlerThread());
                break;
            case AsyncTask:
                pauseAsyncTask();
                break;
        }
    }

    private void stopTimer(ThreadType type) {
        switch (type) {
            case Simple:
                stopTimer(getSimpleThread());
                break;
            case Handler:
                stopTimer(getHandlerThread());
                break;
            case AsyncTask:
                stopAsyncTask();
                break;
        }
    }

    private Thread simpleThread;

    private Thread getSimpleThread() {
        if (simpleThread == null) {
            simpleThread = new Thread(() -> {
                Thread.currentThread().setName("SimpleThread");
                Log.i("TAG", Thread.currentThread().getName());
                int number = getCurrentTimerValue();
                for (int i = number; i < i + 1; i++) {
                    int finalI = i;
                    runOnUiThread(() -> {
                        tvTimerValue.setText(String.valueOf(finalI));
                    });
                }
            });
        }
        Log.i("TAG", Thread.currentThread().getName());
        return simpleThread;
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            int number = bundle.getInt(VALUE);
            tvTimerValue.setText(String.valueOf(number));
        }
    };

    private Thread handlerThread;

    private Thread getHandlerThread() {
        if (handlerThread == null) {
            new Thread(() -> {
                int number = getCurrentTimerValue();
                for (int i = number; i < i + 1; i++) {
                    Message msg = handler.obtainMessage(WHAT);

                    Bundle bundle = new Bundle();
                    bundle.putInt(VALUE, i);
                    msg.setData(bundle);

                    handler.handleMessage(msg);

//                int finalI = i;
//                new Handler(Looper.getMainLooper()).post(() ->
//                        tvTimerValue.setText(String.valueOf(finalI))
//                );
                }
            });
        }
        return handlerThread;
    }

    private void startTimer(Thread thread) {
        thread.start();
    }

    private void pauseTimer(Thread thread) {
        try {
            if (!thread.isInterrupted()) {
                thread.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void stopTimer(Thread thread) {
        if (!thread.isInterrupted()) {
            thread.interrupt();
        }
        thread = null;
    }

    private TimerTask timerTask;

    private TimerTask getTimerTask() {
        if (timerTask == null) {
            timerTask = new TimerTask();
        }
        return timerTask;
    }

    private void startAsyncTask() {
        getTimerTask().execute(getCurrentTimerValue());
    }

    private void pauseAsyncTask() {
        stopAsyncTask();
    }

    private void stopAsyncTask() {
        getTimerTask().cancel(true);
        timerTask = null;
    }

    class TimerTask extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected Void doInBackground(Integer... integers) {
            int number = integers[0];
            for (int i = number; i < i + 1; i++) {
                onProgressUpdate(i);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int number = values[0];
            tvTimerValue.setText(String.valueOf(number));
        }
    }

    private int getCurrentTimerValue() {
        String value = tvTimerValue.getText().toString();
        return Integer.parseInt(value);
    }
}