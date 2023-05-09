package com.example.adhd_analyzer.api;

import com.example.adhd_analyzer.ADHD_analyzer1;
import com.example.adhd_analyzer.R;
import com.example.adhd_analyzer.entities.Data;
import com.example.adhd_analyzer.entities.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataApi {
    Retrofit retrofit;
    WebServiceApi webServiceApi;

    public DataApi(){
        retrofit = new Retrofit.Builder()
                .baseUrl(ADHD_analyzer1.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceApi = retrofit.create(WebServiceApi.class);
    }
    public void get() {
        Call<List<Data>> call = webServiceApi.getDatas();
        call.enqueue(new Callback<List<Data>>() {
            @Override
            public void onResponse(Call<List<Data>> call, Response<List<Data>> response) {
                List<Data> datas = response.body();
            }

            @Override
            public void onFailure(Call<List<Data>> call, Throwable t) {

            }
        });
    }

}
