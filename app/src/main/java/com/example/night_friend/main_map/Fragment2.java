package com.example.night_friend.main_map;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.night_friend.Guide.GuideActivity;
import com.example.night_friend.R;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.tensorflow.lite.Interpreter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import static java.lang.StrictMath.sqrt;

import kr.hyosang.coordinate.*;

public class Fragment2 extends Fragment implements View.OnClickListener {
    private static TMapView mapView = null;

    private static EditText startLocation;
    private static EditText endLocation;
    ListView listView;
    CheckBox cctvEmphasize;
    CheckBox lampEmphasize;
    CheckBox crimEmphasize;
    CheckBox entertainEmphasize;
    public Button bt_guide;

    private static final String ARG_PARAM1 = "locName";
    private static final String ARG_PARAM2 = "code";
    private static final String ARG_PARAM3 = "latitude";
    private static final String ARG_PARAM4 = "longitude";

    private String locName;
    //private String eName;
    private Integer Code;
    private Double lon;
    private Double lat;

    private static String startName;
    private static String endName;

    static TMapPoint point1 = null;
    static TMapPoint point2 = null;
    static Integer time = 0;
    static Integer time_mid = 0;
    static Integer time2 = 0;
    static Integer time3 = 0;
    //static Integer lamp_count=0;
    //static Integer cctv_count=0;

    static Interpreter model;
    float[][] input = new float[1][6];
    float[][] output = new float[1][1];
    Double resultScore;

    static final HashSet<String> pointList_set = new HashSet<String>();;
    static final HashSet<String> pointList_mid_set = new HashSet<String>();
    static final HashSet<String> pointList2_set = new HashSet<>();
    static final HashSet<String> pointList3_set = new HashSet<>();

    static final ArrayList<String> pointList = new ArrayList<>();
    static final ArrayList<String> pointList_mid = new ArrayList<>();
    static final ArrayList<String> pointList2 = new ArrayList<>();
    static final ArrayList<String> pointList3 = new ArrayList<>();
    
    static ArrayList<String> extra_point1 = new ArrayList<>();
    static ArrayList<String> extra_point2 = new ArrayList<>();
    static ArrayList<String> extra_point3 = new ArrayList<>();
    static ArrayList<String> extra_point4 = new ArrayList<>();


    static List passList_mid = null;
    static List passList2 = null;
    static List passList3 = null;
    static String passListStr_mid = null;
    static String passListStr2 = null;
    static String passListStr3 = null;

    final ArrayList<Double> EntertainX = new ArrayList<Double>();
    final ArrayList<Double> EntertainY = new ArrayList<Double>();
    final ArrayList<Double> SafeX = new ArrayList<Double>();
    final ArrayList<Double> SafeY = new ArrayList<Double>();

    private AlertDialog dialog;

    int lamp, cctv, crimY, crimN, entertainY, entertainN;

    public static Fragment2 newInstance(String locName, Integer code, Double lat, Double lon){
        final Fragment2 fragment2 = new Fragment2();

        //Fragment1에서 넘어온 데이터
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, locName);
        args.putInt(ARG_PARAM2,code);
        args.putDouble(ARG_PARAM3, lat);
        args.putDouble(ARG_PARAM4, lon);
        fragment2.setArguments(args);


        Log.e("받아온 이름: ",locName);
        Log.e("코드/위경도: ", code+" / "+lat+", "+lon);

        //받아온 데이터로 출발지, 목적지 설정
        if(code==101){
            point1 = new TMapPoint(lat, lon);
            startName = locName;
            Log.e("출발 위치 설정: ",startName);
            //Log.e("도착 위치: ", endName);
        }
        else if(code==102) {
            point2 = new TMapPoint(lat, lon);
            endName = locName;
            //Log.e("출발 위치: ", startName);
            Log.e("도착 위치 설정: ",endName);
        }

        //출발지, 도착지 모두 설정되었을 때 길찾기 api 실행(pointList에 coordinates 좌표 담기)
        if(point1!=null&&point2!=null) {
            new tmapAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        return fragment2;
    }

