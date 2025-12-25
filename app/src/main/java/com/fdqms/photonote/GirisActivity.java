package com.fdqms.photonote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


//import com.google.firebase.auth.FirebaseAuth;

public class GirisActivity extends AppCompatActivity {

    //private FirebaseAuth mAuth;
    private EditText etMail,etParola;
    private Intent intentAnaSayfa,intentKaydol;//intentMesaj;

    private SharedPreferences.Editor editor;

    private VeriTabani vt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);

        SharedPreferences preferences = getSharedPreferences("uye", Context.MODE_PRIVATE);
        editor = preferences.edit();

        vt = new VeriTabani(GirisActivity.this);

        //mAuth = FirebaseAuth.getInstance();

        etMail = findViewById(R.id.et_mail);
        etParola = findViewById(R.id.et_parola);
        Button btnGiris = findViewById(R.id.btn_giris);
        Button btnKaydol = findViewById(R.id.btn_kaydol);

        intentAnaSayfa = new Intent(this, AnaSayfa.class);
        intentKaydol = new Intent(this, KaydolActivtiy.class);

        if(preferences.getBoolean("giris",false)){
            finish();
            startActivity(intentAnaSayfa);
        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN); // tam ekran

        btnKaydol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentKaydol);
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
                        editor.putBoolean("giris", true);
                        editor.putLong("id",id);
                        editor.putString("mail", etMail.getText().toString());
                        editor.putString("ad", vt.GetUyeAdi(id));
                        editor.apply();
                        finish();
                        startActivity(intentAnaSayfa);
                    }
                }else{
                    int id = vt.UyeAKontrol(etMail.getText().toString(),etParola.getText().toString());
                    if(id>=0){
                        basarisiz = false;
                        editor.putBoolean("giris", true);
                        editor.putLong("id",id);
                        editor.putString("ad",etMail.getText().toString());
                        editor.putString("mail", vt.GetMail(id));
                        editor.apply();
                        finish();
                        startActivity(intentAnaSayfa);
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