package com.example.adhd_analyzer.entities;

public class QAarray {
    int questionNumber;
    int answer;

    public QAarray(int question, int answer){
        this.questionNumber = question;
        this.answer = answer;
    }

    public int getAnswer() {
        return answer;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }
}
