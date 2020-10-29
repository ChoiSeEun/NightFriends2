package com.example.night_friend.matching;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.night_friend.R;
import com.example.night_friend.main_map.Fragment1;
import com.example.night_friend.main_map.Fragment2;
import com.example.night_friend.main_map.Fragment4;
import com.example.night_friend.main_map.GpsInfo;
import com.example.night_friend.main_map.GpsTracker;
import com.example.night_friend.main_map.Night_main;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.night_friend.main_map.Fragment4.otherList;

public class Matching_map extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback{

    private TMapView mapView = null;
    private double userLat, userLon;
    private double starting_middleX,starting_middleY;
    private double dest_middleX,dest_middleY;
    private double destLat,destLon;
    XmlPullParser xpp;
    ArrayList<BUS> buslist=new ArrayList<BUS>();
    ArrayList<BUS> dest_buslist=new ArrayList<BUS>();
    private GpsTracker gpsTracker;
    userdistance userad;
    userdistance selectuser;
    TMapPoint point3,point4;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_map);


        MyBUSAsyncTask1 busAPI=new MyBUSAsyncTask1();
        Intent intent =getIntent();
        gpsTracker = new GpsTracker(Matching_map.this);

        userLat = Fragment4.userLat;
        userLon = Fragment4.userLon;
        busAPI.execute();
        Log.e("dd",userLon+"위도 경도"+userLat);

        Toast.makeText(Matching_map.this, "현재위치 \n위도 " + userLat + "\n경도 " + userLon, Toast.LENGTH_LONG).show();

        destLat = Fragment4.destLat;
        destLon = Fragment4.destLon;

        mapView = new TMapView(Matching_map.this);

        mapView.setSKTMapApiKey("l7xx95f2e1d70e6a430484c5f00181f5ea93");
        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.matching_map);
        linearLayoutTmap.addView(mapView);

        //zoom 레벨
        mapView.setZoomLevel(15);
        mapView.setMapType(TMapView.MAPTYPE_STANDARD);
        mapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        //지도 처음 띄웠을 때 중심 좌표
        final TMapPoint initialPoint = mapView.getCenterPoint();

        mapView.setCenterPoint(userLon, userLat);
        //리스트에서 받아온 정보표시 // Tmapmarker로 받아올 수 있으면 수정하는게 좋음
        if(intent.getExtras()!=null&&intent.getExtras().getInt("key")==11) {
            userad = (userdistance) intent.getSerializableExtra("set");
            selectuser = userad;
            start_middleLocation(userad.getUserLat(), userad.getUserLon());
            dest_middleLocation(userad.getDestLat(), userad.getDestLon());
            onBus();
        }else{

        }

