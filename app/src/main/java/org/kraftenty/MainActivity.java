package org.kraftenty;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.kraftenty.databinding.ActivityMainBinding;
import org.kraftenty.ui.words.WordsViewModel;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private WordsViewModel wordsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // 서버에서 토큰 유효성 검사
            auth.getCurrentUser().reload().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // 유효한 사용자인 경우 메인 화면 표시
                    binding = ActivityMainBinding.inflate(getLayoutInflater());
                    setContentView(binding.getRoot());

                    wordsViewModel = new ViewModelProvider(this).get(WordsViewModel.class);

                    BottomNavigationView navView = findViewById(R.id.nav_view);
                    AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                            R.id.navigation_translate, R.id.navigation_words, R.id.navigation_mypage)
                            .build();
                    NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
                    NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
                    NavigationUI.setupWithNavController(binding.navView, navController);
                } else {
                    // 유효하지 않은 사용자인 경우 로그아웃 처리
                    auth.signOut();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                }
            });
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    public WordsViewModel getWordsViewModel() {
        return wordsViewModel;
    }

}