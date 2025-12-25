package com.fdqms.photonote;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class BlogFragment extends Fragment {
    private AdView mAdView;

    private VeriTabani vt;
    private RecyclerView recyclerView2;
    private RecyclerView recyclerView;
    private RvAdapter rvAdapter;
    private RvAdapter rvAdapter2;

    private TextView tumGonderiler;
    private boolean gonderiVarMi;

    private ArrayList<Gonderi> gonderiler;
    private ArrayList<Gonderi> resimliGonderiler;

    /*void seffaflikGuncelle(int deger){
        rvAdapter.seffaflikGuncelle(deger);
    }*/

    public void GonderiEkle(long id){
        Gonderi gonderi = vt.GonderiGetir(id);
        if(gonderi.resimVarMi()){
            resimliGonderiler.add(0,gonderi);
            rvAdapter2.notifyItemInserted(0);
        }else{
            gonderiler.add(0,gonderi);
            rvAdapter.notifyItemInserted(0);
            tumGonderiler.setVisibility(View.VISIBLE);
            gonderiVarMi = true;// başka bir gönderiye gidip geri geldiğinde düzgün gözükmesi için
        }
    }

    public BlogFragment(){

    }

    public BlogFragment(Activity activity) {

        SharedPreferences preferences = activity.getSharedPreferences("uye", Context.MODE_PRIVATE);

        resimliGonderiler = new ArrayList<>();
        gonderiler = new ArrayList<>();

        vt = new VeriTabani(activity.getApplicationContext());
        ArrayList<Gonderi> tmpGonderiler = vt.GonderileriGetir(preferences.getLong("id", -1));

        for(int i = 0; i< tmpGonderiler.size(); i++){
            if(!tmpGonderiler.get(i).resimVarMi()){
                gonderiVarMi = true;
                break;
            }
        }

        for(int i = 0; i< tmpGonderiler.size(); i++){
            if(tmpGonderiler.get(i).resimVarMi()){
                resimliGonderiler.add(0,tmpGonderiler.get(i));
            }else{
                gonderiler.add(0,tmpGonderiler.get(i));
            }
        }

        rvAdapter = new RvAdapter(gonderiler, activity, R.layout.gonderi,0);
        rvAdapter2 = new RvAdapter(resimliGonderiler, activity, R.layout.gonderi_slide,1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_blog, container, false);

        tumGonderiler = view.findViewById(R.id.tv_tum_gonderiler);

        if(gonderiVarMi){
            tumGonderiler.setVisibility(View.VISIBLE);
        }

        if (!BuildConfig.DEBUG) {
            mAdView = view.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView2 = view.findViewById(R.id.recycler_view_2);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(rvAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView2.setNestedScrollingEnabled(false);
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.setAdapter(rvAdapter2);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView2);

        return view;
    }

}