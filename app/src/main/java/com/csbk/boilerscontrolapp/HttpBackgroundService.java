package com.csbk.boilerscontrolapp;

import android.os.AsyncTask;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;



public class HttpBackgroundService {
    public static final String IP = HttpService.IP;//85.175.232.186
    public static final String PORT = HttpService.PORT;//85.175.232.186

    private static class SendPostRequestTask extends AsyncTask<String, Void, String> {
        private static final OkHttpClient client = new OkHttpClient();
        private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private static final Gson gson = new Gson();


        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String json = params[1];

            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                System.out.println(result);
            }

        }
    }

    public static void sendTplan(int[] correctTplan) {
        String[] correctTplanS ={"0","0","0","0","0","0","0","0","0","0","0","0","0","0"};
        for (int i = 0; i < correctTplan.length; i++) {
            if(correctTplan[i]==3){
                correctTplanS[i]="3";
            }
            if(correctTplan[i]==-3){
                correctTplanS[i]="-3";
            }
        }
        String json = SendPostRequestTask.gson.toJson(correctTplanS);
        new SendPostRequestTask().execute("http://"+IP+":"+PORT+"/settemperaturecorrections", json);
    }

    public static void sendTAlarm(int[] correctTAlarm) {
        String[] correctTalarmS ={"0","0","0","0","0","0","0","0","0","0","0","0","0","0"};
        for (int i = 0; i < correctTAlarm.length; i++) {
            if(correctTAlarm[i]==3){
               correctTalarmS[i]="3";
            }
            if(correctTAlarm[i]==-3){
                correctTalarmS[i]="-3";
            }
        }
        String json = SendPostRequestTask.gson.toJson(correctTalarmS);
        new SendPostRequestTask().execute("http://"+IP+":"+PORT+"/setAlarmCorrections", json);
    }

    public static void sendResetAvary() {
        int resetValue = -1;
        String json = SendPostRequestTask.gson.toJson(resetValue);
        new SendPostRequestTask().execute("http://"+IP+":"+PORT+"/avaryreset", json);
    }
}
