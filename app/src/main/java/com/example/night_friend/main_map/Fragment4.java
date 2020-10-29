package com.example.night_friend.main_map;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.night_friend.R;
import com.example.night_friend.matching.BaseAdapterEx;
import com.example.night_friend.matching.ChatRoom_list;
import com.example.night_friend.matching.Constant;
import com.example.night_friend.matching.Locationitem;
import com.example.night_friend.matching.Matching_chat;
import com.example.night_friend.matching.Matching_map;
import com.example.night_friend.matching.Matching_model;
import com.example.night_friend.matching.Matching_setting;
import com.example.night_friend.matching.matching_data;
import com.example.night_friend.matching.safety_model;
import com.example.night_friend.matching.userLocation;
import com.example.night_friend.matching.userdistance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class Fragment4 extends Fragment implements View.OnClickListener {
    ListView mListView = null;
    BaseAdapterEx mAdapter = null;
    View v;
    Intent intent;
    private GetLocation gPHP;
    static public String userID;
    static int code;
    static public double userLat, userLon;
    static public double destLat, destLon;
    static private String destPlace, startPlace;
    String url = "http://night1234.dothome.co.kr/getLocation.php";
    Matching_model matchings;
    safety_model safety;

    ListView listview;
    private ArrayList<matching_data> personList= new ArrayList<matching_data>();
    static public ArrayList<userdistance> otherList= new ArrayList<userdistance>();

    private EditText et_dest,et_start;
    private Button bt_matching_search,chat_room;
    private Button bt_matching_cancel,bt_matching_map;


    public static Fragment4 newInstance(String place, Integer code, Double lat, Double lon){
        final Fragment4 fragment4 = new Fragment4();

        Log.e("받아온 이름: ",place);
        Log.e("코드/위경도: ", code+" / "+lat+", "+lon);

        if(code==105){
            destPlace = place;
            destLat = lat;
            destLon = lon;

            Log.e("도착 위치 설정: ",destPlace);
            //Log.e("도착 위치: ", endName);
        }

        else if(code==106){
            startPlace = place;
            userLat = lat;
            userLon = lon;

            Log.e("출발 위치 설정: ",startPlace);
            //Log.e("도착 위치: ", endName);
        }

        return fragment4;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment4_matching, container, false);
        gPHP = new GetLocation();
        //테스트용 코드
        //destLat = 37.498374;
        //destLon = 127.027581;
        //userLat = 37.651403;
        //userLon = 127.016117;
        gPHP.execute(url);
        //로그인 얻어오기
        Intent intent_login = getActivity().getIntent();
        userID = intent_login.getStringExtra("userID");
        Intent intent_matching=getActivity().getIntent();



        //매칭,안전지수 모델
        matchings=new Matching_model(userLon,userLat);
        safety = new safety_model(getContext());

        final userLocation userLocation = new userLocation();
        intent= new Intent(getContext(), Matching_map.class);

        listview=(ListView)v.findViewById(R.id.lt_listview);
        Button matching_setting_button=(Button)v.findViewById(R.id.bt_matching_setting);
        et_dest = (EditText)v.findViewById(R.id.et_dest);
        et_start = (EditText)v.findViewById(R.id.et_start);
        chat_room=(Button)v.findViewById(R.id.chat_room);
        bt_matching_search = (Button)v.findViewById(R.id.bt_matching_search);
        bt_matching_cancel = (Button)v.findViewById(R.id.bt_matching_cancel);
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        bt_matching_map=(Button)v.findViewById(R.id.bt_matching_map);
        // 매칭 삭제
        bt_matching_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("매칭 db 삭제 ", " / "+destLat+", "+destLon);
                userLocation.user_Map(Constant.DELETE_URL,userID, userLat, userLon,destLat,destLon);
                et_dest.setText("");
                et_start.setText("");
                // mAdapter.clear();
            }


        });
        bt_matching_map.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent_map=new Intent(getActivity(),Matching_map.class);
                startActivity(intent_map);
            }
        });
        matching_setting_button.setOnClickListener(this);
        // 텍스트 클릭시 장소 검색 액티비티 이동

        // 목적지 검색
        et_dest.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //터치했을 때의 이벤트
                        //try{
                        code=105;

                        Intent intent = new Intent(getActivity(),Fragment1_search.class);
                        startActivity(intent);
                        ///}catch (InflateException e){
                        // 검색창 View가 이미 inflate되어 있는 상태이므로, 에러를 무시합니다.
                        //}

                        break;
                    }
                }
                return false;
            }
        });

        // 출발지 검색
        et_start.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //터치했을 때의 이벤트
                        //try{
                        code = 106;
                        Intent intent = new Intent(getActivity(),Fragment1_search.class);
                        startActivity(intent);
                        ///}catch (InflateException e){
                        // 검색창 View가 이미 inflate되어 있는 상태이므로, 에러를 무시합니다.
                        //}

                        break;
                    }
                }
                return false;
            }
        });
        chat_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_chat=new Intent(getContext(), ChatRoom_list.class);

                startActivity(intent_chat);

            }
        });

        // 매칭 데이터 db 올리기
        bt_matching_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("매칭 destLon, destLat: ", " / "+destLat+", "+destLon);

                onMapPoint();
                Log.e("otherList2", String.valueOf(otherList.size()));
                userLocation.user_Map(Constant.CREATE_URL,userID, userLat, userLon,destLat,destLon);


                setAdapter();

            }
        });
        Bundle bundle4 = getArguments();
        if(bundle4!=null&&bundle4.getInt("q")==27) {
            bt_matching_cancel.performClick();
        }else if(bundle4!=null&&bundle4.getInt("q")==28){
            bt_matching_map.performClick();
        }

        return v;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_matching_setting:
                Intent intent = new Intent(getActivity(), Matching_setting.class);
                startActivity(intent);
                break;
        }
    }


    public void setAdapter () {
        Log.e("setAdapter 실행 확인","");
        mAdapter = new BaseAdapterEx(getActivity(), otherList);

        mListView = (ListView) v.findViewById(R.id.lt_listview);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
//거리 4km이내 사용자 골라내기

    public void onMapPoint(){
        for(int i=0; i<personList.size();i++) {

            matching_data c = personList.get(i);
            double starting_dis=matchings.distance(userLat,userLon,personList.get(i).getUserLat(),personList.get(i).getUserLon(),"kilometer");
            double dest_dis=matchings.distance(destLat,destLon,personList.get(i).getDestLat(),personList.get(i).getDestLon(),"kilometer");
            if((dest_dis<=2)&&(starting_dis<=2)){
                userdistance user= new userdistance(personList.get(i).getId(),personList.get(i).getUserLat(),personList.get(i).getUserLon(),personList.get(i).getDestLat(),personList.get(i).getDestLon(),starting_dis+dest_dis);
                // otherList 중복 수정
                boolean doubled = false;

                for(int j=0; j<otherList.size(); j++){
                    matching_data p =otherList.get(j);
                    if(p.getId().equals(personList.get(i).getId()) || p.getId().equals(userID))
                        doubled = true;
                }
                if(doubled ==false) {
                    otherList.add(user);
                }


            }
            Log.e("otherList", String.valueOf(otherList.size()));

        }
        Collections.sort(otherList);

    }

    //다른 사용자 정보 불러오기
    class GetLocation extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                URL phpUrl = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection)phpUrl.openConnection();

                if ( conn != null ) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    if ( conn.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        while ( true ) {
                            String line = br.readLine();
                            if ( line == null )
                                break;
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {
            try {
                // PHP에서 받아온 JSON 데이터를 JSON오브젝트로 변환
                JSONObject jObject = new JSONObject(str);
                // results라는 key는 JSON배열로 되어있다.
                JSONArray results = jObject.getJSONArray("result");

                for(int i=0; i<results.length();i++){
                    JSONObject c = results.getJSONObject(i);
                    String id = c.getString("id");
                    double Lat = c.getDouble("userLat");
                    double Lon = c.getDouble("userLon");
                    double destlat=c.getDouble("destLat");
                    double destlon=c.getDouble("destLon");

                    matching_data person = new matching_data(id,Lat,Lon,destlat,destlon);

                    Log.e("php person:",id+": "+Lat+", "+Lon+", "+destlat+", "+destlon+"size:"+personList.size());

                    // personList 중복 수정
                    boolean doubled = false;

                    for(int j=0; j<personList.size(); j++){
                        matching_data p =personList.get(j);
                        if(p.getId().equals(id) || id.equals(userID))
                            doubled = true;
                    }
                    if(doubled ==false){
                        personList.add(person);

                    }




                }
                onMapPoint();
                setAdapter();


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    // 검색명 변경
    @Override
    public void onResume() {
        super.onResume();
        et_start.setText(startPlace);
        et_dest.setText(destPlace);
    }

}