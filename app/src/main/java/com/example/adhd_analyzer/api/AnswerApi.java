package com.example.adhd_analyzer.api;

import com.example.adhd_analyzer.ADHD_analyzer1;
import com.example.adhd_analyzer.R;
import com.example.adhd_analyzer.entities.Answer;
import com.example.adhd_analyzer.entities.Data;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AnswerApi {
    Retrofit retrofit;
    WebServiceApi webServiceApi;

    public AnswerApi(){
        retrofit = new Retrofit.Builder()
                .baseUrl(ADHD_analyzer1.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceApi = retrofit.create(WebServiceApi.class);
    }
    public void get() {
        Call<List<Answer>> call = webServiceApi.getAnswers();
        call.enqueue(new Callback<List<Answer>>() {
            @Override
            public void onResponse(Call<List<Answer>> call, Response<List<Answer>> response) {
                List<Answer> answers = response.body();
            }

            @Override
            public void onFailure(Call<List<Answer>> call, Throwable t) {

            }
        });
    }

}

