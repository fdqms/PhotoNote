package com.fdqms.photonote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
/*import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;*/

public class KaydolActivtiy extends AppCompatActivity {

    //private FirebaseAuth mAuth;
    private EditText ad,email,parola;
    private Intent intentAnaSayfa;
    private VeriTabani vt;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kaydol);

        vt = new VeriTabani(KaydolActivtiy.this);
        SharedPreferences preferences = getSharedPreferences("uye", Context.MODE_PRIVATE);
        editor = preferences.edit();

        ad = findViewById(R.id.et_ad);
        email = findViewById(R.id.et_mail);
        parola = findViewById(R.id.et_parola);
        Button btnKaydol = findViewById(R.id.btn_kaydol);

        intentAnaSayfa = new Intent(this, AnaSayfa.class);
        //mAuth = FirebaseAuth.getInstance();

        btnKaydol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ad.getText().toString().isEmpty() || email.getText().toString().isEmpty() || !email.getText().toString().contains("@") || parola.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.hata),Toast.LENGTH_LONG).show();
                }else{
                    long id = vt.UyeEkle(ad.getText().toString(),email.getText().toString(),parola.getText().toString());
                    if(id>=0){
                        editor.putBoolean("giris", true);
                        editor.putString("ad",ad.getText().toString());
                        editor.putString("mail",email.getText().toString());
                        editor.putLong("id",id);
                        editor.apply();
                        finish();
                        startActivity(intentAnaSayfa);
                    }else{
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.kayit_basarisiz),Toast.LENGTH_LONG).show();
                    }
                }
                //kaydol();
            }
        });
    }

    /*
    private void kaydol(){
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), parola.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser kullanici = mAuth.getCurrentUser();
                            intentAnaSayfa.putExtra("kullanici",kullanici);
                            startActivity(intentAnaSayfa);
                        } else {
                            Toast.makeText(KaydolActivtiy.this, "başarısız.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }*/
}