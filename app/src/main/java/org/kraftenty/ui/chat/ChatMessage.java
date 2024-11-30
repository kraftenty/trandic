package org.kraftenty.ui.chat;

import com.google.firebase.Timestamp;


public class ChatMessage {
    private String uid;
    private String content;
    private Timestamp timestamp;

    public ChatMessage() {} // Firestore needs empty constructor

    public ChatMessage(String uid, String content, Timestamp timestamp) {
        this.uid = uid;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getUid() { return uid; }
    public String getContent() { return content; }
    public Timestamp getTimestamp() { return timestamp; }
}