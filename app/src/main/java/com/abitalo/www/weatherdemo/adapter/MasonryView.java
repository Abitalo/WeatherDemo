package com.abitalo.www.weatherdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

import com.abitalo.www.weatherdemo.R;

/**
 * Created by Lancelot on 2015/12/7.
 */
public class MasonryView extends  RecyclerView.ViewHolder implements OnLongClickListener {

    private OnItemLongClickListener mLongClickListener;

    TextView date;
    TextView weather;
    TextView temperature;
    TextView AQI;
    TextView quality;

    public MasonryView(View itemView, OnItemLongClickListener longClickListener){
        super(itemView);
        date= (TextView) itemView.findViewById(R.id.item_date);
        weather= (TextView) itemView.findViewById(R.id.item_weather);
        temperature= (TextView) itemView.findViewById(R.id.item_temperature);
        AQI= (TextView) itemView.findViewById(R.id.item_AQI);
        quality= (TextView) itemView.findViewById(R.id.item_quality);

        this.mLongClickListener = longClickListener;

        itemView.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        if (mLongClickListener != null) {
            mLongClickListener.onItemLongClick(v, getPosition());
        }
        return true;
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View v, int position);
    }
}