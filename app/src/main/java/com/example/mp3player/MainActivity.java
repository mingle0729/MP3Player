package com.example.mp3player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton imgBtnDrawer, imgBtnFavor, imgBtnPrev, imgBtnPlay, imgBtnNext;
    private FrameLayout frameLayout;
    private TextView tvTitle, tvArtist, tvStartTime, tvEndTime;
    private SeekBar seekBar;
    //==================================================
    private static final String MP3PATH = Environment.getExternalStorageDirectory().getPath() + "/";
    private MediaPlayer mPlayer;
    private ArrayList<String> musicList = new ArrayList<>();
    private ArrayList<MusicData> sdCardList = new ArrayList<>();
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

        //sd카드 경로에서 mp3파일의 이름을 수집(좌측메뉴 리스트 등록용)
        findMP3FileName();

        //sd카드 경로에서 MusicData 클래스에 맞게 파일 추출(contentProvider)
//        contentProviderMP3();

    }

//    private void contentProviderMP3() {
//        String[] data = {
//                MediaStore.Audio.Media._ID,
//                MediaStore.Audio.Media.ARTIST,
//                MediaStore.Audio.Media.TITLE,
//                MediaStore.Audio.Media.ALBUM_ID,
//                MediaStore.Audio.Media.DURATION,
//                MediaStore.Audio.Media._COUNT
//        };
//
//        //커서 = title의 asc순 쿼리문
//        Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                data,null,null,data[2]+" ASC");
//
//        if(cursor != null) {
//            while(cursor.moveToNext()) {
//                String id = cursor.getString(cursor.getColumnIndex(data[0]));
//                String artist = cursor.getString(cursor.getColumnIndex(data[1]));
//                String title = cursor.getString(cursor.getColumnIndex(data[2]));
//                String albumArt = cursor.getString(cursor.getColumnIndex(data[3]));
//                String duration = cursor.getString(cursor.getColumnIndex(data[4]));
//
//            }
//        }
//    }

    private void findMP3FileName() {
        File[] nameList = new File(MP3PATH).listFiles();
        for (File f : nameList) {
            if(f.getName().substring(f.getName().length()-3).equals("mp3")) {
                musicList.add(f.getName());
            }
        }
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
    }
}