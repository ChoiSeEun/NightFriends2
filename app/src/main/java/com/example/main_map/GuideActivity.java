package com.example.main_map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;


import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class GuideActivity extends AppCompatActivity {

    private static TMapView mapView = null;

    EditText editText;
    ListView listView;
    GuideAdapter adapter;
    EditText startLocation, endLocation;

    Double start_lat = null;
    Double start_lon = null;
    Double end_lat = null;
    Double end_lon = null;
    Integer time = null;
    Double safe_score = null;

    String sName, eName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_road);

        startLocation = (EditText) findViewById(R.id.startText2);
        endLocation = (EditText) findViewById(R.id.endText2);

        //Fragment2에서 넘어온 데이터
        Intent intent = getIntent();
        start_lat = intent.getExtras().getDouble("start_lat");
        start_lon = intent.getExtras().getDouble("start_lon");
        end_lat = intent.getExtras().getDouble("end_lat");
        end_lon = intent.getExtras().getDouble("end_lon");
        sName = intent.getExtras().getString("startLocation");
        eName = intent.getExtras().getString("endLocation");
        time = intent.getExtras().getInt("time");
        safe_score = intent.getExtras().getDouble("safe_score");
        Log.e("안전지수: ",""+safe_score);

        startLocation.setText(sName);
        endLocation.setText(eName);

        listView = (ListView) findViewById(R.id.road_list);
        listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        adapter = new GuideAdapter();

        //리스트 추가
        adapter.addItem(new Matching_list(time,"***->***->***->***",safe_score,"매칭"));
        //adapter.addItem(new Matching_list(20,"***->***->***->***","87%","매칭"));
        //adapter.addItem(new Matching_list("26분","***->***->***->***","79%","매칭"));
        //adapter.addItem(new Matching_list("30분","***->***->****->****","70%","매칭"));
        //adapter.addItem(new Matching_list("38분","***->***->****->***","68%","매칭"));
        listView.setAdapter(adapter);
    }


    class GuideAdapter extends BaseAdapter{
        ArrayList<Matching_list> items = new ArrayList<Matching_list>();

        @Override
        public int getCount(){
            return items.size();
        }
        public void addItem(Matching_list item){
            items.add(item);
        }
        @Override
        public Object getItem(int position){
            return items.get(position);
        }
        @Override
        public long getItemId(int position){
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup){
            GuideListView view = new GuideListView(getApplicationContext());
            Matching_list item = items.get(position);
            view.setTimeText(item.getTime());
            view.setRoadText(item.getRoad());
            view.setScoreText(item.getScore());
            view.setMatchingBtn(item.getText());

            //리스트 클릭시 GuideActivity로 데이터 넘기기 & 화면 이동
            LinearLayout clickArea = (LinearLayout)view.findViewById(R.id.clickArea);
            clickArea.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Intent intent = new Intent(GuideActivity.this, Night_main.class);
                    intent.putExtra("start_lat",start_lat);
                    intent.putExtra("start_lon",start_lon);
                    intent.putExtra("end_lat",end_lat);
                    intent.putExtra("end_lon",end_lon);
                    intent.putExtra("code",103);
                    startActivity(intent);
                }
            });

            return view;
        }


    }


    class ValueHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
        }
    }

    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.guide_container, fragment);
        fragmentTransaction.commit();
    }

}
