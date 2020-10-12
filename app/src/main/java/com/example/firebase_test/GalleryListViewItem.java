package com.example.firebase_test;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

public class GalleryListViewItem {
    //public VideoView videoView;
    //public String videoPath;
    public ImageView image;
    public Bitmap bitmap;
    public String roadtext;
    public String datetext;
    public String videopath;

    /* public VideoView getVideoView(){
         return videoView;
     }*/
    /*public String getVideoPath(){
        return videoPath;
    }*/
    /*public void setVideoPath(String videoPath,MediaController mc){
        this.videoPath =  videoPath;
        videoView.setMediaController(mc);
        videoView.setVideoPath(videoPath);
        videoView.requestFocus();
        videoView.start();
    }*/
    public void setImage(Bitmap bitmap, String path){
        this.bitmap = bitmap;
        this.videopath = path;
        image.setImageBitmap(bitmap);
    }
    public Bitmap getBitmap(){
        return bitmap;
    }
    public String getRoadtext(){
        return roadtext;
    }
    public void setRoadtext(String roadtext){
        this.roadtext = roadtext;
    }
    public String getDatetext(){
        return datetext;
    }
    public void setDatetext(String datetext){
        this.datetext = datetext;
    }
}
