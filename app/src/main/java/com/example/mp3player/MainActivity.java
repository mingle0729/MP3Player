package com.example.mp3player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton imgBtnDrawer, imgBtnFavor, imgBtnPrev, imgBtnPlay, imgBtnNext;
    private FrameLayout frameLayout;
    private TextView tvTitle, tvArtist, tvStartTime, tvEndTime;
    private SeekBar seekBar;
    private RecyclerView recycler, recyclerFavor, recyclerPCount;
    private DrawerLayout drawer;
    //==================================================
    private MediaPlayer mPlayer;
    private MusicDBDAO musicDB;
    //==================================================
    private ArrayList<MusicData> musicList = new ArrayList<>();
    private ArrayList<MusicData> favorList = new ArrayList<>();
    private ArrayList<MusicData> pCountList = new ArrayList<>();
    //==================================================
    private LinearLayoutManager linearLayoutManager;
    private LinearLayoutManager linearLayoutManagerFavor;
    private LinearLayoutManager linearLayoutManagerPCount;
    private MusicAdapter musicAdapter;
    private MusicAdapter musicAdapterFavor;
    private MusicAdapter musicAdapterPCount;
    //==================================================
    private boolean flag = false;
    private String selectedID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //id 연결
        findViewByIdFunc();

        //sd카드 외부접근권한 허용
        requestPermissionsFunc();

        //db접근 및 mp3파일 추출, db 최신화
        getMP3FileFunc();

        //뷰에 어댑터 연결 및 설정
        adapterFunc();

        eventHandler();

    }

    private void eventHandler() {
        imgBtnDrawer.setOnClickListener(v-> {
            drawer.openDrawer(GravityCompat.START);
        });

        imgBtnFavor.setOnClickListener(v-> {

        });
    }

    private void adapterFunc() {
        musicAdapter = new MusicAdapter(getApplicationContext(),musicList);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(linearLayoutManager);
        musicAdapter.notifyDataSetChanged();
        recycler.setAdapter(musicAdapter);

        musicAdapterFavor = new MusicAdapter(getApplicationContext(),favorList);
        linearLayoutManagerFavor = new LinearLayoutManager(getApplicationContext());
        recyclerFavor.setLayoutManager(linearLayoutManagerFavor);
        musicAdapterFavor.notifyDataSetChanged();
        recyclerFavor.setAdapter(musicAdapterFavor);

        musicAdapterPCount = new MusicAdapter(getApplicationContext(),pCountList);
        linearLayoutManagerPCount = new LinearLayoutManager(getApplicationContext());
        recyclerPCount.setLayoutManager(linearLayoutManagerPCount);
        musicAdapterPCount.notifyDataSetChanged();
        recyclerPCount.setAdapter(musicAdapterPCount);
    }

    private void getMP3FileFunc() {
        //db 접근
        musicDB = musicDB.getInstance(getApplicationContext());

        //전체음악파일 추출
        musicList = musicDB.dbMatchToSdCard();

        //db최신화
        boolean returnValue = musicDB.insertMusicTBL(musicList);
        if(returnValue) {
            Log.d("music DB","db접근 성공");
        } else {
            Log.d("music DB","db접근 실패");
        }

        //좋아요체크된 파일 추출
        favorList = musicDB.selectMusicTBLFavor();
        //조회순 파일 추출
        pCountList = musicDB.selectMusicTBLPlayCount();
    }


    private void requestPermissionsFunc() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MODE_PRIVATE);
    }

    private void findViewByIdFunc() {
        toolbar = findViewById(R.id.toolbar);
        imgBtnDrawer = findViewById(R.id.imgBtnDrawer);
        imgBtnFavor = findViewById(R.id.imgBtnFavor);
        imgBtnPrev = findViewById(R.id.imgBtnPrev);
        imgBtnPlay = findViewById(R.id.imgBtnPlay);
        imgBtnNext = findViewById(R.id.imgBtnNext);
        frameLayout = findViewById(R.id.frameLayout);
        tvTitle = findViewById(R.id.tvTitle);
        tvArtist = findViewById(R.id.tvArtist);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        seekBar = findViewById(R.id.seekBar);
        recycler = findViewById(R.id.recycler);
        recyclerFavor = findViewById(R.id.recyclerFavor);
        recyclerPCount = findViewById(R.id.recyclerPCount);
        drawer = findViewById(R.id.drawer);
    }
}