package org.kraftenty.api.chatgpt;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatResponse {
    private List<Choice> choices;
    private static final Gson gson = new Gson();

    // 응답:   {"t":"나는 스파게티와 라면을 좋아해요.","en":["spaghetti","ramen"],"kr":["스파게티","라면"]}

    public String getFirstMessage() {
        if (choices != null && !choices.isEmpty()) {
            String content = choices.get(0).getMessage().getContent();
            try {
                JsonObject jsonObject = gson.fromJson(content, JsonObject.class);
                String translatedText = jsonObject.get("t").getAsString();
                return translatedText;
            } catch (Exception e) {
                return "Error parsing response: " + e.getMessage();
            }
        }
        return "";
    }

    public List<WordPair> getWordPairs() {
        if (choices != null && !choices.isEmpty()) {
            try {
                String content = choices.get(0).getMessage().getContent();
                JsonObject jsonObject = gson.fromJson(content, JsonObject.class);
                JsonArray enArray = jsonObject.getAsJsonArray("en");
                JsonArray krArray = jsonObject.getAsJsonArray("kr");
                List<WordPair> result = new ArrayList<>();

                for (int i = 0; i < krArray.size(); i++) {
                    result.add(new WordPair(
                            enArray.get(i).getAsString(),
                            krArray.get(i).getAsString()
                    ));
                }
                return result;
            } catch (Exception e) {
                Log.e("ChatResponse", "Error parsing word pairs: " + e.getMessage());
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }

    public static class Choice {
        private Message message;

        public Message getMessage() {
            return message;
        }
    }

    public static class Message {
        private String content;

        public String getContent() {
            return content;
        }
    }
}