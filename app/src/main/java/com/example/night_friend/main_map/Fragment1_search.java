package com.example.night_friend.main_map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.inputmethod.InputMethodManager;

import com.example.night_friend.R;
import com.example.night_friend.matching.BaseAdapterSearch;
import com.example.night_friend.matching.Locationitem;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPOIItem;

import java.util.ArrayList;

import static com.example.night_friend.main_map.Fragment4.code;

public class Fragment1_search extends AppCompatActivity {

    ListView mListView = null;
    BaseAdapterSearch mAdapter = null;
    ArrayList<Locationitem> destList= new ArrayList<Locationitem>(); ;
    Button bt_matching_search2;
    EditText et_dest2, et_dest;
    Handler mHandler;

    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment4_search);

        bt_matching_search2 = findViewById(R.id.bt_matching_search2);
        et_dest2 = findViewById(R.id.et_dest2);
        et_dest = findViewById(R.id.et_dest);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);


        // 검색 시 리스트 뷰에 반영
        bt_matching_search2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mHandler = new Handler();
                Thread t = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        // UI 작업 수행 X
                        mHandler.post(new Runnable(){
                            @Override
                            public void run() {
                                // UI 작업 수행 O
                                imm.hideSoftInputFromWindow(et_dest2.getWindowToken(),0);
                                setAdapter();
                                destList = CreateList();
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                t.start();

            }
        });



    }

    // 어댑터 설정
    public void setAdapter () {
        mAdapter = new BaseAdapterSearch(this, destList);

        mListView = (ListView) findViewById(R.id.lt_search);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                //Intent intent = new Intent(this, ct);
                Locationitem item = destList.get(position);
                //et_dest.setText(item.getPlace_name());

                String destPlace = item.getPlace_name();
                double destLat = item.getX();
                double destLon = item.getY();

                // 매칭 도착지 가져오기
                if(code==105){
                    et_dest2.setHint("목적지를 입력하세요");
                    //String destPlace = item.getPlace_name();
                    //double destLat = item.getX();
                    //double destLon = item.getY();
                    Fragment4.newInstance(destPlace, 105, destLat, destLon);

                }

                // 매칭 출발지 가져오기
                if(code == 106){
                    et_dest2.setHint("출발지를 입력하세요");
                    String startPlace = item.getPlace_name();
                    double userLat = item.getX();
                    double userLon = item.getY();
                    Fragment4.newInstance(startPlace,106,userLat, userLon);

                }


                /*
                Bundle bundle = new Bundle();
                bundle.putString("destPlace",item.getPlace_name());
                bundle.putDouble("destLat",item.getX());
                bundle.putDouble("destLon",item.getY());
                Fragment4 fragment4 = new Fragment4();
                fragment4.setArguments(bundle);
                fragmentTransaction.commit();
                Log.e("item","position: "+position);
                 */


                finish();


            }
        });



    }

    //검색 결과 리스트에 바로 반영되게 하기 위함
    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            mAdapter.notifyDataSetChanged();
        }
    };

    // destList 검색 리스트 생성
    public ArrayList<Locationitem> CreateList(){
        TMapData tMapData = new TMapData();
        String keyword = et_dest2.getText().toString();
        destList.clear();

        tMapData.findAllPOI(keyword, new TMapData.FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(final ArrayList<TMapPOIItem> poiItem) {
                for (int i = 0; i < poiItem.size(); i++) {
                    TMapPOIItem item = poiItem.get(i);
                    //addPoint(input.getText().toString(),item.getPOIPoint().getLatitude(),item.getPOIPoint().getLongitude());

                    String POIname = item.getPOIName();
                    String POIAddress = item.getPOIAddress().replace("null", "");
                    double POILat = item.getPOIPoint().getLatitude();
                    double POILon = item.getPOIPoint().getLongitude();

                    Locationitem dest = new Locationitem(POIname,POIAddress,POILat,POILon);
                    destList.add(dest);


                    Log.d("주소로 찾기", "POI Name" + item.getPOIName().toString() + ","
                            + "Address " + item.getPOIAddress().replace("null", "") + ","
                            + "Point" + item.getPOIPoint().getLatitude() + "Point" + item.getPOIPoint().getLongitude());


                }

                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }

        });
        return destList;
    }




}