package com.example.adhd_analyzer;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
        ansA.setOnClickListener(this);
        ansB.setOnClickListener(this);
        ansC.setOnClickListener(this);
        ansD.setOnClickListener(this);
        submit.setOnClickListener(this);
        totalQuestionsTextView.setText("Total questions :" + totalQuestions);
        loadNewQuestions();


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
        Context context = this.getContext();
        File file = new File(context.getFilesDir(), "ADHD_Quiz.txt");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            for(int i=0; i<totalQuestions; i++){
                String line = QuestionAnswer.question[i] + "- ";
                fos.write(line.getBytes());
                String line2 = QuestionAnswer.answers[i] + "\n";
                fos.write(line2.getBytes());
            }
            files[0] = file.getCanonicalPath().toString();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String passStatus = "Finish message";
        new AlertDialog.Builder(this.getContext())
                .setTitle(passStatus)
                .setMessage("Finish All The Questions!")
                .show();
    }

    @Override
    public void onClick(View view) {
        ansA.setBackgroundColor(Color.DKGRAY);
        ansB.setBackgroundColor(Color.DKGRAY);
        ansC.setBackgroundColor(Color.DKGRAY);
        ansD.setBackgroundColor(Color.DKGRAY);

        Button clickedButton = (Button) view;
        if(clickedButton.getId() == R.id.Submit){
            QuestionAnswer.answers[currentQuestionIndex] = selectedAnswer;
            currentQuestionIndex++;
            loadNewQuestions();

        } else{
            //choise button clicked
            selectedAnswer = clickedButton.getText().toString();
            clickedButton.setBackgroundColor(Color.BLACK);
        }
    }
}