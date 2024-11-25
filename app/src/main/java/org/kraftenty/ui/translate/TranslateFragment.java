package org.kraftenty.ui.translate;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.kraftenty.MainActivity;
import org.kraftenty.api.chatgpt.WordPair;
import org.kraftenty.databinding.FragmentTranslateBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class TranslateFragment extends Fragment {

    private FragmentTranslateBinding binding;
    private TranslateViewModel translateViewModel;
    private TextToSpeech tts;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTranslateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        translateViewModel = new ViewModelProvider(this).get(TranslateViewModel.class);

        EditText inputText = binding.inputText;
        TextView translatedText = binding.translatedText;
        Button translateButton = binding.translateButton;
        ProgressBar progressBar = binding.progressBar;

        translateViewModel.getTranslatedText().observe(getViewLifecycleOwner(), translatedText::setText);
        translateViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            translateButton.setEnabled(!isLoading);
        });

        ChipGroup wordButtonContainer = binding.wordButtonContainer;

        translateViewModel.getWordPairs().observe(getViewLifecycleOwner(), pairs -> {
            wordButtonContainer.removeAllViews();
            for (WordPair pair : pairs) {
                Chip chip = new Chip(requireContext());
                chip.setText(pair.getEnglish() + "  " + pair.getKorean());
                chip.setClickable(true);
                chip.setCheckable(false);
                
                chip.setOnClickListener(v -> {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    
                    Map<String, Object> word = new HashMap<>();
                    Log.d("TranslateFragment", "Pair.english: " + pair.getEnglish());
                    Log.d("TranslateFragment", "Pair.korean: " + pair.getKorean());
                    word.put("en", pair.getEnglish());
                    word.put("kr", pair.getKorean());

                    db.collection("users")
                        .document(userId)
                        .collection("words")
                        .document(pair.getEnglish())  // 영단어를 문서 ID로 사용
                        .set(word)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(requireContext(), pair.getEnglish() + " added to words", Toast.LENGTH_SHORT).show();
                            ((MainActivity) requireActivity()).getWordsViewModel().resetAndReload();
                        })
                        .addOnFailureListener(e -> 
                            Toast.makeText(requireContext(), "Failed to add word: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show());
                });
                
                wordButtonContainer.addView(chip);
            }
        });

        translateButton.setOnClickListener(v -> {
            String text = inputText.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(getContext(), "Please input text to translate.", Toast.LENGTH_SHORT).show();
                return;
            }
            translateViewModel.translate(text);
        });

        // TTS 초기화
        tts = new TextToSpeech(requireContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.KOREAN);
            }
        });

        // Speech 버튼 설정
        binding.speakButton.setOnClickListener(v -> {
            String translatedTextString = binding.translatedText.getText().toString();
            if (!translatedTextString.isEmpty()) {
                tts.speak(translatedTextString, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroyView();
        binding = null;
    }
}