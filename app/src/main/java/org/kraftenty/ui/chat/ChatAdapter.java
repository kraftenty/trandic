package org.kraftenty.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.kraftenty.R;
import org.kraftenty.databinding.ItemChatMessageBinding;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChatAdapter extends ListAdapter<ChatMessage, ChatAdapter.ChatViewHolder> {
    private final String currentUserId;

    protected ChatAdapter(String currentUserId) {
        super(new DiffUtil.ItemCallback<ChatMessage>() {
            @Override
            public boolean areItemsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
                if (oldItem.getTimestamp() == null || newItem.getTimestamp() == null) {
                    return false;
                }
                return oldItem.getTimestamp().equals(newItem.getTimestamp());
            }

            @Override
            public boolean areContentsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
                if (oldItem.getContent() == null || newItem.getContent() == null) {
                    return false;
                }
                return oldItem.getContent().equals(newItem.getContent());
            }
        });
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = getItem(position);
        holder.bind(message);
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatMessageBinding binding;

        ChatViewHolder(ItemChatMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ChatMessage message) {    
            boolean isMyMessage = message.getUid() != null && message.getUid().equals(currentUserId);
            
            if (!isMyMessage) {
                binding.senderText.setVisibility(View.VISIBLE);
                String sender = message.getUid() != null ? message.getUid().substring(0, 3) : "Unknown";
                binding.senderText.setText(sender);
            } else {
                binding.senderText.setVisibility(View.GONE);
            }
            
            binding.messageText.setText(message.getContent());
            
            if (message.getTimestamp() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
                binding.timestampText.setText(sdf.format(message.getTimestamp().toDate()));
            } else {
                binding.timestampText.setText("No timestamp.");
            }

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(binding.getRoot());
            
            if (isMyMessage) {
                binding.messageText.setBackgroundResource(R.drawable.bg_chat_bubble_right);
                binding.timestampText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                constraintSet.clear(R.id.messageText, ConstraintSet.START);
                constraintSet.connect(R.id.messageText, ConstraintSet.END, 
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
                constraintSet.clear(R.id.timestampText, ConstraintSet.START);
                constraintSet.connect(R.id.timestampText, ConstraintSet.END,
                    R.id.messageText, ConstraintSet.START);
            } else {
                binding.messageText.setBackgroundResource(R.drawable.bg_chat_bubble_left);
                binding.timestampText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                constraintSet.clear(R.id.messageText, ConstraintSet.END);
                constraintSet.connect(R.id.messageText, ConstraintSet.START, 
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
                constraintSet.clear(R.id.timestampText, ConstraintSet.END);
                constraintSet.connect(R.id.timestampText, ConstraintSet.START,
                    R.id.messageText, ConstraintSet.END);
            }
            
            constraintSet.applyTo(binding.getRoot());
        }
    }
}