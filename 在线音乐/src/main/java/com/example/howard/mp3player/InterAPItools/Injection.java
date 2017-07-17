package com.example.howard.mp3player.InterAPItools;

import android.content.Context;
import android.os.Build;
import android.webkit.WebSettings;

import com.example.howard.mp3player.MyApplication;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Howard on 2016/7/11.
 */
public class Injection {

    public static OkHttpClient provideOkHttpClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        Request request = new Request.Builder().url("").removeHeader("User-Agent").addHeader("User-Agent",
                getUserAgent(MyApplication.getInstance())).build();
//        httpClient.newCall(request).enqueue();

        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
    }

    public static Retrofit provideRetrofit() {

        return new Retrofit.Builder()
                .baseUrl(SongAPI.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(Injection.provideOkHttpClient())
                .build();
    }

    public static SongAPI provideSongAPI() {

        return Injection.provideRetrofit().create(SongAPI.class);
    }

    private static String getUserAgent(Context context) {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private void checkNameAndValue(String name, String value) {
        if (name == null) throw new NullPointerException("name == null");
        if (name.isEmpty()) throw new IllegalArgumentException("name is empty");
        for (int i = 0, length = name.length(); i < length; i++) {
            char c = name.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
//                throw new IllegalArgumentException(("Unexpected char %#04x at %d in header name: %s", (int) c, i, name));
                throw new IllegalArgumentException("Unexpected char " + c + " at " + i + " in header name: +" + name);
            }
        }
        if (value == null) throw new NullPointerException("value == null");
        for (int i = 0, length = value.length(); i < length; i++) {
            char c = value.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
//                throw new IllegalArgumentException(Util.format("Unexpected char %#04x at %d in %s value: %s", (int) c, i, name, value));
                throw new IllegalArgumentException("Unexpected char " + c + " at " + i + " in +" + name + "value:" + value);
            }
        }
    }

}
