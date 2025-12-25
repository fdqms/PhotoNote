package com.fdqms.photonote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;

import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

/*
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
 */
import com.google.android.flexbox.FlexboxLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YeniGonderi extends AppCompatActivity {

    private int seciliFont = 0;
    private List<StringBuilder> fontVerileri;

    private LinearLayout icerikListesi;
    private SharedPreferences preferences,secenekler;
    private int temaRengi;

    private CustomEditText etBaslik;
    private CustomEditText etDinamik;

    private Uri imgUri;
    private final static int resimKodu = 100;
    private Intent galeri;

    private Animation scale;

//    private RequestQueue kuyruk;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == resimKodu){
                ImageView iv = new ImageView(this);
                imgUri = data.getData();
                iv.setImageURI(imgUri);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                icerikListesi.addView(iv);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
        overridePendingTransition(R.anim.yukaridan,R.anim.asagi);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        secenekler = getSharedPreferences("secenekler", Context.MODE_PRIVATE);
        temaRengi = Color.parseColor(secenekler.getString("temaRengi","#ffffff"));


        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(temaRengi);

        setContentView(R.layout.activity_yeni_gonderi);

        preferences = getSharedPreferences("uye", Context.MODE_PRIVATE);

        //kuyruk = Volley.newRequestQueue(this);

        icerikListesi = findViewById(R.id.icerik_listesi);

        ImageView btnKapat = findViewById(R.id.btn_kapat);
        ImageView btnKabul = findViewById(R.id.btn_kabul);

        ImageView rtMetin = findViewById(R.id.rt_metin);
        ImageView rtAlinti = findViewById(R.id.rt_alinti);
        ImageView rtResim = findViewById(R.id.rt_resim);
        ImageView btnSol = findViewById(R.id.rt_sol);
        ImageView btnOrta = findViewById(R.id.rt_orta);
        ImageView btnSag = findViewById(R.id.rt_sag);



        CustomEditText etPost = findViewById(R.id.etPost);
        fontVerileri = new ArrayList<>();
        etDinamik = etPost;
        etBaslik = findViewById(R.id.et_baslik);

        editTextDuzenle(etPost);

        rtMetin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomEditText editText = new CustomEditText(YeniGonderi.this);
                Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.arch);
                editText.setTypeface(typeface);
                editText.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                editText.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                editText.setBackgroundResource(R.drawable.et_kenar);
                editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                int p = (int) (getResources().getDisplayMetrics().density*10);
                editText.setPadding(p,p,p,p);
                editText.setGravity(Gravity.START);
                editText.setSingleLine(false);
                editTextDuzenle(editText);
                icerikListesi.addView(editText);
                editText.requestFocus();
            }
        });

        rtAlinti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = vi.inflate(R.layout.alinti, null);

                CustomEditText etAlinti = v.findViewById(R.id.et_alinti);
                Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.arch);
                etAlinti.setTypeface(typeface);
                editTextDuzenle(etAlinti);
                etAlinti.requestFocus();
                icerikListesi.addView(v);
            }
        });

        rtResim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galeri = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                String [] mimeTypes = {"image/png"}; // , "image/jpg","image/jpeg"
                galeri.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
                startActivityForResult(galeri, resimKodu);
            }
        });

        btnSol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etDinamik.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            }
        });

        btnOrta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etDinamik.setGravity(Gravity.CENTER);
            }
        });

        btnSag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etDinamik.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            }
        });

        btnKapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnKabul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                VeriTabani vt = new VeriTabani(YeniGonderi.this);
                long uye_id = preferences.getLong("id",-1);
                String gonderiSirasi = gonderiSirasi();
                long id = vt.GonderiEkle(uye_id,etBaslik.getText().toString(),gonderiSirasi);
                int j=0;
                for(int i=0;i<icerikListesi.getChildCount();i++){
                    Object object = icerikListesi.getChildAt(i);
                    if(object instanceof CustomEditText){
                        EditText yazi = (EditText)object;
                        if(!yazi.getText().toString().isEmpty()){
                            vt.YaziEkle(id,i, Html.toHtml(yazi.getText()),yazi.getGravity(),fontVerileri.get(j).toString(),0);
                        }
                        j++;
                    }else if(object instanceof ImageView){
                        ImageView resim = (ImageView) object;
                        vt.ResimEkle(id,i,((BitmapDrawable)resim.getDrawable()).getBitmap());
                    }else if(object instanceof RelativeLayout){
                        RelativeLayout rl_alinti = (RelativeLayout) object;
                        EditText alintiMetni = rl_alinti.findViewById(R.id.et_alinti);
                        if(!alintiMetni.getText().toString().isEmpty()){
                            vt.YaziEkle(id,i, Html.toHtml(alintiMetni.getText()),alintiMetni.getGravity(),fontVerileri.get(j).toString(),1);
                        }
                        j++;
                    }
                }
                vt.close();

                Intent intent = new Intent();
                intent.putExtra("gonderi_id",id);
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.yukaridan,R.anim.asagi);
                ///////////////////////////////////////////////////////////////////////////////////////
                //Toast.makeText(YeniGonderi.this,etPost.getText(),Toast.LENGTH_LONG).show();
                /*
                String url = "http://192.168.1.101/kayit.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        //params.put("metin", etPost.getText().toString());
                        return params;
                    }};
                kuyruk.add(stringRequest);
                */
            }
        });
    }

    private String gonderiSirasi(){
        StringBuilder temp= new StringBuilder();
        for(int i=0;i<icerikListesi.getChildCount();i++){
            Object object = icerikListesi.getChildAt(i);
            if(object instanceof CustomEditText){
                temp.append("0");
            }else if(object instanceof ImageView){
                temp.append("1");
            }else if(object instanceof RelativeLayout){
                temp.append("2");
            }
        }
        return temp.toString();
    }

    private void editTextDuzenle(final CustomEditText editText){

        final StringBuilder yeniVeri = new StringBuilder();
        fontVerileri.add(yeniVeri);

        editText.setTextSize(16);
        editText.setTextColor(ResourcesCompat.getColor(getResources(),R.color.yaziRengi,getTheme()));
        editText.setHintTextColor(ResourcesCompat.getColor(getResources(),R.color.hintColor,getTheme()));
        editText.setHint(getString(R.string.bir_seyler_yaz));

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    etDinamik = editText;
                }
            }
        });

        editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                menu.add(0,0,0,"").setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bold));
                menu.add(0,1,0,"").setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_italic));
                menu.add(0,2,0,"").setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_underlined));
                menu.add(0,3,0,"").setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_color));
                menu.add(0,4,0,"font");

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                menu.removeItem(android.R.id.selectAll);
                menu.removeItem(android.R.id.cut);
                menu.removeItem(android.R.id.copy);
                menu.removeItem(android.R.id.paste);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    menu.removeItem(android.R.id.shareText);
                }
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                int ilk = editText.getSelectionStart();
                int son = editText.getSelectionEnd();
                Editable editable;

                editable = editText.getText();
                SpannableString ss = new SpannableString(editable); // yazı tipini seçtikten sonra boşluk bırakıp normale dönmesi için
                StyleSpan[] styleSpans = ss.getSpans(ilk,son,StyleSpan.class);


                switch (menuItem.getItemId()){
                    case 0:
                        boolean boldKontrol = true;
                        for (StyleSpan styleSpan : styleSpans) {
                            if (styleSpan.getStyle() == Typeface.BOLD) {
                                boldKontrol = false;
                                ss.removeSpan(styleSpan);
                                break;
                            }
                        }
                        if(boldKontrol){
                            ss.setSpan(new StyleSpan(Typeface.BOLD), ilk, son, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        }
                        break;
                    case 1:
                        boolean italicKontrol = true;
                        for (StyleSpan styleSpan : styleSpans) {
                            if (styleSpan.getStyle() == Typeface.ITALIC) {
                                italicKontrol = false;
                                ss.removeSpan(styleSpan);
                                break;
                            }
                        }
                        if(italicKontrol){
                            ss.setSpan(new StyleSpan(Typeface.ITALIC), ilk, son, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        }
                        break;
                    case 2:
                        ss.setSpan(new UnderlineSpan(){
                            public void updateDrawState(@NonNull TextPaint tp) {
                                tp.setUnderlineText(!tp.isUnderlineText());
                            }
                        }, ilk, son, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        break;
                    case 3:
                        RenkDialog(ss,ilk,son,editText);
                        break;
                    case 4:
                        fontDialog(ss,ilk,son,editText,yeniVeri);
                        break;
                }

                // sona odaklanması için
                editText.setText("");
                editText.append(ss);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_DEL){
                    editText.setText("");
                }
                return true;
            }
        });
    }

    private void fontDialog(final SpannableString ss, final int ilk, final int son, final CustomEditText editText, final StringBuilder sb){

        List<String> listDosya = DosyaIslemleri.fontlariListele(new File(getFilesDir().getPath(),"fonts"));
        if(listDosya == null || listDosya.size() == 0){
            Toast.makeText(this,getResources().getString(R.string.yazi_tipi_bulunamadi),Toast.LENGTH_SHORT).show();
        }else{
            String[] dosyalar = new String[listDosya.size()];
            dosyalar = listDosya.toArray(dosyalar);
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogTheme);
            //builder.setTitle("yazı tipini seçin");

            builder.setSingleChoiceItems(dosyalar, seciliFont, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    seciliFont=i;
                }
            });


            builder.setPositiveButton("tamam", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Typeface font = Typeface.createFromFile(DosyaIslemleri.fontGetir(new File(getApplicationContext().getFilesDir().getPath(),"fonts"),seciliFont));
                    CustomTypefaceSpan[] ctfSpans = ss.getSpans(ilk,son,CustomTypefaceSpan.class);

                    if(ctfSpans.length > 0){
                        for (CustomTypefaceSpan ctfSpan : ctfSpans) {
                            String veri = ilk + "/" + son + "/" + seciliFont + " ";
                            if (sb.indexOf(veri) >= 0) {
                                sb.delete(sb.indexOf(veri), sb.indexOf(veri) + veri.length());
                                ss.removeSpan(ctfSpan);
                            }
                        }
                    }else{
                        ss.setSpan(new CustomTypefaceSpan(font), ilk, son, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                        sb.append(ilk).append("/").append(son).append("/").append(seciliFont).append(" ");
                    }

                    editText.setText("");
                    editText.append(ss);
                }
            });

            builder.setNegativeButton("iptal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            builder.create().show();
        }
    }

    private void ozelRenk(final SpannableString ss, final int ilk, final int son, final EditText et, final Dialog dialog){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogTheme);

        final EditText editText = new EditText(this);
        editText.setPadding(50,50,50,0);
        editText.setHint("#ffffff");
        editText.setHintTextColor(ResourcesCompat.getColor(getResources(),R.color.hintColor,getTheme()));
        editText.setTextColor(ResourcesCompat.getColor(getResources(),R.color.yaziRengi,getTheme()));
        editText.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.et_kenar,getTheme()));
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
                    ss.setSpan(new ForegroundColorSpan(Color.parseColor(editText.getText().toString())),ilk,son,Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    et.setText("");
                    et.append(ss);
                    dialog.dismiss();
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

    private void RenkDialog(final SpannableString ss, final int ilk, final int son, final EditText editText){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.renk_paleti);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        FlexboxLayout fbl = dialog.findViewById(R.id.fbl);

        final TypedArray ta = this.getResources().obtainTypedArray(R.array.yazirengi);

        final int[] renkler = new int[ta.length()];

        for (int i = 0; i < ta.length(); i++) {
            renkler[i] = ta.getColor(i,0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100,100);
            params.setMargins(25,25,25,25);

            Button btn = new Button(this);
            btn.setLayoutParams(params);
            btn.setGravity(Gravity.CENTER);

            ShapeDrawable daire = new ShapeDrawable(new OvalShape());
            daire.getPaint().setColor(ta.getColor(i,0));
            btn.setBackground(daire);
            final int id = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ss.setSpan(new ForegroundColorSpan(renkler[id]),ilk,son,Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    editText.setText("");
                    editText.append(ss);
                    dialog.dismiss();
                }
            });
            fbl.addView(btn,params);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100,100);
        params.setMargins(25,25,25,25);

        Button btn = new Button(this);
        btn.setLayoutParams(params);
        btn.setGravity(Gravity.CENTER);

        ShapeDrawable daire = new ShapeDrawable(new OvalShape());
        daire.getPaint().setColor(ResourcesCompat.getColor(getResources(),R.color.yaziRengi,getTheme()));
        btn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_ekle,getTheme()));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ozelRenk(ss,ilk,son,editText,dialog);
            }
        });
        fbl.addView(btn,params);
        ta.recycle();
        dialog.show();
    }
}