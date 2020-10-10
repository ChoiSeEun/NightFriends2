package com.example.night_friend.main_map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.night_friend.R;
import com.example.night_friend.cctv.CCTV;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.lang.Math.sqrt;


public class Fragment1 extends Fragment implements TMapGpsManager.onLocationChangedCallback{

    //private static Context mContext = null;
    Thread mThread;
    Thread cThread;
    public static final int REQUEST_CODE_PERMISSIONS = 1000;

    private static FragmentActivity mContext = null;
    private boolean m_bTrackingMode = true;
    private TMapGpsManager tmapgps = null;
    private static TMapView mapView = null;
    private static BitmapFactory itmapFactory;
    private static Bitmap bitmap, bitmap2;
    private ImageButton bt_search;
    private Button lamp_btn, start_setting_btn, end_setting_btn;
    private EditText et_search;

    boolean recording;
    private Camera camera;
    private MediaRecorder mediaRecorder;
    private Button btn_record;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReference;
    private DatabaseReference dReference;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "code";

    private String mParam1;
    private Integer mParam2;

    static String startName = null;
    static String endName = null;
    static TMapPoint startPoint = null;
    static TMapPoint endPoint = null;

    static String find_Name1 = null;
    static String find_Name2 = null;
    static TMapPoint find_Point = null;

    private GpsInfo gps;
    private boolean isPermission = false;

    Double start_lat=null;
    Double start_lon=null;
    Double end_lat=null;
    Double end_lon=null;

    String passList_mid = null;
    String passList2 = null;
    String passList3 = null;

    Double destLat = null;
    Double destLon = null;
    String destPlace = null;

    double now_lat, now_long;

    ArrayList<String> pointList = new ArrayList<String>();

    private Location mLastlocation = null;
    public Handler GPShandler;
    public final static int REPEAT_DELAY = 5000;
    double gpsLatitude, gpsLongitude;

    private AlertDialog dialog;

    public Fragment1(){

    }

    public static Fragment1 newInstance(){
        final Fragment1 fragment1 = new Fragment1();
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

        lamp_btn = (Button)v.findViewById(R.id.bt_lamp_onoff);
        bt_search = (ImageButton)v.findViewById(R.id.bt_search);
        et_search = (EditText)v.findViewById(R.id.et_search);
        start_setting_btn = (Button)v.findViewById(R.id.bt_set_start);
        end_setting_btn = (Button)v.findViewById(R.id.bt_set_end);
        btn_record = (Button)v.findViewById(R.id.bt_record);
        surfaceView = (SurfaceView)v.findViewById(R.id.record_surface);
        Button bt_mail = (Button)v.findViewById(R.id.bt_mail_save);
        Button bt_pos = (Button)v.findViewById(R.id.bt_my_position);
        Button cctv_btn = (Button) v.findViewById(R.id.bt_cctv_onoff);
        Button bt_return_result = (Button) v.findViewById(R.id.bt_return_result);

        //출발지, 도착지 지정 버튼 안보이게
        start_setting_btn.setVisibility(View.GONE);
        end_setting_btn.setVisibility(View.GONE);
        bt_return_result.setVisibility(View.GONE);
        //   btn_record.setVisibility(View.VISIBLE);
        //   surfaceView.setVisibility(View.VISIBLE);

        mContext = getActivity();

        mapView = new TMapView(getActivity());

        mapView.setSKTMapApiKey("l7xx95f2e1d70e6a430484c5f00181f5ea93");
        LinearLayout linearLayoutTmap = (LinearLayout) v.findViewById(R.id.map_view);
        linearLayoutTmap.addView(mapView);

        mapView.setIconVisibility(true);

        //zoom 레벨
        mapView.setZoomLevel(15);
        mapView.setMapType(TMapView.MAPTYPE_STANDARD);
        mapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
            }

        }

        //setgps();
        mapView.setCenterPoint(127.038827,37.648942);

        //지도 처음 띄웠을 때 중심 좌표
        final TMapPoint initialPoint = mapView.getCenterPoint();
        Log.e("현재 위치 중심 좌표: ",""+initialPoint.getLatitude());


        //onMapPoint(mapView); //마커 표시 테스트

        //onMapCircle(mapView); //원 반경 표시 테스트

        MyCCTVAsyncTask1 cctvAsyncTask1=new MyCCTVAsyncTask1();
        LampAsyncTask lampAsyncTask=new LampAsyncTask();
        try {
            cctvlist=cctvAsyncTask1.execute().get();
            lampList=lampAsyncTask.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("cctvlist", String.valueOf(cctvlist.size()));
        Log.e("testlist", String.valueOf(lampList.size()));

        //readData_lamp();  //가로등 csv 파일 읽기
        //readData_cctv();
        set_zero();

        //현재위치로 이동하는 버튼
        bt_pos.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setgps();
            }
        });

        //검색창 터치시 => 검색 리스트뷰
        et_search.setOnTouchListener(new View.OnTouchListener() {
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
        });
