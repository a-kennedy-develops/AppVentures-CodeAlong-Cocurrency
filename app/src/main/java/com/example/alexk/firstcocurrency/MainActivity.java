package com.example.alexk.firstcocurrency;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String DOWNLOAD_KEY = "download";
    private static final String TAG = "TAGG";
    public static String[] songs = {"Better Now",
            "This is America", "Imagine", "UGH!"};


    Button buttonThread;
    Button buttonAsync;

    public ProgressBar progressBar;

    private ExecutorService threadPool;

    private Handler mHandler;

    private Runnable runnable = new Runnable() {

        public void sendStringMessage(String string) {
            //Create Bundle to hold String value
            Bundle bundle = new Bundle();
            bundle.putString(DOWNLOAD_KEY, string);

            //Create message with Bundle to send to Handler
            Message message = new Message();
            message.setData(bundle);
            mHandler.sendMessage(message);
        }

        @Override
        public void run() {
            //ForEach
            for (String song: songs) {
                long endTime = System.currentTimeMillis() + 3*1000;

                while (System.currentTimeMillis() < endTime) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                sendStringMessage(song);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);

//        final Thread threadDownload = new Thread(runnable, "DownloadThread");
        threadPool = Executors.newFixedThreadPool(1);

        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.getData().containsKey(DOWNLOAD_KEY)) {
                    Toast.makeText(MainActivity.this, message.getData().getString(DOWNLOAD_KEY), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        buttonThread = findViewById(R.id.buttonThread);
        buttonThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                threadDownload.start();
                threadPool.execute(runnable);
            }
        });

        buttonAsync = findViewById(R.id.buttonAsync);
        buttonAsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoWorkAsync doWorkAsync = new DoWorkAsync();
                doWorkAsync.execute(songs);
            }
        });

    }

    public class DoWorkAsync extends AsyncTask<String, Integer, ArrayList<String>> {

        int max;

        @Override
        protected void onPreExecute() {
            Log.d(TAG,"In Pre Execute");
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            ArrayList<String> tempArrayList = new ArrayList<>();

            progressBar.setMax(strings.length);
            int tracker = 0;

            for (String song: songs) {
                long endTime = System.currentTimeMillis() + 3*1000;

                while (System.currentTimeMillis() < endTime) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                tracker++;
                publishProgress(tracker);
                tempArrayList.add(song);
            }

            return tempArrayList;
        }

        @Override
        protected void onPostExecute(final ArrayList<String> strings) {
            Log.d(TAG, "Here are the songs downloaded - " + strings.toString());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
    }

}
