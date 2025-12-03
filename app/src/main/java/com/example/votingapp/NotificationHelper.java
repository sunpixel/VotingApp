package com.example.votingapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    private static final String CHANNEL_GENERAL = "general_channel";
    private static final String CHANNEL_PROGRESS = "progress_channel";

    private static void createChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel general = new NotificationChannel(
                    CHANNEL_GENERAL,
                    "General Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationChannel progress = new NotificationChannel(
                    CHANNEL_PROGRESS,
                    "Progress Notifications",
                    NotificationManager.IMPORTANCE_LOW
            );

            nm.createNotificationChannel(general);
            nm.createNotificationChannel(progress);
        }
    }

    public static void notifySimple(Context context, String title, String text) {
        createChannels(context);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_GENERAL)
                        .setSmallIcon(R.drawable.ic_vote)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true);

        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(1, builder.build());
    }

    public static void notifyAction(Context context, String title, String text) {
        createChannels(context);

        Intent intent = new Intent(context, ResultsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_GENERAL)
                        .setSmallIcon(R.drawable.ic_vote)
                        .setContentTitle(title)
                        .setContentText(text)
                        .addAction(R.drawable.ic_vote, "View Results", pendingIntent)
                        .setAutoCancel(true);

        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(2, builder.build());
    }

    public static void notifyProgress(Context context, String title, String text) {
        createChannels(context);

        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_PROGRESS)
                        .setSmallIcon(R.drawable.ic_vote)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setProgress(0, 0, true);

        nm.notify(3, builder.build());

        new android.os.Handler().postDelayed(() -> {
            nm.cancel(3);
        }, 2000);
    }

    public static void notifyBigText(Context context, String title, String longText) {
        createChannels(context);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle()
                .bigText(longText);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_GENERAL)
                        .setSmallIcon(R.drawable.ic_vote)
                        .setContentTitle(title)
                        .setStyle(style)
                        .setAutoCancel(true);

        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(4, builder.build());
    }

    public static void notifyInbox(Context context, String title, String... lines) {
        createChannels(context);

        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        for (String line : lines) style.addLine(line);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_GENERAL)
                        .setSmallIcon(R.drawable.ic_vote)
                        .setContentTitle(title)
                        .setStyle(style)
                        .setAutoCancel(true);

        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(5, builder.build());
    }
}
