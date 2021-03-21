package com.example.mp3player;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Fragment4 extends Fragment {
    private RecyclerView pCountRecycler;
    private MusicAdapter_top5 musicAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play_count_list,container,false);
        pCountRecycler = view.findViewById(R.id.pCountRecycler);

        //뷰에 어댑터 연결 및 설정
        adapterFunc();
        return view;
    }

    private void adapterFunc() {
        //번들을 받아서 처리
        musicAdapter = getArguments().getParcelable("Adapter");
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        pCountRecycler.setLayoutManager(linearLayoutManager);
        musicAdapter.notifyDataSetChanged();
        pCountRecycler.setAdapter(musicAdapter);
    }
}