//슬라이딩
        SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            String start_addr;
            String dest_addr;
            @Override

            public void onPanelSlide(View panel, float slideOffset) {

                final TextView tv1=(TextView)findViewById(R.id.sliding_id);
                tv1.setText(selectuser.getId()+"의 정보");
                TextView sd_start=(TextView)findViewById(R.id.sd_start);
                TextView sd_dest=(TextView)findViewById(R.id.sd_dest);
                start_addr=getCompleteAddress(getApplicationContext(),selectuser.getUserLat(),selectuser.getUserLon());
                dest_addr=getCompleteAddress(getApplicationContext(),selectuser.getDestLat(),selectuser.getDestLon());
                sd_start.setText("출발지 : "+start_addr);
                sd_dest.setText("도착지 : "+dest_addr);
                Button chat_bt=(Button)findViewById(R.id.bt_chat);
                Button bt_matching=(Button)findViewById(R.id.bt_matching);
                chat_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent_chat=new Intent(getApplication(), Matching_chat.class);
                        intent_chat.putExtra("matching_id",selectuser.getId());
                        startActivity(intent_chat);
                    }
                });
                bt_matching.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Matching_map.this.replaceFragment(Fragment2.newInstance(find_Name1, 102, find_Point.getLatitude(), find_Point.getLongitude()));
                        String rvaddr1=getCompleteAddress(getApplicationContext(),starting_middleX, starting_middleY);
                        String rvaddr2=getCompleteAddress(getApplicationContext(),dest_middleX, dest_middleY);
                        Fragment2.newInstance(rvaddr1, 101,starting_middleX, starting_middleY);
                        Fragment2.newInstance(rvaddr2, 102,dest_middleX, dest_middleY);
                        Intent intent_m=new Intent(getApplication(),Night_main.class);
                        intent_m.putExtra("code3",105);
                        startActivity(intent_m);
                    }
                });



            }

            @Override

            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {


                if(newState.name().toString().equalsIgnoreCase("Collapsed")){
                    // 닫혔을때 처리하는 부분



                }else if(newState.name().equalsIgnoreCase("Expanded")){
                    // 열렸을때 처리하는 부분



                }
            }

        });

      //마커 선택했을때
        mapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem markerItem) {
                Log.e("클릭","클릭");
                show(markerItem);

            }
        });

    }
    //알림
    void show(final TMapMarkerItem markerItem)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Log.e("클릭","클릭");
        builder.setTitle("매칭");
        builder.setMessage(markerItem.getName()+" 의 정보를 확인하겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"예를 선택했습니다.",Toast.LENGTH_LONG).show();
                        //

                        for(int i=0;i<otherList.size();i++){
                            if(otherList.get(i).getId().equals(markerItem.getID())){
                                selectuser=otherList.get(i);
                            }
                        }
                        Log.d("gg",markerItem.getID());
                        TMapMarkerItem markeritem = mapView.getMarkerItemFromID("dest_"+markerItem.getID());
                        markerItem.setCalloutTitle(markerItem.getID()+"님 선택");

                       // Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.selected_marker);
                       // markerItem.setIcon(bitmap);

                        start_middleLocation(markerItem.latitude,markerItem.longitude);
                        dest_middleLocation(markeritem.latitude,markeritem.longitude);
                        onBus();

                        Log.e("클릭", String.valueOf(markerItem.longitude));


                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"아니오를 선택했습니다.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }
//경유지 길 안내
    public void onBus(){
        ArrayList<BUS> start_busarraulist=new ArrayList<BUS>();
        ArrayList<BUS> dest_busarraulist=new ArrayList<BUS>();
        if(start_busarraulist!=null&&dest_busarraulist!=null) {
            try {
                start_busarraulist = new MyBUSAsyncTask2().execute().get();
                dest_busarraulist = new MyBUSAsyncTask3().execute().get();
            } catch (Exception e) {

                e.printStackTrace();

            }
            securityBus(start_busarraulist);
            securityBus(dest_busarraulist);

            matchingline(start_busarraulist.get(0).getYpos(),start_busarraulist.get(0).getXpos(),dest_busarraulist.get(0).getYpos(),dest_busarraulist.get(0).getXpos());

        }else{
            matchingline(starting_middleX,starting_middleY,dest_middleX,dest_middleY);
        }
         }

    //나의 위치
    public void onMarker(){
        TMapPoint tpoint = new TMapPoint(userLat,userLon);
        TMapMarkerItem tItem = new TMapMarkerItem();
        tItem.setTMapPoint(tpoint);
        tItem.setName("나의 위치");
        tItem.setVisible(TMapMarkerItem.VISIBLE);

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.baseline_person_pin_black_36dp);
        tItem.setIcon(bitmap);

        mapView.addMarkerItem("나의 위치",tItem);

        tItem.setCanShowCallout(true);
        tItem.setCalloutTitle("나의 위치");

    }
//매칭 출발지 위치 표시
    public void starting_otherMarker(){
        // 내 id 제외 위치 뜨게 추후 수정할 것
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.baseline_add_location_alt_black_36dp);


        for(int i=0; i<otherList.size();i++){
            matching_data c = otherList.get(i);

            TMapPoint tpoint = new TMapPoint(c.getUserLat(),c.getUserLon());
            TMapMarkerItem tItem = new TMapMarkerItem();
            tItem.setID(c.getId());


            tItem.setName(otherList.get(i).getId());
            tItem.setVisible(TMapMarkerItem.VISIBLE);

            tItem.setTMapPoint(tpoint);
            tItem.setIcon(bitmap);
            tItem.setCalloutRightButtonImage(bitmap);
            //tItem.setCalloutTitle(c.getId());
            tItem.setCanShowCallout(true);


            mapView.addMarkerItem(c.getId(),tItem);
            // }

            Log.e("personList:",c.getId()+": "+c.getUserLat()+", "+c.getUserLon());

        }


    }
