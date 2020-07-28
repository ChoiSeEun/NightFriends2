package com.example.main_map;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

public class Fragment2 extends Fragment implements View.OnClickListener {
    EditText editText;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment2_guide, container, false);
        listView = (ListView) v.findViewById(R.id.road_list);

        Button bt_guide=(Button)v.findViewById(R.id.bt_call2);

        bt_guide.setOnClickListener(this);


        return v;
    }
    @Override
    public void onClick(View v){
        Intent intent = new Intent(getActivity(),GuideActivity.class);
        startActivity(intent);
    }
}