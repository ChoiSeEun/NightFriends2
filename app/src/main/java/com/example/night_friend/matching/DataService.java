package com.example.night_friend.matching;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class DataService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("onTaskRemoved - " , String.valueOf(rootIntent));
        // 여기에 필요한 코드를 추가한다.

        final userLocation userLocation = new userLocation();

        userLocation.user_Map(Constant.DELETE_URL,"test8", 0, 0,0,0);

        Log.e("DataService","성공");


        stopSelf();
    }
}