package com.example.night_friend.main_map;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.night_friend.R;
import com.example.night_friend.matching.BaseAdapterEx;
import com.example.night_friend.matching.Matching_setting;
import com.example.night_friend.matching.matching_data;

import java.util.ArrayList;

public class Fragment4 extends Fragment implements View.OnClickListener {
    ListView mListView = null;
    BaseAdapterEx mAdapter = null;
    ArrayList<matching_data> mData = null;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment4_matching, container, false);
        DataCreated();
        setAdapter();
       Button matching_setting_button=(Button)v.findViewById(R.id.bt_matching_setting);

        matching_setting_button.setOnClickListener(this);
        return v;
    }
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_matching_setting:
                    Intent intent = new Intent(getActivity(), Matching_setting.class);
                    startActivity(intent);
                    break;
            }
            }


            public void DataCreated () {
                mData = new ArrayList<matching_data>();

                for (int i = 0; i < 10; i++) {
                    matching_data s = new matching_data();

                    s.time = "8:00";
                    s.dest = "서울역";


                    mData.add(s);
                }
            }

            public void setAdapter () {
                mAdapter = new BaseAdapterEx(getActivity(), mData);

                mListView = (ListView) v.findViewById(R.id.lt_listview);
                mListView.setAdapter(mAdapter);
            }
        }

