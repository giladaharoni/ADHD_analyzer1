package com.example.adhd_analyzer.api;

import com.example.adhd_analyzer.ADHD_analyzer1;
import com.example.adhd_analyzer.QuestionAnswer;
import com.example.adhd_analyzer.QuizFragment;
import com.example.adhd_analyzer.R;
import com.example.adhd_analyzer.entities.Answer;
import com.example.adhd_analyzer.entities.Data;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AnswerApi {
    private static Retrofit retrofit;
    WebServiceApi webServiceApi;

    private static final String BASE_URL = ADHD_analyzer1.context.getString(R.string.BaseUrl);

    public AnswerApi(){
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

