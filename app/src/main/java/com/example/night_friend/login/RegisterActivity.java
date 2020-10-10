package com.example.night_friend.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.night_friend.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_id, et_password, et_gender, et_age;
    private Button btn_sign;
    private boolean validate=false;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // 액티비티 시작시 처음으로 실행되는 생명주기
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 아이디 값 찾아주기
        et_id=findViewById(R.id.et_sign_id);
        et_password=findViewById(R.id.et_sign_password);
        et_age=findViewById(R.id.et_sign_age);
        et_gender=findViewById(R.id.et_sign_gender);

        // 아이디 중복 확인
        final TextView validateTV = (TextView)findViewById(R.id.tv_multi);
        validateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID=et_id.getText().toString();
                if(validate){
                    return;//검증 완료
                }
                //ID 값을 입력하지 않았다면
                if(userID.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("아이디를 입력하세요.")
                            .setPositiveButton("OK", null)
                            .create();
                    dialog.show();
                    return;
                }

                //검증시작
                Response.Listener<String> responseListener = new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        try{
                            Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_LONG).show();

                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){//사용할 수 있는 아이디라면
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 있는 아이디입니다.")
                                        .setPositiveButton("OK", null)
                                        .create();
                                dialog.show();
                                et_id.setEnabled(false);//아이디값을 바꿀 수 없도록 함
                                validate = true;//검증완료
                                et_id.setBackgroundColor(getResources().getColor(R.color.colorGray));
                                validateTV.setTextColor(getResources().getColor(R.color.colorGray));
                            }else{//사용할 수 없는 아이디라면
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("이미 사용된 아이디입니다.")
                                        .setNegativeButton("OK", null)
                                        .create();
                                dialog.show();
                            }

                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                };//Response.Listener 완료

                //Volley 라이브러리를 이용해서 실제 서버와 통신을 구현하는 부분
                ValidateRequest validateRequest = new ValidateRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(validateRequest);
            }
        });




        // 회원가입 버튼 클릭 시 수행
        btn_sign=findViewById(R.id.btn_sign);
        btn_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText에 현재 입력되어있는 값을 가져온다.
                String userID= et_id.getText().toString();
                String userPassword=et_password.getText().toString();
                String userAge=et_age.getText().toString();
                String userGender=et_gender.getText().toString();

                // 빈칸 여부 체크
                if(userID.equals("")||userPassword.equals("")||userGender.equals("")||userAge.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("빈칸이 존재합니다.")
                            .setNegativeButton("OK", null)
                            .create();
                    dialog.show();
                    return;
                }

                // 아이디 중복 확인 여부 체크
                if(!validate){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("중복 확인을 하세요.")
                            .setNegativeButton("OK", null)
                            .create();
                    dialog.show();
                    return;
                }

                // 생년월일 유효성 판별
                String regEx = "^\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])";
                boolean regCheck = false;
                regCheck = Pattern.matches(regEx, userAge);
                // 주민번호 형식이 아닌 경우
                if(!regCheck) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("알맞는 생년월일을 입력하시오.")
                            .setNegativeButton("OK", null)
                            .create();
                    dialog.show();
                    return;
                }

                // 성별과 나이 값 얻어오기
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat yearFormat=new SimpleDateFormat("yyyy", Locale.getDefault());
                String year=yearFormat.format(currentTime);

                int uA=Integer.parseInt(userAge);
                int Age=uA/10000; int Gender=Integer.parseInt(userGender);

                if(Gender==1 || Gender==3){
                    userGender="남자";
                    if(Gender==1){
                        uA=1900+Age;
                        uA=Integer.parseInt(year)-uA+1;

                    }
                    else{
                        uA=2000+Age;
                        uA=Integer.parseInt(year)-uA+1;
                    }
                }
                else if(Gender==2 || Gender==4){
                    userGender="여자";
                    if(Gender==2){
                        uA=1900+Age;
                        uA=Integer.parseInt(year)-uA+1;
                    }
                    else{
                        uA=2000+Age;
                        uA=Integer.parseInt(year)-uA+1;
                    }

                }
                // 알맞지 않는 주민번호 첫자리.
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("알맞는 생년월일을 입력하시오.")
                            .setNegativeButton("OK", null)
                            .create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> reponseListener=new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        // 회원가입 요청한 후 결과값을 JSONObject로 받음.
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // 서버 통신 성공 여부를 알려줌.
                            boolean success = jsonObject.getBoolean("success");
                            if(success){
                                Toast.makeText(getApplicationContext(),"회원 가입에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                                startActivity(intent);
                            }else { // 회원등록에 실패한 경우
                                Toast.makeText(getApplicationContext(),"회원 가입에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                return; // 로그인으로 넘어가지 않음.
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                // 서버로 Volley를 이용해서 요청함.
                com.example.night_friend.login.RegisterRequest registerRequest=new com.example.night_friend.login.RegisterRequest(userID,userPassword,uA,userGender,reponseListener);
                RequestQueue queue= Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });

    }
    @Override
    protected void onStop() {
        super.onStop();
        if(dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }
}