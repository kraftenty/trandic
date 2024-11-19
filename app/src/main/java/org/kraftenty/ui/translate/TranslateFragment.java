package org.kraftenty.ui.translate;

import android.os.Bundle;
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

import org.kraftenty.api.WordPair;
import org.kraftenty.databinding.FragmentTranslateBinding;

public class TranslateFragment extends Fragment {

    private FragmentTranslateBinding binding;
    private TranslateViewModel translateViewModel;

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
                chip.setText(pair.getKorean() + "  " + pair.getEnglish());
                chip.setClickable(true);
                chip.setCheckable(false);
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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}