package com.fdqms.photonote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;


public class BlogDetay extends AppCompatActivity {

    private Transition fade;

    private SharedPreferences preferences;
    private long uyeId;

    //private String yazar;
    private AnimatedVectorDrawable kaydet;

    private long gonderiId;

    private CheckBox checkBox;
    private boolean kayitVarMi;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        if(kayitVarMi == checkBox.isChecked()){
            intent.putExtra("degisiklik",false);
        }else{
            intent.putExtra("degisiklik",true);
            intent.putExtra("durum",checkBox.isChecked());
            intent.putExtra("g_id",gonderiId);
        }
        setResult(RESULT_OK, intent);
        if(kaydet.isRunning()){
            kaydet.stop();
        }

        finishAfterTransition();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detay);

        preferences = getSharedPreferences("uye", Context.MODE_PRIVATE);
        uyeId = preferences.getLong("id",-1);

        getWindow().setSharedElementEnterTransition(new ChangeBounds().setDuration(500));

        String baslik;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                baslik =null;
            } else {
                baslik = extras.getString("baslik");
                gonderiId = extras.getLong("gonderiId");
            }
        }else{
            baslik = savedInstanceState.getString("baslik");
            gonderiId = savedInstanceState.getLong("gonderiId");
        }

        final VeriTabani vt = new VeriTabani(this);
        LinearLayout linearLayout = findViewById(R.id.linearlayout);
        vt.DetayGetir(linearLayout,gonderiId);
        TextView tvBaslik = findViewById(R.id.tv_detay_baslik);

        tvBaslik.setText(baslik);

        checkBox = findViewById(R.id.checkBox);

        kayitVarMi = vt.KayitKontrol(uyeId,gonderiId) > -1;

        kaydet = kayitVarMi ? (AnimatedVectorDrawable) ResourcesCompat.getDrawable(getResources(),R.drawable.bookmark_cikis,getTheme()): (AnimatedVectorDrawable) ResourcesCompat.getDrawable(getResources(),R.drawable.bookmark,getTheme());
        checkBox.setButtonDrawable(kaydet);
        checkBox.setChecked(kayitVarMi);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(kaydet.isRunning()){
                    checkBox.setChecked(!b);
                    return;
                }
                if(b){
                    kaydet = (AnimatedVectorDrawable) ResourcesCompat.getDrawable(getResources(),R.drawable.bookmark,getTheme());
                    vt.KaydedilenlereEkle(uyeId,gonderiId);
                }else{
                    kaydet = (AnimatedVectorDrawable) ResourcesCompat.getDrawable(getResources(),R.drawable.bookmark_cikis,getTheme());
                    vt.KaydedilenlerdenKaldir(uyeId,gonderiId);
                }
                checkBox.setButtonDrawable(kaydet);
                kaydet.start();
            }
        });



    }
}