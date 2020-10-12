package com.example.firebase_test;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPOIItem;

import java.util.ArrayList;

public class Fragment4_search extends AppCompatActivity {

    ListView mListView = null;
    BaseAdapterSearch mAdapter = null;
    ArrayList<Locationitem> destList= new ArrayList<Locationitem>(); ;
    Button bt_matching_search2;
    EditText et_dest2, et_dest;

    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment4_search);

        bt_matching_search2 = findViewById(R.id.bt_matching_search2);
        et_dest2 = findViewById(R.id.et_dest2);
        et_dest = findViewById(R.id.et_dest);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        mAdapter = new BaseAdapterSearch(this, destList);

        mListView = (ListView) findViewById(R.id.lt_search);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        // 검색 시 리스트 뷰에 반영
        bt_matching_search2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(et_dest2.getWindowToken(),0);
                setAdapter();
                //destList = CreateList();
                CreateList();
                //mAdapter.notifyDataSetChanged();
                /*
                Runnable run = new Runnable(){
                    public void run(){


                    }
                };
                runOnUiThread(run);*/

            }
        });



    }


    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            mAdapter.notifyDataSetChanged();
        }
    };
    // 어댑터 설정
    public void setAdapter () {

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent intent = new Intent(this, ct);
                Locationitem item = destList.get(position);
                //et_dest.setText(item.getPlace_name());

                String destPlace = item.getPlace_name();
                double destLat = item.getX();
                double destLon = item.getY();

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
                Intent intent = new Intent(Fragment4_search.this, Night_main.class);
                intent.putExtra("destLat",destLat);
                intent.putExtra("destLon",destLon);
                intent.putExtra("destPlace",destPlace);
                intent.putExtra("code",104);
                startActivity(intent);

                /*
                Intent intent2 = new Intent(Fragment4_search.this, Fragment4.class);
                intent2.putExtra("destPlace", destPlace);
                intent2.putExtra("destLat",destLat);
                intent2.putExtra("destLon",destLon); */
                //Fragment4.newInstance(destPlace, 103, destLat, destLon);
                finish();


            }
        });



    }

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

    /*
    public void SearchLocation() {


        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String query = et_search.getText().toString();
                    int radius = 10000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
                    int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개

                    //String apikey = "a3e79593add64289f981c6e7b13d1a92";

                    //SearchInfo search= new SearchInfo(query, lat, lon, radius, page);
                    Fragment1.SearchInfo thread = new Fragment1.SearchInfo(query, lat, lon, radius, page);
                    thread.start();

                    List<Locationitem> itemList = thread.getList();
                    for (int i = 0; i < itemList.size(); i++) {
                        Locationitem item = itemList.get(i);


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

     */


}
