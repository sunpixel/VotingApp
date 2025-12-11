package com.example.votingapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PollingService extends Service {

    private Handler handler;
    private Runnable runnable;
    private final int INTERVAL_MS = 5 * 60 * 1000; // 5 minutes
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final String CHANNEL_ID = "chan_polling";

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(getMainLooper());

        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Voting App — polling")
                .setContentText("Polling votes every 5 minutes")
                .setSmallIcon(R.drawable.ic_vote)
                .setContentIntent(pi)
                .build();

        startForeground(200, notification);

        runnable = () -> {
            executor.submit(() -> {
                VoteRepository repo = new VoteRepository(getApplicationContext(), "http://10.0.2.2:8000/");
                try {
                    retrofit2.Response<List<VoteDto>> r = repo.api.getVotes().execute();
                    if (r.isSuccessful()) {
                        CacheHelper.saveVotes(getApplicationContext(), r.body());
                    }
                } catch (Exception ignored) {}
            });
            handler.postDelayed(runnable, INTERVAL_MS);
        };

        handler.post(runnable);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        executor.shutdownNow();
        stopForeground(true);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }
}