//도착지 위치 표시
    public void dest_otherMarker(){
        // 내 id 제외 위치 뜨게 추후 수정할 것
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.destmarker);


        for(int i=0; i<otherList.size();i++){
            matching_data c = otherList.get(i);
            TMapPoint tpoint = new TMapPoint(c.getDestLat(),c.getDestLon());
            TMapMarkerItem tItem = new TMapMarkerItem();
            tItem.setID(c.getId());


            tItem.setName(otherList.get(i).getId());
            tItem.setVisible(TMapMarkerItem.VISIBLE);

            tItem.setTMapPoint(tpoint);
            tItem.setIcon(bitmap);
            tItem.setCalloutRightButtonImage(bitmap);
            //tItem.setCalloutTitle(c.getId());
            tItem.setCanShowCallout(true);


            mapView.addMarkerItem("dest_"+c.getId(),tItem);
            // }

            Log.e("personList:",c.getId()+": "+c.getDestLat()+", "+c.getDestLon());

        }


    }
    public void matchingline(double rvlat,double rvlon,double rv2lat,double rv2lon){

        //넘어온 데이터로 polyline 그리기
        //전부 삭제
        mapView.removeTMapPath();
        TMapData tMapData = new TMapData();
        TMapPoint point1 = new TMapPoint(userLat, userLon);
        TMapPoint point2 = new TMapPoint(destLat, destLon);
        //TMapPoint point3 = new TMapPoint(rvlat, rvlon);
        Log.e("경유지", String.valueOf(rvlon));
        point3 = new TMapPoint(rvlat, rvlon);
        point4 = new TMapPoint(rv2lat, rv2lon);
        ArrayList<TMapPoint> passList =new ArrayList<TMapPoint>();
        passList.add(point3);
        passList.add(point4);

        tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, point1, point2, passList, 10,
                new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine polyLine) {
                        Log.e("폴리라인 그리기","");
                        mapView.addTMapPath(polyLine);

                    }
                });

    }
    //중복코드 합칠 수 있으면 합치는게 좋음
    //출발지 bus api 가져오기
    public void starting_onMapBUS() {

        String Xpos = null;
        String Ypos = null;
        int i=0;
        // buffer = new StringBuffer();
        buslist.clear();
        Log.e("middle", String.valueOf(starting_middleX));

        try {
            String queryUrl = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByPos?serviceKey=" +
                    "5a2lkH8ig8auV2MX1JZr%2BJ0p78EfxlMxdSqJ5b9%2FmI139v1m3HDwqB%2B5a5vlMoplSNXeNIgfPu54Ji6WX0U09w%3D%3D&" +
                    "tmX=" +
                    starting_middleY +
                    "&tmY=" +
                    starting_middleX +
                    "&radius=500&";
            URL url = new URL(queryUrl);
            InputStream is = url.openStream();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            xpp = factory.newPullParser();
            Log.i("Xpos","tlfgod");
            xpp.setInput(new InputStreamReader(is, "UTF-8"));
            String tag;
            String endtag;

            xpp.next();
            int event_type = xpp.getEventType();
            Log.i("Xpos", String.valueOf(event_type != XmlPullParser.END_DOCUMENT));
            BUS bus=new BUS();
            while (event_type != XmlPullParser.END_DOCUMENT) {
                switch (event_type) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        tag=xpp.getName();
                        //Log.i("inaddr", tag);
                        if (tag.equals("itemList")){
                            bus=new BUS();
                        }
                        else if (tag.equals("gpsX")) {
                            xpp.next();
                            Xpos = xpp.getText();
                            Log.i("Xpos",Xpos);
                            bus.setXpos(Double.parseDouble(Xpos));
                        }
                        else if (tag.equals("gpsY")) {
                            xpp.next();
                            Ypos = xpp.getText();
                            Log.i("Ypos",Ypos);
                            bus.setYpos(Double.parseDouble(Ypos));

                        }
                        break;


                    case XmlPullParser.TEXT:

                        break;
                    case XmlPullParser.END_TAG:
                        tag=xpp.getName();
                        if (tag.equals("itemList")) {
                            buslist.add(bus);
                            Log.i("start size", String.valueOf(buslist.size()));
                        }
                        break;
                }
                event_type=xpp.next();


            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //목적지 bus api 가져오기
    public void dest_onMapBUS() {

        String Xpos = null;
        String Ypos = null;
        int i=0;
        // buffer = new StringBuffer();
        dest_buslist.clear();
        Log.e("destmiddle", String.valueOf(dest_middleX));
        Log.e("destmiddle", String.valueOf(dest_middleY));

        try {
            String queryUrl = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByPos?serviceKey=" +
                    "5a2lkH8ig8auV2MX1JZr%2BJ0p78EfxlMxdSqJ5b9%2FmI139v1m3HDwqB%2B5a5vlMoplSNXeNIgfPu54Ji6WX0U09w%3D%3D&" +
                    "tmX=" +
                    dest_middleY +
                    "&tmY=" +
                    dest_middleX +
                    "&radius=500&";
            URL url = new URL(queryUrl);
            InputStream is = url.openStream();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            xpp = factory.newPullParser();
            Log.i("dest","tlfgod");
            xpp.setInput(new InputStreamReader(is, "UTF-8"));
            String tag;
            String endtag;

            xpp.next();
            int event_type = xpp.getEventType();
            Log.i("dest", String.valueOf(event_type != XmlPullParser.END_DOCUMENT));
            BUS bus=new BUS();
            while (event_type != XmlPullParser.END_DOCUMENT) {
                switch (event_type) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        tag=xpp.getName();
                        //Log.i("inaddr", tag);
                        if (tag.equals("itemList")){
                            bus=new BUS();
                        }
                        else if (tag.equals("gpsX")) {
                            xpp.next();
                            Xpos = xpp.getText();
                            Log.i("Xpos",Xpos);
                            bus.setXpos(Double.parseDouble(Xpos));
                        }
                        else if (tag.equals("gpsY")) {
                            xpp.next();
                            Ypos = xpp.getText();
                            Log.i("Ypos",Ypos);
                            bus.setYpos(Double.parseDouble(Ypos));

                        }
                        break;


                    case XmlPullParser.TEXT:
                       /* if (inAddr) { //inAddr true일 때 태그의 내용을 저장.
                            inAddr = false;
                        }
                        if (inXpos) { //inXpos true일 때 태그의 내용을 저장.
                            inXpos = false;
                        }
                        if (inYpos) { //inYpos true일 때 태그의 내용을 저장.
                            inYpos = false;
                        }*/
                        break;
                    case XmlPullParser.END_TAG:
                        tag=xpp.getName();
                        if (tag.equals("itemList")) {
                            dest_buslist.add(bus);
                            Log.i("dest size", String.valueOf(dest_buslist.size()));
                        }
                        break;
                }
                event_type=xpp.next();


            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //bus 화면에 띄우는 메소드
    public TMapPoint securityBus(ArrayList<BUS> buslist){
        if(buslist==null){

        }
        Log.i("buslist",String.valueOf(buslist.get(0).getXpos()));
        TMapPoint tpoint = new TMapPoint(buslist.get(0).getYpos(),buslist.get(0).getXpos());
        TMapMarkerItem tItem = new TMapMarkerItem();
        tItem.setTMapPoint(tpoint);
        tItem.setName("버스 정류장");
        tItem.setVisible(TMapMarkerItem.VISIBLE);

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.poi);
        tItem.setIcon(bitmap);
        Log.e("tItem", String.valueOf(tItem.getTMapPoint().getLatitude()));

        mapView.addMarkerItem("버스 정류장",tItem);

        return tpoint;

    }

    //중간지점 계산
    public void start_middleLocation(double otherLat,double otherLon){
        Log.e("user",userLat+"dd"+otherLat);
        starting_middleX=(userLat+otherLat)/2;
        starting_middleY=(userLon+otherLon)/2;


    }
    public void dest_middleLocation(double otherLat,double otherLon){

        dest_middleX=(destLat+otherLat)/2;
        dest_middleY=(destLon+otherLon)/2;


    }


    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                //위치 값을 가져올 수 있음
                ;
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(Matching_map.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(Matching_map.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(Matching_map.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(Matching_map.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음



        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(Matching_map.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(Matching_map.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(Matching_map.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(Matching_map.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Matching_map.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onLocationChange(Location location) {

    }
    public class MyBUSAsyncTask2 extends AsyncTask<String, Void, ArrayList<BUS>> {

        @Override
        protected ArrayList<BUS> doInBackground(String... strings) {
            starting_onMapBUS();
            return buslist;
        }

        @Override
        protected void onPostExecute(ArrayList<BUS> s) {
            super.onPostExecute(s);


        }
    }
    public class MyBUSAsyncTask3 extends AsyncTask<String, Void, ArrayList<BUS>> {

        @Override
        protected ArrayList<BUS> doInBackground(String... strings) {
            dest_onMapBUS();
            return dest_buslist;
        }

        @Override
        protected void onPostExecute(ArrayList<BUS> s) {
            super.onPostExecute(s);

            //busrv=securityBus();
            //Log.e("bus", String.valueOf(busrv.getLatitude()));


        }
    }

    public class MyBUSAsyncTask1 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            onMarker();
            starting_otherMarker();
            dest_otherMarker();
            //matchingline();




        }
    }

    //위도 경도 주소로 변환
    public static String getCompleteAddress(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");


                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("MyCurrentloctionaddress", strReturnedAddress.toString());
            } else {
                Log.w("MyCurrentloctionaddress", "주소가 리턴되지 않음");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("MyCurrentloctionaddress", "주소를 가져올 수 없음");
        }

        // "대한민국" 글자 지워버림
        strAdd = strAdd.substring(5);

        return strAdd;
    }

}
