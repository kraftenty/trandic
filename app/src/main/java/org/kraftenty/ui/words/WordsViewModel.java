package org.kraftenty.ui.words;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.kraftenty.api.chatgpt.WordPair;

import java.util.ArrayList;
import java.util.List;

public class WordsViewModel extends ViewModel {

    private final MutableLiveData<List<WordPair>> words = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private DocumentSnapshot lastVisible = null;
    private final MutableLiveData<Boolean> isLastPage = new MutableLiveData<>(false);

    public void loadMoreWords() {
        if (isLoading.getValue() || isLastPage.getValue()) return;
        isLoading.setValue(true);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("words")
                .orderBy("en")
                .limit(10);

        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<WordPair> currentWords = words.getValue() != null ? words.getValue() : new ArrayList<>();
            
            if (!queryDocumentSnapshots.isEmpty()) {
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    String en = document.getString("en");
                    String kr = document.getString("kr");
                    currentWords.add(new WordPair(en, kr));
                }
                lastVisible = queryDocumentSnapshots.getDocuments()
                        .get(queryDocumentSnapshots.size() - 1);
                isLastPage.setValue(queryDocumentSnapshots.size() < 10);
            } else {
                isLastPage.setValue(true);
            }
            
            words.setValue(currentWords);
            isLoading.setValue(false);
        }).addOnFailureListener(e -> {
            isLoading.setValue(false);
        });
    }

    public LiveData<List<WordPair>> getWords() {
        return words;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void resetAndReload() {
        lastVisible = null;
        isLastPage.setValue(false);
        words.setValue(new ArrayList<>());
        loadMoreWords();
    }

    public LiveData<Boolean> getIsLastPage() {
        return isLastPage;
    }
}