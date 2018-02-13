package com.example.mypc.ghiamphone.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mypc.ghiamphone.R;
import com.example.mypc.ghiamphone.model.Music;

import java.util.List;

/**
 * Created by MyPC on 13/02/2018.
 */

public class ListMusicAdapter extends ArrayAdapter<Music> {
    private Context context;
    private int resource;
    private List<Music> list;

    public ListMusicAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Music> list) {
        super(context, resource, list);
        this.context = context;
        this.list = list;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView phone = (TextView) convertView.findViewById(R.id.phone_number);
        TextView path = (TextView) convertView.findViewById(R.id.path);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
        Music music = list.get(position);
        name.setText(music.getName());
        phone.setText(music.getPhoneNumber());
        path.setText(music.getPath());
        date.setText( DateUtils.formatDateTime(
                context,
                music.getDate().getTime(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
        ));

        if(music.getStatus().trim().equals("Incoming"))
        imageView.setImageResource(R.drawable.in);
        return convertView;
    }
}
