package com.csbk.boilerscontrolapp;

public class Boiler {
    public String gettPod() {
        return tPod;
    }

    public void settPod(String tPod) {
        this.tPod = tPod +" °C";
    }

    public String getpPod() {
        return pPod;
    }

    public void setpPod(String pPod) {
        this.pPod = pPod + " МПа";
    }

    public String gettUlica() {
        return tUlica;
    }
    public String gettUlicaWithoutCelcii() {
        if (tUlica != null && tUlica.length() >= 3) {
            tUlica=tUlica.replace(',','.');
            return tUlica.substring(0, tUlica.length() - 3);
        } else {
            return tUlica;
        }
    }
    public void settUlica(String tUlica) {
        this.tUlica = tUlica + " °C";
    }
    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }


    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }

    boolean isOk;
    String tPod;
    String pPod;
    String tUlica;
    String correctionTpod;
    String tPlan;
    String tAlarm;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    Integer id;
    int imageResId;
    public String getCorrectionTpod() {
        return correctionTpod;
    }

    public void setCorrectionTpod(String correctionTpod) {
        this.correctionTpod = correctionTpod;
    }
    public String gettPlan() {
        return tPlan;
    }

    public void settPlan(String tPlan) {
        this.tPlan = "План контроллера: " + tPlan + "°C";
    }

    public String gettAlarm() {
        return tAlarm ;
    }

    public void settAlarm(String tAlarm) {
        this.tAlarm = "Ср. точка аварии: " + tAlarm + "°C";
    }

}
