package org.kraftenty.ui.words;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.kraftenty.MainActivity;
import org.kraftenty.api.chatgpt.WordPair;
import org.kraftenty.databinding.FragmentWordsBinding;

import java.util.ArrayList;


public class WordsFragment extends Fragment {

    private FragmentWordsBinding binding;
    private WordsViewModel wordsViewModel;
    private WordsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWordsBinding.inflate(inflater, container, false);
        wordsViewModel = ((MainActivity) requireActivity()).getWordsViewModel();

        setupRecyclerView();
        setupLoadMoreButton();
        observeViewModel();
        wordsViewModel.loadMoreWords();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new WordsAdapter(requireContext());
        binding.wordsRecyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                WordPair wordToDelete = adapter.getCurrentList().get(position);

                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Word")
                        .setMessage(wordToDelete.getEnglish() + " will be deleted. Continue?")
                        .setPositiveButton("Confirm", (dialog, which) -> {
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(userId)
                                    .collection("words")
                                    .document(wordToDelete.getEnglish())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(requireContext(), "Word has been deleted.", Toast.LENGTH_SHORT).show();
                                        wordsViewModel.resetAndReload();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(requireContext(), "Deletion failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        wordsViewModel.resetAndReload();
                                    });
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            wordsViewModel.resetAndReload();
                        })
                        .setOnCancelListener(dialog -> {
                            wordsViewModel.resetAndReload();
                        })
                        .show();
            }
        };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.wordsRecyclerView);
    }

    private void setupLoadMoreButton() {
        binding.loadMoreButton.setOnClickListener(v -> wordsViewModel.loadMoreWords());
    }

    private void observeViewModel() {
        wordsViewModel.getWords().observe(getViewLifecycleOwner(), words -> {
            adapter.submitList(new ArrayList<>(words));
            binding.emptyView.setVisibility(words.isEmpty() ? View.VISIBLE : View.GONE);
            binding.wordsRecyclerView.setVisibility(words.isEmpty() ? View.GONE : View.VISIBLE);
        });

        wordsViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.loadMoreButton.setEnabled(!isLoading);
        });

        wordsViewModel.getIsLastPage().observe(getViewLifecycleOwner(), isLastPage -> {
            binding.loadMoreButton.setVisibility(isLastPage ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}