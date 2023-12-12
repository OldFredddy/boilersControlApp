package com.csbk.boilerscontrolapp;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainForegroundService extends Service {
    private Handler handler = new Handler();
    private HashMap<Boiler, Boolean> previousStates = new HashMap<>();
    private List<Boiler> boilers = new ArrayList<>();
    private NotificationCompat.Builder builder;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "channel_01";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, getMyActivityNotification());
        handler.post(updateTask); // Запуск задачи обновления данных
    }
    private void createNotificationChannel() {
    //   CharSequence name = getString(R.string.channel_name); // Название канала, видимое пользователю
    //   String description = getString(R.string.channel_description); // Описание канала
    //   int importance = NotificationManager.IMPORTANCE_HIGH; // Важность канала
    //   NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
    //   channel.setDescription(description);
    //   NotificationManager notificationManager = (NotificationManager) Objects.requireNonNull(getActivity()).getSystemService(Context.NOTIFICATION_SERVICE);
    //   notificationManager.createNotificationChannel(channel);
    }
    private Notification getMyActivityNotification(){
        // Создайте и настройте уведомление
        String emojiStr="";
        for (int i = 0; i < boilers.size(); i++) {
            emojiStr +=(boilers.get(i).isOk() ? "\uD83D\uDFE2" : "\uD83D\uDD34");
        }
        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Статус котельных")
                .setContentText(emojiStr)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        return builder.build();
    }
    private void sendAlertNotification(Boiler boiler) {
      //  NotificationCompat.Builder alertBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
      //          .setSmallIcon(R.drawable.notification_icon)
      //          .setContentTitle("Проблема с котельной")
      //          .setContentText("Проблема в котельной: ") // TODO имя!
      //          .setPriority(NotificationCompat.PRIORITY_HIGH)
      //          .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }) // Паттерн вибрации
      //          .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
//
      // NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
      // notificationManager.notify(NOTIFICATION_ID, alertBuilder.build()); // Используйте уникальный ID для каждой котельной
    }
    private Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            fetchDataAndUpdateUI();
            handler.postDelayed(this, 15000);
        }
    };

    private void fetchDataAndUpdateUI() {
        new GetDataTask().execute("http://85.175.232.186:4567/params");
    }

    private class GetDataTask extends AsyncTask<String, Void, List<Boiler>> {
        @Override
        protected List<Boiler> doInBackground(String... urls) {
            return HttpService.getBoilers(urls);
        }
        @Override
        protected void onPostExecute(List<Boiler> result) {
            if (result!=null) {
                boilers.clear();
                boilers.addAll(result);
                String statusNotificationStr = "";
                for (Boiler boiler : boilers) {
                    boolean currentState = boiler.isOk();
                    Boolean previousState = previousStates.get(boiler);
                    if (previousState == null || previousState != currentState) {
                        previousStates.put(boiler, currentState);
                        if (!currentState) {
                            sendAlertNotification(boiler); // Отправка специального уведомления
                        }
                    }
                    statusNotificationStr += (currentState ? "\uD83D\uDFE2 " : "\uD83D\uDD34 ");
                }
                builder.setContentText(statusNotificationStr);
             //   NotificationManager notificationManager = (NotificationManager) Objects.requireNonNull(getActivity()).getSystemService(Context.NOTIFICATION_SERVICE);
             //   notificationManager.notify(NOTIFICATION_ID, builder.build());
                updateStatusViews();
            } else {
                for (int i = 0; i < 13; i++) {
                    boilers.get(i).setOk(false);
                }
            }

        }
    }
    private void updateStatusViews() {
        String emojiStr="";
        for (int i = 0; i < boilers.size(); i++) {
            emojiStr +=(boilers.get(i).isOk() ? "\uD83D\uDFE2" : "\uD83D\uDD34");
        }
        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Статус котельных")
                .setContentText(emojiStr)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Ваш код здесь (если необходимо)
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Другие методы, включая createNotificationChannel и sendAlertNotification
}


