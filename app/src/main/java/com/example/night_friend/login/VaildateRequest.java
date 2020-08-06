package com.example.night_friend.login;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

class ValidateRequest extends StringRequest {
    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://night1234.dothome.co.kr/UserValidate.php";
    private Map<String, String> map;

    public ValidateRequest(String userID, Response.Listener<String> listener){
        //해당 URL에 POST방식으로 파마미터들을 전송함
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("userID", userID);

    }
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}

