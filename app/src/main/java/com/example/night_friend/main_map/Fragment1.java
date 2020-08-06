package com.example.night_friend.main_map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.night_friend.cctv.CCTV;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.night_friend.R;

import com.example.night_friend.cctv.CCTV;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Fragment1 extends Fragment implements TMapGpsManager.onLocationChangedCallback {


    private Context mContext = null;
    private boolean m_bTrackingMode = true;
    private TMapView mapView = null;
    private BitmapFactory itmapFactory;



    XmlPullParser xpp;
    String key="5a2lkH8ig8auV2MX1JZr%2BJ0p78EfxlMxdSqJ5b9%2FmI139v1m3HDwqB%2B5a5vlMoplSNXeNIgfPu54Ji6WX0U09w%3D%3D";


    // 권한 체크 요청 코드 정의
    public static final int REQUEST_CODE_PERMISSIONS = 1000;

    private boolean isPermission = false;

    private Button bt_mail_save, bt_my_position;
    private ImageView bt_search;
    private EditText et_search;
    ArrayList<CCTV> cctvlist=new ArrayList<CCTV>();
    CCTV cctv=new CCTV();
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment1_map, container, false);

        mContext = getActivity();

        mapView = new TMapView(getActivity());

        mapView.setSKTMapApiKey("l7xx95f2e1d70e6a430484c5f00181f5ea93");
        LinearLayout linearLayoutTmap = (LinearLayout) v.findViewById(R.id.map_view);
        linearLayoutTmap.addView(mapView);

        //zoom 레벨
        mapView.setZoomLevel(15);
        mapView.setMapType(TMapView.MAPTYPE_STANDARD);
        mapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        //지도 처음 띄웠을 때 중심 좌표
        final TMapPoint initialPoint = mapView.getCenterPoint();

        //final MyAsyncTask myAsyncTask = new MyAsyncTask();
        Intent intent = getActivity().getIntent();
        String userID = intent.getStringExtra("userID");
        String userPassword = intent.getStringExtra("userPassword");

        bt_mail_save = (Button) v.findViewById(R.id.bt_mail_save);
        bt_my_position = (Button) v.findViewById(R.id.bt_my_position);
        bt_search = (ImageView) v.findViewById(R.id.bt_search);
        et_search = (EditText) v.findViewById(R.id.et_search);
        Button lamp_btn = (Button) v.findViewById(R.id.bt_lamp_onoff);
        Button cctv_btn = (Button) v.findViewById(R.id.bt_cctv_onoff);
        et_search.setText(userID);

        //onMapCircle(mapView);

        readData_lamp();  //가로등 csv 파일 읽기

        //가로등 버튼 터치시 마커 표시
        lamp_btn.setOnClickListener(new View.OnClickListener() {
            boolean isLampMarker = false;

            @Override
            public void onClick(View v) {
                mapView.removeAllMarkerItem();
                TMapPoint tpoint = mapView.getCenterPoint();
                /*현재 보이는 화면에는 마커가 없지만 보이지 않는 화면에 표시 되어 있을 경우 버튼을 두 번 터치해야 마커가 보이는 문제 해결 필요..*/

                //지도에 표시된 마커가 있을 때 터치하면 마커 삭제
                if (isLampMarker == true) {
                    mapView.removeAllMarkerItem();
                    isLampMarker = false;
                    return;
                }

                //지도에 표시된 마커가 없을 때 터치하면 마커 표시
                if (initialPoint != tpoint && isLampMarker == false) {
                    securityLight(mapView);
                    isLampMarker = true;
                }

            }
        });


        File files = new File("/data/data/com.example.night_friend.main_map/files/mycctv.txt");



        //cctv 버튼
        cctv_btn.setOnClickListener(new View.OnClickListener() {
            boolean isCctvMarker = false;

            @Override
            public void onClick(View v) {

                mapView.removeAllMarkerItem();
                TMapPoint tpoint = mapView.getCenterPoint();

                //지도에 표시된 마커가 있을 때 터치하면 마커 삭제
                if (isCctvMarker == true) {
                    mapView.removeAllMarkerItem();
                    isCctvMarker = false;
                    return;
                }

                //지도에 표시된 마커가 없을 때 터치하면 마커 표시
                if (initialPoint != tpoint && isCctvMarker == false) {
                    if(cctvlist.size()==0){
                    new MyCCTVAsyncTask1().execute();}
                    else{
                        securityCctv(mapView);
                    }
                    isCctvMarker = true;
                }

            }
        });



        return v;
    }

