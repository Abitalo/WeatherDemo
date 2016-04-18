package com.abitalo.www.weatherdemo;


import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.abitalo.www.weatherdemo.model.Database;
import com.abitalo.www.weatherdemo.ui.CityActivity;
import com.amap.api.maps2d.model.LatLng;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lancelot on 2016/3/3.
 */
public class NetworkRequest extends Thread{
    private Handler     handler=null;

    private Map params=null;
//    private Button      city_btn =null;
//    private TextView    main_content =null;
//    private String      city_name =null;
//    private LatLng      latLng=null;

    public static final String  DEF_CHATSET = "UTF-8";
    public static final int     DEF_CONN_TIMEOUT = 30000;
    public static final int     DEF_READ_TIMEOUT = 30000;

    private static String userAgent     =  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

    private static final String weatherAPPKEY ="3a26d8983a6842ea42b9cc07743cb984";
    private static final String airAPPKEY     ="5029e0af49c44b5699af886d2911d729";

    private enum State{
        WEATHER_REQUEST,
        AIR_REQUEST,
        REQUEST_ALL,
        ERROR
    }
    private static State state;

    //request for air info using city name
    public NetworkRequest(Map params, Handler handler){
        this.params=params;
        this.handler=handler;
        if(!params.containsKey("method"))
        {
            state=State.REQUEST_ALL;
            return;
        }

        String method=(String) params.get("method");

        if (method.equals("air")){
//            this.city_name =(String)params.get("city_name");
            state=State.AIR_REQUEST;
        }
        else if (method.equals("weather")){
//            this.latLng=(LatLng)params.get("latLng");
            state=State.WEATHER_REQUEST;
        }
        else{
            state=State.ERROR;
        }
//        this.city_btn =(Button) params.get("city_btn");
//        this.main_content =(TextView) params.get("main_content");
//        this.handler=handler;
    }

    @Override
    public void run() {
        super.run();

        switch (state){
            case AIR_REQUEST:
                updateView(getRequestAir((String) params.get("city_name")));break;
            case WEATHER_REQUEST:
                updateView(getRequestWeather((LatLng) params.get("latLng")));break;
            case REQUEST_ALL:
                getRequestAll((LatLng)params.get("latLng"));break;
            case ERROR:
                Log.i("Error","Error in NetworkRequest Thread");
        }

    }



    public Map<String,String> getRequestAir(String city_name){
        Map<String,String> ans = new HashMap();
        String result;

        String airUrl = "http://web.juhe.cn:8080/environment/air/cityair";
        Map params = new HashMap();//请求参数
        params.put("city", city_name);//城市名称的中文名称或拼音，如：上海 或 shanghai
        params.put("key",airAPPKEY);//APP Key

        try {
            result =net(airUrl, params, "GET");
            final JSONObject object = JSONObject.fromObject(result);
            if(object.getInt("error_code")==0){
                JSONArray arr=(JSONArray) object.get("result");
                final JSONObject jsonObject=(JSONObject)( (JSONObject) arr.opt(0)).get("citynow");
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        city_btn.setText(jsonObject.getString("city"));
//                        main_content.setText("AQI : "+jsonObject.getString("AQI")+"\n\n"+
//                                "空气质量 :"+jsonObject.getString("quality")+"\n\n"+
//                                "测定时间 :"+jsonObject.getString("date"));

                        ans.put("AQI(空气质量指数)",jsonObject.getString("AQI"));
                        ans.put("空气质量",jsonObject.getString("quality"));
                        ans.put("时间",jsonObject.getString("date"));
//                    }
//                });
            }else{
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        main_content.setText(object.getInt("error_code")+":"+object.getString("reason"));
//                    }
//                });
                ans.put("error",object.getInt("error_code")+":"+object.getString("reason"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ans;
    }

    public Map<String,String> getRequestWeather(LatLng latLng){
        Map<String,String> ans=new HashMap();
        String result;

        String weatherUrl = "http://v.juhe.cn/weather/geo";
        Map params = new HashMap();//请求参数
        params.put("key",weatherAPPKEY);//APP Key
        params.put("lon",latLng.longitude);
        params.put("lat",latLng.latitude);

        try {
            result =net(weatherUrl, params, "GET");
            final JSONObject object = JSONObject.fromObject(result);
            if(object.getInt("error_code")==0){
                final JSONObject jsonObject=(JSONObject) ((JSONObject) object.get("result")).get("today");
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        city_btn.setText(jsonObject.getString("city"));
//                        main_content.setText("天气 : "+jsonObject.getString("weather")+"\n\n"+
//                                "气温 :"+jsonObject.getString("temperature")+"\n\n");
                ans.put("city",jsonObject.getString("city"));
                ans.put("天气",jsonObject.getString("weather"));
                ans.put("气温",jsonObject.getString("temperature"));
                Integer weather_id=Integer.parseInt(((JSONObject)jsonObject.get("weather_id")).getString("fa"));
                this.params.put("weather_id",(weather_id==53)?32:weather_id);

//                    }
//                });
            }else{
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        main_content.setText(object.getInt("error_code")+":"+object.getString("reason"));
//                    }
//                });
                ans.put("error",object.getInt("error_code")+":"+object.getString("reason"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ans;
    }

    private void getRequestAll(LatLng latLng){
        final Map<String,String> map=getRequestWeather(latLng);
        final int index=(int)params.get("weather_id");

        map.putAll(getRequestAir(map.get("city")));
        handler.post(new Runnable() {
            @Override
            public void run() {
                ((Button)params.get("city")).setText(map.get("city"));

                ((TextView)params.get("weather")).setText("天气 :"+map.get("天气"));
                ((TextView)params.get("temperature")).setText("气温 :"+map.get("气温"));
                ((TextView)params.get("AQI")).setText("AQI(空气质量指数) :"+map.get("AQI(空气质量指数)"));
                ((TextView)params.get("quality")).setText("空气质量 :"+map.get("空气质量"));
                ((ImageView)params.get("icon_weather")).setImageResource(Database.icon_id[index]);

                params.clear();
                params.putAll(map);

                CityActivity.initDatabase();
            }
        });
//        params.clear();
//        params.putAll(map);
    }

    private void updateView(final Map<String,String> ans){
        final Button city_btn=(Button) params.get("city_btn");


        if( ans.get("city").equals( city_btn.getText().toString() ) )
            return;

        final TextView main_content=(TextView) params.get("main_content");

        if(ans.containsKey("error")){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    main_content.setText(ans.get("error"));
                }
            });
            return ;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                city_btn.setText(ans.get("city"));
                StringBuffer sb=new StringBuffer();
                for(Map.Entry i : ans.entrySet()){
                    sb.append(i.getKey()+" :"+i.getValue()+"\n\n");
                }
                main_content.setText(sb.toString());
            }
        });
    }

    private static String net(String strUrl, Map params,String method) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            StringBuffer sb = new StringBuffer();
            if(method==null || method.equals("GET")){
                strUrl = strUrl+"?"+urlencode(params);
            }
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if(method==null || method.equals("GET")){
                conn.setRequestMethod("GET");
            }else{
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("User-agent", userAgent);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            if (params!= null && method.equals("POST")) {
                try {
                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    out.writeBytes(urlencode(params));
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
            String strRead;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }

    //将map型转为请求参数型
    private static String urlencode(Map<String,Object> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}

