package com.csbk.boilerscontrolapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;

import java.util.*;

public class MainForegroundService extends Service {
    private Handler handler = new Handler();
    Map<Integer, Boolean> previousStates = new HashMap<>();
    private List<Boiler> boilers = new ArrayList<>();
    private NotificationCompat.Builder builder;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "channel_01";
    public String[] boilerNames = {
            "Склады Мищенко",                   //0   кот№1 Склады Мищенко
            "Выставка Ендальцева",              //1   кот№2 Ендальцев
            "ЧукотОптТорг",                     //2   кот№3 ЧукотОптТорг
            "ЧСБК база",                        //3   кот№4 "ЧСБК Новая"
            "Офис СВТ",                         //4   кот№5 офис "СВТ"
            "Общежитие на Южной",               //5   кот№6 общежитие на Южной
            "Офис ЧСБК",                        //6   кот№7 офис ЧСБК
            "Рынок",                            //7   кот№8 "Рынок"
            "Макатровых",                       //8   кот№9 Макатровых
            "ДС «Сказка»",                      //9   кот№10  "Д/С Сказка"
            "Полярный",                         //10  кот№11 Полярный
            "Департамент",                      //11  кот№12 Департамент
            "Квартиры в офисе",                 //12  кот№13 квартиры в офисе
            "ТО Шишкина"                        //13  кот№14 ТО Шишкина
    };

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, getMyActivityNotification());
        handler.post(updateTask); // Запуск задачи обновления данных
    }
    private void createNotificationChannel() {
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH; // Важность канала

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        // Включение вибрации
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[] { 2000,2000,2000,2000,2000,2000,2000,2000 });

        // Установка звука уведомления
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        channel.setSound(soundUri, Notification.AUDIO_ATTRIBUTES_DEFAULT);

        // Регистрация канала в системе
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }
    private Notification getMyActivityNotification(){
        String emojiStr="";
        for (int i = 0; i < boilers.size(); i++) {
            int status = boilers.get(i).isOk();
            if (status == 0) {
                emojiStr += "\uD83D\uDFE1"; // Желтый
            } else if (status == 1) {
                emojiStr += "\uD83D\uDFE2"; // Зеленый
            } else if (status == 2) {
                emojiStr += "\uD83D\uDD34"; // Красный
            }
        }
        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Сервис уведомлений по котельным")
                .setContentText("\uD83D\uDFE2 Сервис в работе \uD83D\uDFE2")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        return builder.build();
    }
    private void sendAlertNotification(int numBoiler) {
          NotificationCompat.Builder alertBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                  .setSmallIcon(R.drawable.notification_icon)
                //  .setLargeIcon(R.drawable.boiler_icon_1) //TODO Посмотри
                  .setContentTitle("Котельные")
                  .setContentText("Проблема в котельной №"+(numBoiler+1) +" "+boilerNames[numBoiler] ) // TODO имя!
                  .setPriority(NotificationCompat.PRIORITY_MAX)
                  .setVibrate(new long[] {2000,2000,2000,2000,2000,2000,2000,2000}) // Паттерн вибрации
                  .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

          NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
          notificationManager.notify(numBoiler, alertBuilder.build()); // Используйте уникальный ID для каждой котельной
    }
    private Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            fetchDataAndUpdateUI();
            handler.postDelayed(this, 15000);
        }
    };

    private void fetchDataAndUpdateUI() {
        new GetDataTask().execute("http://"+HttpService.IP+":"+HttpService.PORT+"/getparams");
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
                for (int i = 0; i < boilers.size(); i++) {
                    Boiler boiler = boilers.get(i);
                    boolean currentState = boiler.isOk() > 1;
                    Integer boilerId = boiler.getId();
                    Boolean previousState = previousStates.get(boilerId);
                    if (previousState == null || previousState != currentState) {
                        previousStates.put(boilerId, currentState);
                        if (!currentState) {
                            sendAlertNotification(i);
                        }
                    }
                }
             //   builder.setContentText(statusNotificationStr);
             //   NotificationManager notificationManager = (NotificationManager) Objects.requireNonNull(getApplicationContext()).getSystemService(Context.NOTIFICATION_SERVICE);
             //   notificationManager.notify(NOTIFICATION_ID, builder.build());
              //  updateStatusViews();
            } else {
                for (int i = 0; i < boilers.size(); i++) {
                    boilers.get(i).setOk(1);
                }
            }

        }
    }
    private void updateStatusViews() {
        String emojiStr="";
        for (int i = 0; i < boilers.size(); i++) {
            int status = boilers.get(i).isOk();
            if (status == 0) {
                emojiStr += "\uD83D\uDFE1"; // Желтый
            } else if (status == 1) {
                emojiStr += "\uD83D\uDFE2"; // Зеленый
            } else if (status == 2) {
                emojiStr += "\uD83D\uDD34"; // Красный
            }
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


