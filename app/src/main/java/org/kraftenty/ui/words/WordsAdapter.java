package org.kraftenty.ui.words;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.kraftenty.R;
import org.kraftenty.api.chatgpt.WordPair;
import org.kraftenty.api.unsplash.UnsplashApi;
import org.kraftenty.api.unsplash.UnsplashResponse;
import org.kraftenty.config.ApiConfig;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Locale;

public class WordsAdapter extends ListAdapter<WordPair, WordsAdapter.WordViewHolder> {

    private static final String CLIENT_ID = ApiConfig.getUnsplashClientId();
    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiConfig.getUnsplashBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private static final UnsplashApi unsplashApi = retrofit.create(UnsplashApi.class);
    private TextToSpeech tts;

    protected WordsAdapter(Context context) {
        super(new DiffUtil.ItemCallback<WordPair>() {
            @Override
            public boolean areItemsTheSame(@NonNull WordPair oldItem, @NonNull WordPair newItem) {
                return oldItem.getEnglish().equals(newItem.getEnglish());
            }

            @Override
            public boolean areContentsTheSame(@NonNull WordPair oldItem, @NonNull WordPair newItem) {
                return oldItem.getEnglish().equals(newItem.getEnglish()) &&
                       oldItem.getKorean().equals(newItem.getKorean());
            }
        });
        tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.KOREAN);
            }
        });
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_word, parent, false);
        return new WordViewHolder(view, tts);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        WordPair word = getItem(position);
        holder.bind(word);
    }

    static class WordViewHolder extends RecyclerView.ViewHolder {
        private final ImageView wordImage;
        private final TextView englishText;
        private final TextView koreanText;
        private final TextToSpeech tts;

        public WordViewHolder(@NonNull View itemView, TextToSpeech tts) {
            super(itemView);
            this.tts = tts;
            wordImage = itemView.findViewById(R.id.wordImage);
            englishText = itemView.findViewById(R.id.englishText);
            koreanText = itemView.findViewById(R.id.koreanText);

            itemView.setOnClickListener(v -> {
                String koreanWord = koreanText.getText().toString();
                tts.speak(koreanWord, TextToSpeech.QUEUE_FLUSH, null, null);
            });
        }

        public void bind(WordPair word) {
            englishText.setText(word.getEnglish());
            koreanText.setText(word.getKorean());

            unsplashApi.searchPhotos(CLIENT_ID, word.getEnglish(), 1)
                    .enqueue(new Callback<UnsplashResponse>() {
                        @Override
                        public void onResponse(Call<UnsplashResponse> call, Response<UnsplashResponse> response) {
                            if (response.isSuccessful() && response.body() != null && 
                                !response.body().results.isEmpty()) {
                                String imageUrl = response.body().results.get(0).urls.small;
                                Glide.with(itemView.getContext())
                                        .load(imageUrl)
                                        .centerCrop()
                                        .into(wordImage);
                            }
                        }

                        @Override
                        public void onFailure(Call<UnsplashResponse> call, Throwable t) {
                            // 이미지 로드 실패 시 처리
                            // 그냥 빈 이미지로 둠
                            
                        }
                    });
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}