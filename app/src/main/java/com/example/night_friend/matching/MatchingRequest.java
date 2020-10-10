package com.example.night_friend.matching;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class MatchingRequest extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://night1234.dothome.co.kr/userLocation.php";
    private Map<String, String> map;


    public MatchingRequest(String user, double userLat, double userLon, Response.Listener<String>  listener){
        super(Method.POST,URL, listener,null);

        map=new HashMap<>();
        map.put("user",user);
        map.put("userLat",userLat+"");
        map.put("userLon",userLon+"");

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
