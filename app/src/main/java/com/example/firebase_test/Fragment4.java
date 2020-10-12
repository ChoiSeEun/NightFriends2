package com.example.firebase_test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPOIItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.firebase_test.Fragment1.REQUEST_CODE_PERMISSIONS;

public class Fragment4 extends Fragment implements View.OnClickListener{
    ListView mListView = null;
    BaseAdapterEx mAdapter = null;
    ArrayList<matching_data> mData = null;
    View v;
    Intent intent;
    private GetLocation gPHP;
    private GpsInfo gps;
    private Context mContext;



    private double userLat, userLon;
    static private double destLat, destLon;

    static private String destPlace;
    private boolean isPermission = false;
    String url = "http://night1234.dothome.co.kr/getLocation.php";
    //Matching_model matchings;
    private ArrayList<matching_data> personList= new ArrayList<matching_data>();
    //private ArrayList<userdistance> otherList= new ArrayList<userdistance>();
    private ArrayList<Locationitem> destList = new ArrayList<Locationitem>();

    private EditText et_dest;
    private Button bt_matching_search;


    public static Fragment4 newInstance(String place, Integer code, Double lat, Double lon){
        final Fragment4 fragment4 = new Fragment4();

        Log.e("받아온 이름: ",place);
        Log.e("코드/위경도: ", code+" / "+lat+", "+lon);

        if(code==103){
            destPlace = place;
            destLat = lat;
            destLon = lon;

            Log.e("도착 위치 설정: ",destPlace);
            //Log.e("도착 위치: ", endName);
        }

        return fragment4;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v=inflater.inflate(R.layout.fragment4_matching, container, false);


        gPHP = new GetLocation();

        gPHP.execute(url);

        //setAdapter();
        Button matching_setting_button=(Button)v.findViewById(R.id.bt_matching_setting);
        Button matching_map=(Button)v.findViewById(R.id.matching_map);
        //matchings=new Matching_model(userLon,userLat);
        //Intent intent_matching=getActivity().getIntent();
        final userLocation userLocation = new userLocation();
        //intent= new Intent(getContext(), Matching_map.class);

        // editText, button

        et_dest = (EditText)v.findViewById(R.id.et_dest);
        bt_matching_search = (Button)v.findViewById(R.id.bt_matching_search);



        //getNowLocation();
        userLat=37.65119833333333;
        userLon=127.01616;
        Log.e("getLocation", "Lat: " + userLat + "Lon: " + userLon);


        matching_setting_button.setOnClickListener(this);

        /*

        // 텍스트 클릭시 장소 검색 액티비티 이동

        et_dest.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //터치했을 때의 이벤트
                        //try{


                        Intent intent = new Intent(getActivity(),Fragment4_search.class);
                        startActivity(intent);
                        ///}catch (InflateException e){
                        // 검색창 View가 이미 inflate되어 있는 상태이므로, 에러를 무시합니다.
                        //}

                        break;
                    }
                }
                return false;
            }
        }); */

        Bundle bundle = getArguments();

        if(bundle!=null && bundle.getInt("code")==104){
            destPlace = bundle.getString("destPlace");
            destLat = bundle.getDouble("destLat");
            destLon = bundle.getDouble("destLon");

            et_dest.setText(destPlace);
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
        //mAdapter = new BaseAdapterEx(getActivity(), otherList);

        mListView = (ListView) v.findViewById(R.id.lt_listview);
        mListView.setAdapter(mAdapter);
    }

    /*
    public void onMapPoint(){


        Log.i("userdissize", String.valueOf(personList.size()));
        double lat_difference=matchings.LatitudeInDifference();
        double lon_difference=matchings.LongitudeInDifference(userLon);
        for(int i=0; i<personList.size();i++) {

            matching_data c = personList.get(i);
            double dis=matchings.distance(userLat,userLon,personList.get(i).getUserLat(),personList.get(i).getUserLon(),"kilometer");
            if(dis<=4){
                userdistance user=new userdistance(personList.get(i).getId(),personList.get(i).getUserLat(),personList.get(i).getUserLon(),dis);

                // otherList 중복 수정
                boolean doubled = false;

                for(int j=0; j<otherList.size(); j++){
                    matching_data p =otherList.get(j);
                    if(p.getId().equals(personList.get(i).getId()))
                        doubled = true;
                }
                if(doubled ==false)
                    otherList.add(user);

                //otherList.add(user);
            }
            Log.i("otherList", String.valueOf(otherList.size()));


        }
        Collections.sort(otherList);


    } */


    // 현재 위치 받아오기
    public void getNowLocation(){
        if(!isPermission){
            //callPermission();
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

    // 현재 위치
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

                    //matching_data person = new matching_data(id,Lat,Lon);

                    Log.e("php person:",id+": "+Lat+", "+Lon+"size:"+personList.size());


                    // personList 중복 수정
                    boolean doubled = false;

                    for(int j=0; j<personList.size(); j++){
                        matching_data p =personList.get(j);
                        //if(p.getId().equals(id) )
                        doubled = true;
                    }
                    //if(doubled ==false)
                    //personList.add(person);

                }

                //onMapPoint();
                setAdapter();


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    // 주소 > 위도, 경도 변환
    public void getLocationFromAddress(String strAddress)  {

        Geocoder coder = new Geocoder(getContext());
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {

            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            destLat = (double) (location.getLatitude() * 1E6);
            destLon = (double) (location.getLongitude() * 1E6);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }

    // 사용자 목적지 db 저장

    public void destDB(){
        destLat = this.getArguments().getDouble("destLat");
        destLon = this.getArguments().getDouble("destLon");

        Log.e("destDB",destLat+", "+destLon);

    }


    // 목적지 검색


    public void SearchDest() {
        final TMapData tMapData = new TMapData();
        final String keyword = et_dest.getText().toString();


        tMapData.findAllPOI(keyword, new TMapData.FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(final ArrayList<TMapPOIItem> poiItem) {
                for (int i = 0; i < poiItem.size(); i++) {
                    TMapPOIItem item = poiItem.get(i);
                    //addPoint(input.getText().toString(),item.getPOIPoint().getLatitude(),item.getPOIPoint().getLongitude());

                    String POIname = item.getPOIName();
                    double POILat = item.getPOIPoint().getLatitude();
                    double POILon = item.getPOIPoint().getLongitude();

                    Locationitem dest = new Locationitem(POIname,item.getPOIAddress().replace("null", ""),POILat,POILon);
                    destList.add(dest);

                    Log.d("주소로 찾기", "POI Name" + item.getPOIName().toString() + ","
                            + "Address " + item.getPOIAddress().replace("null", "") + ","
                            + "Point" + item.getPOIPoint().getLatitude() + "Point" + item.getPOIPoint().getLongitude());
                }
            }

        });


    }

    // 검색명 변경
    @Override
    public void onResume() {
        super.onResume();

        et_dest.setText(destPlace);
    }


    /*
    // 강제 종료시 실행 되지 않음.
    @Override
    public void onDestroy() {
        super.onDestroy();
        userLocation userLocation = new userLocation();
        userLocation.user_Map(Constant.DELETE_URL,"test3", 0, 0);
        Log.e("onDestroy","계정 삭제");
    }

     */
}
