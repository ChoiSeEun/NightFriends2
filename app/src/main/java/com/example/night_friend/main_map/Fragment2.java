package com.example.night_friend.main_map;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.example.night_friend.R;

import static com.example.night_friend.main_map.Fragment1.REQUEST_CODE_PERMISSIONS;

public class Fragment2 extends Fragment {
    private AlertDialog dialog;
    private Button bt_call2;
    private GpsInfo gps;
    private boolean isPermission = false;

    private String user = "test";

    private double userLat, userLon;

    Intent intent;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment2_guide, container, false);

        bt_call2=(Button)v.findViewById(R.id.bt_call2);
        final userLocation userLocation = new userLocation();

        //intent= new Intent(getContext(), Matching_map.class);

        getNowLocation();
        Log.e("getLocation", "Lat: " + userLat + "Lon: " + userLon);

        bt_call2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNowLocation();

                userLocation.user_Map("test3", userLat, userLon);

                intent.putExtra("userLat",userLat);
                intent.putExtra("userLon",userLon);

                startActivityForResult(intent,0);

                Log.e("bt_call", "Lat: " + userLat + "Lon: " + userLon);

                startActivity(intent);


            }
        });



        /*
        bt_call2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(),GuideActivity.class);
                //startActivity(intent);

                getNowLocation();

                Log.e("getLocation", "Lat: " + userLat + "Lon: " + userLon);

                Response.Listener<String> listener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // 서버 통신 성공 여부를 알려줌
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                Toast.makeText(getContext(), "매칭 등록에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                            } else { // 실패한 경우
                                Toast.makeText(getContext(), "매칭 등록에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                // 서버로 Volley를 이용해서 요청함.
                MatchingRequest matchingRequest = new MatchingRequest(user, userLat, userLon, listener);
                RequestQueue queue = Volley.newRequestQueue(getContext());
                queue.add(matchingRequest);
            }


    });

         */

        return v;
    }


    public void getNowLocation(){
        if(!isPermission){
            callPermission();
            return ;
        }

        gps = new GpsInfo(getContext());

        if(gps.isGetLocation()){
            Log.e("getLocation", "getLocation 요청");
            userLat = gps.getLat();
            userLon = gps.getLon();
        }
        else{
            // gps 사용 불가
            gps.showSettings();
        }
    }


    // 권한 요청
    public void callPermission() {
        // 권한 체크
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // 권한 요청 대화상자 표시
            ActivityCompat.requestPermissions((Activity) getContext(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSIONS);
            return;

        }
        else{
            isPermission = true;
        }

    }

}