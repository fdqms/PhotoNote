package com.fdqms.photonote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;


public class AnaSayfa extends AppCompatActivity {

    private static final int YeniRequestCode = 0;
    private static final int DetayRequestCode = 1;

    private int oncekiSecim=0;
    private Fragment frg1,frg2,frg3,oncekiFragment;
    private BottomNavigationView bnv;
    private InterstitialAd mInterstitialAd;

    private int temaRengi;
    private SharedPreferences secenekler;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            if(requestCode==YeniRequestCode){
                if(resultCode == RESULT_OK){
                    ((BlogFragment)frg1).GonderiEkle(data.getLongExtra("gonderi_id",-1));
                }
                bnv.getMenu().getItem(oncekiSecim).setChecked(true);
            }else if(requestCode==DetayRequestCode){
                KayitliFragment kayitliFragment = (KayitliFragment)frg2;
                if(data.getBooleanExtra("degisiklik",false)){
                    if(data.getBooleanExtra("durum",false)){
                        kayitliFragment.GonderiEkle(data.getLongExtra("g_id",-1));
                    }else{
                        kayitliFragment.GonderiSil(data.getLongExtra("g_id",-1));
                    }
                }
            }
        }
    }

    /*
    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && seffaflik < 255){
            seffaflik=0xFF000000;
            ((BlogFragment)frg1).seffaflikGuncelle(seffaflik);
            //((KayitliFragment)frg2).seffaflikGuncelle(seffaflik);
        }else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && seffaflik > 0){
            seffaflik--;
            ((BlogFragment)frg1).seffaflikGuncelle(seffaflik);
            ((KayitliFragment)frg2).seffaflikGuncelle(seffaflik);
        }
        return true;
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        secenekler = getSharedPreferences("secenekler", Context.MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_sayfa);

        /*
        ApplicationInfo ai = null;
        try {
            ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID","ca-app-pub-5260711859318238~1590162154");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }*/

        bnv = findViewById(R.id.bottom_navigation);

        temaRengi = Color.parseColor(secenekler.getString("temaRengi","#ffffff"));

        if (!BuildConfig.DEBUG) {

            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-5260711859318238/1613147223");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }

            });
        }


        frg1 = new BlogFragment(AnaSayfa.this);
        frg2 = new KayitliFragment(AnaSayfa.this);
        frg3 = new ProfilFragment();

        oncekiFragment = frg1;

        bnv.setItemIconTintList(null);

        Drawable icon = bnv.getMenu().getItem(0).getIcon();
        icon.setColorFilter(temaRengi, PorterDuff.Mode.MULTIPLY);
        bnv.getMenu().getItem(0).setIcon(icon);
        oncekiSecim = 0;

        for(int i=0;i<bnv.getMenu().size();i++){
            float[] outerRadii = new float[8];
            Arrays.fill(outerRadii, 0); // radius = (56+50)/2  (menu+icon)/2
            RoundRectShape r = new RoundRectShape(outerRadii, null, null);
            ShapeDrawable shapeDrawable = new ShapeDrawable(r);
            shapeDrawable.getPaint().setColor(temaRengi);
            Drawable back = bnv.findViewById(bnv.getMenu().getItem(i).getItemId()).getBackground();
            RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(temaRengi), back,shapeDrawable);
            bnv.findViewById(bnv.getMenu().getItem(i).getItemId()).setBackground(rippleDrawable);
        }

        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Fragment fragment = null;

                bnv.getMenu().getItem(oncekiSecim).getIcon().clearColorFilter();
                menuItem.getIcon().setColorFilter(temaRengi, PorterDuff.Mode.MULTIPLY);

                switch (menuItem.getItemId()){
                    case R.id.item1:
                        fragment = frg1;
                        oncekiFragment = frg1;
                        oncekiSecim = 0;
                        break;
                    case R.id.item2:
                        fragment = frg2;
                        oncekiFragment=frg2;
                        oncekiSecim = 1;
                        break;
                    case R.id.item3:
                        fragment=oncekiFragment;
                        ApplicationInfo ai = null;
                        try {
                            ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                            Bundle bundle = ai.metaData;
                            String a = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                            if(!a.equals("")){
                                startActivityForResult(new Intent(getApplicationContext(), YeniGonderi.class),YeniRequestCode);
                                overridePendingTransition(R.anim.asagidan,R.anim.yukari);
                                if (!BuildConfig.DEBUG) {
                                    if (mInterstitialAd.isLoaded()) {
                                        mInterstitialAd.show();
                                    }
                                }
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;

                    case R.id.item4:
                        fragment = frg3;
                        oncekiFragment=frg3;
                        oncekiSecim=3;
                        break;
                }

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.fade_in,R.anim.fade_out);
                ft.replace(R.id.icerik_listesi,fragment);
                ft.commit();

                return true;
            }
        });

        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.fade_out).replace(R.id.icerik_listesi,frg1).commitNow();
    }


}