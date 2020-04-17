package com.example.mark_xix.api;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.mark_xix.utils.GlobalAppContextSingleton;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiServiceGenerator {

    private static Retrofit.Builder builder;
    private static Retrofit retrofit;
    private static OkHttpClient.Builder httpClient;
    private static HttpLoggingInterceptor logging;

    public static void setApiBaseUrl(String url) {

        String urlFormatted = "http://" + url + "/";

        builder = new Retrofit.Builder()
                .baseUrl(urlFormatted)
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.build();

        httpClient = new OkHttpClient.Builder();

        logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    public static <S> S createService(Class<S> serviceClass) {
        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        return retrofit.create(serviceClass);
    }
}
