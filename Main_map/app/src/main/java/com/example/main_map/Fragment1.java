package com.example.main_map;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

public class Fragment1 extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment1_map, container, false);

        MapView mapView = new MapView(getActivity());
        mapView.setDaumMapApiKey("a3e79593add64289f981c6e7b13d1a92");
        ViewGroup mapViewContainer = (ViewGroup) v.findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setZoomLevel(4, true);

        onMapPoint(mapView);

        onMapCircle(mapView);

        return v;
    }


    public void onMapPoint(MapView mapView){
        //위도 경도
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(37.498095, 127.027610);
        mapView.setMapCenterPoint(mapPoint, true);

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("강남역");
        marker.setTag(0);
        marker.setMapPoint(mapPoint);
        // 기본으로 제공하는 BluePin 마커 모양.
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(marker);
    }
    //원 반경 표시하는
    public void onMapCircle(MapView mapView){
        MapCircle circle1 = new MapCircle(
                MapPoint.mapPointWithGeoCoord(37.498094, 127.027610), // center
                500, // radius
                Color.argb(128, 255, 0, 0), // strokeColor
                Color.argb(128, 0, 255, 0) // fillColor
        );
        circle1.setTag(1234);
        mapView.addCircle(circle1);

        MapCircle circle2 = new MapCircle(
                MapPoint.mapPointWithGeoCoord(37.551094, 127.019470), // center
                1000, // radius
                Color.argb(128, 255, 0, 0), // strokeColor
                Color.argb(128, 255, 255, 0) // fillColor
        );
        circle2.setTag(5678);
        mapView.addCircle(circle2);

// 지도뷰의 중심좌표와 줌레벨을 Circle이 모두 나오도록 조정.

        MapPointBounds[] mapPointBoundsArray = { circle1.getBound(), circle2.getBound() };
        MapPointBounds mapPointBounds = new MapPointBounds(mapPointBoundsArray);
        int padding = 50; // px
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));
    }
}

