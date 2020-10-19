package com.example.night_friend.main_map;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.night_friend.R;


public class Fragment3 extends Fragment implements View.OnClickListener {
    public static final int REQUEST_CODE_SETTING = 101;
    EditText editText2, editText3, editText7, editText12;
    String p1, p2, p3, p4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment3_sos, container, false);
        Button button = v.findViewById(R.id.bt_matching_search);
        Button button2 = v.findViewById(R.id.bt_call2);
        Button button5 = v.findViewById(R.id.bt_call3);
        Button button6 = v.findViewById(R.id.bt_call4);
        Button sos_button = v.findViewById(R.id.bt_sos);
        Button gallery_button = v.findViewById(R.id.bt_recording_save);
        Button imageButton = (Button)v.findViewById(R.id.bt_sos_setting);

        editText2 = (EditText) v.findViewById(R.id.et_call1);
        editText3 = (EditText) v.findViewById(R.id.et_call2);
        editText7 = (EditText) v.findViewById(R.id.et_call3);
        editText12 = (EditText) v.findViewById(R.id.et_call4);

        SharedPreferences sp = this.getActivity().getPreferences( Context.MODE_PRIVATE);
        SharedPreferences.Editor edt=sp.edit();
        String save1 = sp.getString("name1", "");
        String save2 = sp.getString("name2", "");
        String save3 = sp.getString("name3", "");
        String save4 = sp.getString("name4", "");

        editText2.setText(save1);
        editText3.setText(save2);
        editText7.setText(save3);
        editText12.setText(save4);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String data=editText2.getText().toString();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + p1));
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = editText3.getText().toString();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + p2));
                startActivity(intent);
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = editText7.getText().toString();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + p3));
                startActivity(intent);
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = editText12.getText().toString();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + p4));
                startActivity(intent);
            }
        });

        sos_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:112"));
                startActivity(intent);
            }
        });
        gallery_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SOS_gallery.class);
                startActivity(intent);
            }
        });
        imageButton.setOnClickListener(this);

        return v;

    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.bt_sos_setting:
                Intent intent = new Intent(getActivity(), SOS_setting.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityForResult(intent,REQUEST_CODE_SETTING);
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Intent intent = this.getActivity().getIntent();
        //String phone1 = intent.getStringExtra("phone1");
        String phone1 = ((SOS_setting) SOS_setting.context_setting).ph1;
        String phone2 = ((SOS_setting) SOS_setting.context_setting).ph2;


        if (requestCode == REQUEST_CODE_SETTING) {
            if (resultCode == 1) {
                editText2.setText(data.getStringExtra("name1"));
                p1 = data.getStringExtra("phone1");
                Toast.makeText(this.getActivity(), "Result: " + data.getStringExtra("phone1"), Toast.LENGTH_SHORT).show();
            }
            if (resultCode == 2) {
                editText3.setText(data.getStringExtra("name2"));
                p2 = data.getStringExtra("phone2");
                Toast.makeText(this.getActivity(), "Result: " + data.getStringExtra("phone2"), Toast.LENGTH_SHORT).show();
            }
            if (resultCode == 3) {
                editText7.setText(data.getStringExtra("name3"));
                p3 = data.getStringExtra("phone3");
                Toast.makeText(this.getActivity(), "Result: " + data.getStringExtra("phone3"), Toast.LENGTH_SHORT).show();
            }
            if (resultCode == 4) {
                editText12.setText(data.getStringExtra("name4"));
                p4 = data.getStringExtra("phone4");
                Toast.makeText(this.getActivity(), "Result: " + data.getStringExtra("phone4"), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
