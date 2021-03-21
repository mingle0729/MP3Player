package com.example.mp3player;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Fragment1 extends Fragment {
    private RecyclerView recycler;
    private MusicAdapter musicAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Context context;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.normal,container,false);

        recycler = view.findViewById(R.id.recycler);

        //뷰에 어댑터 연결 및 설정
        adapterFunc();

        return view;
    }


    private void adapterFunc() {
        //번들을 받아서 처리
        musicAdapter = getArguments().getParcelable("Adapter");
        linearLayoutManager = new LinearLayoutManager(context);
        recycler.setLayoutManager(linearLayoutManager);
        musicAdapter.notifyDataSetChanged();
        recycler.setAdapter(musicAdapter);
    }
}
