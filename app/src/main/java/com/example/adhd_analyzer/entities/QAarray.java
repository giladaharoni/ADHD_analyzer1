package com.example.adhd_analyzer.entities;

public class QAarray {
    int question;
    int answer;

    public QAarray(int question, int answer){
        this.answer = answer;
        this.question = question;
    }

    public int getAnswer() {
        return answer;
    }

    public int getQuestion() {
        return question;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public void setQuestion(int question) {
        this.question = question;
    }
}
