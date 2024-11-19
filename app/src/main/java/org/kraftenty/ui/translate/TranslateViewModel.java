package org.kraftenty.ui.translate;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.kraftenty.api.ChatGPTService;
import org.kraftenty.api.ChatRequest;
import org.kraftenty.api.ChatResponse;
import org.kraftenty.config.ApiConfig;
import org.kraftenty.api.WordPair;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;

public class TranslateViewModel extends AndroidViewModel {
    private final ChatGPTService service;
    private final MutableLiveData<String> translatedText = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<List<WordPair>> wordPairs = new MutableLiveData<>(new ArrayList<>());

    public TranslateViewModel(Application application) {
        super(application);
        ApiConfig.init(application);
        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        service = retrofit.create(ChatGPTService.class);
    }

    public void translate(String text) {
        isLoading.setValue(true);
        
        ChatRequest request = new ChatRequest(text);
        service.translate("Bearer " + ApiConfig.getApiKey(), request)
                .enqueue(new Callback<ChatResponse>() {
                    @Override
                    public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            translatedText.setValue(response.body().getFirstMessage());
                            wordPairs.setValue(response.body().getWordPairs());
                        } else {
                            translatedText.setValue("Error: " + response.message());
                            wordPairs.setValue(new ArrayList<>());
                        }
                        isLoading.setValue(false);
                    }

                    @Override
                    public void onFailure(Call<ChatResponse> call, Throwable t) {
                        translatedText.setValue("Error: " + t.getMessage());
                        wordPairs.setValue(new ArrayList<>());
                        isLoading.setValue(false);
                    }
                });
    }

    public LiveData<String> getTranslatedText() {
        return translatedText;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<List<WordPair>> getWordPairs() {
        return wordPairs;
    }
}