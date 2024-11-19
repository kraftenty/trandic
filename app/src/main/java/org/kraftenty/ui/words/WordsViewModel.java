package org.kraftenty.ui.words;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WordsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public WordsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Words fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}