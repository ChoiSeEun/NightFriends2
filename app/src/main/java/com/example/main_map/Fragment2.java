package com.example.main_map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.tensorflow.lite.Interpreter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static java.sql.Types.NULL;

public class Fragment2 extends Fragment implements View.OnClickListener {
    private static TMapView mapView = null;

    private static EditText startLocation;
    private static EditText endLocation;
    ListView listView;

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

    static Interpreter model;
    float[][] input = new float[1][6];
    float[][] output = new float[1][1];
    Double resulttemp;

    int lamp, cctv, crimY, crimN, entertainY, entertainN;

    public static Fragment2 newInstance(String locName, Integer code, Double lat, Double lon){
        final Fragment2 fragment2 = new Fragment2();

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, locName);
        args.putInt(ARG_PARAM2,code);
        args.putDouble(ARG_PARAM3, lat);
        args.putDouble(ARG_PARAM4, lon);
        fragment2.setArguments(args);


        Log.e("받아온 이름: ",locName);
        Log.e("코드/위경도: ", code+" / "+lat+", "+lon);


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


        TMapData tMapData = new TMapData();

        //경로안내 정보
        tMapData.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, point1, point2, new TMapData.FindPathDataAllListenerCallback() {

            @Override
            public void onFindPathDataAll(Document document) {
                Element root = document.getDocumentElement();
                NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");

                time = Integer.parseInt(root.getElementsByTagName("tmap:totalTime").item(0).getTextContent().trim())/60;

                Log.e("total Distance: ",""+root.getElementsByTagName("tmap:totalDistance").item(0).getTextContent().trim());
                for( int i=0; i<nodeListPlacemark.getLength(); i++ ) {
                    NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                    for( int j=0; j<nodeListPlacemarkItem.getLength(); j++ ) {
                        if( nodeListPlacemarkItem.item(j).getNodeName().equals("description") ) {
                            Log.d("debug", nodeListPlacemarkItem.item(j).getTextContent().trim() );
                        }
                    }
                }
            }

        });


        return fragment2;
    }

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


        Button bt_guide=(Button)v.findViewById(R.id.bt_call2);
        ImageButton bt_search=(ImageButton)v.findViewById(R.id.bt_search_f2);
        ImageButton bt_cancel=(ImageButton)v.findViewById(R.id.bt_cancel_f2);

        //bt_guide.setOnClickListener(this);

        //길찾기 버튼 클릭시 GuideActivity로 데이터 넘기기 & 화면 이동
        bt_guide.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = new Intent(getActivity(), GuideActivity.class);

                //((OnApplySelectedLister)activity).onCatagoryApplySelected(37.13255);
                intent.putExtra("start_lat", point1.getLatitude());
                intent.putExtra("start_lon", point1.getLongitude());
                intent.putExtra("end_lat", point2.getLatitude());
                intent.putExtra("end_lon", point2.getLongitude());
                intent.putExtra("time",time);
                intent.putExtra("startLocation", startName);
                intent.putExtra("endLocation", endName);
                intent.putExtra("safe_score", resulttemp);
                startActivity(intent);
            }

        });

        //출발지, 도착지 텍스트 설정
        if(Code!=null) {
            startLocation.setText(startName);
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

        //안전지수 모델 삽입
        model = getTfliteInterpreter("converted_model.tflite");

        lamp = 10;
        cctv = 2;
        crimY = 1;
        crimN = 0;
        entertainY = 1;
        entertainN = 0;

        input[0][0] = lamp;
        input[0][1] = cctv;
        input[0][2] = crimY;
        input[0][3] = crimN;
        input[0][4] = entertainY;
        input[0][5] = entertainN;

        model.run(input, output);
        resulttemp = Math.round(output[0][0]*100)/100.0;  //소수점 둘째자리까지 나타냄

        return v;
    }

    @Override
    public void onClick(View v){
        Intent intent = new Intent(getActivity(),GuideActivity.class);
        startActivity(intent);
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