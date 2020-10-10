package com.example.night_friend.matching;

public class Constant {
    private static final String BASE_PATH = "http://night1234.dothome.co.kr/";

    // 사용자의 위치 추가
    public static final String CREATE_URL = BASE_PATH + "userLocation.php";

    // 다른 사람의 위치 얻어오기
    public static final String GET_URL = BASE_PATH + "getLocation.php";

    // 사용자의 위치 데이터 삭제
    public static final String DELETE_URL = BASE_PATH + "userDelete.php";

    // 사용자의 매칭 여부 파악
    public static final String ANS_URL = BASE_PATH + "userMatchingAns.php";

    public static final String GET_METHOD = "GET";
    static final String POST_METHOD = "POST"; }
