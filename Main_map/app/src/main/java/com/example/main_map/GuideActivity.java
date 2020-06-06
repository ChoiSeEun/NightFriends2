package com.example.main_map;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;


import java.util.ArrayList;

public class GuideActivity extends AppCompatActivity {

    EditText editText;
    ListView listView;
    GuideAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_road);

        listView = (ListView) findViewById(R.id.road_list);
        adapter = new GuideAdapter();
        adapter.addItem(new Matching_list("20분","***->***->***->***","87%","매칭"));
        adapter.addItem(new Matching_list("26분","***->***->***->***","79%","매칭"));
        adapter.addItem(new Matching_list("30분","***->***->****->****","70%","매칭"));
        adapter.addItem(new Matching_list("38분","***->***->****->***","68%","매칭"));
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

            return view;
        }
    }
}
