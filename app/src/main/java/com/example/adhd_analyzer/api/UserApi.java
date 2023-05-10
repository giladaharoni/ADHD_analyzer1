package com.example.adhd_analyzer.api;

import com.example.adhd_analyzer.ADHD_analyzer1;
import com.example.adhd_analyzer.R;
import com.example.adhd_analyzer.entities.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserApi {
    private static Retrofit retrofit;
    private static WebServiceApi webServiceApi;
    private static final String BASE_URL = ADHD_analyzer1.context.getString(R.string.BaseUrl);
    public UserApi(){
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
        Call<List<User>> call = webServiceApi.getUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                List<User> users = response.body();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
    }

}
