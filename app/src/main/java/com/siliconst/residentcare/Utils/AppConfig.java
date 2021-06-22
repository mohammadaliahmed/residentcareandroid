package com.siliconst.residentcare.Utils;

import com.siliconst.residentcare.NetworkResponses.retrofit.SimpleXmlConverterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppConfig {
    //            public static String LPTOP_ID = "http://192.168.8.105/ticket/public/";//home
    public static String LPTOP_ID = "http://192.168.100.31/ticket/public/";//office
    public static String SERVER_URL = "http://demo.residentcare.co/";
    public static String SHARE_URL = "http://accessale.com/";
    public static String SMS_URL = "https://telenorcsms.com.pk:27677/";
    public static String BASE_URL = SERVER_URL;
    public static String API_USERNAME = "WF9.FJ8u'FP{c5Pw";
    public static String API_PASSOWRD = "3B~fauh5s93j[FKb";

    public static String BASE_URL_Image = BASE_URL + "uploads/";
    public static String BASE_URL_Image_ATTATCHMENTS = BASE_URL + "storage/";
    public static String BASE_URL_AUDIO = BASE_URL + "public/audio/";
    public static String BASE_URL_Videos = BASE_URL + "public/videos/";
    public static String BASE_URL_QR = BASE_URL + "public/qr/";
    public static String TOKKEN = "http://acnure.com/";

    public static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }


    public static Retrofit getRetrofit2() {
        return new Retrofit.Builder()
                .baseUrl(AppConfig.SMS_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

    }

}
