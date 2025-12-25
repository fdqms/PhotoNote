package com.fdqms.photonote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityOptionsCompat;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


//import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    //private FirebaseAuth mAuth;
    private EditText etMail,etParola;
    private SwitchCompat switchCompat;
    private Intent intentAnaSayfa,intentKaydol;//intentMesaj;

    private SharedPreferences.Editor editor;

    private VeriTabani vt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences secenekler = getSharedPreferences("secenekler",Context.MODE_PRIVATE);
        if(secenekler.getBoolean("darkMode",false)){
            ((UiModeManager)getSystemService(Context.UI_MODE_SERVICE)).setNightMode(UiModeManager.MODE_NIGHT_YES);
        }else{
            ((UiModeManager)getSystemService(Context.UI_MODE_SERVICE)).setNightMode(UiModeManager.MODE_NIGHT_NO);
        }


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences("uye", Context.MODE_PRIVATE);
        editor = preferences.edit();

        vt = new VeriTabani(MainActivity.this);

        //mAuth = FirebaseAuth.getInstance();

        etMail = findViewById(R.id.et_mail);
        etParola = findViewById(R.id.et_parola);
        switchCompat = findViewById(R.id.switchOto);
        Button btnGiris = findViewById(R.id.btn_giris);
        Button btnKaydol = findViewById(R.id.btn_kaydol);

        intentAnaSayfa = new Intent(this, AnaSayfa.class);
        intentKaydol = new Intent(this, KaydolActivtiy.class);

        if(preferences.getBoolean("giris",false)){
            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(MainActivity.this, R.anim.kisa_f_in, R.anim.kisa_f_out).toBundle();
            startActivity(intentAnaSayfa,bundle);
            finish();
        }

        btnKaydol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(MainActivity.this,
                        R.anim.kisa_f_in, R.anim.kisa_f_out).toBundle();
                startActivity(intentKaydol,bundle);
                finish();
            }
        });

        btnGiris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean basarisiz=true;
                if(etMail.getText().toString().contains("@")){
                    int id = vt.UyeMKontrol(etMail.getText().toString(),etParola.getText().toString());
                    if(id>=0){
                        basarisiz = false;
                        editor.putBoolean("giris", switchCompat.isChecked());
                        editor.putLong("id",id);
                        editor.putString("mail", etMail.getText().toString());
                        editor.putString("ad", vt.GetUyeAdi(id));
                        editor.apply();
                        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(MainActivity.this,
                                R.anim.kisa_f_in, R.anim.kisa_f_out).toBundle();
                        startActivity(intentAnaSayfa,bundle);
                        finish();
                    }
                }else{
                    int id = vt.UyeAKontrol(etMail.getText().toString(),etParola.getText().toString());
                    if(id>=0){
                        basarisiz = false;
                        editor.putBoolean("giris", switchCompat.isChecked());
                        editor.putLong("id",id);
                        editor.putString("ad",etMail.getText().toString());
                        editor.putString("mail", vt.GetMail(id));
                        editor.apply();
                        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(MainActivity.this,
                                R.anim.kisa_f_in, R.anim.kisa_f_out).toBundle();
                        startActivity(intentAnaSayfa,bundle);
                        finish();
                    }
                }
                if(basarisiz){
                    Toast.makeText(getApplicationContext(),"giriş başarısız",Toast.LENGTH_LONG).show();
                }
                /*
                if(!etMail.getText().toString().equals("") && !etParola.getText().toString().equals("")){
                    girisYap();
                }*/
            }
        });

    }

    /*
    private void girisYap(){
        mAuth.signInWithEmailAndPassword(etMail.getText().toString(), etParola.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser kullanici = mAuth.getCurrentUser();
                            intentAnaSayfa.putExtra("kullanici",kullanici);
                            finish();
                            startActivity(intentAnaSayfa);
                        } else {
                            Toast.makeText(GirisActivity.this, "giriş başarısız",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }*/
}