package com.example.unreallover.mediaplayer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.unreallover.mediaplayer.Entities.Music;
import com.example.unreallover.mediaplayer.MainActivity;
import com.example.unreallover.mediaplayer.R;
import com.example.unreallover.mediaplayer.Utils.MyApplication;

import java.io.Serializable;
import java.util.List;

import static com.example.unreallover.mediaplayer.Utils.Constant.SER_KEY;

/**
 * Created by Unreal Lover on 2017/11/1.
 */

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder>{
    private List<Music> mplaylist ;
    public PlayListAdapter( List<Music> mplaylist) {
        this.mplaylist = mplaylist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Music music = mplaylist.get(position);
        holder.artist.setText(music.getartist());
        holder.title.setText(music.gettitle());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
//                Music music = mplaylist.get(position);
                Intent intent = new Intent(MyApplication.getContext(), MainActivity.class);
                intent.putExtra("ID",position);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(SER_KEY, (Serializable) mplaylist);
                intent.putExtras(mBundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                MyApplication.getContext().startActivity(intent);
            }
        });
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mplaylist.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView artist;
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            artist = (TextView) itemView.findViewById(R.id.artist);
            cardView = (CardView) itemView;
//            cardView.setCardBackgroundColor(Integer.parseInt("#38A2EC"));
        }
    }

}
