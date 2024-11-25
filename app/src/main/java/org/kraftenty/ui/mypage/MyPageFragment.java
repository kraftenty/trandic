package org.kraftenty.ui.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import org.kraftenty.ChangePasswordActivity;
import org.kraftenty.LoginActivity;
import org.kraftenty.MainActivity;
import org.kraftenty.R;
import org.kraftenty.databinding.FragmentMypageBinding;

public class MyPageFragment extends Fragment {

    private FragmentMypageBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMypageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            binding.userEmail.setText(user.getEmail());
        }

        binding.changePasswordButton.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ChangePasswordActivity.class));
        });

        binding.logoutButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                })
                .setNegativeButton("No", null)
                .show();
        });

        binding.deleteAccountButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_reauthenticate, null);
                    EditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);

                    new AlertDialog.Builder(requireContext())
                        .setTitle("Re-authenticate")
                        .setMessage("Please enter your password to continue")
                        .setView(dialogView)
                        .setPositiveButton("Confirm", (dialogInterface, i) -> {
                            String password = passwordEditText.getText().toString();
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null) {
                                AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), password);
                                currentUser.reauthenticate(credential)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            currentUser.delete()
                                                .addOnCompleteListener(deleteTask -> {
                                                    if (deleteTask.isSuccessful()) {
                                                        Toast.makeText(requireContext(), "Account deleted successfully", 
                                                            Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(requireContext(), LoginActivity.class));
                                                        requireActivity().finish();
                                                    } else {
                                                        Toast.makeText(requireContext(), 
                                                            "Failed to delete account: " + deleteTask.getException().getMessage(),
                                                            Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        } else {
                                            Toast.makeText(requireContext(), "Authentication failed", 
                                                Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                })
                .setNegativeButton("No", null)
                .show();
        });

        binding.resetWordsListButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Reset Word List")
                    .setMessage("All words will be deleted. Continue?")
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(userId)
                                .collection("words")
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    WriteBatch batch = FirebaseFirestore.getInstance().batch();
                                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                                        batch.delete(document.getReference());
                                    }
                                    batch.commit().addOnSuccessListener(aVoid -> {
                                        Toast.makeText(requireContext(), "Word list has been reset.", Toast.LENGTH_SHORT).show();
                                        ((MainActivity) requireActivity()).getWordsViewModel().resetAndReload();
                                    }).addOnFailureListener(e -> 
                                        Toast.makeText(requireContext(), "Initialization failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}