package com.example.night_friend.login;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://night1234.dothome.co.kr/Register.php";
    private Map<String, String> map;


    public RegisterRequest(String userID, String userPassword, int userAge,String userGender, Response.Listener<String> listener){
        super(Method.POST,URL, listener,null);

        map=new HashMap<>();
        map.put("userID",userID);
        map.put("userPassword",userPassword);
        map.put("userAge",userAge+"");
        map.put("userGender",userGender);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}