/*
        //경로이탈 했는지 5초마다 확인하는 핸들러
        GPShandler = new Handler() {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                setgps();
                Log.e("gps 위치: ",gpsLatitude+","+gpsLongitude);
                boolean isOut = true;
                for(int i=0;i<pointList.size();i++){
                    double x1 = 0, y1 = 0;

                    //coordinates 배열은 "경도, 위도" 문자열 형태로 되어 있으므로 파싱 필요
                    String[] ele1 = pointList.get(i).split(",");

                    if(!ele1[0].isEmpty() && !ele1[1].isEmpty()) {
                        y1 = Double.parseDouble(ele1[0]);
                        x1 = Double.parseDouble(ele1[1]);
                        //Log.e("pointList x1,y1 ", "" + x1 + ", " + y1);
                    }


                    if(distance2(x1,y1,gpsLatitude,gpsLongitude)<15){
                        isOut = false;
                        Log.d("경로이탈 ","false");
                        break;
                    }

                }

                for(int i=0;i<extra_point.size();i++){
                    double x1 = 0, y1 = 0;

                    String[] ele1 = extra_point.get(i).split(",");

                    if(!ele1[0].isEmpty() && !ele1[1].isEmpty()) {
                        x1 = Double.parseDouble(ele1[0]);
                        y1 = Double.parseDouble(ele1[1]);
                        //Log.e("extra x1,y1 ", "" + x1 + ", " + y1);
                    }


                    if(distance2(x1,y1,gpsLatitude,gpsLongitude)<15){
                        isOut = false;
                        Log.d("경로이탈 ","false");
                        break;
                    }

                }

                if(isOut==true){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    dialog = builder.setMessage("경로를 이탈하였습니다.").setPositiveButton("확인",null).create();
                    dialog.show();
                    Log.e("경로이탈 ","true");
                }

                this.sendEmptyMessageDelayed(0, REPEAT_DELAY);        // REPEAT_DELAY 간격으로 계속해서 반복하게 만들어준다

            }

        };
*/
        //검색 버튼 클릭: 지도 검색 => 마커 표시 => 해당 마커 이름, 위도, 경도 얻기
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TMapData tmapdata = new TMapData();

                TMapMarkerItem mk = new TMapMarkerItem();
                TMapPoint mp = new TMapPoint(destLat,destLon);
                mapView.setCenterPoint(destLon,destLat, false);
                bitmap = itmapFactory.decodeResource(mContext.getResources(), R.drawable.poi);

                mk.setName(destPlace);
                mk.setIcon(bitmap);
                mk.setTMapPoint(mp);

                mk.setCalloutTitle(destPlace);
                mk.setCanShowCallout(true);

                mapView.addMarkerItem("markerItem" + destPlace, mk);

                Log.d("POI Name: ", destPlace + ", " +
                        "Point: " + destLat+", "+destLon);

                //마커 누르면 마커 이름, 위도, 경도 받아오기
                mapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
                    @Override
                    public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                        if(!arrayList.isEmpty()){
                            find_Name1 = arrayList.get(0).getName();
                            find_Point = arrayList.get(0).getTMapPoint();
                            start_setting_btn.setVisibility(View.VISIBLE);
                            end_setting_btn.setVisibility(View.VISIBLE);
                            Log.e("마커 이름: ",""+find_Name1);
                            Log.e("마커의 위도/경도: ",""+find_Point);
                        }

                        else{ //마커가 눌리지 않았을 때는 출발지, 도착지 지정 버튼 사라지게
                            start_setting_btn.setVisibility(View.GONE);
                            end_setting_btn.setVisibility(View.GONE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                        return false;
                    }
                });
            }
        });


        //출발지 지정 버튼
        start_setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Night_main) mContext).replaceFragment(Fragment2.newInstance(find_Name1, 101, find_Point.getLatitude(), find_Point.getLongitude()));
            }
        });

        //도착지 지정 버튼
        end_setting_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ((Night_main)mContext).replaceFragment(Fragment2.newInstance(find_Name1, 102, find_Point.getLatitude(), find_Point.getLongitude()));
            }
        });

