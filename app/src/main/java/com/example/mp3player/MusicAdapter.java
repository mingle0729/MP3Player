package com.example.mp3player;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

                }
            });
        }

    }

    @NonNull
    @Override
    public MusicAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.CustomViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void OnItemClickListener (OnItemClickListener listener) {
        this.mListener = listener;
    }

}
