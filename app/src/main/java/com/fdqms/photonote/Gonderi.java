package com.fdqms.photonote;

import android.graphics.Bitmap;

import java.util.Date;

public class Gonderi {

    public Gonderi(){

    }

    private long gonderiId;
    private int begeni = 0;
    private int okunmaSuresi=0;
    private String baslik;
    private String yazar;
    private CharSequence icerik;
    private boolean resimVarMi;
    private Bitmap resim;
    private Date tarih;

    public Date getTarih() {
        return this.tarih;
    }

    public void setTarih(Date tarih) {
        this.tarih = tarih;
    }

    public Bitmap getResim() {
        return resim;
    }

    public void setResim(Bitmap resim) {
        this.resim = resim;
    }

    public boolean resimVarMi() {
        return resimVarMi;
    }

    public void setResimVarMi(boolean resimVarMi) {
        this.resimVarMi = resimVarMi;
    }

    public long getGonderiId() {
        return gonderiId;
    }

    public void setGonderiId(long gonderiId) {
        this.gonderiId = gonderiId;
    }

    public void setIcerik(CharSequence icerik) {
        this.icerik = icerik;
    }

    public void setYazar(String yazar) {
        this.yazar = yazar;
    }

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }
    public CharSequence getIcerik() {
        return icerik;
    }

    public String getBegeni() {
        return ""+begeni;
    }

    public String getOkunmaSuresi() {
        return okunmaSuresi+" dk";
    }

    public void setOkunmaSuresi(int okunmaSuresi) {
        this.okunmaSuresi = okunmaSuresi;
    }

}
