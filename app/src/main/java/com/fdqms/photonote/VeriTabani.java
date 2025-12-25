package com.fdqms.photonote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class VeriTabani extends SQLiteOpenHelper {

    private Context context;
    private static final String VT_ADI = "PhotoNote";
    private static final int VT_VERSION = 1;

    public VeriTabani(@Nullable Context context) {
        super(context, VT_ADI, null, VT_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE uyeler (id INTEGER PRIMARY KEY AUTOINCREMENT,ad TEXT UNIQUE NOT NULL,mail TEXT UNIQUE NOT NULL,parola TEXT NOT NULL,foto BLOB)");
        sqLiteDatabase.execSQL("CREATE TABLE gonderiler (id INTEGER PRIMARY KEY AUTOINCREMENT,uye_id INTEGER NOT NULL,g_baslik TEXT NOT NULL,tarih INTEGER NOT NULL,g_sirasi TEXT NOT NULL)");
        sqLiteDatabase.execSQL("CREATE TABLE yazilar (id INTEGER PRIMARY KEY AUTOINCREMENT,g_id INTEGER NOT NULL, sira INTEGER NOT NULL,yazi TEXT NOT NULL,font TEXT,gravity INTEGER NOT NULL,alinti INTEGER)");
        sqLiteDatabase.execSQL("CREATE TABLE resimler (id INTEGER PRIMARY KEY AUTOINCREMENT, g_id INTEGER NOT NULL, sira INTEGER NOT NULL,resim BLOB NOT NULL)");
        sqLiteDatabase.execSQL("CREATE TABLE kaydedilenler (id INTEGER PRIMARY KEY AUTOINCREMENT, uye_id INTEGER NOT NULL, g_id INTEGER UNIQUE NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int eskiVersion, int yeniVersion) {
        if(yeniVersion>eskiVersion){
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS uyeler");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS gonderiler");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS yazilar");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS resimler");
            onCreate(sqLiteDatabase);
        }
    }

    private long donusturTimeStamp(Date date){
        return date.getTime();
    }

    private Date donusturDate(Long timeStamp){
        return new Date(timeStamp);
    }

    public long UyeEkle(String ad,String mail, String parola){
        long id=-1;
        try{
            SQLiteDatabase vt = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("ad",ad);
            cv.put("mail",mail);
            cv.put("parola",parola);
            id = vt.insert("uyeler",null,cv);
            vt.close();
        }catch (Exception e){
            //e.printStackTrace();
        }

        return id;
    }

    public long UyeGuncelle(long uyeId, Bitmap resim, String ad, String mail){
        long id = -1;
        byte[] resimVerisi = bitmapByteArrayCevir(resim);
        try{
            SQLiteDatabase vt = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("ad",ad);
            cv.put("mail",mail);
            cv.put("foto",resimVerisi);
            id = vt.update("uyeler",cv,"id=?",new String[] { String.valueOf(uyeId) });
            vt.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return id;
    }

    public long UyeGuncelle(long uyeId, String ad, String mail){
        long id = -1;
        try{
            SQLiteDatabase vt = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("ad",ad);
            cv.put("mail",mail);
            id = vt.update("uyeler",cv,"id=?",new String[] { String.valueOf(uyeId) });
            vt.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return id;
    }

    public long UyeParolaGuncelle(long uyeId, String parola){
        long id = -1;
        try{
            SQLiteDatabase vt = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("parola",parola);
            id = vt.update("uyeler",cv,"id=?",new String[] { String.valueOf(uyeId) });
            vt.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return id;
    }

    public int UyeMKontrol(String mail, String parola){
        int id=-1;
        try{
            SQLiteDatabase vt = this.getReadableDatabase();
            Cursor cursor = vt.rawQuery("SELECT id,parola FROM uyeler WHERE mail='"+mail+"'",null);
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                if(parola.equals(cursor.getString(cursor.getColumnIndex("parola")))){
                    id = cursor.getInt(cursor.getColumnIndex("id"));
                }
            }
            cursor.close();
            vt.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return id;
    }

    public int UyeAKontrol(String ad, String parola){
        int id=-1;
        try{
            SQLiteDatabase vt = this.getReadableDatabase();
            Cursor cursor = vt.rawQuery("SELECT id,parola FROM uyeler WHERE ad='"+ad+"'",null);
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                if(parola.equals(cursor.getString(cursor.getColumnIndex("parola")))){
                    id = cursor.getInt(cursor.getColumnIndex("id"));
                }
                cursor.close();
            }
            vt.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return id;
    }

    public Bitmap GetUyeResmi(long uyeId){

        try{
            SQLiteDatabase vt = this.getReadableDatabase();
            Cursor cursor = vt.rawQuery("SELECT foto FROM uyeler WHERE id="+uyeId,null);
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                return byteArrayBitmapCevir(cursor.getBlob(cursor.getColumnIndex("foto")));
            }
            cursor.close();
            vt.close();
        }catch (Exception e){

        }
        return null;
    }

    public String GetUyeAdi(long uye_id){
        String ad = "";
        try{
            SQLiteDatabase vt = this.getReadableDatabase();
            Cursor cursor = vt.rawQuery("SELECT ad FROM uyeler WHERE id="+uye_id,null);
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                ad = cursor.getString(cursor.getColumnIndex("ad"));
            }
            cursor.close();
            vt.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ad;
    }

    public String GetMail(long uye_id){
        String mail = "";
        try{
            SQLiteDatabase vt = this.getReadableDatabase();
            Cursor cursor = vt.rawQuery("SELECT mail FROM uyeler WHERE id="+uye_id,null);
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                mail = cursor.getString(cursor.getColumnIndex("mail"));
            }
            cursor.close();
            vt.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return mail;
    }

    public int GonderiSayisi(long uyeId){
        int gonderiSayisi = 0;

        try{
            SQLiteDatabase vt = this.getReadableDatabase();
            Cursor cursor = vt.rawQuery("SELECT COUNT(*) FROM gonderiler WHERE uye_id="+uyeId,null);
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                gonderiSayisi= cursor.getInt(0);
            }
            cursor.close();
            vt.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return gonderiSayisi;
    }

    public long GonderiEkle(long uyeId, String gonderiBaslik, String gonderiSirasi){
        long id=-1;
        Date simdi = Calendar.getInstance().getTime();
        long timeStamp = donusturTimeStamp(simdi);
        try{
            SQLiteDatabase vt = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("uye_id",uyeId);
            cv.put("g_baslik",gonderiBaslik);
            cv.put("g_sirasi",gonderiSirasi);
            cv.put("tarih",timeStamp);
            id=vt.insert("gonderiler",null,cv);
            vt.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return id;
    }

    public void YaziEkle(long gonderiId, int sira, String yazi,int gravity,String font,long alinti){

        ContentValues cv = new ContentValues();
        try{
            SQLiteDatabase vt = this.getWritableDatabase();

            cv.put("g_id",gonderiId);
            cv.put("sira",sira);
            cv.put("gravity",gravity);
            cv.put("font",font);
            cv.put("yazi", yazi);
            cv.put("alinti",alinti);
            vt.insert("yazilar",null,cv);
            vt.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void ResimEkle(long gonderiId, int sira, Bitmap resim){
        byte[] resimVerisi = bitmapByteArrayCevir(resim);
        try{
            SQLiteDatabase vt = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("g_id",gonderiId);
            cv.put("sira",sira);
            cv.put("resim",resimVerisi);
            vt.insert("resimler",null,cv);
            vt.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static CharSequence bosluklariSil(CharSequence source) {

        if(source == null)
            return "";

        int i = source.length();

        while(--i >= 0 && Character.isWhitespace(source.charAt(i))) {
        }

        return source.subSequence(0, i+1);
    }

    public int GravityGetir(long gonderiId, int sira){
        int tmp = -1;
        try{
            SQLiteDatabase vt = this.getReadableDatabase();
            Cursor cursor = vt.rawQuery("SELECT gravity FROM yazilar WHERE g_id="+gonderiId+" AND sira="+sira,null);
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                tmp = cursor.getInt(cursor.getColumnIndex("gravity"));
            }
            cursor.close();
            vt.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return tmp;
    }

    public SpannableString YaziGetir(long gonderiId, int sira){
        SpannableString ss = null;
        String[] fonts;
        String[] tip;
        try{
            SQLiteDatabase vt = this.getReadableDatabase();
            Cursor cursor = vt.rawQuery("SELECT font,yazi FROM yazilar WHERE g_id="+gonderiId+" AND sira="+sira,null);
            cursor.moveToFirst();
            if(cursor.getCount() > 0){
                ss = new SpannableString(Html.fromHtml(cursor.getString(cursor.getColumnIndex("yazi"))));
                fonts = cursor.getString(cursor.getColumnIndex("font")).split(" ");

                for (String font : fonts) {
                    tip = font.split("/");
                    try{
                        Typeface typeface = Typeface.createFromFile(DosyaIslemleri.fontGetir(new File(context.getFilesDir().getPath(), "fonts"), Integer.parseInt(tip[2])));
                        ss.setSpan(new CustomTypefaceSpan(typeface), Integer.parseInt(tip[0]), Integer.parseInt(tip[1]), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    }catch (Exception e){

                    }
                }
            }

            vt.close();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return ss;
    }

    public Bitmap ResimGetir(long gonderiId, int sira){
        Bitmap bitmap = null;

        try{
            SQLiteDatabase vt = this.getReadableDatabase();
            Cursor cursor = vt.rawQuery("SELECT resim FROM resimler WHERE g_id="+gonderiId+" AND sira="+sira,null);
            cursor.moveToFirst();
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                bitmap = byteArrayBitmapCevir(cursor.getBlob(cursor.getColumnIndex("resim")));
            }
            cursor.close();
            vt.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    public int OkumaHesaplama(long id){
        StringBuilder yazi= new StringBuilder();
        try{
            SQLiteDatabase vt = this.getReadableDatabase();
            Cursor cursor = vt .rawQuery("SELECT yazi FROM yazilar WHERE g_id="+id,null);
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                while (!cursor.isAfterLast()){
                    yazi.append(cursor.getString(cursor.getColumnIndex("yazi"))).append(" ");
                    cursor.moveToNext();
                }
            }
            cursor.close();
            vt.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return (int)Math.ceil((double)yazi.toString().split(" ").length/190);
    }

    public LinearLayout DetayGetir(LinearLayout linearLayout, long id){
        String gonderiSirasi;
        boolean resimKontrol=true;
        try{
            SQLiteDatabase vt = this.getReadableDatabase();
            Cursor cursor = vt.rawQuery("SELECT g_sirasi FROM gonderiler WHERE id="+id,null);
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                gonderiSirasi = cursor.getString(cursor.getColumnIndex("g_sirasi"));
                for(int i=0;i<gonderiSirasi.length();i++){
                    if(gonderiSirasi.charAt(i)=='0'){//yazi
                        final TextView yazi = new TextView(context);
                        LinearLayout.LayoutParams textParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        textParam.setMargins(25,20,25,20);
                        yazi.setLayoutParams(textParam);
                        yazi.setTextAppearance(context, android.R.style.TextAppearance_Material);
                        Typeface typeface = ResourcesCompat.getFont(context, R.font.arch);
                        yazi.setTypeface(typeface);
                        yazi.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                        yazi.setGravity(GravityGetir(id,i));
                        yazi.setTextColor(context.getResources().getColor(R.color.yaziRengi));
                        yazi.setText(bosluklariSil(YaziGetir(id,i)));
                        yazi.setIncludeFontPadding(false);
                        yazi.setTextSize(16);
                        linearLayout.addView(yazi);
                    }else if(gonderiSirasi.charAt(i)=='1'){//resim
                        ImageView resim = new ImageView(context);
                        resim.setAdjustViewBounds(true);
                        resim.setImageBitmap(ResimGetir(id,i));
                        resim.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        linearLayout.addView(resim);
                        if(resimKontrol){
                            resim.setTransitionName("tResim");
                            resimKontrol=false;
                        }
                    }else if(gonderiSirasi.charAt(i)=='2'){//alntı
                        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View v = vi.inflate(R.layout.alinti_detay, null);
                        TextView alinti = v.findViewById(R.id.tv_alinti);
                        alinti.setText(bosluklariSil(YaziGetir(id,i)));
                        linearLayout.addView(v);
                    }
                }
                vt.close();
                cursor.close();
                linearLayout.getChildAt(0).setTransitionName("tIcerik");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return linearLayout;
    }

    public Gonderi GonderiGetir(long gonderiId){
        Gonderi gonderi = null;
        String gonderiSirasi;
        try{
            SQLiteDatabase vt = this.getReadableDatabase();
            Cursor cursor = vt.rawQuery("SELECT uye_id,g_baslik,g_sirasi,tarih FROM gonderiler WHERE id="+gonderiId+" ORDER BY id DESC",null);
            cursor.moveToFirst();
            if(cursor.getCount() > 0){

                gonderi = new Gonderi();
                gonderiSirasi = cursor.getString(cursor.getColumnIndex("g_sirasi"));
                int resimSira=0;
                for(int i=0;i<gonderiSirasi.length();i++){
                    if(gonderiSirasi.charAt(i)=='1'){
                        resimSira = i;
                        break;
                    }
                }
                if(gonderiSirasi.contains("1")){
                    gonderi.setResim(ResimGetir(gonderiId,resimSira));
                    gonderi.setResimVarMi(true);
                }
                gonderi.setGonderiId(gonderiId);
                gonderi.setYazar(GetUyeAdi(cursor.getLong(cursor.getColumnIndex("uye_id"))));
                gonderi.setBaslik(cursor.getString(cursor.getColumnIndex("g_baslik")));
                gonderi.setTarih(donusturDate(cursor.getLong(cursor.getColumnIndex("tarih"))));
                CharSequence icerik = bosluklariSil(YaziGetir(gonderiId,0));
                gonderi.setIcerik(icerik.toString().length() > 100 ? icerik.subSequence(0,100) : icerik);
                gonderi.setOkunmaSuresi(OkumaHesaplama(gonderiId));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return gonderi;
    }

    public long KayitKontrol(long uyeId,long gonderiId){
        long id=-1;
        try{
            SQLiteDatabase vt = this.getReadableDatabase();
            Cursor cursor = vt.rawQuery("SELECT g_id FROM kaydedilenler WHERE g_id = "+gonderiId+" AND uye_id="+uyeId,null);
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                id = cursor.getLong(cursor.getColumnIndex("g_id"));
            }
            vt.close();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return id;
    }

    public void KaydedilenlerdenKaldir(long uyeId,long gonderiId){
        long id=0;
        try{
            SQLiteDatabase vt = this.getWritableDatabase();
            id = vt.delete("kaydedilenler","g_id=? AND uye_id=?",new String[]{gonderiId+"",uyeId+""});
            vt.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'kaydedilenler'"); // auto increment sıfırlama
            vt.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void KaydedilenlereEkle(long uyeId,long gonderiId){
        long id=0;
        try{
            SQLiteDatabase vt = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("g_id",gonderiId);
            cv.put("uye_id",uyeId);
            id = vt.insert("kaydedilenler",null,cv);
            vt.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<Gonderi> KayitliGonderileriGetir(long uyeId){
        ArrayList<Gonderi> gonderiler = new ArrayList<>();
        try{
            SQLiteDatabase vt = this.getReadableDatabase();
            Cursor cursor = vt.rawQuery("SELECT g_id FROM kaydedilenler WHERE uye_id="+uyeId+" ORDER BY id DESC",null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                gonderiler.add(GonderiGetir(cursor.getLong(cursor.getColumnIndex("g_id"))));
                cursor.moveToNext();
            }
            cursor.close();
            vt.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return gonderiler;
    }

    public ArrayList<Gonderi> GonderileriGetir(long uyeId){
        int gonderiId;
        String baslik,gonderiSirasi;

        ArrayList<Gonderi> gonderiList = new ArrayList<>();
        try{
            SQLiteDatabase vt = this.getReadableDatabase();
            Cursor cursor = vt.rawQuery("SELECT id,g_baslik,g_sirasi,tarih FROM gonderiler WHERE uye_id="+uyeId+" ORDER BY id DESC",null);
            cursor.moveToFirst();

            while(!cursor.isAfterLast()){

                Gonderi gonderi = new Gonderi();
                baslik = cursor.getString(cursor.getColumnIndex("g_baslik"));
                gonderiId = cursor.getInt(cursor.getColumnIndex("id"));
                gonderiSirasi = cursor.getString(cursor.getColumnIndex("g_sirasi"));
                int resimSira=0;
                for(int i=0;i<gonderiSirasi.length();i++){
                    if(gonderiSirasi.charAt(i)=='1'){
                        resimSira = i;
                        break;
                    }
                }
                if(gonderiSirasi.contains("1")){
                    gonderi.setResim(ResimGetir(gonderiId,resimSira));
                    gonderi.setResimVarMi(true);
                }
                gonderi.setGonderiId(gonderiId);
                gonderi.setYazar(GetUyeAdi(uyeId));
                gonderi.setBaslik(baslik);
                CharSequence icerik = bosluklariSil(YaziGetir(gonderiId,0));
                gonderi.setIcerik(icerik.toString().length() > 100 ? icerik.subSequence(0,100) : icerik);
                gonderi.setOkunmaSuresi(OkumaHesaplama(gonderiId));
                gonderi.setTarih(donusturDate(cursor.getLong(cursor.getColumnIndex("tarih"))));

                gonderiList.add(gonderi);
                cursor.moveToNext();
            }
            vt.close();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return gonderiList;
    }

    private static byte[] bitmapByteArrayCevir(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,0,outputStream);
        return outputStream.toByteArray();
    }

    private Bitmap byteArrayBitmapCevir(byte[] veri){
        return  BitmapFactory.decodeByteArray(veri, 0 ,veri.length);
    }
}
