package com.csbk.boilerscontrolapp;

import android.animation.*;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

public class BoilersAdapter extends  RecyclerView.Adapter<BoilersAdapter.BoilersViewHolder> {
    private static int viewHolderCount;
    private int boilersItems;
    private List<Boiler> boilersList = new ArrayList<>();
    private Context context;
    public BoilersAdapter(Context context,int boilersOfItems){
        boilersItems=boilersOfItems;
        viewHolderCount = 0;
        this.context=context;
    }
    public void setBoilersList(List<Boiler> boilersList) {
        this.boilersList = boilersList;
        notifyDataSetChanged();
    }
    @NonNull
    @NotNull
    @Override
    public BoilersViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForBoilersItem = R.layout.recycler_view_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForBoilersItem, parent, false);
        BoilersViewHolder viewHolder = new BoilersViewHolder(view);

        return viewHolder;
    }
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
    @Override
    public void onBindViewHolder(@NonNull @NotNull BoilersViewHolder holder, int position) {
        Boiler currentBoiler = boilersList.get(position);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        holder.bind(metrics,currentBoiler.gettPod(), currentBoiler.getpPod(), currentBoiler.gettUlica(), currentBoiler.gettPlan(), currentBoiler.gettAlarm(), boilerNames[position],currentBoiler.getImageResId());

        if( (currentBoiler.isOk()==2) && (position != NO_POSITION)) {
            holder.startAnimation();
        } else {
            holder.stopAnimation();
        }
    }



    @Override
    public int getItemCount() {
        return boilersItems;
    }

    class BoilersViewHolder extends RecyclerView.ViewHolder{
        TextView tv_boilerTpod;
        TextView tv_boilerPpod;
        TextView tv_boilerTulica;
        TextView tv_boilerTplan;
        TextView tv_boilerTalarm;
        TextView tv_boilerLabel;
        ImageView iv_boilerIcon;
        Button buttonMinusTplan;
        Button buttonPlusTplan;
        Button buttonPlusTalarm;
        Button buttonMinusTalarm;
        private ValueAnimator colorAnimation = null;

        public BoilersViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tv_boilerTpod = itemView.findViewById(R.id.tv_boilerTpod);
            tv_boilerPpod = itemView.findViewById(R.id.tv_boilerPpod);
            tv_boilerTulica = itemView.findViewById(R.id.tv_boilerTulica);
            tv_boilerTplan = itemView.findViewById(R.id.tv_boilerTplan);
            tv_boilerTalarm = itemView.findViewById(R.id.tv_boilerTalarm);
            tv_boilerLabel = itemView.findViewById(R.id.tv_label_of_boiler);
            iv_boilerIcon = itemView.findViewById(R.id.iv_boilerIcon);
            buttonMinusTalarm = itemView.findViewById(R.id.btn_minus_talarm);
            buttonPlusTalarm = itemView.findViewById(R.id.btn_plus_talarm);
            buttonPlusTplan = itemView.findViewById(R.id.btn_plus_tplan);
            buttonMinusTplan = itemView.findViewById(R.id.btn_minus_tplan);
            itemView.setTag(false);
            buttonMinusTplan.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                     if (position!= NO_POSITION){
                          int[] correctForScada = {0, 0, 0, 0, 0, 0, 0, 0,0,0,0,0,0,0};
                          correctForScada[position]=-3;
                          HttpService.sendTplan(correctForScada);
                     }
                }
            });
            buttonPlusTplan.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position!= NO_POSITION){
                        int[] correctForScada = {0, 0, 0, 0, 0, 0, 0, 0,0,0,0,0,0,0};
                        correctForScada[position]=3;
                        HttpService.sendTplan(correctForScada);
                    }
                }
            });
            buttonPlusTalarm.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position!= NO_POSITION){
                        int[] correctAlarm = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                        correctAlarm[position]=3;
                        HttpService.sendTAlarm(correctAlarm);
                    }
                }
            });
            buttonMinusTalarm.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position!= NO_POSITION){
                        int[] correctAlarm = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                        correctAlarm[position]=-3;
                        HttpService.sendTAlarm(correctAlarm);
                    }
                }
            });
        }
        public void startAnimation() {
            if (colorAnimation == null) {
                colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), Color.RED, Color.BLACK);
                colorAnimation.setDuration(2000);
                colorAnimation.setRepeatCount(ValueAnimator.INFINITE);
                colorAnimation.setRepeatMode(ValueAnimator.REVERSE);
                colorAnimation.addUpdateListener(animator -> itemView.setBackgroundColor((int) animator.getAnimatedValue()));
                colorAnimation.start();
            }
        }
        public void stopAnimation() {
            if (colorAnimation != null) {
                colorAnimation.cancel();
                colorAnimation = null;
                itemView.setBackgroundColor(Color.parseColor("#2C2F37"));
            }
        }
        void bind(DisplayMetrics metrics,String tPod, String pPod, String tUlica, String tPlan,
                  String tAlarm,String boilerLabel, int imageResId){
            // Установка текста
            tv_boilerTpod.setText(tPod + " °C");
            tv_boilerPpod.setText(pPod + " МПа");
            tv_boilerTulica.setText(tUlica + " °C");
            tv_boilerTplan.setText("Контроллер: " + tPlan + " °C");

            if (!tAlarm.equals("Нет связи!")) {
                double tAlarmDouble = Double.parseDouble(tAlarm);
                tAlarmDouble = Math.round(tAlarmDouble * 10.0) / 10.0;
                tAlarm = String.format("%.1f", tAlarmDouble);
            }
            tv_boilerTalarm.setText("Ср. т. аварии: " + tAlarm + "°C");
            tv_boilerLabel.setText(boilerLabel);

            // Масштабирование размера шрифта
            float baseSize = 11;
            float baseDpi = (2400f / metrics.xdpi + 1080f / metrics.ydpi) / 2;
            float currentDpi = (metrics.widthPixels / metrics.xdpi + metrics.heightPixels / metrics.ydpi) / 2;
            float scaleFactor = currentDpi / baseDpi;
            float newSize = baseSize * scaleFactor;
            tv_boilerTpod.setTextSize(TypedValue.COMPLEX_UNIT_SP, newSize);
            tv_boilerPpod.setTextSize(TypedValue.COMPLEX_UNIT_SP, newSize);
            tv_boilerTulica.setTextSize(TypedValue.COMPLEX_UNIT_SP, newSize);
            tv_boilerTplan.setTextSize(TypedValue.COMPLEX_UNIT_SP, newSize);
            tv_boilerTalarm.setTextSize(TypedValue.COMPLEX_UNIT_SP, newSize);
            tv_boilerLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, newSize);

            Glide.with(iv_boilerIcon.getContext())
                    .load(imageResId)
                    .apply(new RequestOptions()
                            .circleCrop()
                            .format(DecodeFormat.PREFER_ARGB_8888))
                    .into(iv_boilerIcon);
        }
    }
}
