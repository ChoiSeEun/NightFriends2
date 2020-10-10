package com.example.night_friend.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.night_friend.R;
import com.example.night_friend.main_map.Night_main;


import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {


    private EditText et_id, et_password;
    private Button btn_login;
    private TextView tv_signup;
    private CheckBox ch_autologin;

    SharedPreferences setting;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        et_id=findViewById(R.id.et_login_id);
        et_password=findViewById(R.id.et_login_password);
        btn_login=findViewById(R.id.btn_login);
        tv_signup=findViewById(R.id.tv_signup);
        ch_autologin=findViewById(R.id.ch_autologin);
        // 자동 로그인 위한 SharedPreferences 생성
        setting = getSharedPreferences("setting", 0);
        editor= setting.edit();
        //Intent logout_intent =getIntent();
/*
        if(setting.getBoolean("ch_autologin", false)){
            et_id.setText(setting.getString("ID", ""));
            et_password.setText(setting.getString("PW", ""));
            ch_autologin.setChecked(true);
        }
*/
        // 회원가입 버튼을 클릭 시 수행
        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });



        btn_login.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // EditText에 현재 입력되어있는 값을 가져온다.
                String userID= et_id.getText().toString();
                String userPassword=et_password.getText().toString();
                Log.i("login","login");

                Response.Listener<String> responseListener=new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject((response));
                            //JSONObject jsonObject = new JSONObject(response);

                            // 서버 통신 성공 여부를 알려줌.
                            boolean success = jsonObject.getBoolean("success");
                            Log.i("login", String.valueOf(success));
                            if(success){ // 로그인에 성공한 경우
                                String userID=jsonObject.getString(
                                        "userID");
                                //String userPassword=jsonObject.getString("userPassword");

                                Log.i("login", userID);
                                Toast.makeText(getApplicationContext(),"로그인에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(LoginActivity.this, Night_main.class);
                                intent.putExtra("userID",userID);
                                //intent.putExtra("userPassword",userPassword);
                                startActivity(intent);

                            }else { // 로그인에 실패한 경우
                                Toast.makeText(getApplicationContext(),"아이디나 비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("오류","error");
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userID,userPassword,responseListener);
                RequestQueue queue= Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });
        // 자동 로그인 체크 여부
        ch_autologin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(ch_autologin.isChecked()){
                    String ID = et_id.getText().toString();
                    String PW = et_password.getText().toString();
                    Log.i(ID,"체크함");

                    editor.putString("ID", ID);
                    editor.putString("PW", PW);
                    editor.putBoolean("ch_autologin", true);
                    editor.commit();
                }else{

                    editor.clear();
                    editor.commit();

                }
            }
        });
        // 자동로그인 체크 유지
        if(setting.getBoolean("ch_autologin", false)){

            String settingData1=setting.getString("ID", "");
            String settingData2=setting.getString("PW", "");

            Log.i(settingData1,"로그인");

            et_id.setText(settingData1);

            et_password.setText(settingData2);

            ch_autologin.setChecked(true);
            btn_login.performClick();
/*
            Intent intent = new Intent(LoginActivity.this, Night_main.class);
            intent.putExtra("userID", settingData1);
            startActivity(intent);
            this.finish();
*/

        }

    }
    private void NotConnected_showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("네트워크 연결 오류");
        builder.setMessage("사용 가능한 무선네트워크가 없습니다.\n" + "먼저 무선네트워크 연결상태를 확인해 주세요.")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish(); // exit
                        //application 프로세스를 강제 종료
                        android.os.Process.killProcess(android.os.Process.myPid() );
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

}
