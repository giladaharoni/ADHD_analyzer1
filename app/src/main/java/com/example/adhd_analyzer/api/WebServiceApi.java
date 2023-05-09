package com.example.adhd_analyzer.api;
import com.example.adhd_analyzer.entities.Answer;
import com.example.adhd_analyzer.entities.Data;
import com.example.adhd_analyzer.entities.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface WebServiceApi {
    @GET("users")
    Call<List<User>> getUsers();
    @GET("data")
    Call<List<Data>> getDatas();
    @GET("answers")
    Call<List<Answer>> getAnswers();


}
