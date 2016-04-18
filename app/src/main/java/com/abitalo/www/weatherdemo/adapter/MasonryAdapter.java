package com.abitalo.www.weatherdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abitalo.www.weatherdemo.R;
import com.abitalo.www.weatherdemo.model.Item_Weather;

import java.util.List;

/**
 * Created by Lancelot on 2015/12/4.
 */
public class MasonryAdapter extends RecyclerView.Adapter<MasonryView>{
    private List<Item_Weather> list;

    private MasonryView.OnItemLongClickListener mItemLongClickListener;

    public MasonryAdapter(List<Item_Weather> list) {
        this.list =list;
    }

    @Override
    public MasonryView onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_weather, viewGroup, false);
        return new MasonryView(view,mItemLongClickListener);
    }


    @Override
    public void onBindViewHolder(MasonryView masonryView, int position) {
        masonryView.date.setText("日期:"+list.get(position).getDateString());
        masonryView.weather.setText("天气:"+list.get(position).getWeather());
        masonryView.temperature.setText("气温:"+list.get(position).getTemperatureString());
        masonryView.AQI.setText("AQI:"+String.valueOf(list.get(position).getAQI()));
        masonryView.quality.setText("空气质量："+list.get(position).getQuality());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemLongClickListener(MasonryView.OnItemLongClickListener listener){
        this.mItemLongClickListener = listener;
    }
}