package com.csbk.boilerscontrolapp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class HttpService {
    private static final OkHttpClient client = new OkHttpClient();
    static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final Gson gson = new Gson();
    private static final String IP = "85.175.232.186";//85.175.232.186

    public static void sendTplan(int[] correctTplan) {
       HttpBackgroundService.sendTplan(correctTplan);
    }

    public static void sendTAlarm(int[] correctTAlarm) {
      HttpBackgroundService.sendTAlarm(correctTAlarm);
    }

    public static void sendResetAvary() {
        int resetValue = -1;
        String json = gson.toJson(resetValue);
        sendPostRequest("http://"+IP+":4567/avaryreset", json);
    }

    private static void sendPostRequest(String url, String json) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Boiler> getBoilers(String... urls){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urls[0])
                .build();
        boolean isFetchSuccessful = false;
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                isFetchSuccessful = true;
                String jsonResponse = response.body().string();
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Boiler>>() {
                }.getType();
                return gson.fromJson(jsonResponse, listType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isFetchSuccessful){
            return  null;
        }
        return  null; //TODO проверить - заходит ли сюда
    }
}
