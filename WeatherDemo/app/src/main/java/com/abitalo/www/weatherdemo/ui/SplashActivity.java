package com.abitalo.www.weatherdemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.abitalo.www.weatherdemo.R;
import com.abitalo.www.weatherdemo.model.Database;
import com.abitalo.www.weatherdemo.model.DatabaseOpenHelper;

public class SplashActivity extends Activity {

    private static final int SPLASH_DISPLAY_LENGTH = 2000; //延迟三秒

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initDatabase();
        new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this,MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void initDatabase(){
        DatabaseOpenHelper helper=new DatabaseOpenHelper(SplashActivity.this,"weatherdemo.db");
        Database.db=helper.getWritableDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Info","你这是在装逼你知道吗！！！");
    }
}
