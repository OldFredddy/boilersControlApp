package com.csbk.boilerscontrolapp;

import android.content.Context;
import android.graphics.Color;
import android.os.*;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.csbk.boilerscontrolapp.databinding.FragmentSecondBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private RecyclerView boilersList;
    private BoilersAdapter boilersAdapter;
    List<Boiler> boilers;
    public String[] boilerNames = {
            "Склады Мищенко",                   //0   кот№1 Склады Мищенко
            "Выставка Ендальцева",              //1   кот№2 Ендальцев
            "ЧукотОптТорг",                     //2   кот№3 ЧукотОптТорг
            "ЧСБК база",                       //3   кот№4 "ЧСБК Новая"
            "Офис СВТ",                         //4   кот№5 офис "СВТ"
            "Общежитие на Южной",               //5   кот№6 общежитие на Южной
            "Офис ЧСБК",                        //6   кот№7 офис ЧСБК
            "Рынок",                            //7   кот№8 "Рынок"
            "Макатровых",                       //8   кот№9 Макатровых
            "ДС «Сказка»",                        //9   кот№10  "Д/С Сказка"
            "Полярный",                         //10  кот№11 Полярный
            "Департамент",                      //11  кот№12 Департамент
            "Квартиры в офисе",               //12  кот№13 квартиры в офисе
            "ТО Шишкина"                             //13  кот№14 ТО Шишкина
    };
    LinearLayout statusContainer;
    LinearLayout emojiContainer;
    private static final int STD_NUMBER_OF_BOILERS=14;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        boilersList = binding.BoilersViewList;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        boilersList.setLayoutManager(layoutManager);
        boilersList.setHasFixedSize(true);
        boilersAdapter = new BoilersAdapter(14);
        boilers = createTestListOfBoilers();
        updateTask.run();
        boilersAdapter.setBoilersList(boilers);
        boilersList.setAdapter(boilersAdapter);

        return binding.getRoot();
    }
    private List<TextView> emojiTextViews = new ArrayList<>();
    private TextView graphTextView;
    int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "status_id";
    private HashMap<Boiler, Boolean> previousStates = new HashMap<>();

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emojiContainer = view.findViewById(R.id.emoji_container); // Получаем emojiContainer
        emojiTextViews.clear();
        statusContainer = view.findViewById(R.id.status_container);
        String startStatusNotification = "";
        for (int i = 0; i < boilers.size(); i++) {
            TextView statusView = new TextView(getContext());
            statusView.setPadding(4, 0, 4, 0);
            statusView.setText(boilers.get(i).isOk() ? "\uD83D\uDFE2" : "\uD83D\uDD34");
            statusView.setTextSize(16);
            emojiContainer.addView(statusView); // Добавляем во внутренний контейнер
            emojiTextViews.add(statusView);
            startStatusNotification += (boilers.get(i).isOk() ? "\uD83D\uDFE2 " : "\uD83D\uDD34 ");
        }
        graphTextView = new TextView(getContext());
        graphTextView.setTextSize(12);
        graphTextView.setGravity(Gravity.CENTER);
        int whiteColor = Color.parseColor("#FFFFFF");
        graphTextView.setTextColor(whiteColor);
        graphTextView.setPadding(0, 8, 0, 8);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        graphTextView.setLayoutParams(params);
        statusContainer.addView(graphTextView);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public List<Boiler> createTestListOfBoilers() {
        List<Boiler> boilers = new ArrayList<>();
        Context context = getActivity();
        if (context != null) {
            for (int i = 0; i < STD_NUMBER_OF_BOILERS; i++) {
                Boiler boiler = new Boiler();
                boiler.settPod("***");
                boiler.setpPod("***");
                boiler.settUlica(String.valueOf(i-5));
                boiler.setOk(true);
                boiler.settPlan("***");
                boiler.settAlarm("Нет связи!");
                int imageResId = context.getResources().getIdentifier("boiler_icon_" + (i + 1), "drawable", context.getPackageName());
                boiler.setImageResId(imageResId);

                boilers.add(boiler);
            }
        }
        return boilers;
    }

    private Handler handler = new Handler();
    private Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            fetchDataAndUpdateUI();
            handler.postDelayed(this, 3000);
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
                updateStatusViews();
            } else {
                boilers.clear();
                boilers.addAll(createTestListOfBoilers());
            }
            Context context = getActivity();
            for (int i = 0; i < boilers.size(); i++) {
                int imageResId = context.getResources().getIdentifier("boiler_icon_" + (i + 1), "drawable", context.getPackageName());
                boilers.get(i).setImageResId(imageResId);
            }
            boilersAdapter.notifyDataSetChanged();
        }
    }

    private void updateStatusViews() {
        for (int i = 0; i < boilers.size(); i++) {
            TextView statusView = emojiTextViews.get(i);
            statusView.setText(boilers.get(i).isOk() ? "\uD83D\uDFE2" : "\uD83D\uDD34");
        }

        double tStreet = Float.parseFloat(boilers.get(1).gettUlicaWithoutCelcii());
        int tPlanGraph = (int) Math.round(tStreet*tStreet*0.00886 - 0.803*tStreet + 54);
        graphTextView.setText("По графику: " + tPlanGraph + " °C");
    }
}