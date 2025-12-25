package com.fdqms.photonote;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.PermissionChecker.checkSelfPermission;


public class ProfilFragment extends Fragment {

    private Uri imgUri;
    private Intent galeri;
    private Button btnDuzenle;
    private int temaRengi;
    private SharedPreferences secenekler,preferences;
    private SharedPreferences.Editor editor,secenekEditor;
    private EditText ad,mail;
    private ImageView profil;
    private TextView gonderi,takip,takipci;
    private boolean duzenlemeModu = false;
    private VeriTabani vt;
    private boolean resimSecim = false;
    private String eKadi,eMail;

    private static final int resimKodu=100;
    private static final int dosyaKodu=101;

    public ProfilFragment(){

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            switch (requestCode){
                case resimKodu:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        profil.setForeground(null);
                    }
                    imgUri = data.getData();
                    profil.setImageURI(imgUri);
                    resimSecim = true;
                    break;
                case dosyaKodu:
                    Uri uri = data.getData();
                    File hedefDosya=null;
                    boolean olusturmaKontrol = false;
                    try {
                        String path = DosyaIslemleri.getPath(getActivity(), uri);
                        File kaynak = new File(path);
                        String dosya = path.split("/")[path.split("/").length-1].replace(" ","").toLowerCase();
                        if(dosya.substring(dosya.length()-3).equals("ttf")){
                            if(getContext()!=null && getContext().getFilesDir().exists()){
                                File kokDizin = new File(getContext().getFilesDir().getPath(),"fonts");
                                if(!kokDizin.exists()){
                                    kokDizin.mkdirs();
                                }
                                hedefDosya = new File(kokDizin,dosya);
                                if(!hedefDosya.exists()){
                                    olusturmaKontrol = hedefDosya.createNewFile();
                                }
                                DosyaIslemleri.kopyala(kaynak,hedefDosya);
                                Toast.makeText(getActivity(),getResources().getString(R.string.basarili),Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getContext(),getResources().getString(R.string.uzanti_ttf),Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(getContext(),getResources().getString(R.string.uzanti_ttf),Toast.LENGTH_LONG).show();
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(),getResources().getString(R.string.hata)+": "+e,Toast.LENGTH_LONG).show();
                    } catch (Exception e){
                        if(olusturmaKontrol){
                            hedefDosya.delete();
                        }
                        Toast.makeText(getContext(),getResources().getString(R.string.depo_erisim_kabul),Toast.LENGTH_LONG).show();
                        Intent intentAyarlar = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
                        intentAyarlar.addCategory(Intent.CATEGORY_DEFAULT);
                        intentAyarlar.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentAyarlar);
                    }

                    break;
            }
        }else{
            if(resultCode == resimKodu){
                if(profil.getDrawable() == null){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        profil.setForeground(ContextCompat.getDrawable(getActivity(), R.drawable.profil_foto));
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            try {
                startActivityForResult(Intent.createChooser(intent, "Bir dosya seç"), dosyaKodu);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(getActivity(), getResources().getString(R.string.dosya_yoneticisi_yukle), Toast.LENGTH_SHORT).show();
            }
        } else {
            depolamaKontrol();
        }
    }

    public void depolamaKontrol() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(Intent.createChooser(intent, "Bir dosya seç"), dosyaKodu);
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.dosya_yoneticisi_yukle), Toast.LENGTH_SHORT).show();
                }

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1);
            }
        }else{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            try {
                startActivityForResult(Intent.createChooser(intent, "Bir dosya seç"), dosyaKodu);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(getActivity(), getResources().getString(R.string.dosya_yoneticisi_yukle), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        RotateAnimation hafifSola = new RotateAnimation(0, -60, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        hafifSola.setDuration(500);
        RotateAnimation tumdenCevir = new RotateAnimation(0, 420, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        tumdenCevir.setDuration(500);
        tumdenCevir.setStartOffset(500);
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.setStartOffset(1000);
        animationSet.addAnimation(hafifSola);
        animationSet.addAnimation(tumdenCevir);

        gonderi.startAnimation(animationSet);
        takip.startAnimation(animationSet);
        takipci.startAnimation(animationSet);

        animationSet.cancel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        preferences = getActivity().getSharedPreferences("uye", Context.MODE_PRIVATE);
        secenekler = getActivity().getSharedPreferences("secenekler", Context.MODE_PRIVATE);
        temaRengi = Color.parseColor(secenekler.getString("temaRengi","#ffffff"));


        editor = preferences.edit();
        secenekEditor = secenekler.edit();

        vt = new VeriTabani(getContext());

        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        profil = view.findViewById(R.id.iv_profil);
        ad = view.findViewById(R.id.tv_ad);
        mail = view.findViewById(R.id.tv_mail);

        gonderi = view.findViewById(R.id.tv_gonderi_sayisi);
        takip = view.findViewById(R.id.tv_takip_sayisi);
        takipci = view.findViewById(R.id.tv_takipci_sayisi);

        ad.setText(preferences.getString("ad","ad"));
        mail.setText(preferences.getString("mail","mail"));
        Bitmap uyeResmi = vt.GetUyeResmi(preferences.getLong("id",-1));
        if(uyeResmi == null){
            profil.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.profil_foto));
        }else {
            profil.setImageBitmap(uyeResmi);
        }

        Button btnCikis = view.findViewById(R.id.btn_cikis);
        btnDuzenle = view.findViewById(R.id.btn_duzenle);
        SwitchCompat swDarkMode = view.findViewById(R.id.sw_dark_mode);
        swDarkMode.setChecked(secenekler.getBoolean("darkMode",false));
        swDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                secenekEditor.putBoolean("darkMode",isChecked);
                secenekEditor.commit();
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });
        btnDuzenle.getBackground().setTint((temaRengi & 0x00FFFFFF) | 0x80000000);
        Button btnFontEkle = view.findViewById(R.id.btn_font_ekle);
        Button btnTemaRengi = view.findViewById(R.id.btn_tema_rengi_sec);

        Button btnParola = view.findViewById(R.id.btn_parola);

        gonderi.setText(vt.GonderiSayisi(preferences.getLong("id",0))+"");

        btnCikis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.clear();
                editor.apply();
                Intent i = new Intent(getActivity(),MainActivity.class);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(getActivity(),
                        android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(i,optionsCompat.toBundle());
                getActivity().finish();
            }
        });

        profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(duzenlemeModu){
                    galeri = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(galeri, resimKodu);
                }
            }
        });

        btnDuzenle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean hatasiz = true;

                Drawable drawable = btnDuzenle.getBackground();
                drawable = DrawableCompat.wrap(drawable);
                if(duzenlemeModu){
                    DrawableCompat.setTint(drawable, (temaRengi & 0x00FFFFFF) | 0x80000000);
                    btnDuzenle.setText(getResources().getString(R.string.duzenle));
                    duzenlemeModu = false;

                    long id;

                    if(mail.getText().toString().contains("@")){
                        if(resimSecim){
                            id = vt.UyeGuncelle(preferences.getLong("id",-1),((BitmapDrawable)profil.getDrawable()).getBitmap(),ad.getText().toString(),
                                    mail.getText().toString());
                            if(id > 0){
                                editor.putLong("id",id);
                                Toast.makeText(getActivity(),getResources().getString(R.string.basarili),Toast.LENGTH_LONG).show();
                            }else{
                                hatasiz = false;
                            }
                        }else{
                            id = vt.UyeGuncelle(preferences.getLong("id",-1),ad.getText().toString(), mail.getText().toString());
                            if(id > 0){
                                Toast.makeText(getActivity(),getResources().getString(R.string.basarili),Toast.LENGTH_LONG).show();
                            }else{
                                hatasiz = false;
                            }
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            profil.setForeground(null);
                        }
                        if(hatasiz){
                            editor.putString("ad",ad.getText().toString());
                            editor.putString("mail",mail.getText().toString());
                            editor.commit();
                        }else{
                            Toast.makeText(getActivity(),getResources().getString(R.string.hata),Toast.LENGTH_LONG).show();
                            ad.setText(eKadi);
                            mail.setText(eMail);
                        }
                    }else{
                        ad.setText(eKadi);
                        mail.setText(eMail);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            profil.setForeground(ResourcesCompat.getDrawable(getResources(),R.drawable.profil_foto,getActivity().getTheme()));
                        }
                        Toast.makeText(getActivity(),getResources().getString(R.string.mail_hatali),Toast.LENGTH_LONG).show();
                    }
                }else{
                    DrawableCompat.setTint(drawable, ContextCompat.getColor(getContext(), R.color.kabulYesil));
                    btnDuzenle.setText(getResources().getString(R.string.tamam));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        profil.setForeground(ContextCompat.getDrawable(getContext(), R.drawable.resim_sec));
                    }
                    duzenlemeModu = true;
                    eKadi = ad.getText().toString();
                    eMail = mail.getText().toString();
                }
                editTextDuzenlenebilme(ad,duzenlemeModu);
                editTextDuzenlenebilme(mail,duzenlemeModu);
            }
        });

        btnFontEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                depolamaKontrol();
            }
        });

        btnTemaRengi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temaDialog();
            }
        });

        btnParola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parolaGuncelle();
            }
        });

        return view;
    }

    private void temaDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogTheme);

        final EditText editText = new EditText(getContext());
        editText.setPadding(50,50,50,0);
        editText.setHint("#ffffff");
        editText.setHintTextColor(ResourcesCompat.getColor(getResources(),R.color.hintColor,getActivity().getTheme()));
        editText.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.et_kenar,getActivity().getTheme()));
        editText.setFilters(new InputFilter[]{new InputFilter() {
            public CharSequence filter(CharSequence src, int start,
                                       int end, Spanned dst, int dstart, int dend) {
                if(src.equals("")){
                    return null;
                }

                String kontrol = dst.subSequence(0, dstart).
                        toString() + src.subSequence(start, end) +
                        dst.subSequence(
                                dend, dst.length()).toString();

                Pattern pattern = Pattern.compile("^#(?:[0-9a-fA-F]{6})$");
                Matcher matcher = pattern.matcher(kontrol);
                if(!matcher.matches() && !matcher.hitEnd())
                    return "";
                return null;
            }
        },});


        builder.setView(editText);

        builder.setPositiveButton("tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if(editText.length()==7){
                    secenekEditor.putString("temaRengi",editText.getText().toString());
                    secenekEditor.commit();
                    // bottom navigation üzerinde etkili olması için activity'yi rest atıyoruz
                    getActivity().recreate();
                }
            }
        });

        builder.setNegativeButton("iptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.cancel();
            }
        });


        builder.show();
    }

    private void editTextDuzenlenebilme(EditText editText,boolean duzenleme){
        if(duzenleme){
            editText.setTextColor(ResourcesCompat.getColor(getResources(),R.color.hintColor,getActivity().getTheme()));
        }else{
            editText.setTextColor(ResourcesCompat.getColor(getResources(),R.color.yaziRengi,getActivity().getTheme()));
        }
        editText.setFocusable(duzenleme);
        editText.setEnabled(duzenleme);
        editText.setClickable(duzenleme);
        editText.setFocusableInTouchMode(duzenleme);
    }

    private void parolaGuncelle(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogTheme);

        final EditText editText = new EditText(getContext());

        editText.setHint(getResources().getString(R.string.parola));
        editText.setPadding(50,50,50,0);
        editText.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.et_kenar,getActivity().getTheme()));

        editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        builder.setView(editText);

        builder.setPositiveButton("tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                long sonuc = vt.UyeParolaGuncelle(preferences.getLong("id",-1),editText.getText().toString());
                if(sonuc > -1){
                    Toast.makeText(getContext(),getResources().getString(R.string.basarili),Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(),getResources().getString(R.string.hata),Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.setNegativeButton("iptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }
}