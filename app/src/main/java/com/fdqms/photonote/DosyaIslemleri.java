package com.fdqms.photonote;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class DosyaIslemleri {

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static void kopyala(File kaynak, File hedef) throws IOException {
        try (InputStream in = new FileInputStream(kaynak)) {
            try (OutputStream out = new FileOutputStream(hedef)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    public static List<String> fontlariListele(File kok){
        List<String> dosyalar = new ArrayList<>();
        File[] dosyaListesi = kok.listFiles();
        if(dosyaListesi != null){
            for(File dosya : dosyaListesi){
                if(dosya.isFile()){
                    dosyalar.add(dosya.getName());
                }
            }
            return dosyalar;
        }
        return null;
    }

    public static File fontGetir(File kok,int i){
        File[] dosyaListesi = kok.listFiles();
        assert dosyaListesi != null;
        return dosyaListesi[i];
    }
}
