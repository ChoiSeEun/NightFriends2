package com.example.main_map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Fragment2 extends Fragment implements View.OnClickListener {
    private static TMapView mapView = null;

    static EditText startLocation;
    EditText endLocation;
    ListView listView;

    private static final String ARG_PARAM1 = "startName";
    private static final String ARG_PARAM2 = "endName";
    private static final String ARG_PARAM3 = "code";
    private static final String ARG_PARAM4 = "latitude";
    private static final String ARG_PARAM5 = "longitude";

    private String sName;
    private String eName;
    private Integer Code;
    private Double lon;
    private Double lat;

    static TMapPoint point1 = null;
    static TMapPoint point2 = null;
    static Integer time = 0;

    public static Fragment2 newInstance(String startName, String endName, Integer code, Double lat, Double lon){
        final Fragment2 fragment2 = new Fragment2();

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, startName);
        args.putString(ARG_PARAM2, endName);
        args.putInt(ARG_PARAM3,code);
        args.putDouble(ARG_PARAM4, lat);
        args.putDouble(ARG_PARAM5, lon);
        fragment2.setArguments(args);

        EditText editText = (EditText) startLocation.findViewById(R.id.et_start);

        Log.e("받아온 이름: ","출발-"+startName+" 도착-"+endName);
        Log.e("코드/위경도: ", code+" / "+lat+", "+lon);
        editText.setText(startName);


        if(code==101){
            point1 = new TMapPoint(lat, lon);
            Log.e("출발 위치 설정: ",lat+", "+lon);
        }
        else if(code==102) {
            point2 = new TMapPoint(lat, lon);
            Log.e("도착 위치 설정: ",lat+", "+lon);
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment2_guide, container, false);
        listView = (ListView) v.findViewById(R.id.road_list);

        //LayoutInflater layoutInflater = (LayoutInflater)((Night_main)getActivity()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View view = layoutInflater.inflate(R.layout.fragment1_map,null);

        mapView = new TMapView(getActivity());

        startLocation = (EditText) v.findViewById(R.id.et_start);
        endLocation = (EditText) v.findViewById(R.id.et_end);


        Button bt_guide=(Button)v.findViewById(R.id.bt_call2);
        ImageButton bt_search_s=(ImageButton)v.findViewById(R.id.bt_search_start);
        ImageButton bt_search_e=(ImageButton)v.findViewById(R.id.bt_search_end);

        //bt_guide.setOnClickListener(this);

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
                intent.putExtra("startLocation", sName);
                intent.putExtra("endLocation", eName);
                startActivity(intent);
            }

        });

        bt_search_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String start = startLocation.getText().toString();
                Log.d("출발지: ",start);
                ((Night_main)getActivity()).replaceFragment(Fragment1.newInstance(start, 101));
            }
        });

        bt_search_e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String end = endLocation.getText().toString();
                Log.d("도착지: ",end);
                ((Night_main)getActivity()).replaceFragment(Fragment1.newInstance(end, 102));
            }
        });

        //출발지/도착지 텍스트 설정
        if(sName!=null && eName==null){
            startLocation.setText(sName);
        }
        else if(eName!=null){
            startLocation.setText(sName);
            endLocation.setText(eName);
        }


        return v;
    }

    @Override
    public void onClick(View v){
        Intent intent = new Intent(getActivity(),GuideActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            sName = getArguments().getString(ARG_PARAM1);
            eName = getArguments().getString(ARG_PARAM2);
            Code = getArguments().getInt(ARG_PARAM3);
            lat = getArguments().getDouble(ARG_PARAM4);
            lon = getArguments().getDouble(ARG_PARAM5);
        }
    }


    /*
    //Activity로 데이터 전달을 위한 인터페이스
    public interface OnMyListener{
        void onReceivedData(int lat);
    }

    private OnMyListener mOnMyListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnMyListener){
            mOnMyListener = (OnMyListener) context;
        } else{
            throw new RuntimeException(context.toString()+" must implement OnMyListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mOnMyListener=null;
    } */
}