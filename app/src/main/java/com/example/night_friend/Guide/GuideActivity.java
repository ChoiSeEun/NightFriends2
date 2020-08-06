package com.example.night_friend.Guide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;


import com.example.night_friend.R;
import com.example.night_friend.main_map.AutoRecordActivity;
import com.example.night_friend.matching.Matching_list;

import java.util.ArrayList;

public class GuideActivity extends AppCompatActivity  {

    EditText editText;
    ListView listView;
    GuideAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_road);

        listView = (ListView) findViewById(R.id.road_list);
        adapter = new GuideAdapter();
        adapter.addItem(new Matching_list("20분","***->***->***->***","안전지수:87%","매칭"));
        adapter.addItem(new Matching_list("26분","***->***->***->***","안전지수:79%","매칭"));
        adapter.addItem(new Matching_list("30분","***->***->****->****","안전지수:70%","매칭"));
        adapter.addItem(new Matching_list("38분","***->***->****->***","안전지수:68%","매칭"));
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(GuideActivity.this, "선택되었습니다", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), AutoRecordActivity.class);
                // 다음 페이지에서 자동녹화가 시작되도록 false 설정 변수 전달
                intent.putExtra("record",false);
                startActivity(intent);
            }
        });
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

            return view;
        }
    }
}
