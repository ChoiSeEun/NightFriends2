package com.example.night_friend.main_map;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.night_friend.R;

import java.util.ArrayList;

public class GalleryListViewAdapter extends BaseAdapter {
    public ArrayList<com.example.night_friend.main_map.GalleryListViewItem> items = new ArrayList<com.example.night_friend.main_map.GalleryListViewItem>();
    @Override
    public int getCount() {
        return items.size();
    }
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sosgallery_item,parent,false);
        }
        //  VideoView videoView = (VideoView)convertView.findViewById(R.id.galleryvideo);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.videoImage);
        TextView roadtext = (TextView)convertView.findViewById(R.id.roadtext);
        TextView datetext = (TextView)convertView.findViewById(R.id.datetext);

        com.example.night_friend.main_map.GalleryListViewItem item = items.get(position);
        //videoView.setVideoPath(item.getVideoPath());
        imageView.setImageBitmap(item.getBitmap());
        roadtext.setText(item.getRoadtext());
        datetext.setText(item.getDatetext());

        //비디오 재생
        LinearLayout clickArea = (LinearLayout)convertView.findViewById(R.id.videoclick);
        clickArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(this,VideoplayActivity.class));
            }
        });
        return convertView;
    }
    public void addItem(Bitmap bitmap, String path, String road, String date){
        com.example.night_friend.main_map.GalleryListViewItem item = new com.example.night_friend.main_map.GalleryListViewItem();
        //  item.setVideoPath(videopath,mc);
        item.setImage(bitmap,path);
        item.setRoadtext(road);
        item.setDatetext(date);
    }
}
