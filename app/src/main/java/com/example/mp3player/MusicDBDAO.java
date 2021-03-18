package com.example.mp3player;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;
import android.util.Log;

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
        }finally {
            db.close();
        }

        return returnValue;
    }

    //db 내용 수정(선호, 조회수)
    public boolean updateMusicTBL(MusicData mData) {
        boolean returnValue = false;
        SQLiteDatabase db = getWritableDatabase();

        try {
            String query = "update musicTBL set playCount = " + mData.getPlayCount() + ", favor = " + mData.getFavor() + " where id = '" + mData.getId() + "');";
            db.execSQL(query);
            returnValue = true;
        } catch (Exception e) {
            returnValue = false;
        } finally {
            db.close();
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
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media._COUNT
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

                    MusicData mData = new MusicData(id, artist, title,albumArt, duration,0,0);
                    list.add(mData);
                }
            }catch(Exception e) {
                Log.e("sdCardListAdd", "cursor Error");
            }finally {
                cursor.close();
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
            while(cursor.moveToNext()) {
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
        }catch(Exception e) {
            Log.e("selectMusicTBL","select Error");
        }finally {
            db.close();
            cursor.close();
        }


        return list;
    }

    //좋아요 리스트 select
    public ArrayList<MusicData> selectMusicTBLFavor() {
        ArrayList<MusicData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from musicTBL where favor = 1;", null);

        try {
            while(cursor.moveToNext()) {
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
        }catch(Exception e) {
            Log.e("selectMusicTBLFavor","select Error(favor)");
        }finally {
            db.close();
            cursor.close();
        }
        return list;
    }

    //조회순 select
    public ArrayList<MusicData> selectMusicTBLPlayCount() {
        ArrayList<MusicData> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from musicTBL order by playCount asc;", null);

        try {
            while(cursor.moveToNext()) {
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

        }catch(Exception e) {
            Log.e("selectMusicTBLFavor","select Error(favor)");
        }finally {
            db.close();
            cursor.close();
        }

        return list;
    }

    //sdCardListAdd 와 db의 select를 비교하여 db에 없는 내용을 db에 추가
    public ArrayList<MusicData> dbMatchToSdCard() {
        //db
        ArrayList<MusicData> dbList = selectMusicTBL();
        return dbList; //수정할거임
    }

}