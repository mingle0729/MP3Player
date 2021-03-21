package com.example.mp3player;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Fragment2 extends Fragment {
    private RecyclerView recyclerFavor;
    private MusicAdapter musicAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favor,container,false);

        recyclerFavor = view.findViewById(R.id.recyclerFavor);

        //뷰에 어댑터 연결 및 설정
        adapterFunc();

        return view;
    }


    private void adapterFunc() {
        //번들을 받아서 처리
        musicAdapter = getArguments().getParcelable("Adapter");
        linearLayoutManager = new LinearLayoutManager(context);
        recyclerFavor.setLayoutManager(linearLayoutManager);
        musicAdapter.notifyDataSetChanged();
        recyclerFavor.setAdapter(musicAdapter);
    }
}
