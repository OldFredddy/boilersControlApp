package com.csbk.boilerscontrolapp;

import android.os.AsyncTask;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;



public class HttpBackgroundService {
    public static final String IP = "85.175.232.186";//85.175.232.186

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
        String json = SendPostRequestTask.gson.toJson(correctTplan);
        new SendPostRequestTask().execute("http://"+IP+":4567/correcttplan", json);
    }

    public static void sendTAlarm(int[] correctTAlarm) {
        String json = SendPostRequestTask.gson.toJson(correctTAlarm);
        new SendPostRequestTask().execute("http://"+IP+":4567/correcttalarm", json);
    }

    public static void sendResetAvary() {
        int resetValue = -1;
        String json = SendPostRequestTask.gson.toJson(resetValue);
        new SendPostRequestTask().execute("http://"+IP+":4567/avaryreset", json);
    }
}
