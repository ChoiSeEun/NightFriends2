package com.example.night_friend.main_map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.night_friend.R;

public class SOS_setting extends AppCompatActivity {
    CheckBox cb_sos,cb_112,cb_all;
    EditText name1, name2, name3, name4;
    public static EditText phone1;
    EditText phone2, phone3, phone4;
    public static Context context_setting;
    public String ph1,ph2,ph3,ph4;
    private TextView tell1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_o_s_setting);

        context_setting=this;

        Switch autoSwitch=(Switch)findViewById(R.id.sw_matching);
        Switch sosSwitch=(Switch)findViewById(R.id.switch2);

        cb_sos=findViewById(R.id.cb_faster);
        cb_112=findViewById(R.id.ch_cctv);
        cb_all=findViewById(R.id.cb_criminal);

        ImageButton add1=(ImageButton)findViewById(R.id.bt_guide_search);
        ImageButton add2=(ImageButton)findViewById(R.id.bt_refresh);
        ImageButton add3=(ImageButton)findViewById(R.id.bt_cancle);
        ImageButton add4=(ImageButton)findViewById(R.id.imageButton5);

        name1=(EditText)findViewById(R.id.editText);
        name2=(EditText)findViewById(R.id.editText5);
        name3=(EditText)findViewById(R.id.editText6);
        name4=(EditText)findViewById(R.id.editText10);

        phone1=(EditText)findViewById(R.id.editText4);
        phone2=(EditText)findViewById(R.id.editText8);
        phone3=(EditText)findViewById(R.id.editText9);
        phone4=(EditText)findViewById(R.id.editText11);

        SharedPreferences sp=getSharedPreferences("sfile",MODE_PRIVATE);
        String n1=sp.getString("name1","");
        String n2=sp.getString("name2","");
        String n3=sp.getString("name3","");
        String n4=sp.getString("name4","");

        String save=sp.getString("phone1","");
        String save2=sp.getString("phone2","");
        String save3=sp.getString("phone3","");
        String save4=sp.getString("phone4","");

        name1.setText(n1);
        name2.setText(n2);
        name3.setText(n3);
        name4.setText(n4);

        phone1.setText(save);
        phone2.setText(save2);
        phone3.setText(save3);
        phone4.setText(save4);


        autoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getApplicationContext(),"switch on",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"switch off",Toast.LENGTH_LONG).show();
                }
            }
        });

        sosSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getApplicationContext(),"switch on",Toast.LENGTH_LONG).show();
                    cb_sos.setEnabled(true);
                    cb_112.setEnabled(true);
                    cb_all.setEnabled(true);
                }else{
                    Toast.makeText(getApplicationContext(),"switch off",Toast.LENGTH_LONG).show();
                    cb_sos.setEnabled(false);
                    cb_112.setEnabled(false);
                    cb_all.setEnabled(false);
                }
            }
        });

        cb_sos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cb_sos.isChecked()==true){
                    Toast.makeText(SOS_setting.this,"sos 사용: "+cb_sos.isChecked(), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(SOS_setting.this,"sos 사용: "+cb_sos.isChecked(), Toast.LENGTH_LONG).show();
                }
            }
        });

        cb_112.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cb_112.isChecked()==true){
                    Toast.makeText(SOS_setting.this,"112 신고: "+cb_112.isChecked(), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(SOS_setting.this,"112 신고: "+cb_112.isChecked(), Toast.LENGTH_LONG).show();
                }
            }
        });

        cb_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cb_all.isChecked()==true){
                    Toast.makeText(SOS_setting.this,"모두에게 신고: "+cb_all.isChecked(), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(SOS_setting.this,"모두에게 신고: "+cb_all.isChecked(), Toast.LENGTH_LONG).show();
                }
            }
        });

        add1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = new Intent(getApplicationContext(), Night_main.class);
                intent.putExtra("phone1",phone1.getText().toString());
                intent.putExtra("name1",name1.getText().toString());
                //ph1=phone1.getText().toString();

                SharedPreferences sharedPreferences =getSharedPreferences("sfile",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();

                String name_txt=name1.getText().toString();
                String text=phone1.getText().toString();

                editor.putString("name1",name_txt);
                editor.putString("phone1",text);
                editor.commit();

                name1.setText(name_txt);
                phone1.setText(text);
                Toast.makeText(SOS_setting.this,"등록완료",Toast.LENGTH_LONG).show();

                setResult(1,intent);
            }
        });

        add2.setOnClickListener(new View.OnClickListener(){
           @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), Night_main.class);
                //ph2=phone2.getText().toString();
                intent.putExtra("phone2",phone2.getText().toString());
                intent.putExtra("name2",name2.getText().toString());

                SharedPreferences sharedPreferences = getSharedPreferences("sfile",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();

                String name_txt=name2.getText().toString();
                String text=phone2.getText().toString();

                editor.putString("name2",name_txt);
                editor.putString("phone2",text);
                editor.commit();

                name2.setText(name_txt);
                phone2.setText(text);
                Toast.makeText(SOS_setting.this,"등록완료",Toast.LENGTH_LONG).show();
                setResult(2,intent);
            }
        });

        add3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), Night_main.class);
                //ph3=phone3.getText().toString();
                intent.putExtra("phone3",phone3.getText().toString());
                intent.putExtra("name3",name3.getText().toString());

                SharedPreferences sharedPreferences = getSharedPreferences("sfile",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();

                String name_txt=name3.getText().toString();
                String text=phone3.getText().toString();

                editor.putString("name3",name_txt);
                editor.putString("phone3",text);
                editor.commit();

                name3.setText(name_txt);
                phone3.setText(text);
                Toast.makeText(SOS_setting.this,"등록완료",Toast.LENGTH_LONG).show();
                setResult(3,intent);
            }
        });

        add4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), Night_main.class);
                //ph4=phone4.getText().toString();
                intent.putExtra("phone4",phone4.getText().toString());
                intent.putExtra("name4",name4.getText().toString());

                SharedPreferences sharedPreferences = getSharedPreferences("sfile",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();

                String name_txt=name4.getText().toString();
                String text=phone4.getText().toString();

                editor.putString("name4",name_txt);
                editor.putString("phone4",text);
                editor.commit();

                name4.setText(name_txt);
                phone4.setText(text);
                Toast.makeText(SOS_setting.this,"등록완료",Toast.LENGTH_LONG).show();
                setResult(4,intent);
            }
        });

    }

}
