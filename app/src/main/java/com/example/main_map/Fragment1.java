package com.example.main_map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Fragment1 extends Fragment implements TMapGpsManager.onLocationChangedCallback{

    private Context mContext = null;
    private boolean m_bTrackingMode = true;
    private TMapView mapView = null;
    private BitmapFactory itmapFactory;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment1_map, container, false);

        Button lamp_btn = (Button)v.findViewById(R.id.bt_lamp_onoff);

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

        onMapPoint(mapView);

        //onMapCircle(mapView);

        readData_lamp();  //가로등 csv 파일 읽기

        //가로등 버튼 터치시 마커 표시
        lamp_btn.setOnClickListener(new View.OnClickListener(){
            boolean isLampMarker = false;

            @Override
            public void onClick(View v) {
                mapView.removeAllMarkerItem();
                TMapPoint tpoint = mapView.getCenterPoint();
                /*현재 보이는 화면에는 마커가 없지만 보이지 않는 화면에 표시 되어 있을 경우 버튼을 두 번 터치해야 마커가 보이는 문제 해결 필요..*/

                //지도에 표시된 마커가 있을 때 터치하면 마커 삭제
                if(isLampMarker==true){
                    mapView.removeAllMarkerItem();
                    isLampMarker=false;
                    return;
                }

                //지도에 표시된 마커가 없을 때 터치하면 마커 표시
                if(initialPoint != tpoint && isLampMarker==false){
                    securityLight(mapView);
                    isLampMarker=true;
                }

            }
        });



        return v;
    }

    //마커 표시 테스트
    public void onMapPoint(TMapView mapView){

        TMapPoint tpoint = new TMapPoint(37.498095, 127.027610);

        TMapMarkerItem tItem = new TMapMarkerItem();

        Bitmap bitmap = itmapFactory.decodeResource(mContext.getResources(), R.drawable.poi);

        tItem.setIcon(bitmap);
        tItem.setPosition(0.5f, 1.0f);
        tItem.setTMapPoint(tpoint);
        tItem.setName("강남역");
        mapView.addMarkerItem("강남역", tItem);

        mapView.setCenterPoint(127.027610,37.498095, false);

    }

    //원 반경 표시하는
    public void onMapCircle(TMapView mapView){
        TMapPoint tMapPoint = new TMapPoint(37.498094, 127.027610);

        TMapCircle tMapCircle = new TMapCircle();
        tMapCircle.setCenterPoint(tMapPoint);
        tMapCircle.setRadius(500);
        tMapCircle.setCircleWidth(2);
        tMapCircle.setLineColor(Color.BLUE);
        tMapCircle.setLineColor(Color.GRAY);
        tMapCircle.setAreaAlpha(100);
        mapView.addTMapCircle("circle1", tMapCircle);

        TMapPoint tMapPoint2 = new TMapPoint(37.551094, 127.019470);

        TMapCircle tMapCircle2 = new TMapCircle();
        tMapCircle2.setCenterPoint(tMapPoint2);
        tMapCircle2.setRadius(500);
        tMapCircle2.setCircleWidth(2);
        tMapCircle2.setLineColor(Color.RED);
        tMapCircle2.setAreaColor(Color.GRAY);
        tMapCircle2.setAreaAlpha(100);
        mapView.addTMapCircle("circle2", tMapCircle);

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

                TMapPoint leftTop = mapView.getLeftTopPoint();
                TMapPoint rightBottom = mapView.getRightBottomPoint();
                if(leftTop.getLatitude()>x && rightBottom.getLatitude()<x && leftTop.getLongitude()<y && rightBottom.getLongitude()>y){
                    mapView.addMarkerItem("markerItem" + i, mk);
                }
            }
        }
    }

    @Override
    public void onLocationChange(Location location) {
        if(m_bTrackingMode){
            mapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }
}

