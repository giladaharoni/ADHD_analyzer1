package com.example.adhd_analyzer;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adhd_analyzer.api.UserApi;
import com.example.adhd_analyzer.api.WebServiceApi;
import com.example.adhd_analyzer.entities.QAarray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuizFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizFragment extends Fragment implements View.OnClickListener{

    TextView totalQuestionsTextView;
    TextView questionTextView;
    Button ansA, ansB, ansC, ansD;
    Button submit;
    Button startOver;
    int totalQuestions = QuestionAnswer.question.length;
    int currentQuestionIndex = 0;
    String selectedAnswer = "";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String username;

    public static String files[] = {
            "", "", "", "", ""
    };

    public QuizFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment quiz_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QuizFragment newInstance(String param1, String param2) {
        QuizFragment fragment = new QuizFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //totalQuestionsTextView = findViewById(R.id.total);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz_fragment, container, false);
        totalQuestionsTextView = view.findViewById(R.id.total);
        questionTextView = view.findViewById(R.id.question);
        ansA = view.findViewById(R.id.Ans_never);
        ansB = view.findViewById(R.id.Ans_rarely);
        ansC = view.findViewById(R.id.Ans_often);
        ansD = view.findViewById(R.id.Ans_very_often);
        submit = view.findViewById(R.id.Submit);
        startOver = view.findViewById(R.id.Start_over);
        ansA.setOnClickListener(this);
        ansB.setOnClickListener(this);
        ansC.setOnClickListener(this);
        ansD.setOnClickListener(this);
        submit.setOnClickListener(this);
        startOver.setOnClickListener(this);
        totalQuestionsTextView.setText("Total questions :" + totalQuestions);
        loadNewQuestions();

        //username = savedInstanceState.getString("username");

        return view;
    }

    private void loadNewQuestions() {
        if(currentQuestionIndex == totalQuestions){
            finishQuiz();
            return;
        }
        questionTextView.setText(QuestionAnswer.question[currentQuestionIndex]);
    }

    private void finishQuiz() {
        String passStatus = "Finish";
        new AlertDialog.Builder(this.getContext())
                .setTitle(passStatus)
                .setMessage("Finish All The Questions! \n you can start again whenever you want!")
                .show();

        List<QAarray> listJson = new ArrayList<QAarray>();
        //QAarray[] arrayJson = new QAarray[66];
        for(int i = 0; i <= 65; i++){
            switch (QuestionAnswer.answers[i]){
                case "Never":
                    //arrayJson[i] = new QAarray(i, 1);
                    listJson.add(new QAarray(i, 1));
                    continue;
                case "Rarely":
                    //arrayJson[i] = new QAarray(i, 2);
                    listJson.add(new QAarray(i, 2));
                    continue;
                case "Often":
                    //arrayJson[i] = new QAarray(i, 3);
                    listJson.add(new QAarray(i, 3));
                    continue;
                case "Very often":
                    //arrayJson[i] = new QAarray(i, 4);
                    listJson.add(new QAarray(i, 4));
                    continue;
                default:
                    listJson.add(new QAarray(i, 0));
                    //arrayJson[i] = new QAarray(i, 0);
            }


        }
        username = login.theUser.getUserName();
        Context context = this.getContext();
        WebServiceApi api = UserApi.getRetrofitInstance().create(WebServiceApi.class);
        Call<Void> call = api.uploadQuizAnswers(username, listJson);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(getActivity(), "the answers updates!", Toast.LENGTH_SHORT).show();
            }
            public void onFailure(Call<Void> call, Throwable t){
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        currentQuestionIndex=0;
        loadNewQuestions();
    }

    @Override
    public void onClick(View view) {

        ansA.setBackgroundColor(getResources().getColor(R.color.myButtonColor));
        ansB.setBackgroundColor(getResources().getColor(R.color.myButtonColor));
        ansC.setBackgroundColor(getResources().getColor(R.color.myButtonColor));
        ansD.setBackgroundColor(getResources().getColor(R.color.myButtonColor));

        Button clickedButton = (Button) view;
        if(clickedButton.getId() == R.id.Start_over){
            currentQuestionIndex=0;
            loadNewQuestions();
        }
        else if(clickedButton.getId() == R.id.Submit){
            if(currentQuestionIndex == totalQuestions){
                return;
            }
            QuestionAnswer.answers[currentQuestionIndex] = selectedAnswer;
            selectedAnswer = "";
            currentQuestionIndex++;
            loadNewQuestions();

        } else{
            //choise button clicked
            selectedAnswer = clickedButton.getText().toString();
            clickedButton.setBackgroundColor(getResources().getColor(R.color.button2));
        }
    }
}