package com.example.adhd_analyzer.api;
import com.example.adhd_analyzer.QuestionAnswer;
import com.example.adhd_analyzer.entities.Answer;
import com.example.adhd_analyzer.entities.Data;
import com.example.adhd_analyzer.entities.DataGet;
import com.example.adhd_analyzer.entities.QAUobjects;
import com.example.adhd_analyzer.entities.QAarray;
import com.example.adhd_analyzer.entities.User;
import com.example.adhd_analyzer.logger_sensors.ProcessedData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebServiceApi {
    @GET("users")
    Call<List<User>> getUsers();
    @GET("users/register")
    Call<User> register(@Query("username") String username, @Query("fullname") String fullname, @Query("password") String password);

    @GET("users/login")
    Call<User> login(@Query("username") String username, @Query("password") String password);

    @PUT("users")
    Call<Void> updateUser(@Query("username") String username, @Query("fullname") String fullname, @Query("password") String password);

    @GET("ProcessData")
    Call<List<DataGet>> getDatas(@Query("username") String username, @Query("session") int session);

    @GET("ProcessData/lastSession")
    Call<List<DataGet>> getLastDatas(@Query("username") String username);

    @POST("ProcessData")
    Call<Void> uploadData(@Query("username") String username,@Body List<ProcessedData> dataList);
    @GET("answers")
    Call<List<Answer>> getAnswers();


    @GET("QuizAnswers")
    Call<List<QAUobjects>> getQuizAnswersByUser(@Query("username") String username);


    @POST("QuizAnswers")
    Call<Void> uploadQuizAnswers(@Query("username") String username, @Body List<QAarray> answers);



    @PUT("QuizAnswers/update/{username}")
    Call<Void> updateAnswers(@Path("username") String username, @Body QuizAnswersUpdateRequest request);


    public static class QuizAnswersUpdateRequest {
        public String username;
        public List<Answer> answers;
    }

}
