package com.example.night_friend.Guide;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.night_friend.R;
import com.example.night_friend.main_map.Fragment4;
import com.example.night_friend.main_map.Night_main;
import com.example.night_friend.matching.Matching_list;
import com.example.night_friend.matching.Matching_map;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;

public class GuideActivity extends AppCompatActivity {

    private static TMapView mapView = null;
    static final int REQUEST_CODE=201;

    EditText editText;
    ListView listView;
    GuideAdapter adapter;
    EditText startLocation, endLocation;

    Double start_lat = null;
    Double start_lon = null;
    Double end_lat = null;
    Double end_lon = null;
    Integer time, time_mid, time2, time3 = null;
    Double safe_score, safe_score_mid, safe_score2, safe_score3  = null;
    String passList_mid, passList2, passList3 = null;

    String sName, eName;

    ImageButton bt_search, bt_delete;
    Button bt_matching_search;
    Switch matching_st;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_road);

        startLocation = (EditText) findViewById(R.id.startText2);
        endLocation = (EditText) findViewById(R.id.endText2);
        matching_st=(Switch)findViewById(R.id.switch2);
        matching_st.setChecked(false);


        //출발지, 도착지 검색 => 지도화면으로 이동
        bt_search = (ImageButton)findViewById(R.id.bt_search_gr);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideActivity.this, Night_main.class);
                startActivity(intent);
            }
        });

        //길찾기 정보 삭제 => fragment2로 돌아가기 & 출발지, 도착지 삭제
        bt_delete = (ImageButton)findViewById(R.id.bt_cancel_gr);
        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(2,intent);
                finish();
            }
        });

        //경로 옵션 변경 (fragment2로 돌아가기 & 출발지, 도착지 유지)
        bt_matching_search = (Button)findViewById(R.id.bt_matching_search);
        bt_matching_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Fragment2에서 넘어온 데이터
        Intent intent = getIntent();
        matching_st.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Intent intent_m = new Intent(getApplication(), Night_main.class);
                    if (intent.getExtras().getInt("code3") == 105) {
                        intent_m.putExtra("key", 66);
                        startActivity(intent_m);
                    } else {

                        Fragment4.newInstance(sName, 104, start_lat, start_lon);
                        Fragment4.newInstance(eName, 103, end_lat, end_lon);

                        intent_m.putExtra("key", 44);
                        startActivity(intent_m);
                    }
                }

            }
        });

        start_lat = intent.getExtras().getDouble("start_lat");
        start_lon = intent.getExtras().getDouble("start_lon");
        end_lat = intent.getExtras().getDouble("end_lat");
        end_lon = intent.getExtras().getDouble("end_lon");
        sName = intent.getExtras().getString("startLocation");
        eName = intent.getExtras().getString("endLocation");
        time = intent.getExtras().getInt("time");
        safe_score = intent.getExtras().getDouble("safe_score");
        Log.e("안전지수: ",""+safe_score);

        safe_score_mid = intent.getExtras().getDouble("safe_score_mid");
        time_mid = intent.getExtras().getInt("time_mid");
        passList_mid = intent.getExtras().getString("passList_mid");

        safe_score2 = intent.getExtras().getDouble("safe_score2");
        time2 = intent.getExtras().getInt("time2");
        passList2 = intent.getExtras().getString("passList2");

        safe_score3 = intent.getExtras().getDouble("safe_score3");
        time3 = intent.getExtras().getInt("time3");
        passList3 = intent.getExtras().getString("passList3");


        startLocation.setText(sName);
        endLocation.setText(eName);

        listView = (ListView) findViewById(R.id.road_list);
        listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        adapter = new GuideAdapter();

        //리스트 추가
        adapter.addItem(new Matching_list(time,"최단 경로",safe_score,"최단 경로"));
        adapter.addItem(new Matching_list(time_mid,"밤길친구 경로1",safe_score_mid,"밤길친구 경로1"));
        adapter.addItem(new Matching_list(time2,"밤길친구 경로2",safe_score2,"밤길친구 경로2"));
        adapter.addItem(new Matching_list(time3, "밤길친구 경로3",safe_score3,"밤길친구 경로3"));
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
        public View getView(final int position, View convertView, ViewGroup viewGroup){
            GuideListView view = new GuideListView(getApplicationContext());
            Matching_list item = items.get(position);
            view.setTimeText(item.getTime());
            view.setRoadText(item.getRoad());
            view.setScoreText(item.getScore());
            //view.setMatchingBtn(item.getText());

            //리스트 클릭시 Night_main으로 데이터 넘기기 & 화면 이동
            LinearLayout clickArea = (LinearLayout)view.findViewById(R.id.clickArea);
            clickArea.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Toast.makeText(v.getContext(),items.get(position).getText(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(GuideActivity.this, Night_main.class);

                    //각 리스트뷰 눌렀을때마다 다르게 데이터 넘기기(2~4번째)
                    if(getItemId(position)==1){
                        //Log.e("리스트뷰 클릭 ","("+getItemId(position)+")"+passList_mid);
                        intent.putExtra("passList", passList_mid);
                        intent.putExtra("code2",1031);
                    } else if(getItemId(position)==2){
                        //Log.e("리스트뷰 클릭 ","("+getItemId(position)+")"+passList2);
                        intent.putExtra("passList", passList2);
                        intent.putExtra("code2",1032);
                    } else if(getItemId(position)==3){
                        //Log.e("리스트뷰 클릭 ","("+getItemId(position)+")"+passList3);
                        intent.putExtra("passList", passList3);
                        intent.putExtra("code2",1033);
                    }
                    //공통적으로 넘기는 값
                    intent.putExtra("start_lat",start_lat);
                    intent.putExtra("start_lon",start_lon);
                    intent.putExtra("end_lat",end_lat);
                    intent.putExtra("end_lon",end_lon);
                    intent.putExtra("code",103);


                    startActivityForResult(intent,REQUEST_CODE);
                }
            });

            return view;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode!=202){
            return;
        }
        if(requestCode==REQUEST_CODE){
            String resultMsg = data.getStringExtra("result_msg");
            Log.e("result_msg", resultMsg);
        }else{
            Toast.makeText(GuideActivity.this,"REQUEST_CODE가 아님",Toast.LENGTH_SHORT).show();
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
