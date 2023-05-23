package com.example.adhd_analyzer.api;

import com.example.adhd_analyzer.ADHD_analyzer1;
import com.example.adhd_analyzer.R;
import com.example.adhd_analyzer.entities.Data;
import com.example.adhd_analyzer.entities.DataGet;
import com.example.adhd_analyzer.entities.User;
import com.example.adhd_analyzer.logger_sensors.ModuleDB;
import com.example.adhd_analyzer.login;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataApi {
    private static Retrofit retrofit;
    WebServiceApi webServiceApi;
    private static final String BASE_URL = "https://adhdanaylzeradminapi.azurewebsites.net/api/";

    public DataApi(){
        retrofit = new Retrofit.Builder()
                .baseUrl(ADHD_analyzer1.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceApi = retrofit.create(WebServiceApi.class);
    }
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public void get() {
        Call<List<DataGet>> call = webServiceApi.getDatas(login.theUser.getUserName(),1);
        call.enqueue(new Callback<List<DataGet>>() {
            @Override
            public void onResponse(Call<List<DataGet>> call, Response<List<DataGet>> response) {
                List<DataGet> datas = response.body();
            }

            @Override
            public void onFailure(Call<List<DataGet>> call, Throwable t) {

            }
        });
    }

}
