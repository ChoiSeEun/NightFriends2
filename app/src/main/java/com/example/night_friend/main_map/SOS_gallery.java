package com.example.night_friend.main_map;


import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ListView;
import android.widget.MediaController;

import androidx.appcompat.app.AppCompatActivity;

import com.example.night_friend.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class SOS_gallery extends AppCompatActivity {
    ListView listView;
    com.example.night_friend.main_map.GalleryListViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sosgallery);

        listView = (ListView)findViewById(R.id.listview);
        adapter = new com.example.night_friend.main_map.GalleryListViewAdapter();
        listView.setAdapter(adapter);
        //저장된 동영상 파일 이름 다 가져오기
        /*String path = Environment.getExternalStorageDirectory().getAbsolutePath()+
                File.separator+"List"+File.separator;
        File list = new File(path);
        String [] List = list.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                Boolean bOK = false;
                if(name.toLowerCase().endsWith(".mp4")) bOK =true;
                //if(name.toLowerCase().endsWith(".mp3")) bOK = true;
               return bOK;
            }
        });
        for (int i=0;i<List.length;i++) {
            try {
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(List[i], MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 360, 480);
                adapter.addItem(thumbnail, List[i], "road", "date");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }
}
