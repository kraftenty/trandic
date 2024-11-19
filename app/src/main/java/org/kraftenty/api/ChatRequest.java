package org.kraftenty.api;

import java.util.ArrayList;
import java.util.List;

public class ChatRequest {
    private String model;
    private List<Message> messages;
    private ResponseFormat response_format;

    private static final String MODEL = "gpt-3.5-turbo";
    private static final String RESPONSE_FORMAT = "json_object";
    private static final String SYSTEM_CONTENT = "You are a translator and linguistic analyzer. For every user input, respond in the following JSON format:{\"t\":\"<translated sentence in Korean>\",\"en\":[<list of key English words>],\"kr\":[<list of key Korean words corresponding to the English words>]}.";
    private static final String SYSTEM_ROLE = "system";
    private static final String USER_ROLE = "user";

    public ChatRequest(String userContent) {
        this.model = MODEL;
        this.messages = new ArrayList<>();
        this.messages.add(new Message(SYSTEM_ROLE, SYSTEM_CONTENT));
        this.messages.add(new Message(USER_ROLE, userContent));
        this.response_format = new ResponseFormat(RESPONSE_FORMAT);
    }

    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    public static class ResponseFormat {
        private String type;

        public ResponseFormat(String type) {
            this.type = type;
        }
    }
}