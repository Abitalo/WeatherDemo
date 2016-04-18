package com.abitalo.www.weatherdemo.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.abitalo.www.weatherdemo.NetworkRequest;
import com.abitalo.www.weatherdemo.R;
import com.abitalo.www.weatherdemo.adapter.MasonryAdapter;
import com.abitalo.www.weatherdemo.adapter.MasonryView;
import com.abitalo.www.weatherdemo.model.Database;
import com.abitalo.www.weatherdemo.model.Item_Weather;
import com.amap.api.maps2d.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// 重新进入城市时，需要再点一下收藏才能显示之前保存的记录。
/**
 * Created by Lancelot on 2016/3/5.
 */
public class CityActivity extends Activity implements View.OnClickListener,MasonryView.OnItemLongClickListener{
    private Handler handler=new Handler();
    private static Map params=null;
    private AlertDialog myDialog=null;

    private Button city_title=null;
    private ImageButton favorite_btn=null;
    private TextView city_weather=null;
    private TextView city_temperature=null;
    private TextView city_AQI=null;
    private TextView city_quality=null;
    private ImageView city_weather_icon=null;
    private ImageView back_btn=null;
    private RecyclerView recyclerView=null;

    private static List<Item_Weather> data=new ArrayList<>();
    private static MasonryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        city_title=(Button)findViewById(R.id.city_title);
        city_weather=(TextView) findViewById(R.id.city_weather);
        city_temperature=(TextView) findViewById(R.id.city_temperature);
        city_AQI=(TextView) findViewById(R.id.city_AQI);
        city_quality=(TextView) findViewById(R.id.city_quality);
        back_btn=(ImageView)findViewById(R.id.back_btn);
        favorite_btn=(ImageButton)findViewById(R.id.favorite_btn);
        back_btn=(ImageView)findViewById(R.id.back_btn);
        city_weather_icon=(ImageView)findViewById(R.id.city_weather_icon);

        init();
        initRecyclerView();
    }

    private void init(){
        Intent mIntent = getIntent();
        LatLng latLng = mIntent.getParcelableExtra("latLng");

        params = new HashMap();

        params.put("latLng",latLng);

        params.put("city",city_title);
        params.put("weather",city_weather);
        params.put("temperature",city_temperature);
        params.put("AQI",city_AQI);
        params.put("quality",city_quality);
        params.put("icon_weather",city_weather_icon);

        new NetworkRequest(params,handler).start();

        favorite_btn.setOnClickListener(this);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public static void initDatabase(){
        Cursor cursor= Database.db.rawQuery("select CITYNAME,AQI,QUALITY,DATE,WEATHER,TEMPERATURE_LOW,TEMPERATURE_HIGH from CITYINFO where CITYNAME='"+params.get("city")+"'",null);
        if(null != cursor){
            while(cursor.moveToNext()){
                data.add(new Item_Weather(cursor.getString(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getLong(3),
                        cursor.getString(4),
                        cursor.getInt(5),
                        cursor.getInt(6)));
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void initRecyclerView(){
        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        adapter=new MasonryAdapter(data);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.setOnItemLongClickListener(this);
    }
    @Override
    public void onClick(View v) {
        Item_Weather item=new Item_Weather((String)params.get("city"),
                (String)params.get("AQI(空气质量指数)"),
                (String)params.get("空气质量"),
                Calendar.getInstance().getTimeInMillis(),
                (String)params.get("天气"),
                (String)params.get("气温"));

        data.add(item);
        adapter.notifyDataSetChanged();

        ContentValues values=new ContentValues();
        values.put("CITYNAME",item.getCity());
        values.put("AQI",item.getAQI());
        values.put("QUALITY",item.getQuality());
        values.put("DATE",item.getDate());
        values.put("WEATHER",item.getWeather());
        values.put("TEMPERATURE_LOW",item.getTemperature_low());
        values.put("TEMPERATURE_HIGH",item.getTemperature_high());

        Database.db.insert("CITYINFO", null, values);
    }

    @Override
    public void onItemLongClick(View v,final int position) {
        myDialog = new AlertDialog.Builder(this).create();
        myDialog.show();
        myDialog.getWindow().setContentView(R.layout.item_delete_dialog);
        myDialog.getWindow()
                .findViewById(R.id.item_delete_submit_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteItem(position);
                        myDialog.dismiss();
                    }
                });
        myDialog.getWindow()
                .findViewById(R.id.item_delete_reject_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });
    }

    public void deleteItem(int position){
        long timezone=data.get(position).getDate();

        //delete from view
        data.remove(position);
        adapter.notifyItemRemoved(position);

        //delete from database
        Database.db.delete("CITYINFO","DATE=?",new String[]{String.valueOf(timezone)});
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        params.clear();
        data.clear();
    }
}