/*
        dReference = mDatabase.getReference("lamp/"); // 변경값을 확인할 child 이름

        dReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long start = System.currentTimeMillis();
                //dataSnapshot.getKey();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //dataSnapshot.getKey();
                    Log.d("MainActivity", "Single ValueEventListener : " + snapshot.getValue());
                    //String str= (String) snapshot.getValue();

                    //Log.d("MainActivity", "getkey : " + snapshot.getKey());

                }
                long end = System.currentTimeMillis();
                Log.d("cctv_수행시간 : ", String.valueOf(( end - start )));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/

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
                    cThread=null;
                    isCctvMarker = false;
                    return;
                }
                if(cctvlist.size()==0){
                    //new LampAsyncTask().execute();
                    readData_cctv();
                }
                //지도에 표시된 마커가 없을 때 터치하면 마커 표시
                if (initialPoint != tpoint && isCctvMarker == false) {
                    cThread=new Thread(new Runnable()
                    {
                        @Override
                        public void run() {
                            //do loading data or whatever hard here
                            while(Thread.currentThread()==cThread) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        securityCctv(mapView);

                                    }
                                });
                                try{
                                    Thread.sleep(1000);
                                }catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });cThread.start();
                    isCctvMarker = true;
                }


            }
        });


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
                    mThread=null;
                    isLampMarker=false;
                    return;
                }
                if(lampList.size()==0){
                    //new LampAsyncTask().execute();
                    readData_lamp();
                }
                //지도에 표시된 마커가 없을 때 터치하면 마커 표시
                if(initialPoint != tpoint && isLampMarker==false){
                    mThread=new Thread(new Runnable()
                    {
                        @Override
                        public void run() {
                            //do loading data or whatever hard here
                            while(Thread.currentThread()==mThread) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        securityLight(mapView);

                                    }
                                });
                                try{
                                    Thread.sleep(1000);
                                }catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });mThread.start();

                    isLampMarker=true;
                }

            }
        });


        bt_return_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("result_msg","결과");
                getActivity().setResult(202,intent);
                getActivity().finish();
            }
        });

        //GuideActivity에서 넘어온 데이터 받기 (경로안내 polyline 그리기 위함)
        Bundle bundle = getArguments();

        if(bundle!=null&&bundle.getInt("code")==103) {
            start_lat = bundle.getDouble("start_lat");
            start_lon = bundle.getDouble("start_lon");
            end_lat = bundle.getDouble("end_lat");
            end_lon = bundle.getDouble("end_lon");

            //넘어온 데이터로 polyline 그리기
            TMapData tMapData = new TMapData();
            final TMapPoint point1 = new TMapPoint(start_lat, start_lon);
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
                            mapView.setCenterPoint(point1.getLongitude(),point1.getLatitude());
                        }

                    });
            recording = false;
            autoRecord();
            pointList = Fragment2.pointList; //coordinates 좌표(ArrayList)


            for(int i=0;i<pointList.size()-1;i++){
                double x1 = 0, y1 = 0, x2 = 0, y2 = 0;
                double dist = 0;

                //coordinates 배열은 "경도, 위도" 문자열 형태로 되어 double형 위도, 경도 얻기 위해서 파싱
                String[] ele1 = pointList.get(i).split(",");
                String[] ele2 = pointList.get(i+1).split(",");

                if(!ele1[0].isEmpty() && !ele1[1].isEmpty()) {
                    y1 = Double.parseDouble(ele1[0]);
                    x1 = Double.parseDouble(ele1[1]);
                    //Log.e("x1,y1 ", "" + x1 + ", " + y1);
                }
                if(!ele2[0].isEmpty() && !ele2[1].isEmpty()) {
                    y2 = Double.parseDouble(ele2[0]);
                    x2 = Double.parseDouble(ele2[1]);
                    //Log.e("x2,y2 ", "" + x2 + ", " + y2);
                }

                if(x1!=0 && x2!=0) {
                    //두 지점 사이 거리 구해서 40m 이상이면 중간 지점 좌표 별도 배열에 추가
                    distance(x1, y1, x2, y2);
                    Log.e("두 지점사이 거리: ", "" + dist+"m");
                }


            }

            GPShandler.sendEmptyMessage(0);
        }

        //passList_mid를 넘겨받을 경우
        if(bundle!=null&&bundle.getInt("code")==1031) {
            start_lat = bundle.getDouble("start_lat");
            start_lon = bundle.getDouble("start_lon");
            end_lat = bundle.getDouble("end_lat");
            end_lon = bundle.getDouble("end_lon");
            passList_mid = bundle.getString("passList_mid");
            Log.e("넘어온 passList_mid",passList_mid);
            String pass[] =passList_mid.split(",");
            Log.e("문자열split","[1]"+Double.parseDouble(pass[1])+" [2]"+Double.parseDouble(pass[0]));

            //넘어온 데이터로 polyline 그리기
            TMapData tMapData = new TMapData();
            final TMapPoint point1 = new TMapPoint(start_lat, start_lon);
            TMapPoint point2 = new TMapPoint(end_lat, end_lon);
            ArrayList passList = new ArrayList(); //경유지 List
            TMapPoint passPoint = new TMapPoint(Double.parseDouble(pass[1]), Double.parseDouble(pass[0]));
            passList.add(passPoint);


            //searchOption 부여하여 경로 그리기
            tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, point1, point2, passList, 10,
                    new TMapData.FindPathDataListenerCallback() {
                        @Override
                        public void onFindPathData(TMapPolyLine polyLine) {
                            Log.e("폴리라인 그리기","passList_mid");
                            mapView.addTMapPath(polyLine);
                            mapView.setCenterPoint(point1.getLongitude(),point1.getLatitude());
                        }

                    });
            recording = false;
            autoRecord();

            pointList = Fragment2.pointList_mid;

            for(int i=0;i<pointList.size()-1;i++){
                double x1 = 0, y1 = 0, x2 = 0, y2 = 0;
                double dist = 0;

                //coordinates 배열은 "경도, 위도" 문자열 형태로 되어 double형 위도, 경도 얻기 위해서 파싱
                String[] ele1 = pointList.get(i).split(",");
                String[] ele2 = pointList.get(i+1).split(",");

                if(!ele1[0].isEmpty() && !ele1[1].isEmpty()) {
                    y1 = Double.parseDouble(ele1[0]);
                    x1 = Double.parseDouble(ele1[1]);
                    //Log.e("x1,y1 ", "" + x1 + ", " + y1);
                }
                if(!ele2[0].isEmpty() && !ele2[1].isEmpty()) {
                    y2 = Double.parseDouble(ele2[0]);
                    x2 = Double.parseDouble(ele2[1]);
                    //Log.e("x2,y2 ", "" + x2 + ", " + y2);
                }

                if(x1!=0 && x2!=0) {
                    //두 지점 사이 거리 구해서 40m 이상이면 중간 지점 좌표 별도 배열에 추가
                    distance(x1, y1, x2, y2);
                    //Log.e("두 지점사이 거리: ", "" + dist+"m");
                }


            }

            GPShandler.sendEmptyMessage(0);


        }

        //passList2를 넘겨받을 경우
        if(bundle!=null&&bundle.getInt("code")==1032) {
            start_lat = bundle.getDouble("start_lat");
            start_lon = bundle.getDouble("start_lon");
            end_lat = bundle.getDouble("end_lat");
            end_lon = bundle.getDouble("end_lon");
            passList2 = bundle.getString("passList2");
            String pass[] =passList2.split(",");

            //넘어온 데이터로 polyline 그리기
            TMapData tMapData = new TMapData();
            final TMapPoint point1 = new TMapPoint(start_lat, start_lon);
            TMapPoint point2 = new TMapPoint(end_lat, end_lon);
            ArrayList passList = new ArrayList(); //경유지 List
            TMapPoint passPoint = new TMapPoint(Double.parseDouble(pass[1]), Double.parseDouble(pass[0]));
            passList.add(passPoint);

            //searchOption 부여하여 경로 그리기
            tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, point1, point2, passList, 10,
                    new TMapData.FindPathDataListenerCallback() {
                        @Override
                        public void onFindPathData(TMapPolyLine polyLine) {
                            Log.e("폴리라인 그리기","passList2");
                            mapView.addTMapPath(polyLine);
                            mapView.setCenterPoint(point1.getLongitude(),point1.getLatitude());
                        }

                    });

            recording = false;
            autoRecord();

            pointList = Fragment2.pointList2;

            for(int i=0;i<pointList.size()-1;i++){
                double x1 = 0, y1 = 0, x2 = 0, y2 = 0;
                double dist = 0;

                //coordinates 배열은 "경도, 위도" 문자열 형태로 되어 double형 위도, 경도 얻기 위해서 파싱
                String[] ele1 = pointList.get(i).split(",");
                String[] ele2 = pointList.get(i+1).split(",");

                if(!ele1[0].isEmpty() && !ele1[1].isEmpty()) {
                    y1 = Double.parseDouble(ele1[0]);
                    x1 = Double.parseDouble(ele1[1]);
                    //Log.e("x1,y1 ", "" + x1 + ", " + y1);
                }
                if(!ele2[0].isEmpty() && !ele2[1].isEmpty()) {
                    y2 = Double.parseDouble(ele2[0]);
                    x2 = Double.parseDouble(ele2[1]);
                    //Log.e("x2,y2 ", "" + x2 + ", " + y2);
                }

                if(x1!=0 && x2!=0) {
                    //두 지점 사이 거리 구해서 40m 이상이면 중간 지점 좌표 별도 배열에 추가
                    distance(x1, y1, x2, y2);
                    //Log.e("두 지점사이 거리: ", "" + dist+"m");
                }


            }

            GPShandler.sendEmptyMessage(0);

        }

        //passList3을 넘겨받을 경우
        if(bundle!=null&&bundle.getInt("code")==1033) {
            start_lat = bundle.getDouble("start_lat");
            start_lon = bundle.getDouble("start_lon");
            end_lat = bundle.getDouble("end_lat");
            end_lon = bundle.getDouble("end_lon");
            passList3 = bundle.getString("passList3");
            String pass[] =passList3.split(",");

            //넘어온 데이터로 polyline 그리기
            TMapData tMapData = new TMapData();
            final TMapPoint point1 = new TMapPoint(start_lat, start_lon);
            TMapPoint point2 = new TMapPoint(end_lat, end_lon);
            ArrayList passList = new ArrayList(); //경유지 List
            TMapPoint passPoint = new TMapPoint(Double.parseDouble(pass[1]), Double.parseDouble(pass[0]));
            passList.add(passPoint);

            //searchOption 부여하여 경로 그리기
            tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, point1, point2, passList, 10,
                    new TMapData.FindPathDataListenerCallback() {
                        @Override
                        public void onFindPathData(TMapPolyLine polyLine) {
                            Log.e("폴리라인 그리기","passList2");
                            mapView.addTMapPath(polyLine);
                            mapView.setCenterPoint(point1.getLongitude(),point1.getLatitude());
                        }

                    });

            recording = false;
            autoRecord();

            pointList = Fragment2.pointList3;

            for(int i=0;i<pointList.size()-1;i++){
                double x1 = 0, y1 = 0, x2 = 0, y2 = 0;
                double dist = 0;

                //coordinates 배열은 "경도, 위도" 문자열 형태로 되어 double형 위도, 경도 얻기 위해서 파싱
                String[] ele1 = pointList.get(i).split(",");
                String[] ele2 = pointList.get(i+1).split(",");

                if(!ele1[0].isEmpty() && !ele1[1].isEmpty()) {
                    y1 = Double.parseDouble(ele1[0]);
                    x1 = Double.parseDouble(ele1[1]);
                    //Log.e("x1,y1 ", "" + x1 + ", " + y1);
                }
                if(!ele2[0].isEmpty() && !ele2[1].isEmpty()) {
                    y2 = Double.parseDouble(ele2[0]);
                    x2 = Double.parseDouble(ele2[1]);
                    //Log.e("x2,y2 ", "" + x2 + ", " + y2);
                }

                if(x1!=0 && x2!=0) {
                    //두 지점 사이 거리 구해서 40m 이상이면 중간 지점 좌표 별도 배열에 추가
                    distance(x1, y1, x2, y2);
                    //Log.e("두 지점사이 거리: ", "" + dist+"m");
                }


            }

            GPShandler.sendEmptyMessage(0);

        }

        //검색 listView 액티비티에서 넘어오는 데이터 받기 위함
        if(bundle!=null && bundle.getInt("code")==104){
            destPlace = bundle.getString("destPlace");
            destLat = bundle.getDouble("destLat");
            destLon = bundle.getDouble("destLon");

            et_search.setText(destPlace);
        }


        //pointList와 extra_list 마커로 확인
        bt_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = 0;
                for (String i : pointList) {
                    if(!i.isEmpty()) {

                        TMapMarkerItem mk = new TMapMarkerItem();
                        String[] xy = i.split(",");

                        double y = Double.parseDouble(xy[0]);
                        double x = Double.parseDouble(xy[1]);

                        TMapPoint mp = new TMapPoint(x,y);
                        Bitmap bitmap = itmapFactory.decodeResource(mContext.getResources(), R.drawable.poi);

                        mk.setName("coordinates");
                        mk.setIcon(bitmap);
                        mk.setTMapPoint(mp);

                        mapView.addMarkerItem("coordinates_Marker" + i, mk);
                    }
                }

                for (String i : extra_point) {
                    if(!i.isEmpty()) {

                        TMapMarkerItem mk = new TMapMarkerItem();
                        String[] xy = i.split(",");

                        double x = Double.parseDouble(xy[0]);
                        double y = Double.parseDouble(xy[1]);

                        TMapPoint mp = new TMapPoint(x,y);
                        Bitmap bitmap = itmapFactory.decodeResource(mContext.getResources(), R.drawable.poi);

                        mk.setName("coordinates");
                        mk.setIcon(bitmap);
                        mk.setTMapPoint(mp);

                        mapView.addMarkerItem("coordinates_Marker" + i, mk);
                    }
                }


            }

        });
        /* -------------시설물 갯수 세기 위한 테스트----------------------
        TMapData tMapData = new TMapData();
        TMapPoint point1 = new TMapPoint(37.570342, 126.976510);
        TMapPoint point2 = new TMapPoint(37.573950, 126.973443);
        ArrayList passList = null;
        final ArrayList<String> pointList = new ArrayList<String>(); */

        //테스트 경로 그리기
        /*tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, point1, point2, passList, 00,
                new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine polyLine) {
                        Log.e("폴리라인 그리기","");
                        mapView.addTMapPath(polyLine);

                    }
                });



        //coordinates 좌표들 리스트(pointList)에 넣기
        tMapData.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, point1, point2, new TMapData.FindPathDataAllListenerCallback() {

            @Override
            public void onFindPathDataAll(Document document) {
                Element root = document.getDocumentElement();
                NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");

                Log.e("total Distance: ",""+root.getElementsByTagName("tmap:totalDistance").item(0).getTextContent().trim());
                for( int i=0; i<nodeListPlacemark.getLength(); i++ ) {
                    NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                    for( int j=0; j<nodeListPlacemarkItem.getLength(); j++ ) {
                        if( nodeListPlacemarkItem.item(j).getNodeName().equals("description") ) {
                            Log.d("Description", nodeListPlacemarkItem.item(j).getTextContent().trim() );
                        }
                        if( nodeListPlacemarkItem.item(j).getNodeName().equals("LineString") || nodeListPlacemarkItem.item(j).getNodeName().equals("Point")) {
                            //Log.d("LineString", nodeListPlacemarkItem.item(j).getTextContent().trim());
                            String s = nodeListPlacemarkItem.item(j).getTextContent().trim();
                            String[] newS = s.split("\\s");
                            for(String element : newS) {
                                pointList.add(element);
                                Log.d("LineString", element);
                            }


                            //TMapPOIItem item = (TMapPOIItem)poiItem.get(i);
                            //TMapMarkerItem mk = new TMapMarkerItem();

                            //bitmap = itmapFactory.decodeResource(mContext.getResources(), R.drawable.poi);

                            //mk.setName(item.getPOIName());
                            //mk.setIcon(bitmap);
                            //mk.setTMapPoint(mp);

                        }
                        if( nodeListPlacemarkItem.item(j).getNodeName().equals("Point") ) {
                            Log.d("Point", nodeListPlacemarkItem.item(j).getTextContent().trim() );
                        }
                    }
                }
            }

        }); */


        /*메일버튼 클릭시 길찾기 경로에 있는 coordinates 마커 표시 --- 가로등 갯수 세기 테스트용
        bt_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = 0;
                for (String i : pointList) {
                    if(!i.isEmpty()) {
                        //Log.e("pointList: ", "" + i + "//");
                        TMapMarkerItem mk = new TMapMarkerItem();
                        String[] xy = i.split(",");

                        double y = Double.parseDouble(xy[0]);
                        double x = Double.parseDouble(xy[1]);
                        //Log.e("x: ",""+x+"y:"+y);
                        TMapPoint mp = new TMapPoint(x,y);
                        Bitmap bitmap = itmapFactory.decodeResource(mContext.getResources(), R.drawable.poi);

                        mk.setName("coordinates");
                        mk.setIcon(bitmap);
                        mk.setTMapPoint(mp);

                        mapView.addMarkerItem("coordinates_Marker" + i, mk);
                    }
                }
                Log.e("-----count----",""+count);
            }

        });

        //현재위치 버튼 클릭시 길찾기 경로와 겹치는 가로등 마커 표시 --- 가로등 갯수 세기 테스트용
        bt_pos.setOnClickListener(new View.OnClickListener() {
            int count = 0;
            @Override
            public void onClick(View v) {
                TMapPoint mp = null;
                double x, y;
                for (int i = 0; i < cctvlist.size(); i++) {
                    if(cctvlist.get(i).getXpos()!=0 && cctvlist.get(i).getYpos()!=0) {

                        TMapMarkerItem mk = new TMapMarkerItem();
                        x = cctvlist.get(i).getYpos();
                        y = cctvlist.get(i).getXpos();
                        mp = new TMapPoint(x, y);
                        Bitmap bitmap = itmapFactory.decodeResource(mContext.getResources(), R.drawable.poi);

                        //Log.d("Location", "Latitude: " + x + ", Longitude: " + y);
                        mk.setName("CCTV");
                        mk.setIcon(bitmap);
                        mk.setTMapPoint(mp);

                        for (String ii : pointList) {
                            if(!ii.isEmpty()) {
                                String[] xy = ii.split(",");

                                double xx = Double.parseDouble(xy[1]);
                                double yy = Double.parseDouble(xy[0]);

                                //Log.e("xx = ",""+xx+" x: "+x);
                                if(x-xx<=0.00018 && x-xx>=-0.00018 && y-yy<=0.00018 && y-yy>=-0.00018){
                                    count++;
                                    mapView.addMarkerItem("cctv_Marker" + i, mk);
                                    Log.e("xpos: ",""+x+" ypos: "+y+" / count: "+count);
                                }
                            }
                        }

                    }
                }
                Log.e("count: ",""+count);
            }
        }); */

        return v;
    }

    //두 지점이 30m 이상 떨어져 있으면 중간 지점 좌표를 담는 배열
    ArrayList<String> extra_point = new ArrayList<>();

    //위도, 경도로 두 지점 사이 거리 구하여 30m 이상이면 중간 지점 좌표 구해서 별도 배열에 추가
    public void distance(double lat1, double lon1, double lat2, double lon2) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1609.344;

        //30m 미만이면 함수 빠져나옴
        if(dist<30){
            return;
        }

        //30m 이상이면 중간 지점 좌표 구해서 배열에 추가
        double mid_lat = midPoint(lat1,lon1,lat2,lon2).getLatitude();
        double mid_lon = midPoint(lat1,lon1,lat2,lon2).getLongitude();
        String mid_point_str = ""+mid_lat+","+mid_lon;
        extra_point.add(mid_point_str);
        //Log.d("extra_point","추가 됨");

        //30m 미만이 될 때 까지 재귀호출하여 중간 지점 좌표 추가
        distance(lat1,lon1,mid_lat,mid_lon);
        distance(lat2,lon2,mid_lat,mid_lon);
    }

    //두 지점 사이의 거리만 구해서 반환
    public double distance2(double lat1, double lon1, double lat2, double lon2) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1609.344;

        return dist;
    }

    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }


    public static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    //두 지점의 중간 지점 좌표 구하기
    public TMapPoint midPoint(double lat1,double lon1,double lat2,double lon2){

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        ////TMapPoint 형태로 위도, 경도 한꺼번에 반환할 수 있도록 함
        TMapPoint midpoint = new TMapPoint(Math.toDegrees(lat3), Math.toDegrees(lon3));

        //Log.d("midPoint: ",Math.toDegrees(lat3) + " " + Math.toDegrees(lon3));
        return midpoint;
    }

    public void set_zero() {
        start_lat = 0.0;
        start_lon = 0.0;
        end_lat = 0.0;
        end_lon = 0.0;

    }
    public void autoRecord(){
        btn_record.setVisibility(View.VISIBLE);
        TedPermission.with(mContext)
                .setPermissionListener(permission)
                .setRationaleMessage("자동녹화를 위하여 권한을 허용해주세요.")
                .setDeniedMessage("자동 녹화 권한이 거부되었습니다. 사용하시려면 설정 > 권한에서 허용해주세요")
                .setPermissions(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO)
                .check();
        btn_record.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(recording){
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    camera.lock();
                    recording = false;
                    mediaRecorder = null;
                    Toast.makeText(mContext,"자동 녹화가 종료되었습니다.",Toast.LENGTH_SHORT).show();
                }else{
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext,"자동 녹화가 시작되었습니다.",Toast.LENGTH_SHORT).show();
                            try {
                                mediaRecorder = new MediaRecorder();
                                camera.unlock();
                                mediaRecorder.setCamera(camera);
                                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER); //녹화 소리
                                mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                                mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P)); //동영상 화질
                                mediaRecorder.setOrientationHint(90); //촬영각도
                                mediaRecorder.setOutputFile("/sdcard/nightfriend.mp3");//저장경로
                                mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
                                mediaRecorder.prepare();
                                mediaRecorder.start();
                                recording = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(mContext, "오류가 발생하였습니다", Toast.LENGTH_SHORT).show();
                                mediaRecorder.release();
                                mediaRecorder = null;
                            }
                        }
                    });
                }
            }
        });
    }
    PermissionListener permission = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            camera = Camera.open();
            camera.setDisplayOrientation(90);
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(surfaceListener);
            surfaceHolder.setType(surfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            if(!recording)
                btn_record.performClick();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(mContext,"권한 거부",Toast.LENGTH_SHORT).show();
        }
    };
    private SurfaceHolder.Callback surfaceListener = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }
        private void refreshCamera(Camera camera) { //카메라 초기화
            if(surfaceHolder.getSurface() == null){
                return;
            }
            try{
                camera.stopPreview();
            }catch(Exception e){
                e.printStackTrace();
            }
            setCamera(camera);
        }
        private void setCamera(Camera cam){
            camera = cam;
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            refreshCamera(camera);
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };
    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            //현재위치의 좌표를 알수있는 부분
            if (location != null) {
                gpsLatitude = location.getLatitude();
                gpsLongitude = location.getLongitude();
                mapView.setLocationPoint(gpsLongitude, gpsLatitude);
                mapView.setCenterPoint(gpsLongitude, gpsLatitude);
                Log.d("test", gpsLatitude+","+gpsLongitude);

            }
            mLastlocation = location;
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    //gps 설정
    LocationManager lm = null;
    public void setgps() {
        Location location = null;
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        /*if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }*/

        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) { //네트워크가 활성화 되어 있다면
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }else{
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER); //마지막에 알려진 위치
        }

        //실제 단말기 gps 이용시 NETWORK_PROVIDER 사용
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자(실내에선 NETWORK_PROVIDER 권장)
                5000, // 통지사이의 최소 시간간격 (miliSecond)
                0, // 통지사이의 최소 변경거리 (m)
                mLocationListener);

    }

    //csv 파일 이용 데이터 읽어오기 (가로등)
    static public List<test> lampList=new ArrayList<>();
    //private ArrayList<test> testList2 = new ArrayList<>();
    private ArrayList<Double> xpos = new ArrayList<Double>();
    private ArrayList<Double> ypos = new ArrayList<Double>();
    private void readData_lamp() {
        dReference = mDatabase.getReference("lamp/"); // 변경값을 확인할 child 이름

        dReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long start = System.currentTimeMillis();
                dataSnapshot.getKey();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    test t = new test();
                    for (DataSnapshot deeperSnapshot : snapshot.getChildren()) {
                        //Log.d("MainActivity", "depper key : " + deeperSnapshot.getKey());
                        if(deeperSnapshot.getKey().equals("경도")){
                            //Log.d("MainActivity", "depper key : " + deeperSnapshot.getValue());
                            //Log.d("MainActivity", "depper key : " +deeperSnapshot.getValue());
                            t.setLatitude(Double.parseDouble(String.valueOf(deeperSnapshot.getValue())));
                            //Log.d("MainActivity", "depper key : " + t.getLatitude());
                        }else if (deeperSnapshot.getKey().equals("위도")){
                            t.setLongitude(Double.parseDouble(String.valueOf(deeperSnapshot.getValue())));

                        }
                    }
                    lampList.add(t);
                }
                long end = System.currentTimeMillis();
                Log.d("수행시간 : ", String.valueOf(( end - start )/1000));
                Log.d("lamplist", String.valueOf(lampList.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //가로등 마커 표시
    public void securityLight(final TMapView mapView){
        TMapPoint mp = null;
        double x, y;

        for (int i = 0; i < lampList.size(); i++) {
            //Log.i("dd",String.valueOf(testList.get(i).getLatitude()));
            if(lampList.get(i).getLatitude()!=0 && lampList.get(i).getLongitude()!=0) {
                TMapPoint leftTop = mapView.getLeftTopPoint();
                TMapPoint rightBottom = mapView.getRightBottomPoint();

                x = lampList.get(i).getLatitude();
                y = lampList.get(i).getLongitude();
                if(leftTop.getLatitude()>x && rightBottom.getLatitude()<x && leftTop.getLongitude()<y && rightBottom.getLongitude()>y){
                    mp = new TMapPoint(x, y);
                    TMapMarkerItem mk = new TMapMarkerItem();
                    Bitmap bitmap = itmapFactory.decodeResource(mContext.getResources(), R.drawable.poi);

                    mk.setName("가로등");
                    mk.setIcon(bitmap);
                    mk.setTMapPoint(mp);
                    mapView.addMarkerItem("lamp_markerItem" + i, mk);
                }
            }
        }
    }

    public class LampAsyncTask extends AsyncTask<String, Void, List<test>> {

        @Override
        protected List<test> doInBackground(String... strings) {
            //getXmlData();
            readData_lamp();
            return lampList;
        }

        @Override
        protected void onPostExecute(List<test> s) {
            super.onPostExecute(s);
            //securityLight2(mapView);
            //securityLight(mapView);
        }
    }

    //CCTV 데이터 읽기
    static public ArrayList<CCTV> cctvlist=new ArrayList<CCTV>();
    //CCTV cctv=new CCTV();
    private void readData_cctv(){
        mReference = mDatabase.getReference("cctv/"); // 변경값을 확인할 child 이름

        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long start = System.currentTimeMillis();
                dataSnapshot.getKey();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CCTV cctv=new CCTV();
                    for (DataSnapshot deeperSnapshot : snapshot.getChildren()) {


                        //Log.d("MainActivity", "depper key : " + deeperSnapshot.getKey());
                        if(deeperSnapshot.getKey().equals("경도")){

                            cctv.setYpos(Double.parseDouble(String.valueOf(deeperSnapshot.getValue())));

                        }else if (deeperSnapshot.getKey().equals("위도")){
                            cctv.setXpos(Double.parseDouble(String.valueOf(deeperSnapshot.getValue())));

                        }
                    }
                    cctvlist.add(cctv);
                }
                long end = System.currentTimeMillis();
                Log.d("수행시간 : ", String.valueOf(( end - start )/1000));
                Log.d("cctvlist", String.valueOf(cctvlist.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //cctv 화면에 띄우는 메소드
    public void securityCctv(final TMapView mapView){
        TMapPoint mp = null;
        double x, y;
        for (int i = 0; i < cctvlist.size(); i++) {
            if(cctvlist.get(i).getXpos()!=0 && cctvlist.get(i).getYpos()!=0) {

                TMapMarkerItem mk = new TMapMarkerItem();
                x = cctvlist.get(i).getXpos();
                y = cctvlist.get(i).getYpos();
                mp = new TMapPoint(x,y);
                //Log.d("cctv", "Latitude: " + x + ", Longitude: " + y);
                Bitmap bitmap = itmapFactory.decodeResource(mContext.getResources(), R.drawable.poi);


                mk.setName("cctv");
                mk.setIcon(bitmap);
                mk.setTMapPoint(mp);
                mapView.addMarkerItem("markerItem" + i, mk);

            }
        }
    }

    public class MyCCTVAsyncTask1 extends AsyncTask<String, Void, ArrayList<CCTV>> {

        @Override
        protected ArrayList<CCTV> doInBackground(String... strings) {

            readData_cctv();
            return cctvlist;
        }

        @Override
        protected void onPostExecute(ArrayList<CCTV> s) {
            super.onPostExecute(s);
            Log.d("cctv", String.valueOf(cctvlist.size()));
            //securityCctv(mapView);

        }
    }


    public void setGPS(TMapView tMapView){
        tmapgps = new TMapGpsManager(getActivity());
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);
        tmapgps.OpenGps();

        Log.e("현재위치 좌표: ",""+tmapgps.getLocation().getLatitude());
        tMapView.setTrackingMode(true);
        tMapView.setSightVisible(true);

        //return tmapgps.getLocation();
    }

    @Override
    public void onLocationChange(Location location) {
        if(m_bTrackingMode){
            mapView.setLocationPoint(location.getLongitude(), location.getLatitude());
            mapView.setCenterPoint(location.getLongitude(),location.getLatitude());
        }
        now_lat = location.getLatitude();
        now_long = location.getLongitude();
    }

    //마커 표시 테스트
    public void onMapPoint(TMapView mapView){

        //TMapPoint tpoint = new TMapPoint(37.498095, 127.027610);
        TMapPoint tpoint = new TMapPoint(37.569882, 126.977317);

        TMapMarkerItem tItem = new TMapMarkerItem();

        Bitmap bitmap = itmapFactory.decodeResource(mContext.getResources(), R.drawable.poi);

        tItem.setIcon(bitmap);
        tItem.setPosition(0.5f, 1.0f);
        tItem.setTMapPoint(tpoint);
        tItem.setName("강남역");
        //mapView.addMarkerItem("강남역", tItem);

        //mapView.setCenterPoint(127.027610,37.498095, false);
        mapView.setCenterPoint(126.977317,37.569882, false);

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
}