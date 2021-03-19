package com.example.mp3player;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.CustomViewHolder> {
    private Context context;
    private ArrayList<MusicData> list;
    private OnItemClickListener mListener;

    public MusicAdapter(Context context, ArrayList<MusicData> list) {
        this.context = context;
        this.list = list;
    }

    //뷰홀더 정의(recyclerView에서 화면 뒤에 가려진 객체의 저장 및 재사용목적)
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private ImageView lm_ivAlbum;
        private TextView lm_tvTitle, lm_tvArtist, lm_tvDuration;
        private ProgressBar lm_pb;
        private ImageButton lm_imgBtn;

        public CustomViewHolder(@NonNull View itemView) {
            //뷰홀더에 홀딩할 데이터
            super(itemView);
            lm_ivAlbum = itemView.findViewById(R.id.lm_ivAlbum);
            lm_tvTitle = itemView.findViewById(R.id.lm_tvTitle);
            lm_tvArtist = itemView.findViewById(R.id.lm_tvArtist);
            lm_tvDuration = itemView.findViewById(R.id.lm_tvDuration);
            lm_pb = itemView.findViewById(R.id.lm_pb);
            lm_imgBtn = itemView.findViewById(R.id.lm_imgBtn);

            itemView.setOnClickListener(v-> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(v, position);
                }
            });
        }

    }

    //뷰홀더 안의 객체 생성
    @NonNull
    @Override
    public MusicAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_item, parent,false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    //뷰홀더의 position번째 객체에 대한 정의
    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.CustomViewHolder viewHolder, int position) {
        //앨범 이미지를 먼저 가져와서 아이템에 배치
        Bitmap albumImg = getAlbumImg(context, Long.parseLong(list.get(position).getAlbumArt()),200);
        if(albumImg != null) {
            viewHolder.lm_ivAlbum.setImageBitmap(albumImg);
        }

        //아이템(뷰홀더 안의 객체) 나머지 데이터 배치
        SimpleDateFormat sdf = new SimpleDateFormat("m:ss");
        viewHolder.lm_tvTitle.setText(list.get(position).getTitle());
        viewHolder.lm_tvArtist.setText(list.get(position).getArtist());
        viewHolder.lm_tvDuration.setText(sdf.format(Integer.parseInt(list.get(position).getDuration())));
    }


    //앨범의 이미지를 가져오는 메소드
    private Bitmap getAlbumImg(Context context, long albumArt, int maxSize) {
        // url -> 컨텐트 리졸버 -> 상대방 컨텐트 프로바이더
        BitmapFactory.Options options = new BitmapFactory.Options();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://media/external/audio/albumart/"+albumArt);

        if(uri != null) {
            ParcelFileDescriptor pfd = null;
            try {
                pfd = resolver.openFileDescriptor(uri,"r");

                //메모리할당을 막았다가 비트맵으로 전환할 때 할당 실시
                options.inJustDecodeBounds = true;
                int scale = 0;
                if(options.outHeight > maxSize || options.outWidth > maxSize) {
                    scale = (int)Math.pow(2,(int)Math.round(Math.log(maxSize /
                            (double)Math.max(options.outHeight,options.outWidth)) / Math.log(0.5)));
                }

                //메모리할당 실시
                options.inJustDecodeBounds = false;
                options.inSampleSize = scale;
                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(),null,options);

                //정의된 수치(maxSize)보다 크다면 조정하고 뷰에 표시
                if(bitmap != null) {
                    if(options.outWidth != maxSize || options.outHeight != maxSize){
                        Bitmap btm = Bitmap.createScaledBitmap(bitmap, maxSize, maxSize,true);
                        bitmap.recycle();
                        bitmap = btm;
                    }
                }

            } catch (FileNotFoundException e) {
                Log.d("Context Resolver","Resolver Error");
            }finally {
                try {
                    if(pfd != null)
                    pfd.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        //list 가 null이 아니면 list의 사이즈, null이면 0을 리턴
        return (list != null)?list.size():0;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener (OnItemClickListener listener) {
        this.mListener = listener;
    }

}
