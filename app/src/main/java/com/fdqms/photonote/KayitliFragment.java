package com.fdqms.photonote;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class KayitliFragment extends Fragment{

    private RecyclerView recyclerView;
    private ArrayList<Gonderi> gonderiler;
    private RvAdapter rvAdapter;
    private VeriTabani vt;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public KayitliFragment(){}

    public KayitliFragment(Activity activity){
        SharedPreferences preferences = activity.getSharedPreferences("uye", Context.MODE_PRIVATE);
        vt = new VeriTabani(activity.getApplicationContext());
        gonderiler = vt.KayitliGonderileriGetir(preferences.getLong("id", -1));
        rvAdapter = new RvAdapter(gonderiler, activity, R.layout.gonderi,2);
    }

    private void setRecyclerView(){
        final StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(rvAdapter);
    }

    public void GonderiSil(long id){
        for(int i=0;i<gonderiler.size();i++){
            if(id == gonderiler.get(i).getGonderiId()){
                gonderiler.remove(i);
                rvAdapter.notifyItemRemoved(i);
                break;
            }
        }
    }

    public void GonderiEkle(long id){
        gonderiler.add(0,vt.GonderiGetir(id));
        rvAdapter.notifyItemInserted(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kayitli, container, false);

        recyclerView = view.findViewById(R.id.rv_kayitli);

        setRecyclerView();

        return view;
    }
}