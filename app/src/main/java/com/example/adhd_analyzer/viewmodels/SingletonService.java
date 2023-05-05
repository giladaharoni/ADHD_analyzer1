package com.example.adhd_analyzer.viewmodels;

public class SingletonService {
    private static int PERMISSION_CODE_LOCATION;

    public static int PERMISSION() {
        return PERMISSION_CODE_LOCATION;
    }
}
