package com.example.mp3player;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ImageButton imgBtnDrawer, imgBtnFavor, imgBtnPrev, imgBtnPlay, imgBtnNext, lm_imgBtnBack, lm_imgBtn_search;
    private TextView tvTitle, tvArtist, tvStartTime, tvEndTime;
    private EditText edt_search;
    private SeekBar seekBar;
    private BottomNavigationView bottomMenu;
    private DrawerLayout drawer;
    private ImageView ivAlbum;
    //==================================================
    private MusicDBDAO musicDB;
    private MusicData musicData;
    private ArrayList<MusicData> musicList = new ArrayList<>();
    private ArrayList<MusicData> favorList = new ArrayList<>();
    private ArrayList<MusicData> pCountList = new ArrayList<>();
    private ArrayList<MusicData> pCountList5 = new ArrayList<>();
    private ArrayList<MusicData> searchList = new ArrayList<>();
    private MusicAdapter musicAdapter;
    private MusicAdapter musicAdapterFavor;
    private MusicAdapter musicAdapterPCount;
    private MusicAdapter_top5 musicAdapterPCount5;
    private MusicAdapter musicAdapterSearch;
    //==================================================
    private SimpleDateFormat sdf = new SimpleDateFormat("m:ss");
    private MediaPlayer mPlayer = new MediaPlayer();
    private boolean flag = false; // false = ?????? ????????????, true = ?????? ?????????
    private int index;
    private int type;
    private long backBtn = 0;
    private int maxIndex;
    private String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //id ??????
        findViewByIdFunc();

        //sd?????? ?????????????????? ??????
        requestPermissionsFunc();

        //db??????
        getMP3FileFunc();

        //????????? ??????
        getAdapterFunc();

        //????????????????????? ??????(layout.normal??? ????????????)
        firstFragmentReplace();

        //SeekBar?????? ?????????
        seekBarchangeFunc();

        //????????? -> ????????? ????????? ??????
        setPlayerData(0);
        mPlayer.pause();

        eventHandler();
    }

    private void eventHandler() {
        //left_mene??? ?????? ???????????? ???????????????
        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.sortName:
                        firstFragmentReplace();

                        lm_imgBtn_search.setVisibility(View.INVISIBLE);
                        edt_search.setVisibility(View.INVISIBLE);
                        break;

                    case R.id.sortFavor:
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        Fragment2 fragment2 = new Fragment2();
                        Bundle bundle = new Bundle(1);
                        bundle.putParcelable("Adapter", musicAdapterFavor);
                        fragment2.setArguments(bundle);
                        ft.replace(R.id.lm_frameLayout, fragment2);
                        ft.commit();

                        lm_imgBtn_search.setVisibility(View.INVISIBLE);
                        edt_search.setVisibility(View.INVISIBLE);
                        break;

                    case R.id.sortPCount:
                        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                        Fragment3 fragment3 = new Fragment3();
                        Bundle bundle1 = new Bundle(1);
                        bundle1.putParcelable("Adapter", musicAdapterPCount);
                        fragment3.setArguments(bundle1);
                        ft1.replace(R.id.lm_frameLayout, fragment3);
                        ft1.commit();

                        lm_imgBtn_search.setVisibility(View.INVISIBLE);
                        edt_search.setVisibility(View.INVISIBLE);
                        break;

                    case R.id.search:
                        FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                        Fragment5 fragment5 = new Fragment5();
                        Bundle bundle2 = new Bundle(1);
                        bundle2.putParcelable("Adapter", musicAdapterSearch);
                        fragment5.setArguments(bundle2);
                        ft2.replace(R.id.lm_frameLayout, fragment5);
                        ft2.commit();

                        lm_imgBtn_search.setVisibility(View.VISIBLE);
                        edt_search.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        //????????? top5 ??????????????? ????????? ??????
        musicAdapterPCount5.setOnItemClickListener((view, position) -> {
            type = 3;
            setPlayerData(position);
            drawer.closeDrawer(GravityCompat.START);
        });


        //?????? ?????????????????? ??????????????? ????????? ??????
        musicAdapter.setOnItemClickListener((view, position) -> {
            //player ???????????????????????? ?????? ????????????
            type = 0;
            setPlayerData(position);
            drawer.closeDrawer(GravityCompat.START);
        });

        //?????? ?????????????????? ??????????????? ????????? ??????
        musicAdapterFavor.setOnItemClickListener((view, position) -> {
            //player ???????????????????????? ?????? ????????????
            type = 1;
            setPlayerData(position);
            drawer.closeDrawer(GravityCompat.START);
        });

        //????????? ?????????????????? ??????????????? ????????? ??????
        musicAdapterPCount.setOnItemClickListener((view, position) -> {
            //player ???????????????????????? ?????? ????????????
            type = 2;
            setPlayerData(position);
            drawer.closeDrawer(GravityCompat.START);

        });

        imgBtnPrev.setOnClickListener(v -> {
            mPlayer.stop();
            mPlayer.reset();
            if (index == 0) {
                Toast.makeText(this, "?????????????????? ??????????????????", Toast.LENGTH_SHORT).show();
                setPlayerData(index);
            } else {
                index--;
                setPlayerData(index);
            }
        });

        imgBtnPlay.setOnClickListener(v -> {
            if (flag) {
                mPlayer.pause();
                imgBtnPlay.setImageResource(R.drawable.ic_baseline_play_circle_outline_60);
                flag = false;
            } else {
                mPlayer.start();
                imgBtnPlay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_60);
                flag = true;
                setSeekbarThread();
            }
        });

        imgBtnNext.setOnClickListener(v -> {
            mPlayer.stop();
            mPlayer.reset();
            if (index == maxIndex-1) {
                Toast.makeText(this, "?????????????????? ??????????????????", Toast.LENGTH_SHORT).show();
                index--;
            }
            index++;
            setPlayerData(index);


        });

        imgBtnFavor.setOnClickListener(v -> {
            if (musicData == null) {
                Toast.makeText(this, "????????? ???????????? ??????", Toast.LENGTH_SHORT).show();
            }

            if (musicData.getFavor() == 1) {
                musicData.setFavor(0);
                imgBtnFavor.setImageResource(R.drawable.ic_baseline_star_outline_40);
                favorList.remove(musicData);
                musicAdapterFavor.notifyDataSetChanged();
            } else {
                musicData.setFavor(1);
                imgBtnFavor.setImageResource(R.drawable.ic_baseline_star_40);
                favorList.add(musicData);
                musicAdapterFavor.notifyDataSetChanged();
            }
        });

        lm_imgBtn_search.setOnClickListener(v -> {
            str = edt_search.getText().toString();
            searchList = musicDB.selectMusicTBLLike(str);
            musicAdapterSearch = new MusicAdapter(getApplicationContext(), searchList);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment5 fragment5 = new Fragment5();
            Bundle bundle1 = new Bundle(1);
            bundle1.putParcelable("Adapter", musicAdapterSearch);
            fragment5.setArguments(bundle1);
            ft.replace(R.id.lm_frameLayout, fragment5);
            ft.commit();

            musicAdapterSearch.setOnItemClickListener((view, position) -> {
                type = 4;
                setPlayerData(position);
                drawer.closeDrawer(GravityCompat.START);
            });
        });

        imgBtnDrawer.setOnClickListener(v -> {
            drawer.openDrawer(GravityCompat.START);
        });

        lm_imgBtnBack.setOnClickListener(v -> {
            drawer.closeDrawer(GravityCompat.START);
        });


    }


    private void seekBarchangeFunc() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //???????????? mPlayer??? seekBar??? ????????? ??????
                if (fromUser) {
                    mPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void getAdapterFunc() {
        musicAdapter = new MusicAdapter(getApplicationContext(), musicList);
        musicAdapterFavor = new MusicAdapter(getApplicationContext(), favorList);
        musicAdapterPCount = new MusicAdapter(getApplicationContext(), pCountList);
        musicAdapterPCount5 = new MusicAdapter_top5(getApplicationContext(), pCountList5);
        musicAdapterSearch = new MusicAdapter(getApplicationContext(), searchList);
    }

    private void getMP3FileFunc() {
        musicDB = musicDB.getInstance(getApplication());

        //?????????????????? ??????
        musicList = musicDB.dbMatchToSdCard();

        //db?????????
        boolean returnValue = musicDB.insertMusicTBL(musicList);
        if (returnValue) {
            Log.d("music DB", "db?????? ??????");
        } else {
            Log.d("music DB", "db?????? ??????");
        }

        //?????????????????? ?????? ??????
        favorList = musicDB.selectMusicTBLFavor();
        //????????? ?????? ??????
        pCountList = musicDB.selectMusicTBLPlayCount();
        //????????? ?????? ??????(top5)
        pCountList5 = musicDB.selectMusicTBLPlayCountTop5();
        //?????? ?????? ??????
        searchList = musicDB.selectMusicTBLLike(str);
    }

    private void firstFragmentReplace() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment1 fragment1 = new Fragment1();
        Fragment4 fragment4 = new Fragment4();

        Bundle bundle = new Bundle(1);
        bundle.putParcelable("Adapter", musicAdapter);
        Bundle bundle1 = new Bundle(1);
        bundle1.putParcelable("Adapter", musicAdapterPCount5);

        fragment1.setArguments(bundle);
        fragment4.setArguments(bundle1);
        ft.replace(R.id.lm_frameLayout, fragment1);
        ft.replace(R.id.lm_frameLayout_pCount, fragment4);
        ft.commit();
    }


    private void requestPermissionsFunc() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MODE_PRIVATE);
    }

    private void findViewByIdFunc() {
        imgBtnDrawer = findViewById(R.id.imgBtnDrawer);
        imgBtnFavor = findViewById(R.id.imgBtnFavor);
        imgBtnPrev = findViewById(R.id.imgBtnPrev);
        imgBtnPlay = findViewById(R.id.imgBtnPlay);
        imgBtnNext = findViewById(R.id.imgBtnNext);
        lm_imgBtnBack = findViewById(R.id.lm_imgBtnBack);
        lm_imgBtn_search = findViewById(R.id.lm_imgBtn_search);
        edt_search = findViewById(R.id.edt_search);
        ivAlbum = findViewById(R.id.ivAlbum);
        bottomMenu = findViewById(R.id.bottomMenu);
        tvTitle = findViewById(R.id.tvTitle);
        tvArtist = findViewById(R.id.tvArtist);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        seekBar = findViewById(R.id.seekBar);
        drawer = findViewById(R.id.drawer);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    //SeekBar ???????????????
    private void setSeekbarThread() {
        Thread thread1 = new Thread(() -> {
            //????????? ??? ????????????
            while (mPlayer.isPlaying()) {
                seekBar.setProgress(mPlayer.getCurrentPosition());
                runOnUiThread(() -> {
                    tvStartTime.setText(sdf.format(mPlayer.getCurrentPosition()));
                });
                SystemClock.sleep(100);
            }
        });
        thread1.start();
    }

    //Player????????? ??? ??????
    public void setPlayerData(int position) {
        index = position;

        mPlayer.stop();
        mPlayer.reset();

        //1-> normal, 2-> ??????????????????, 3-> ?????????????????????
        switch (type) {
            case 0:
                musicData = musicList.get(position);
                maxIndex = musicList.size();
                break;
            case 1:
                musicData = favorList.get(position);
                maxIndex = favorList.size();
                break;
            case 2:
                musicData = pCountList.get(position);
                maxIndex = pCountList.size();
                break;
            case 3:
                musicData = pCountList5.get(position);
                maxIndex = pCountList5.size();
                break;
            case 4:
                musicData = searchList.get(position);
                maxIndex = searchList.size();
                break;
        }

        tvTitle.setText(musicData.getTitle());
        tvArtist.setText(musicData.getArtist());
        tvEndTime.setText(sdf.format(Integer.parseInt(musicData.getDuration())));

        //????????? ?????? ??????
        if (musicData.getFavor() == 1) {
            imgBtnFavor.setImageResource(R.drawable.ic_baseline_star_40);
        } else {
            imgBtnFavor.setImageResource(R.drawable.ic_baseline_star_outline_40);
        }

        //?????? ????????? ????????????
        Bitmap albumImg = musicAdapter.getAlbumImg(getApplicationContext(), Long.parseLong(musicData.getAlbumArt()), 200);
        if (albumImg != null) {
            ivAlbum.setImageBitmap(albumImg);
        } else {
            ivAlbum.setImageResource(R.drawable.ic_baseline_sms_failed);
        }

        //????????????
        Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicData.getId());
        try {
            mPlayer.setDataSource(MainActivity.this, uri);
            mPlayer.prepare();
            mPlayer.start();
            imgBtnPlay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_60);
            flag = true;
            musicData.setPlayCount(musicData.getPlayCount() + 1);
            seekBar.setProgress(0);
            seekBar.setMax(Integer.parseInt(musicData.getDuration()));

            setSeekbarThread();

            //???????????? ?????????
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    imgBtnNext.callOnClick();
                }
            });
        } catch (IOException e) {
            Log.e("uri", "uri Error");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        boolean returnValue = musicDB.updateMusicTBL(musicList);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        if (System.currentTimeMillis() > backBtn + 2000) {
            backBtn = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "?????? ??? ????????? ???????????????", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() <= backBtn + 2000) {
            finish();
        }
    }

}