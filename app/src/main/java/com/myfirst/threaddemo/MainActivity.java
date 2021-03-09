package com.myfirst.threaddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final static String VALUE = "VALUE";
    private final static int WHAT = 3543;

    enum ThreadType {Simple, Handler, AsyncTask}

    public ThreadType currentType;

    private TextView tvTimerValue;

    public RadioButton simpleThreadRadioButton;
    public RadioButton handlerRadioButton;
    public RadioButton asyncTaskThreadRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTimerValue = findViewById(R.id.tv_timer_value);
        tvTimerValue.setText("0");

        //todo add UI logic

        simpleThreadRadioButton = findViewById(R.id.rb_simple_thread);
        simpleThreadRadioButton.setOnClickListener(radioButtonClickListener);

        handlerRadioButton = findViewById(R.id.rb_handler);
        handlerRadioButton.setOnClickListener(radioButtonClickListener);

        asyncTaskThreadRadioButton = findViewById(R.id.rb_async_task);
        asyncTaskThreadRadioButton.setOnClickListener(radioButtonClickListener);


    }

    View.OnClickListener radioButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RadioButton rb = (RadioButton) v;
            switch (rb.getId()) {
                case R.id.rb_simple_thread:
                    currentType =  ThreadType.Simple;
                    break;
                case R.id.rb_handler:
                    currentType =  ThreadType.Handler;
                    break;
                case R.id.rb_async_task:
                    currentType = ThreadType.AsyncTask;
                    break;

                default:
                    break;
            }
        }
    };

    public void onStartClick(View view) {
        startTimer(currentType);
    }

    public void onPauseClick(View view) {
        pauseTimer(currentType);
    }

    public void onStopClick(View view) {
        stopTimer(currentType);
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
                Log.i("SimpleThread", Thread.currentThread().getName());
                int number = getCurrentTimerValue();
                Log.i("SimpleThread", "NUMBER = " + number);
                for (int i = number; i < i + 1; i++) {
                    int finalI = i;
                    runOnUiThread(() -> {
                        tvTimerValue.setText(String.valueOf(finalI));
                        Log.i("SimpleThread", "finalI = " + finalI);
                    });
                }
            });
        }
        Log.i("SimpleThread", " " + Thread.currentThread().getName());
        return simpleThread;
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            int number = bundle.getInt(VALUE);
            Log.i("Handler", "number = " + number);
            tvTimerValue.setText(String.valueOf(number));
        }
    };

    private Thread handlerThread;

    private Thread getHandlerThread() {
        if (handlerThread == null) {
            new Thread(() -> {
                int number = getCurrentTimerValue();
                Log.i("Handler", "current number = " + number);
                for (int i = number; i < i + 1; i++) {
                    Message msg = handler.obtainMessage(WHAT);

                    Log.i("Handler", "number i++= " + number);
                    Bundle bundle = new Bundle();
                    bundle.putInt(VALUE, i);
                    Log.i("Handler", "i++= " + i);
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
                Log.i("SimpleThread", "thread.wait()");
                thread.wait();
            }
        } catch (InterruptedException e) {
            Log.i("SimpleThread", "thread.wait() err = " + e.toString());
            e.printStackTrace();
        }
    }

    private void stopTimer(Thread thread) {
        if (!thread.isInterrupted()) {
            Log.i("SimpleThread", "thread.interrupt()");
            thread.interrupt();
        }
        thread = null;
    }

    private TimerTask timerTask;

    private TimerTask getTimerTask() {
        if (timerTask == null) {
            timerTask = new TimerTask();
        }
        Log.i("AsyncTask", "getTimerTask() = " + " " + timerTask);
        return timerTask;
    }

    private void startAsyncTask() {

        if(Build.VERSION.SDK_INT >= 11/*HONEYCOMB*/) {
            getTimerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,getCurrentTimerValue());
        } else {
            getTimerTask().execute(getCurrentTimerValue());
        }
        Log.i("AsyncTask111", "startAsyncTask() = "  + getCurrentTimerValue() +" timerObj = " + getTimerTask());
    }

    private void pauseAsyncTask() {
        stopAsyncTask();
    }

    private void stopAsyncTask() {
        getTimerTask().cancel(true);
        Log.i("AsyncTask111", "stopAsyncTask()  timerObj = " + getTimerTask());
        timerTask = null;
    }

    class TimerTask extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected Void doInBackground(Integer... integers) {

            int number = integers[0];
            Log.i("AsyncTask111", "number = " + number);
            for (int i = number; i < i + 1; i++) {
                //onProgressUpdate(i);
                // выводим промежуточные результаты
                publishProgress(i);
                Log.i("AsyncTask", "current number = " + number);

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int number = values[0];
            Log.i("AsyncTask", "current number onProgressUpdate = " + number);
            tvTimerValue.setText(String.valueOf(number));
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.i("AsyncTask", "onPostExecute = " + this);
            super.onPostExecute(unused);
        }
    }

    private int getCurrentTimerValue() {
        String value = tvTimerValue.getText().toString();
        Log.i("Handler", "getCurrentTimerValue() = " + value);
        Log.i("SimpleThread", "getCurrentTimerValue() = " + value);
        Log.i("AsyncTask", "getCurrentTimerValue() = " + value);
        return Integer.parseInt(value);
    }
}