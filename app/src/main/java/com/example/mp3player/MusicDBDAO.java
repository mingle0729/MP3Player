package com.example.mp3player;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MusicDBDAO extends SQLiteOpenHelper {
    private final static String DB_NAME = "musicDB";
    private final static int VERSION = 1;
    private Context context;

    //싱글톤방식(DB는 쓸 때 마다 객체를 만들기보다, 한번만 만들어두고 쓰는게 맞다)
    private static MusicDBDAO musicDBDAO;

    public MusicDBDAO(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
    }

    public static MusicDBDAO getInstance(Context context) {
        if (musicDBDAO == null) {
            musicDBDAO = new MusicDBDAO(context);
        }
        return musicDBDAO;
    }

    //테이블 생성
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table musicTBL(" +
                        "id varhar(20) not null primary key," +
                        "artist varchar(10) not null," +
                        "title varchar(10) not null," +
                        "artAlbum varchar(20)," +
                        "duration varchar(10) not null," +
                        "playCount int default 0 not null," +
                        "favor int not null)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists musicTBL");
        onCreate(db);
    }


    //insert(이미 있는 자료이면 포함시키지 않을 것)
    public boolean insertMusicTBL(ArrayList<MusicData> list) {
        boolean returnValue = false;
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            for (MusicData m : list) {
                //db에서 전체 조회 후 비교
                ArrayList<MusicData> musicList = selectMusicTBL();

                if (musicList.contains(m) == false) {
                    String query = "insert into musicTBL values(" +
                            "'" + m.getId() + "'," +
                            "'" + m.getArtist() + "'," +
                            "'" + m.getTitle() + "'," +
                            "'" + m.getAlbumArt() + "'," +
                            "'" + m.getDuration() + "'," + 0 + "," + 0 + ");";

                    db.execSQL(query);
                    returnValue = true;
                }

            }
        } catch (Exception e) {
            Log.d("insert DB", "이미 값이 있음");
            returnValue = false;
        }

        return returnValue;
    }

    //db 내용 update(선호, 조회수)
    public boolean updateMusicTBL(ArrayList<MusicData> list) {
        boolean returnValue = false;
        SQLiteDatabase db = getWritableDatabase();

        try {
            for (MusicData mData : list) {
                String query = "update musicTBL set playCount = " + mData.getPlayCount() + ", favor = " + mData.getFavor() + " where id = " + mData.getId() + ";";
                db.execSQL(query);
                returnValue = true;
                Log.d("updateDB", "업데이트성공");
            }
        } catch (Exception e) {
            returnValue = false;
            Log.d("updateDB", "업데이트실패");
        }

        return returnValue;
    }

    //sd카드안의 mp3파일 추출(이후 db와 비교할것임)
    public ArrayList<MusicData> sdCardListAdd() {
        ArrayList<MusicData> list = new ArrayList<>();

        String[] data = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION
        };

        //커서 = title의 asc순 쿼리문
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                data, null, null, data[2] + " ASC");

        if (cursor != null) {

            try {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex(data[0]));
                    String artist = cursor.getString(cursor.getColumnIndex(data[1]));
                    String title = cursor.getString(cursor.getColumnIndex(data[2]));
                    String albumArt = cursor.getString(cursor.getColumnIndex(data[3]));
                    String duration = cursor.getString(cursor.getColumnIndex(data[4]));

                    MusicData mData = new MusicData(id, artist, title, albumArt, duration, 0, 0);
                    list.add(mData);
                }
            } catch (Exception e) {
                Log.e("sdCardListAdd", "cursor Error");
            }
        }

        return list;
    }

    //select(이후 sdCardList와 비교대상임)
    public ArrayList<MusicData> selectMusicTBL() {
        ArrayList<MusicData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from musicTBL;", null);

        try {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String artist = cursor.getString(1);
                String title = cursor.getString(2);
                String albumArt = cursor.getString(3);
                String duration = cursor.getString(4);
                int playCount = cursor.getInt(5);
                int favor = cursor.getInt(6);

                MusicData musicData = new MusicData(id, artist, title, albumArt, duration, playCount, favor);
                list.add(musicData);
            }
        } catch (Exception e) {
            Log.e("selectMusicTBL", "select Error");
        }

        return list;
    }

    //좋아요 리스트 select
    public ArrayList<MusicData> selectMusicTBLFavor() {
        ArrayList<MusicData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from musicTBL where favor = 1;", null);

        try {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String artist = cursor.getString(1);
                String title = cursor.getString(2);
                String albumArt = cursor.getString(3);
                String duration = cursor.getString(4);
                int playCount = cursor.getInt(5);
                int favor = cursor.getInt(6);

                MusicData musicData = new MusicData(id, artist, title, albumArt, duration, playCount, favor);
                list.add(musicData);
            }
        } catch (Exception e) {
            Log.e("selectMusicTBLFavor", "select Error(favor)");
        }
        return list;
    }

    //조회순 select
    public ArrayList<MusicData> selectMusicTBLPlayCount() {
        ArrayList<MusicData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from musicTBL order by playCount desc;", null);

        try {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String artist = cursor.getString(1);
                String title = cursor.getString(2);
                String albumArt = cursor.getString(3);
                String duration = cursor.getString(4);
                int playCount = cursor.getInt(5);
                int favor = cursor.getInt(6);

                MusicData musicData = new MusicData(id, artist, title, albumArt, duration, playCount, favor);
                list.add(musicData);
            }

        } catch (Exception e) {
            Log.e("selectMusicTBLPCount", "select Error(playcount)");
        }

        return list;
    }

    //sdCardListAdd 와 db의 select를 비교하여 db에 없는 내용을 db에 추가
    public ArrayList<MusicData> dbMatchToSdCard() {
        //db
        ArrayList<MusicData> dbList = selectMusicTBL();
        //sdcard
        ArrayList<MusicData> sdList = sdCardListAdd();

        //1. db가 비어있다면 sd카드내용을 그대로 리턴
        if (dbList == null) {
            return sdList;
        }

        //2. db가 sd카드의 내용을 모두 가지고 있다면 db의 내용을 리턴
        if (dbList.containsAll(sdList)) {
            return dbList;
        }

        //3. db에 없는 내용이 sd카드에 존재할 경우 db에 추가
        for (int i = 0; i < sdList.size(); i++) {
            if (!dbList.contains(sdList.get(i))) {
                dbList.add(sdList.get(i));
            }
        }

        return dbList;
    }


    public ArrayList<MusicData> selectMusicTBLPlayCountTop5() {
        ArrayList<MusicData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from musicTBL order by playCount desc limit 5;", null);

        try {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String artist = cursor.getString(1);
                String title = cursor.getString(2);
                String albumArt = cursor.getString(3);
                String duration = cursor.getString(4);
                int playCount = cursor.getInt(5);
                int favor = cursor.getInt(6);

                MusicData musicData = new MusicData(id, artist, title, albumArt, duration, playCount, favor);
                list.add(musicData);
            }

        } catch (Exception e) {
            Log.e("selectMusicTBLPCount 5", "select Error(pcount5)");
        }

        return list;
    }

    public ArrayList<MusicData> selectMusicTBLLike(String str) {
        ArrayList<MusicData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from musicTBL where  artist like '%" + str + "%';", null);

        //title 검색
        try {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String artist = cursor.getString(1);
                String title = cursor.getString(2);
                String albumArt = cursor.getString(3);
                String duration = cursor.getString(4);
                int playCount = cursor.getInt(5);
                int favor = cursor.getInt(6);

                MusicData musicData = new MusicData(id, artist, title, albumArt, duration, playCount, favor);
                list.add(musicData);
            }

        } catch (Exception e) {
            Log.e("selectMusicTBL LIKE", "select Error(LIKE)");
        }

        //artist 검색
        try {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String artist = cursor.getString(1);
                String title = cursor.getString(2);
                String albumArt = cursor.getString(3);
                String duration = cursor.getString(4);
                int playCount = cursor.getInt(5);
                int favor = cursor.getInt(6);

                MusicData musicData = new MusicData(id, artist, title, albumArt, duration, playCount, favor);
                list.add(musicData);
            }

        } catch (Exception e) {
            Log.e("selectMusicTBL LIKE", "select Error(LIKE/artist)");
        }

        if(list == null) {
            Toast.makeText(context, "조회된 정보가 없습니다", Toast.LENGTH_SHORT).show();
        } else {
            return list;
        }

        return null;
    }
}
