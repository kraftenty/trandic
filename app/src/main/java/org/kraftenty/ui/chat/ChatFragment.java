package org.kraftenty.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import org.kraftenty.databinding.FragmentChatBinding;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatFragment extends Fragment {
    private FragmentChatBinding binding;
    private ChatAdapter adapter;
    private FirebaseFirestore db;
    private String currentUserId;
    private ListenerRegistration messagesListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        
        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        
        setupRecyclerView();
        setupMessageSending();
        listenToMessages();
        
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new ChatAdapter(currentUserId);
        binding.chatRecyclerView.setAdapter(adapter);
        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setupMessageSending() {
        binding.sendButton.setOnClickListener(v -> {
            String message = binding.messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                Map<String, Object> chatMessage = new HashMap<>();
                chatMessage.put("uid", currentUserId);
                chatMessage.put("content", message);
                chatMessage.put("timestamp", FieldValue.serverTimestamp());

                db.collection("chat")
                    .add(chatMessage)
                    .addOnSuccessListener(documentReference -> {
                        binding.messageInput.setText("");
                    })
                    .addOnFailureListener(e -> 
                        Toast.makeText(requireContext(), "Failed to send message", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void listenToMessages() {
        messagesListener = db.collection("chat")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener((value, error) -> {
                if (error != null || binding == null) {
                    return;
                }
                if (value != null) {
                    List<ChatMessage> messages = new ArrayList<>();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        messages.add(doc.toObject(ChatMessage.class));
                    }
                    adapter.submitList(messages);
                    binding.chatRecyclerView.scrollToPosition(messages.size() - 1);
                }
            });
    }

    @Override
    public void onDestroyView() {
        if (messagesListener != null) {
            messagesListener.remove();
        }
        super.onDestroyView();
        binding = null;
    }
}
