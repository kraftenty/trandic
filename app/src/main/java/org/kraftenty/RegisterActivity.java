package org.kraftenty;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText emailEditText, passwordEditText, confirmPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        Button registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();
            
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please input every field.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Password Incorrect.", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Register Success!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Register Failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}