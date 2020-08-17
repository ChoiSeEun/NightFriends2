package com.example.main_map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.ParserConfigurationException;


public class Fragment1 extends Fragment implements TMapGpsManager.onLocationChangedCallback{

    //private static Context mContext = null;

    private static FragmentActivity mContext = null;
    private boolean m_bTrackingMode = true;
    private static TMapView mapView = null;
    private static BitmapFactory itmapFactory;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "code";

    private String mParam1;
    private Integer mParam2;

    static String startName = null;
    static String endName = null;
    static TMapPoint startPoint = null;
    static TMapPoint endPoint = null;

    Double start_lat=null;
    Double start_lon=null;
    Double end_lat=null;
    Double end_lon=null;

    public Fragment1(){

    }

    public static Fragment1 newInstance(String param1, final Integer code){
        final Fragment1 fragment1 = new Fragment1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, code);
        fragment1.setArguments(args);

        Log.e("New Instance","");
        //Fragment2에서 넘어온 데이터로 POI 검색하기
        TMapData tmapdata = new TMapData();
        tmapdata.findAllPOI(param1, new TMapData.FindAllPOIListenerCallback() {
            TMapPoint mp = null;
            double x, y;
            @Override
            public void onFindAllPOI(ArrayList poiItem) {
                for(int i = 0; i < poiItem.size(); i++) {
                    TMapPOIItem item = (TMapPOIItem) poiItem.get(i);
                    TMapMarkerItem mk = new TMapMarkerItem();
                    x = item.getPOIPoint().getLatitude();
                    y = item.getPOIPoint().getLongitude();
                    mp = item.getPOIPoint();

                    Bitmap bitmap = itmapFactory.decodeResource(mContext.getResources(), R.drawable.poi);

                    mk.setName(item.getPOIName());
                    mk.setIcon(bitmap);
                    mk.setTMapPoint(mp);

                    mk.setCalloutTitle(item.getPOIName());
                    mk.setCanShowCallout(true);

                    Bitmap bitmap2 = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_stat_name);
                    mk.setCalloutRightButtonImage(bitmap2);

                    mapView.addMarkerItem("markerItem" + i, mk);


                    //풍선뷰 클릭시 위도/경도 받기
                    mapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
                        @Override
                        public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                            if(!arrayList.isEmpty()){
                                if(code==101) {
                                    startName = arrayList.get(0).getName();
                                    startPoint = arrayList.get(0).getTMapPoint();
                                    Log.e("마커 이름: ",""+startName);
                                    Log.e("마커의 위도/경도: ",""+arrayList.get(0).getTMapPoint());
                                }
                                else if(code==102){
                                    endName = arrayList.get(0).getName();
                                    endPoint = arrayList.get(0).getTMapPoint();
                                    Log.e("마커 이름:",""+endName);
                                    Log.e("마커의 위도/경도: ",""+arrayList.get(0).getTMapPoint());
                                }

                            }
                            return false;
                        }

                        @Override
                        public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                            return false;
                        }
                    });

                    //풍선뷰 아이콘 클릭시 출발지/도착지 설정 및 길찾기 화면으로 이동
                    mapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
                        @Override
                        public void onCalloutRightButton(TMapMarkerItem markerItem) {
                            if(code==101){
                                ((Night_main)mContext).replaceFragment(Fragment2.newInstance(startName, endName, 101, startPoint.getLatitude(), startPoint.getLongitude()));
                            }
                            else if(code==102) {
                                ((Night_main) mContext).replaceFragment(Fragment2.newInstance(startName, endName,102, endPoint.getLatitude(), endPoint.getLongitude()));
                            }
                            Log.d("풍선뷰 Click: ", "선택 됨");
                        }
                    });

                    Log.d("POI Name: ", item.getPOIName().toString() + ", " +
                            "Address: " + item.getPOIAddress().replace("null", "")  + ", " +
                            "Point: " + item.getPOIPoint().toString());

                }
            }
        });


        return fragment1;
    }

    //다른 프래그먼트에서 넘어오는 데이터 저장
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getInt(ARG_PARAM2);
        }

    }

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
        set_zero();

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

        Bundle bundle = getArguments();

        if(bundle!=null) {
            start_lat = bundle.getDouble("start_lat");
            start_lon = bundle.getDouble("start_lon");
            end_lat = bundle.getDouble("end_lat");
            end_lon = bundle.getDouble("end_lon");

            //넘어온 데이터로 polyline 그리기
            TMapData tMapData = new TMapData();
            TMapPoint point1 = new TMapPoint(start_lat, start_lon);
            TMapPoint point2 = new TMapPoint(end_lat, end_lon);
            ArrayList passList = null; //경유지 List
            Log.e("넘어온 데이터 start_lat",""+start_lat);
            Log.e("넘어온 데이터 start_lon",""+start_lon);
            Log.e("넘어온 데이터 end_lat",""+end_lat);
            Log.e("넘어온 데이터 end_lon",""+end_lon);

            //searchOption 부여하여 경로 그리기
            tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, point1, point2, passList, 10,
                    new TMapData.FindPathDataListenerCallback() {
                        @Override
                        public void onFindPathData(TMapPolyLine polyLine) {
                            Log.e("폴리라인 그리기","");
                            mapView.addTMapPath(polyLine);

                        }
                    });
        }


        //api 호출 없이 tmap api에 정의된 함수를 통해 경로안내 xml 데이터 가져오기
        /* tMapData.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, point1, point2, new TMapData.FindPathDataAllListenerCallback() {
            @Override
            public void onFindPathDataAll(Document document) {
                Element root = document.getDocumentElement();
                NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");

                for( int i=0; i<nodeListPlacemark.getLength(); i++ ) {
                    NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                    for( int j=0; j<nodeListPlacemarkItem.getLength(); j++ ) {
                        if( nodeListPlacemarkItem.item(j).getNodeName().equals("LineString") ) {
                            //NodeList nod = nodeListPlacemarkItem.item(j).getChildNodes();

                            Log.d("debug", nodeListPlacemarkItem.item(j).getNodeValue());
                        }
                    }
                }
            }
        }); */

        Log.d("Test", "Param1: "+mParam1);

        return v;
    }

    public void set_zero(){
        start_lat=0.0;
        start_lon=0.0;
        end_lat=0.0;
        end_lon=0.0;

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

    //csv 파일 이용 데이터 읽어오기
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

    //가로등 마커 표시
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