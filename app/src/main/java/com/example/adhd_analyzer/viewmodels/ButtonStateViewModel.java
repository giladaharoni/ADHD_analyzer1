package com.example.adhd_analyzer.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ButtonStateViewModel extends ViewModel {
    private MutableLiveData<Boolean> isButtonClickable = new MutableLiveData<>();

    public LiveData<Boolean> getButtonClickableState() {
        if (isButtonClickable.getValue() == null) {
            // Set the initial state of the button (clickable)
            isButtonClickable.setValue(true);
        }
        return isButtonClickable;
    }

    public void setButtonClickableState(boolean isClickable) {
        isButtonClickable.setValue(isClickable);
    }
}
