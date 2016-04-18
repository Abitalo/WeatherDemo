package com.abitalo.www.weatherdemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.abitalo.www.weatherdemo.NetworkRequest;
import com.abitalo.www.weatherdemo.R;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lancelot on 2016/3/2.
 */
public class MainActivity extends Activity implements AMap.OnMapClickListener,Button.OnClickListener{
    private Handler handler=new Handler();

//    private ImageView location_sign=null;
    private Button city_btn =null;
    private TextView main_content =null;
    private MapView mMapView=null;
    private AMap mAMap=null;
    private Marker marker;

    private static LatLng latLngNow=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView=(MapView)findViewById(R.id.primary_map);
        city_btn =(Button)findViewById(R.id.city_name);
        main_content =(TextView)findViewById(R.id.main_content);
        mMapView.onCreate(savedInstanceState);

        init();
    }

    private void init(){
        if(mAMap == null){
            mAMap=mMapView.getMap();
        }

        latLngNow=new LatLng(31.25,121.45);

        CameraUpdate cu=CameraUpdateFactory.changeLatLng(latLngNow);
        mAMap.moveCamera(cu);

        requestWeather(latLngNow);

        mAMap.setOnMapClickListener(this);
        city_btn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        city_btn.setTextColor(Color.BLACK);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    private void requestAir(String city_name){
        Map params=new HashMap();
        params.put("method","air");
        params.put("city_name",city_name);
        params.put("city_btn", city_btn);
        params.put("main_content", main_content);

        new NetworkRequest(params,handler).start();
    }
    private void requestWeather(LatLng latLng){
        Map params=new HashMap();
        params.put("method","weather");
        params.put("latLng",latLng);
        params.put("city_btn", city_btn);
        params.put("main_content", main_content);

        new NetworkRequest(params,handler).start();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        //main_content.setText(latLng.longitude+"\n"+latLng.latitude);
        latLngNow=latLng;

        if(marker != null){
            marker.remove();
        }
        marker=mAMap.addMarker(new MarkerOptions().position(latLng));
        requestWeather(latLng);
    }

    @Override
    public void onClick(View v) {
        ((Button)v).setTextColor(Color.WHITE);
        Intent intent=new Intent(this,CityActivity.class);
        intent.putExtra("latLng",latLngNow);

        startActivity(intent);
    }
}
