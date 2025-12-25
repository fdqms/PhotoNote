package com.fdqms.photonote;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.ViewHolder> {

    public RvAdapter(ArrayList<Gonderi> gonderiler,Activity activity, int layoutId,int tip) {
        this.gonderiler = gonderiler;
        this.activity=activity;
        this.layoutId = layoutId;
        this.tip = tip;
        this.secenekler = activity.getSharedPreferences("secenekler", Context.MODE_PRIVATE);
        //this.seffaflik = 0x48000000;
        this.temaRengi = (Color.parseColor(secenekler.getString("temaRengi","#ffffff")) & 0x00FFFFFF) | 0x48000000;
    }

    /*
    public void seffaflikGuncelle(int deger){
        this.seffaflik = Integer.decode("0x"+hexDonustur(deger)+"000000");
        for (int i=0;i<viewHolders.size();i++){
            viewHolders.get(i).icerik.setTextColor(temaRengi);
            onViewRecycled(viewHolders.get(i));
        }
    }

    private String hexDonustur(int deger){
        int rem;
        String hex="";
        char hexchars[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        while(deger>0)
        {
            rem=deger%16;
            hex=hexchars[rem]+hex;
            deger=deger/16;
        }
        return hex;
    }
     */

    private ArrayList<Gonderi> gonderiler;
    private Activity activity;
    private int layoutId,tip;
    private boolean yatay;

    private int temaRengi;
    private SharedPreferences secenekler;

    @NonNull
    @Override
    public RvAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId,parent,false);
        if(layoutId == R.layout.gonderi){
            this.yatay = false;
            return new ViewHolder(itemView);
        }else{
            this.yatay = tip == 1;
            return new ViewHolder(itemView,yatay);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Gonderi gonderi = gonderiler.get(position);

        if(this.yatay){
            holder.baslik.setText(gonderi.getBaslik());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MMMM.yyyy", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            holder.tarih.setText(dateFormat.format(gonderi.getTarih()));
            holder.resim.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.resim.setAdjustViewBounds(true);
            holder.resim.setImageBitmap(gonderi.getResim());
            holder.resim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resimDetayaGit(view,holder,gonderi);
                }
            });
        }else{
            holder.baslik.setText(gonderi.getBaslik());
            holder.icerik.setTextSize(16);
            holder.sure.setText(gonderi.getOkunmaSuresi());
            holder.begeni.setText(gonderi.getBegeni());

            if(tip==0){
                holder.id.setText(sayiDonustur(position));
                holder.icerik.setText(gonderi.getIcerik());
                if(position%2 == 0){
                    holder.id.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                }else{
                    holder.id.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                }
                holder.id.setTextColor(temaRengi);
            }else{
                holder.id.setTextSize(0);
                ((RelativeLayout.LayoutParams)holder.baslik.getLayoutParams()).setMarginStart(0);
                ((RelativeLayout.LayoutParams)holder.baslik.getLayoutParams()).setMarginEnd(0);
                holder.icerik.setText(gonderi.getIcerik());
                ((RelativeLayout.LayoutParams)holder.icerik.getLayoutParams()).setMarginStart(0);
                ((RelativeLayout.LayoutParams)holder.icerik.getLayoutParams()).setMarginEnd(0);
                holder.icerik.requestLayout();
                ((RelativeLayout.LayoutParams)holder.sure.getLayoutParams()).setMarginStart(0);
                holder.sure.requestLayout();
            }

            holder.rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    detayaGit(holder,gonderi);
                }
            });

            //holder.rl.setAnimation(AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.fade_animation));

        }
    }

    private void detayaGit(ViewHolder holder,Gonderi gonderi){
        List<androidx.core.util.Pair<View, String>> pairs = new ArrayList<>();
        pairs.add(new androidx.core.util.Pair<View, String>(holder.baslik,"tBaslik"));
        pairs.add(new androidx.core.util.Pair<View, String>(holder.icerik,"tIcerik"));

        Intent i = new Intent(activity,BlogDetay.class);

        i.putExtra("baslik",holder.baslik.getText().toString());
        i.putExtra("gonderiId",gonderi.getGonderiId());

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs.get(0),pairs.get(1));
        activity.startActivityForResult(i,1,options.toBundle());
    }

    private void resimDetayaGit(View view,ViewHolder holder,Gonderi gonderi){
        Context context = view.getContext();

        List<Pair<View,String>> pairs = new ArrayList<>();

        pairs.add(new Pair<View, String>(holder.resim,"tResim"));
        pairs.add(new Pair<View, String>(holder.baslik,"tBaslik"));

        Intent i = new Intent(context,BlogDetay.class);

        i.putExtra("baslik",holder.baslik.getText());
        i.putExtra("gonderiId",gonderi.getGonderiId());

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity,pairs.get(0),pairs.get(1));
        activity.startActivityForResult(i,1,options.toBundle());
    }

    @Override
    public int getItemCount() {
        return gonderiler.size();
    }

    public String sayiDonustur(int sayi){
        sayi = sayi+1;
        if(sayi<10){
            return "0"+sayi;
        }
        return ""+sayi;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView resim;
        TextView id;
        TextView baslik;
        CustomTextView icerik;
        TextView sure;
        TextView begeni;
        RelativeLayout rl;
        TextView tarih;

        public ViewHolder(View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.tv_id);
            baslik = itemView.findViewById(R.id.tv_baslik);
            icerik = itemView.findViewById(R.id.tv_icerik);
            sure = itemView.findViewById(R.id.tv_sure);
            begeni = itemView.findViewById(R.id.tv_begeni);
            rl = itemView.findViewById(R.id.rl_gonderi);

        }

        public ViewHolder(View itemView, boolean yatay) {
            super(itemView);

            if(yatay){
                resim = itemView.findViewById(R.id.iv_resim);
                baslik = itemView.findViewById(R.id.tv_slide_baslik);
                tarih = itemView.findViewById(R.id.tv_tarih);
            }else{
                id = itemView.findViewById(R.id.tv_id);
                baslik = itemView.findViewById(R.id.tv_baslik);
                icerik = itemView.findViewById(R.id.tv_icerik);
                sure = itemView.findViewById(R.id.tv_sure);
                begeni = itemView.findViewById(R.id.tv_begeni);
                rl = itemView.findViewById(R.id.rl_gonderi);
            }
        }
    }
}
