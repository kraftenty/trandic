package org.kraftenty;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText currentPasswordEditText, newPasswordEditText, confirmNewPasswordEditText;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        user = FirebaseAuth.getInstance().getCurrentUser();

        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmNewPasswordEditText = findViewById(R.id.confirmNewPasswordEditText);
        Button changePasswordButton = findViewById(R.id.changePasswordButton);

        changePasswordButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();
            String confirmNewPassword = confirmNewPasswordEditText.getText().toString();

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmNewPassword)) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // 현재 사용자 재인증
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // 비밀번호 변경
                    user.updatePassword(newPassword).addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to update password: " + task2.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}