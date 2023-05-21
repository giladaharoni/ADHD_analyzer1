package com.example.adhd_analyzer.entities;

public class QAUobjects {
    int questionNumber;
    int answer;

    String username;

    public QAUobjects(int question, int answer){
        this.questionNumber = question;
        this.answer = answer;
    }

    public String getUsername() {
        return username;
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

    public void setUsername(String username) {
        this.username = username;
    }
}
