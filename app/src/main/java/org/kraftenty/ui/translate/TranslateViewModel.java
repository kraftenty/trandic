package org.kraftenty.ui.translate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TranslateViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TranslateViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Translate fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}