    //Fragment1에서 넘어온 데이터 설정(출발지/목적지 지정)
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            locName = getArguments().getString(ARG_PARAM1);
            Code = getArguments().getInt(ARG_PARAM2);
            lat = getArguments().getDouble(ARG_PARAM3);
            lon = getArguments().getDouble(ARG_PARAM4);
        }
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment2_guide, container, false);
        listView = (ListView) v.findViewById(R.id.road_list);

        mapView = new TMapView(getActivity());

        startLocation = (EditText) v.findViewById(R.id.et_start);
        endLocation = (EditText) v.findViewById(R.id.et_end);
        cctvEmphasize = (CheckBox) v.findViewById(R.id.ch_cctv);
        lampEmphasize = (CheckBox) v.findViewById(R.id.ch_lamp);
        crimEmphasize = (CheckBox) v.findViewById(R.id.ch_criminal);
        entertainEmphasize = (CheckBox)v.findViewById(R.id.ch_fun);
        bt_guide=(Button)v.findViewById(R.id.bt_call2);
        
        ImageButton bt_refresh =(ImageButton)v.findViewById(R.id.bt_refresh);
        ImageButton bt_search=(ImageButton)v.findViewById(R.id.bt_search_f2);
        ImageButton bt_cancel=(ImageButton)v.findViewById(R.id.bt_cancel_f2);
        Bundle bundle = getArguments();
        //가로등, cctv
        //final HashSet<Double> lamp_cnt_list = new HashSet<>();
        //final HashSet<Double> cctv_cnt_list = new HashSet<>();

        //유흥업소 데이터 api 불러오는 함수 호출 및 데이터 저장
        final ArrayList<String> EntertainAdress = new ArrayList<String>();
        //final ArrayList<Double> EntertainX = new ArrayList<Double>();
        //final ArrayList<Double> EntertainY = new ArrayList<Double>();
        getEntertainData(EntertainAdress,EntertainX,EntertainY);

        //안전지대 데이터 api 불러오는 함수 호출 및 데이터 저장
        final ArrayList<String> SafeAdress = new ArrayList<String>();
        //final ArrayList<Double> SafeX = new ArrayList<Double>();
        //final ArrayList<Double> SafeY = new ArrayList<Double>();
        getSafeData(SafeAdress,SafeX,SafeY);

        //안전지수 모델 삽입
        model = getTfliteInterpreter("safe_score_model.tflite");

        //bt_guide.setOnClickListener(this);


        //길찾기 버튼 클릭시 GuideActivity로 데이터 넘기기 & 화면 이동
        bt_guide.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                //출발지, 도착지 지정안하고 길찾기 버튼 누르면 경고
                if(point1==null||point2==null||point1.getLatitude()==0.0||point2.getLatitude()==0.0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    dialog = builder.setMessage("출발지와 목적지를 지정해주세요.").setPositiveButton("확인",null).create();
                    dialog.show();
                    return;
                }

                //옵션 선택 알림 표시
                if(cctvEmphasize.isChecked()||lampEmphasize.isChecked()||
                        crimEmphasize.isChecked()||entertainEmphasize.isChecked())
                    Toast.makeText(getActivity(),"선택하신 옵션을 우선적으로 안전지수가 계산됩니다.",Toast.LENGTH_LONG).show();
                
                add_extra_point(pointList,pointList_set, extra_point1);
                add_extra_point(pointList2,pointList2_set, extra_point2);
                add_extra_point(pointList3,pointList3_set, extra_point3);
                add_extra_point(pointList_mid, pointList_mid_set, extra_point4);

                
                double resultScore = getCount(pointList_set);
                double resultScore_mid = getCount(pointList_mid_set);
                double resultScore2 = getCount(pointList2_set);
                double resultScore3 = getCount(pointList3_set);

                Intent intent = new Intent(getActivity(), GuideActivity.class);
                if(bundle!=null&&bundle.getInt("code3")==105) {
                    intent.putExtra("code3", 105);
                }

                //((OnApplySelectedLister)activity).onCatagoryApplySelected(37.13255);
                intent.putExtra("start_lat", point1.getLatitude());
                intent.putExtra("start_lon", point1.getLongitude());
                intent.putExtra("end_lat", point2.getLatitude());
                intent.putExtra("end_lon", point2.getLongitude());
                intent.putExtra("time",time);
                intent.putExtra("time_mid",time_mid);
                intent.putExtra("time2",time2);
                intent.putExtra("time3",time3);
                intent.putExtra("startLocation", startName);
                intent.putExtra("endLocation", endName);
                intent.putExtra("safe_score", resultScore);
                intent.putExtra("safe_score_mid", resultScore_mid);
                intent.putExtra("safe_score2", resultScore2);
                intent.putExtra("safe_score3", resultScore3);
                intent.putExtra("passList_mid", passListStr_mid);
                intent.putExtra("passList2", passListStr2);
                intent.putExtra("passList3", passListStr3);
                startActivityForResult(intent,1);
            }

        });

        //출발지, 도착지 텍스트 설정
        if(startName!=null)
            startLocation.setText(startName);

        if(endName!=null){
            endLocation.setText(endName);
        }

        //검색 버튼 클릭시 지도 화면으로 이동
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Night_main)getActivity()).replaceFragment(Fragment1.newInstance());
            }
        });

        //X버튼 클릭시 설정된 데이터 초기화
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocation.setText(null);
                endLocation.setText(null);
                startName=null;
                endName=null;
                point1=null;
                point2=null;
            }
        });
        
        // 전환 버튼 클릭 시 출발지와 도착지 변경
        bt_refresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(point1!=null&&point2!=null){
                    String temp=startName;
                    startName = endName;
                    endName=temp;
                    startLocation.setText(startName);
                    endLocation.setText(endName);
                    Log.e("출발 위치 재 설정: ",startName);
                    Log.e("도착 위치 재 설정: ",endName);
                }
            }
        });

        if(bundle!=null&&bundle.getInt("code3")==105) {
            bt_guide.performClick();
        }
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode!=2){
            return;
        }
        if(requestCode==1){
            startName=null;
            endName=null;
            point1=null;
            point2=null;
            startLocation.setText(null);
            endLocation.setText(null);
        }else{
            Toast.makeText(getContext(),"REQUEST_CODE가 아님",Toast.LENGTH_SHORT).show();
        }
    }


    //안전지수 구하기
    public double getCount(HashSet<String> hashList){

        //안전지수 모델 삽입
        Interpreter model = null;
        model = getTfliteInterpreter("safe_score_model.tflite");
        float[][] input = new float[1][6];
        float[][] output = new float[1][1];
        Double resultScore;

        //가로등, cctv 경로와 겹치면 위도만 추가하는 리스트 => 리스트 길이=갯수
        final HashSet<Double> lamp_cnt_list = new HashSet<>();
        final HashSet<Double> cctv_cnt_list = new HashSet<>();

        double lamp_x = 0.0, lamp_y = 0.0;
        lamp = 0;
        lamp_cnt_list.clear();

        for (String ii : hashList) {
            boolean isCount = false;
            double x = 0.0, y = 0.0;

            if (!ii.isEmpty()) {
                String[] xy = ii.split(",");  // ,기준으로 문자열 자르기

                y = Double.parseDouble(xy[0]);
                x = Double.parseDouble(xy[1]);

                for (int i = 0; i < Fragment1.lampList.size(); i++) {

                    lamp_x = Fragment1.lampList.get(i).getLatitude();
                    lamp_y = Fragment1.lampList.get(i).getLongitude();

                    int k = 0;

                    if (x - lamp_x <= 0.0001 && x - lamp_x >= -0.0001 && y - lamp_y <= 0.0001 && y - lamp_y >= -0.0001) {
                        isCount = true;
                        break;
                        //lamp++;
                    }
                }
            }

            if (isCount == true) {
                lamp_cnt_list.add(lamp_x);
                //Log.e("lamp: ","xpos: "+lamp_x+" ypos: "+lamp_y+"  lampcount: "+lamp_cnt_list.size());
            }
        }

        //CCTV
        cctv = 0;
        double cctv_x=0.0, cctv_y;
        cctv_cnt_list.clear();

        for (String ii : hashList) {
            boolean isCount = false;
            double x = 0.0, y = 0.0;

            if (!ii.isEmpty()) {
                String[] xy = ii.split(",");  // ,기준으로 문자열 자르기

                y = Double.parseDouble(xy[0]);
                x = Double.parseDouble(xy[1]);

                for (int i = 0; i < Fragment1.cctvlist.size(); i++) {

                    cctv_x = Fragment1.cctvlist.get(i).getXpos();
                    cctv_y = Fragment1.cctvlist.get(i).getYpos();

                    int k = 0;

                    if (x - cctv_x <= 0.0001 && x - cctv_x >= -0.0001 && y - cctv_y <= 0.0001 && y - cctv_y >= -0.0001) {
                        isCount = true;
                        break;
                    }
                }
            }

            if (isCount == true) {
                cctv_cnt_list.add(cctv_x);
            }
        }

        //유흥업소와의 좌표 비교
        int entertainCount = 0;
        int entertainStandard = 5;
        if(entertainEmphasize.isChecked())
            entertainStandard = 10;
        //Log.e("유흥업소api확인",""+EntertainX.size());
        //Log.e("유흥업소api확인2",""+EntertainX.get(0));

        for (int i = 0; i < EntertainX.size(); i++) {
            for (String ii : hashList) {
                if (!ii.isEmpty()) {
                    String[] xy = ii.split(",");

                    double yy = Double.parseDouble(xy[0]);
                    double xx = Double.parseDouble(xy[1]);

                    double ex = EntertainX.get(i);
                    double ey = EntertainY.get(i);

                    //좌표변환
                    //라이브러리 자체에서 x,y가 변환되어 계산됨
                    CoordPoint pt = new CoordPoint(ey, ex);
                    CoordPoint ktmPt = TransCoord.getTransCoord(pt, TransCoord.COORD_TYPE_TM, TransCoord.COORD_TYPE_WGS84);

                    //Log.e("변환된 x",""+ktmPt.y);
                    //Log.e("경로의 x",""+xx);
                    //Log.e("변환된 y",""+ktmPt.x);
                    //Log.e("경로의 y",""+yy);
                    //Log.e("차이값계산",""+(xx-ktmPt.y));
                    if (xx - ktmPt.y <= 0.1 && xx - ktmPt.y >= -0.1 && yy - ktmPt.x <= 0.1 && yy - ktmPt.x >= -0.1) {
                        entertainCount++;
                    }
                }
            }
            if (entertainCount >= entertainStandard) { //유흥업소가 5개 이상이면 유흥지역이라고 판단
                entertainY = 1;
                entertainN = 0;
                break;
            }
            entertainY = 0;
            entertainN = 1;
        }

        //안전지대와의 좌표 비교
        int safeCount = 0;
        int safeStandard = 10;
        if(crimEmphasize.isChecked())
            safeStandard = 15;
        //Log.e("안전지대api확인",""+SafeX.size());
        //Log.e("안전지대api확인2",""+SafeX.get(0));
        for (int i = 0; i < SafeX.size(); i++) {
            for (String ii : hashList) {
                if (!ii.isEmpty()) {
                    String[] xy = ii.split(",");

                    double yy = Double.parseDouble(xy[0]);
                    double xx = Double.parseDouble(xy[1]);

                    double sx = SafeX.get(i);
                    double sy = SafeY.get(i);

                    if (xx - sx <= 0.1 && xx - sx >= -0.1 && yy - sy <= 0.1 && yy - sy >= -0.1) {
                        safeCount++;
                    }
                }
            }
            if (safeCount >= safeStandard) { //안전지대가 10 이상이면 안전지역이라고 판단 (임시적으로 안전지역==우범지역X으로 판단, 이후 데이터에 따라 수정 예정)
                crimY = 0;
                crimN = 1;
                break;
            }
            crimY = 1;
            crimN = 0;
        }
        if(lampEmphasize.isChecked())
            input[0][0] = (lamp_cnt_list.size())/2;
        else
            input[0][0] = lamp_cnt_list.size();
        if(cctvEmphasize.isChecked())
            input[0][1] = (cctv_cnt_list.size())/2;
        else
            input[0][1] = cctv_cnt_list.size();

        input[0][2] = crimY;
        input[0][3] = crimN;
        input[0][4] = entertainY;
        input[0][5] = entertainN;

        model.run(input, output);
        resultScore = Math.abs(Math.round(output[0][0] * 100) / 100.0);

        Log.e("설정된 가로등 갯수: ", "" + lamp_cnt_list.size());
        Log.e("설정된 cctv 갯수: ", "" + cctv_cnt_list.size());
        Log.e("유흥지역 판단 갯수:", "" + entertainCount);
        Log.e("안전지대 판단 갯수:", "" + safeCount);

        return resultScore;
    }

    //tmap 길찾기 api 통해 경로탐색 (티맵추천경로) - pointList에 coordinates 좌표 담기
    private static void getTmapRoadXml(TMapPoint startPoint, TMapPoint endPoint){
        double startX=startPoint.getLongitude(); //lon
        double startY=startPoint.getLatitude(); //lat
        double endX=endPoint.getLongitude();
        double endY=endPoint.getLatitude();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            String queryUrl = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&format=xml&callback=result&appKey=l7xx95f2e1d70e6a430484c5f00181f5ea93&startX=" + startX + "&startY=" + startY + "&endX=" + endX + "&endY=" + endY + "&startName=%EC%B6%9C%EB%B0%9C%EC%A7%80&endName=%EB%8F%84%EC%B0%A9%EC%A7%80&searchOption=0";

            URL url = new URL(queryUrl);//문자열로 된 요청 url을 URL 객체로 생성.
            InputStream is = url.openStream(); //url위치로 입력스트림 연결

            Document doc = builder.parse(is);

            Element root = doc.getDocumentElement();
            NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");

            time = Integer.parseInt(root.getElementsByTagName("tmap:totalTime").item(0).getTextContent().trim()) / 60;
            pointList.clear();
            pointList_set.clear();
            Log.e("Time 티맵 추천 경로: ",time+"분");
            Log.e("total Distance: ", "" + root.getElementsByTagName("tmap:totalDistance").item(0).getTextContent().trim());
            int t = 0;
            for (int i = 0; i < nodeListPlacemark.getLength(); i++) {
                NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                for (int j = 0; j < nodeListPlacemarkItem.getLength(); j++) {
                    if (nodeListPlacemarkItem.item(j).getNodeName().equals("LineString")) {//|| nodeListPlacemarkItem.item(j).getNodeName().equals("Point")) {
                        //Log.d("LineString", nodeListPlacemarkItem.item(j).getTextContent().trim());
                        String s = nodeListPlacemarkItem.item(j).getTextContent().trim();
                        //Log.e("LineString: ", s);
                        String[] newS = s.split("\\s"); //공백을 기준으로 문자열 자르기
                        for (String element : newS) {
                            pointList.add(element);
                            pointList_set.add(element);
                        }

                    }

                }
            }
        } catch(MalformedURLException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        } catch(ParserConfigurationException e){
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }

    //중간지점 경유(경유1) pointList에 coordinates 좌표 담기
    private static void getTmapRoadXml_mid(TMapPoint startPoint, TMapPoint endPoint){
        double startX=startPoint.getLongitude(); //lon
        double startY=startPoint.getLatitude(); //lat
        double endX=endPoint.getLongitude();
        double endY=endPoint.getLatitude();
        //StringBuffer buffer=new StringBuffer();
        passList_mid = midPoint(startX,startY,endX,endY);

        passListStr_mid = passList_mid.get(0)+","+passList_mid.get(1);
        Log.e("passListStr_mid: ",passListStr_mid);
        //Log.e("midPoint: ","lat: "+passList.get(0)+" lon: "+passList.get(1));

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            String queryUrl2 = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&format=xml&callback=result&appKey=l7xx95f2e1d70e6a430484c5f00181f5ea93&startX=" + startX + "&startY=" + startY + "&endX=" + endX + "&endY=" + endY + "&passList="+passListStr_mid+"&startName=%EC%B6%9C%EB%B0%9C%EC%A7%80&endName=%EB%8F%84%EC%B0%A9%EC%A7%80&searchOption=10";

            URL url = new URL(queryUrl2);//문자열로 된 요청 url을 URL 객체로 생성.
            InputStream is = url.openStream(); //url위치로 입력스트림 연결
            Document doc = builder.parse(is);

            Element root = doc.getDocumentElement();
            NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");

            time_mid = Integer.parseInt(root.getElementsByTagName("tmap:totalTime").item(0).getTextContent().trim()) / 60;
            pointList_mid.clear();
            pointList_mid_set.clear();
            Log.e("Time 경유 mid ",""+time_mid+"분");
            Log.e("total Distance mid 경유 ", "" + root.getElementsByTagName("tmap:totalDistance").item(0).getTextContent().trim());
            int t = 0;
            for (int i = 0; i < nodeListPlacemark.getLength(); i++) {
                NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                for (int j = 0; j < nodeListPlacemarkItem.getLength(); j++) {
                    if (nodeListPlacemarkItem.item(j).getNodeName().equals("LineString")) {//|| nodeListPlacemarkItem.item(j).getNodeName().equals("Point")) {
                        //Log.d("LineString", nodeListPlacemarkItem.item(j).getTextContent().trim());
                        String s = nodeListPlacemarkItem.item(j).getTextContent().trim();
                        //Log.e("LineString_mid: ", s);
                        String[] newS = s.split("\\s"); //공백을 기준으로 문자열 자르기
                        for (String element : newS) {
                            pointList_mid.add(element);
                            pointList_mid_set.add(element);
                        }

                    }

                }
            }
        } catch(MalformedURLException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        } catch(ParserConfigurationException e){
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }

    //(x1,y2) 경유 (경유2) pointList에 coordinates 좌표 담기
    private static void getTmapRoadXml2(TMapPoint startPoint, TMapPoint endPoint){
        double startX=startPoint.getLongitude(); //lon
        double startY=startPoint.getLatitude(); //lat
        double endX=endPoint.getLongitude();
        double endY=endPoint.getLatitude();

        passList2 = getPoint1(startX,startY,endX,endY);
        passListStr2 = passList2.get(0)+","+passList2.get(1);
        Log.e("passListStr2: ",passListStr2);

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            String queryUrl2 = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&format=xml&callback=result&appKey=l7xx95f2e1d70e6a430484c5f00181f5ea93&startX=" + startX + "&startY=" + startY + "&endX=" + endX + "&endY=" + endY + "&passList="+passListStr2+"&startName=%EC%B6%9C%EB%B0%9C%EC%A7%80&endName=%EB%8F%84%EC%B0%A9%EC%A7%80&searchOption=10";

            URL url = new URL(queryUrl2);//문자열로 된 요청 url을 URL 객체로 생성.
            InputStream is = url.openStream(); //url위치로 입력스트림 연결
            Document doc = builder.parse(is);

            Element root = doc.getDocumentElement();
            NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");

            time2 = Integer.parseInt(root.getElementsByTagName("tmap:totalTime").item(0).getTextContent().trim()) / 60;
            pointList2.clear();
            pointList2_set.clear();
            Log.e("Time 경유1 ",""+time2+"분");
            Log.e("total Distance 경유1 ", "" + root.getElementsByTagName("tmap:totalDistance").item(0).getTextContent().trim());
            int t = 0;
            for (int i = 0; i < nodeListPlacemark.getLength(); i++) {
                NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                for (int j = 0; j < nodeListPlacemarkItem.getLength(); j++) {
                    if (nodeListPlacemarkItem.item(j).getNodeName().equals("LineString")) {//|| nodeListPlacemarkItem.item(j).getNodeName().equals("Point")) {
                        //Log.d("LineString", nodeListPlacemarkItem.item(j).getTextContent().trim());
                        String s = nodeListPlacemarkItem.item(j).getTextContent().trim();
                        //Log.e("LineString_mid: ", s);
                        String[] newS = s.split("\\s"); //공백을 기준으로 문자열 자르기
                        for (String element : newS) {
                            pointList2.add(element);
                            pointList2_set.add(element);
                        }

                    }

                }
            }
        } catch(MalformedURLException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        } catch(ParserConfigurationException e){
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }

    //(x2,y1) 경유 (경유3) pointList에 coordinates 좌표 담기
    private static void getTmapRoadXml3(TMapPoint startPoint, TMapPoint endPoint){
        double startX=startPoint.getLongitude(); //lon
        double startY=startPoint.getLatitude(); //lat
        double endX=endPoint.getLongitude();
        double endY=endPoint.getLatitude();

        passList3 = getPoint2(startX,startY,endX,endY);
        passListStr3 = passList3.get(0)+","+passList3.get(1);
        Log.e("passListStr3: ",passListStr3);

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            String queryUrl2 = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&format=xml&callback=result&appKey=l7xx95f2e1d70e6a430484c5f00181f5ea93&startX=" + startX + "&startY=" + startY + "&endX=" + endX + "&endY=" + endY + "&passList="+passListStr3+"&startName=%EC%B6%9C%EB%B0%9C%EC%A7%80&endName=%EB%8F%84%EC%B0%A9%EC%A7%80&searchOption=10";

            URL url = new URL(queryUrl2);//문자열로 된 요청 url을 URL 객체로 생성.
            InputStream is = url.openStream(); //url위치로 입력스트림 연결
            Document doc = builder.parse(is);

            Element root = doc.getDocumentElement();
            NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");

            time3 = Integer.parseInt(root.getElementsByTagName("tmap:totalTime").item(0).getTextContent().trim()) / 60;
            pointList3.clear();
            pointList3_set.clear();
            Log.e("Time 경유2 ",""+time3+"분");
            Log.e("total Distance 경유2 ", "" + root.getElementsByTagName("tmap:totalDistance").item(0).getTextContent().trim());
            int t = 0;
            for (int i = 0; i < nodeListPlacemark.getLength(); i++) {
                NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                for (int j = 0; j < nodeListPlacemarkItem.getLength(); j++) {
                    if (nodeListPlacemarkItem.item(j).getNodeName().equals("LineString")) {//|| nodeListPlacemarkItem.item(j).getNodeName().equals("Point")) {
                        //Log.d("LineString", nodeListPlacemarkItem.item(j).getTextContent().trim());
                        String s = nodeListPlacemarkItem.item(j).getTextContent().trim();
                        //Log.e("LineString_mid: ", s);
                        String[] newS = s.split("\\s"); //공백을 기준으로 문자열 자르기
                        for (String element : newS) {
                            pointList3.add(element);
                            pointList3_set.add(element);
                        }

                    }

                }
            }
        } catch(MalformedURLException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        } catch(ParserConfigurationException e){
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }


    public static class tmapAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            getTmapRoadXml(point1, point2);
            getTmapRoadXml_mid(point1, point2);//경유지
            getTmapRoadXml2(point1, point2);//경유지
            getTmapRoadXml3(point1, point2);//경유지

            return null;
        }
    }

    //중간지점 좌표
    public static List midPoint(double lat1,double lon1,double lat2,double lon2){

        ArrayList passList = new ArrayList();
        double lat3 = (lat1+lat2)/2;
        double lon3 = (lon1+lon2)/2;

        passList.add(lat3);
        passList.add(lon3);
        Log.i("midPoint: ","lat: "+lat3+" lon: "+lon3);

        return passList;
    }

    //(x1,y2) 좌표 구하기
    public static List getPoint1(double lat1,double lon1,double lat2,double lon2){

        ArrayList passList = new ArrayList();

        passList.add(lat1);
        passList.add(lon2);

        return passList;
    }

    //(x2,y1) 좌표 구하기
    public static List getPoint2(double lat1,double lon1,double lat2,double lon2){

        ArrayList passList = new ArrayList();

        passList.add(lat2);
        passList.add(lon1);

        return passList;
    }

    //유흥업소 데이터 api 가져오기
    static void getEntertainData(final List EntertainAdress, final List EntertainX, final List EntertainY){
        final String key = "4177664554736565373770654e514d";
        final String query = "%EC%A0%84%EB%A0%A5%EB%A1%9C";
        new Thread(new Runnable() {
            @Override
            public void run() {
                EntertainApiData();
            }
            private void EntertainApiData() {
                StringBuffer buffer = new StringBuffer();
                int startIndex;
                int endIndex;
                for (int i=1;i<5000;i+=1000){ //api특성 상 나눠서 호출
                    startIndex=i;
                    if(startIndex==4001)
                        endIndex=4854; //데이터의 최대 개수 (그 이상 호출 시 오류)
                    else
                        endIndex=i+999;
                    String queryUrl = "http://openapi.seoul.go.kr:8088/"+key+"/xml/LOCALDATA_072302/"+startIndex+"/"+endIndex+"/";
                    try{
                        URL url = new URL(queryUrl);
                        InputStream is = url.openStream();

                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        XmlPullParser xpp = factory.newPullParser();
                        xpp.setInput(new InputStreamReader(is,"UTF-8"));

                        String tag;
                        xpp.next();
                        int eventType = xpp.getEventType();
                        while(eventType!=XmlPullParser.END_DOCUMENT){
                            switch (eventType){
                                case XmlPullParser.START_DOCUMENT:
                                    buffer.append("파싱 시작\n\n");
                                    break;
                                case XmlPullParser.START_TAG:
                                    tag = xpp.getName();

                                    if(tag.equals("row"));
                                    else if(tag.equals("SITEWHLADDR")){
                                        xpp.next();
                                        EntertainAdress.add(xpp.getText());
                                    }
                                    else if(tag.equals("X")){
                                        xpp.next();
                                        double tmp = Double.parseDouble(xpp.getText());
                                        EntertainX.add(tmp);
                                    }
                                    else if(tag.equals("Y")){
                                        xpp.next();
                                        double tmp = Double.parseDouble(xpp.getText());
                                        EntertainY.add(tmp);
                                    }
                                    break;
                                case XmlPullParser.TEXT:
                                    break;
                                case XmlPullParser.END_TAG:
                                    tag = xpp.getName();
                                    if(tag.equals("row")) buffer.append("\n");
                                    break;
                            }
                            eventType = xpp.next();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    buffer.append("파싱 끝\n\n");
                }
            }
        }).start();
    }
    //안전지대 데이터 api 가져오기
    static void getSafeData(final List SafeAdress, final List SafeX, final List SafeY){
        final String Id = "10000348";
        final String Key ="5d17ee51f95947ca";
        final String query = "%EC%A0%84%EB%A0%A5%EB%A1%9C";
        new Thread(new Runnable() {
            @Override
            public void run() {
                SafeApiData();
            }
            private void SafeApiData() {
                StringBuffer buffer = new StringBuffer();
                String queryUrl = "http://www.safe182.go.kr/api/lcm/safeMap.do?esntlId="+Id+
                        "&authKey="+Key+"&pageIndex=132&pageUnit=100&xmlUseYN=Y";

                try{
                    URL url = new URL(queryUrl);
                    InputStream is = url.openStream();

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(new InputStreamReader(is,"UTF-8"));

                    String tag;
                    xpp.next();
                    int eventType = xpp.getEventType();
                    while(eventType!=XmlPullParser.END_DOCUMENT){
                        switch (eventType){
                            case XmlPullParser.START_DOCUMENT:
                                buffer.append("파싱 시작\n\n");
                                break;
                            case XmlPullParser.START_TAG:
                                tag = xpp.getName();
                                if(tag.equals("list"));
                                else if(tag.equals("adres")){
                                    xpp.next();
                                    SafeAdress.add(xpp.getText());
                                }
                                else if(tag.equals("lcinfoLa")){
                                    xpp.next();
                                    double tmp = Double.parseDouble(xpp.getText());
                                    SafeX.add(tmp);
                                }
                                else if(tag.equals("lcinfoLo")){
                                    xpp.next();
                                    double tmp = Double.parseDouble(xpp.getText());
                                    SafeY.add(tmp);
                                }
                                break;
                            case XmlPullParser.TEXT:
                                break;
                            case XmlPullParser.END_TAG:
                                tag=xpp.getName();
                                if(tag.equals("list")) buffer.append("\n");
                                break;
                        }
                        eventType = xpp.next();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                buffer.append("파싱 끝\n");
            }
        }).start();
    }

    @Override
    public void onClick(View v){
        Intent intent = new Intent(getActivity(),GuideActivity.class);
        startActivity(intent);
    }
    
    public static void add_extra_point(ArrayList<String> pointList, HashSet<String> hashSet, ArrayList<String> extra_list){
        for(int i=0;i<pointList.size()-1;i++){
            double x1 = 0, y1 = 0, x2 = 0, y2 = 0;
            //double dist = 0;

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
                distance(hashSet, extra_list, x1, y1, x2, y2);
                //Log.e("두 지점사이 거리: ", "" + dist+"m");
            }


        }
    }

    public static void distance(HashSet<String> hashSet, ArrayList<String> extra_list, double lat1, double lon1, double lat2, double lon2) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1609.344;


        if(dist>30) {
            //30m 이상이면 중간 지점 좌표 구해서 배열에 추가

            double mid_lat = (double) midPoint_array(lat1, lon1, lat2, lon2).get(0);
            double mid_lon = (double) midPoint_array(lat1, lon1, lat2, lon2).get(1);
            String mid_point_str = "" + mid_lon + "," + mid_lat;

            hashSet.add(mid_point_str);
            extra_list.add(mid_point_str);
            Log.d("extra_point",mid_point_str+" dist: "+dist);

            //30m 미만이 될 때 까지 재귀호출하여 중간 지점 좌표 추가
            distance(hashSet,extra_list, lat1,lon1,mid_lat,mid_lon);
            distance(hashSet,extra_list, lat2,lon2,mid_lat,mid_lon);
        }

    }

    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }


    public static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    public static ArrayList midPoint_array(double lat1, double lon1, double lat2, double lon2){

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        //TMapPoint 형태로 위도, 경도 한꺼번에 반환할 수 있도록 함
        ArrayList<Double> midpoint = new ArrayList<Double>();
        midpoint.add(Math.toDegrees(lat3));
        midpoint.add(Math.toDegrees(lon3));

        //Log.d("midPoint: ",midpoint.get(0) + " " + midpoint.get(1));

        return midpoint;
    }

    //딥러닝 모델 관련
    private  Interpreter getTfliteInterpreter(String modelPath){
        try{
            return new Interpreter(loadModelFile(getActivity(), modelPath));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private MappedByteBuffer loadModelFile(Activity activity, String modelPath) throws IOException{
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset,declaredLength);
    }

}