//cctv api 가져오기
    public void onMapCCTV() {

        String addr = null;
        String Xpos = null;
        String Ypos = null;
        int i=0;
        // buffer = new StringBuffer();


        try {
            String queryUrl = "http://data.ulsan.go.kr/rest/ulsancctv/getUlsancctvList?ServiceKey="+key
                    + "&numOfRows=3000";
            URL url = new URL(queryUrl);
            InputStream is = url.openStream();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            xpp = factory.newPullParser();

            xpp.setInput(new InputStreamReader(is, "UTF-8"));
            String tag;
            String endtag;
            xpp.next();
            int event_type = xpp.getEventType();

            CCTV cctv=new CCTV();
            while (event_type != XmlPullParser.END_DOCUMENT) {

                switch (event_type) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        tag=xpp.getName();
                        //Log.i("inaddr", tag);
                        if (tag.equals("list")){
                            cctv=new CCTV();
                        }
                        else if (tag.equals("ulsancctvAddr")) {
                            xpp.next();
                            addr = xpp.getText();
                            //Log.i("addr",addr);
                            cctv.setAddr(addr);
                        }
                        else if (tag.equals("ulsancctvXpos")) {
                            xpp.next();
                            Xpos = xpp.getText();
                            //Log.i("Xpos",Xpos);
                            cctv.setXpos(Double.parseDouble(Xpos));

                        }
                        else if (tag.equals("ulsancctvYpos")) {
                            xpp.next();
                            Ypos = xpp.getText();
                            //Log.i("Ypos",Ypos);
                            cctv.setYpos(Double.parseDouble(Ypos));
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
                        if (tag.equals("list")) {
                            cctvlist.add(cctv);
                            Log.i("cctvlist size", String.valueOf(cctvlist.size()));
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




    // 권한 요청
        public void callPermission () {
            // 권한 체크
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                // 권한 요청 대화상자 표시
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSIONS);
                return;

            } else {
                isPermission = true;
            }

        }

        // 권한 체크 여부 확인
        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults){
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            switch (requestCode) {
                case REQUEST_CODE_PERMISSIONS:
                    if (ActivityCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getActivity(), "권한 체크 거부 됨", Toast.LENGTH_SHORT).show();
                    } else {
                        isPermission = true;
                    }
                    return;
            }
        }
    //csv 파일 이용
    private List<test> testList=new ArrayList<>();

    private void readData_lamp() {
        InputStream is = getResources().openRawResource(R.raw.seocho);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8") )
        );

        String line="";

        try{
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                Log.d("MyActivity", "Line: " + line);
                String[] tokens = line.split(",");
                test t = new test();
                if (tokens[4].length() > 0) {   //위도 값이 비어있지 않으면
                    t.setLatitude(Double.parseDouble(tokens[4]));
                } else {
                    t.setLongitude(0);
                }
                if (tokens[5].length() > 0) {   //경도 값이 비어있지 않으면
                    t.setLongitude(Double.parseDouble(tokens[5]));
                } else {
                    t.setLongitude(0);
                }
                testList.add(t);
                Log.d("MyActivity", "Just created: " + t);

            }
        } catch (IOException e){
            Log.d("MyActivity","Error reading data file on line"+line, e);
            e.printStackTrace();
        }
    }

    public void securityLight(final TMapView mapView){
        TMapPoint mp = null;
        double x, y;

        for (int i = 0; i < testList.size(); i++) {
            if(testList.get(i).getLatitude()!=0 && testList.get(i).getLongitude()!=0) {

                TMapMarkerItem mk = new TMapMarkerItem();
                x = testList.get(i).getLatitude();
                y = testList.get(i).getLongitude();
                mp = new TMapPoint(x, y);
                Bitmap bitmap = itmapFactory.decodeResource(mContext.getResources(), R.drawable.poi);

                Log.d("Location", "Latitude: " + testList.get(i).getLatitude() + ", Longitude: " + testList.get(i).getLongitude());
                mk.setName("가로등");
                mk.setIcon(bitmap);
                mk.setTMapPoint(mp);
                mapView.addMarkerItem("markerItem" + i, mk);
/*
                TMapPoint leftTop = mapView.getLeftTopPoint();
                TMapPoint rightBottom = mapView.getRightBottomPoint();
                if(leftTop.getLatitude()>x && rightBottom.getLatitude()<x && leftTop.getLongitude()<y && rightBottom.getLongitude()>y){
                    mapView.addMarkerItem("markerItem" + i, mk);
                }*/
            }
        }
    }
    //cctv 화면에 띄우는 메소드
    public void securityCctv(final TMapView mapView){
        TMapPoint mp = null;
        double x, y;
        Log.i("dd333",String.valueOf(cctvlist.size()));
        for (int i = 0; i < cctvlist.size(); i++) {
            Log.i("dd",String.valueOf(cctvlist.get(i).getXpos()));
            if(cctvlist.get(i).getXpos()!=0 && cctvlist.get(i).getYpos()!=0) {

                TMapMarkerItem mk = new TMapMarkerItem();
                x = cctvlist.get(i).getYpos();
                y = cctvlist.get(i).getXpos();
                mp = new TMapPoint(x, y);
                Log.d("cctv", "Latitude: " + x + ", Longitude: " + y);
                Bitmap bitmap = itmapFactory.decodeResource(mContext.getResources(), R.drawable.poi);


                mk.setName("cctv");
                mk.setIcon(bitmap);
                mk.setTMapPoint(mp);
                mapView.addMarkerItem("markerItem" + i, mk);
               /* TMapPoint leftTop = mapView.getLeftTopPoint();
                TMapPoint rightBottom = mapView.getRightBottomPoint();
                if(leftTop.getLatitude()>x && rightBottom.getLatitude()<x && leftTop.getLongitude()<y && rightBottom.getLongitude()>y){

                }*/
            }
        }
    }

    @Override
    public void onLocationChange(Location location) {
        if(m_bTrackingMode){
            mapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }


public class MyCCTVAsyncTask1 extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {

        onMapCCTV();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            securityCctv(mapView);

        }
    }
}

