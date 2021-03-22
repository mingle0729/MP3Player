package com.example.mp3player;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Fragment5 extends Fragment {
    private ImageButton lm_imgBtn_search;
    private EditText edt_search;
    private RecyclerView recyclerSearch;
    private MusicAdapter musicAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search,container,false);

        recyclerSearch = view.findViewById(R.id.recyclerSearch);
        lm_imgBtn_search = view.findViewById(R.id.lm_imgBtn_search);
        edt_search = view.findViewById(R.id.edt_search);

        //뷰에 어댑터 연결 및 설정
        adapterFunc();


        return view;
    }


    private void adapterFunc() {
        //번들을 받아서 처리
        musicAdapter = getArguments().getParcelable("Adapter");
        linearLayoutManager = new LinearLayoutManager(context);
        recyclerSearch.setLayoutManager(linearLayoutManager);
        musicAdapter.notifyDataSetChanged();
        recyclerSearch.setAdapter(musicAdapter);
    }
}
