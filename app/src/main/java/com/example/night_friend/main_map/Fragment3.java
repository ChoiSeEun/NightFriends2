package com.example.night_friend.main_map;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.night_friend.R;


public class Fragment3 extends Fragment implements View.OnClickListener {
    public static final int REQUEST_CODE_SETTING = 101;
    EditText call1, call2, call3, call4;
    String p1, p2, p3, p4;
    String return_name1, return_name2, return_name3, return_name4;


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

        call1 = (EditText) v.findViewById(R.id.et_call1);
        call2= (EditText) v.findViewById(R.id.et_call2);
        call3 = (EditText) v.findViewById(R.id.et_call3);
        call4 = (EditText) v.findViewById(R.id.et_call4);

        SharedPreferences sp = this.getActivity().getSharedPreferences("sfile", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt=sp.edit();
        String save_name1 = sp.getString("name1", "");
        String save_name2 = sp.getString("name2", "");
        String save_name3 = sp.getString("name3", "");
        String save_name4 = sp.getString("name4", "");

        String save_phone1 = sp.getString("phone1","");
        String save_phone2 = sp.getString("phone2","");
        String save_phone3 = sp.getString("phone3","");
        String save_phone4 = sp.getString("phone4","");

        call1.setText(save_name1);
        call2.setText(save_name2);
        call3.setText(save_name3);
        call4.setText(save_name4);

        if(return_name1!=null){
            edt.putString("name1", return_name1);
            edt.putString("phone1", p1);
            edt.commit();
        }

        if(return_name2!=null){
            edt.putString("name2", return_name2);
            edt.putString("phone2", p2);
            edt.commit();
        }

        if(return_name3!=null){
            edt.putString("name3", return_name3);
            edt.putString("phone3", p3);
            edt.commit();
        }

        if(return_name4!=null){
            edt.putString("name4", return_name4);
            edt.putString("phone4", p4);
            edt.commit();
        }

        //호출 버튼 클릭시
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
                //String data = editText3.getText().toString();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + p2));
                startActivity(intent);
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String data = editText7.getText().toString();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + p3));
                startActivity(intent);
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String data = editText12.getText().toString();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + p4));
                startActivity(intent);
            }
        });

        //SOS 버튼 클릭시
        sos_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SOS_setting.callPoliceState==true) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:112"));
                    startActivity(intent);
                }
                if(SOS_setting.callPoliceState==false){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"+"122"));
                    startActivity(intent);
                }
                if(SOS_setting.callAllState==true){
                    SmsManager sms = SmsManager.getDefault();
                    if(save_phone1!=null) {
                        sms.sendTextMessage("01011111111", null, "긴급신고 요청", null, null);
                    }
                    if(save_phone2!=null) {
                        sms.sendTextMessage("5556", null, "긴급신고 요청", null, null);
                    }
                    if(save_phone3!=null) {
                        sms.sendTextMessage("5556", null, "긴급신고 요청", null, null);
                    }
                    if(save_phone4!=null) {
                        sms.sendTextMessage("5556", null, "긴급신고 요청", null, null);
                    }

                }
                if(SOS_setting.callAllState==false){
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage("01011111111", null, "긴급신고 요청", null, null);
                }
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
                return_name1 = data.getStringExtra("name1");
                call1.setText(return_name1);
                p1 = data.getStringExtra("phone1");
                Log.d("result1",return_name1+","+p1);
                Toast.makeText(this.getActivity(), "Result: " + data.getStringExtra("phone1"), Toast.LENGTH_SHORT).show();
            }
            if (resultCode == 2) {
                return_name2 = data.getStringExtra("name2");
                call2.setText(return_name2);
                p2 = data.getStringExtra("phone2");
                Log.d("result2",return_name2+","+p2);
                Toast.makeText(this.getActivity(), "Result: " + data.getStringExtra("phone2"), Toast.LENGTH_SHORT).show();
            }
            if (resultCode == 3) {
                return_name3 = data.getStringExtra("name3");
                call3.setText(return_name3);
                p3 = data.getStringExtra("phone3");
                Toast.makeText(this.getActivity(), "Result: " + data.getStringExtra("phone3"), Toast.LENGTH_SHORT).show();
            }
            if (resultCode == 4) {
                return_name4 = data.getStringExtra("name4");
                call4.setText(return_name4);
                p4 = data.getStringExtra("phone4");
                Toast.makeText(this.getActivity(), "Result: " + data.getStringExtra("phone4"), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
