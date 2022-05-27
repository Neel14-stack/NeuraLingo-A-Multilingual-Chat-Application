package com.invincible.neuralingo.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.invincible.neuralingo.ChatActivity;
import com.invincible.neuralingo.IntegrationActivity;
import com.invincible.neuralingo.Model.ApiDataModel;
import com.invincible.neuralingo.Model.ChatMessageModel;
import com.invincible.neuralingo.R;
import com.invincible.neuralingo.databinding.ReceiveMessageLayoutBinding;
import com.invincible.neuralingo.databinding.ReceivedMessageRepliedLayoutBinding;
import com.invincible.neuralingo.databinding.SentMessageLayoutBinding;
import com.invincible.neuralingo.databinding.SentMessageRepliedLayoutBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatMessagesAdapter extends RecyclerView.Adapter {

    Context context;
    String uid;

    ArrayList<ChatMessageModel> chatMessageModelArrayList;
    final String LOG_TAG = ChatMessagesAdapter.class.getSimpleName();
    public static Map<String, Bitmap> usersBitmap = new HashMap<>();
    public static Map<String, String> userName = new HashMap<>();
    static Map<String, Boolean> alreadyRequested = new HashMap<>();

    DatabaseReference databaseReference;
    final static int ITEM_SENT = 1;
    final static int ITEM_RECEIVED = 2;
    final static int ITEM_SENT_REPLIED = 3;
    final static int ITEM_RECEIVED_REPLIED = 4;
    public ChatMessagesAdapter(Context context, ArrayList<ChatMessageModel> chatMessageModelArrayList, String uid)
    {
        this.context = context;
        this.chatMessageModelArrayList = chatMessageModelArrayList;
        this.uid = uid;
        this.databaseReference = FirebaseDatabase.getInstance(context.getString(R.string.database_url)).getReference();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SENT) {
            SentMessageLayoutBinding messageLayoutBinding = SentMessageLayoutBinding.inflate(LayoutInflater.from(context), parent, false);
            return new SentMessageViewHolder(messageLayoutBinding);
        } else if (viewType == ITEM_RECEIVED) {
            ReceiveMessageLayoutBinding receiveMessageLayoutBinding = ReceiveMessageLayoutBinding.inflate(LayoutInflater.from(context), parent, false);
            return new ReceivedMessageViewHolder(receiveMessageLayoutBinding);
        } else if(viewType == ITEM_SENT_REPLIED)
        {
            SentMessageRepliedLayoutBinding sentMessageRepliedLayoutBinding = SentMessageRepliedLayoutBinding.inflate(LayoutInflater.from(context), parent, false);
            return new SentMessageRepliedViewHolder(sentMessageRepliedLayoutBinding);
        }else if(viewType == ITEM_RECEIVED_REPLIED)
        {
            ReceivedMessageRepliedLayoutBinding receivedMessageRepliedLayoutBinding = ReceivedMessageRepliedLayoutBinding.inflate(LayoutInflater.from(context), parent, false);
            return new ReceivedMessageRepliedHolder(receivedMessageRepliedLayoutBinding);
        }
        {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String senderId = chatMessageModelArrayList.get(position).getSender_id();
        if(position > 0 && chatMessageModelArrayList.get(position-1).getSender_id().equals(senderId) && holder.getItemViewType() == ITEM_RECEIVED)
        {
            ReceivedMessageViewHolder receivedMessageViewHolder = (ReceivedMessageViewHolder) holder;
            receivedMessageViewHolder.senderImage.setVisibility(View.GONE);
            receivedMessageViewHolder.receiveMessageUser.setVisibility(View.GONE);
        }else if(holder.getItemViewType() == ITEM_RECEIVED){
            ReceivedMessageViewHolder receivedMessageViewHolder = (ReceivedMessageViewHolder) holder;
            receivedMessageViewHolder.senderImage.setVisibility(View.VISIBLE);
            receivedMessageViewHolder.receiveMessageUser.setVisibility(View.VISIBLE);

        }
        boolean alreadyHandled  = false;
        if(userName.get(senderId) == null && alreadyRequested.getOrDefault(senderId, Boolean.FALSE) == Boolean.FALSE)
        {
            alreadyHandled = true;
            alreadyRequested.put(senderId, Boolean.TRUE);
            databaseReference.child("Users").child(senderId).child("Profile").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    userName.put(senderId, dataSnapshot.child("name").getValue(String.class));
                    byte[] bytes = Base64.decode(dataSnapshot.child("image").getValue(String.class), Base64.DEFAULT);
                    ChatMessagesAdapter.this.notifyDataSetChanged();
                    if(holder.getItemViewType() == ITEM_RECEIVED)
                    {
                        usersBitmap.put(senderId, IntegrationActivity
                                .decodeSampledBitmap(bytes, 50, 50));
                        ReceivedMessageViewHolder receivedMessageViewHolder = (ReceivedMessageViewHolder) holder;
                        receivedMessageViewHolder.senderImage.setImageBitmap(usersBitmap.get(senderId));
                        receivedMessageViewHolder.receiveMessageUser.setText(userName.get(senderId));
                    }
                }
            });
        }
        ChatMessageModel chatMessageModel = chatMessageModelArrayList.get(position);

        switch (holder.getItemViewType())
        {
            case ITEM_SENT:
                SentMessageViewHolder viewHolder = (SentMessageViewHolder) holder;
                viewHolder.sentMessage.setText(chatMessageModelArrayList.get(position).getMessage(ChatActivity.langKey));
                if(chatMessageModelArrayList.get(position).getReceived()==0)
                {
                    viewHolder.successSentImage.setImageResource(R.drawable.ic_baseline_access_time_24);
                }else{
                    viewHolder.successSentImage.setImageResource(R.drawable.ic_baseline_done_white_24);
                }
                break;

            case ITEM_RECEIVED:
                ReceivedMessageViewHolder receivedMessageViewHolder = (ReceivedMessageViewHolder) holder;
                receivedMessageViewHolder.receivedMessage.setText(chatMessageModelArrayList.get(position).getMessage(ChatActivity.langKey));
                if(!alreadyHandled)
                {
                    receivedMessageViewHolder.senderImage.setImageBitmap(usersBitmap.get(senderId));
                    receivedMessageViewHolder.receiveMessageUser.setText(userName.get(senderId));
                }
                break;
            case ITEM_SENT_REPLIED:
                SentMessageRepliedViewHolder sentMessageRepliedViewHolder = (SentMessageRepliedViewHolder) holder;
                sentMessageRepliedViewHolder.sentMessage.setText(chatMessageModel.getMessage(ChatActivity.langKey));
                sentMessageRepliedViewHolder.repliedToUserName.setText(userName.getOrDefault(chatMessageModel.getRepliedModel().getSender_id(), ""));
                sentMessageRepliedViewHolder.repliedToMessage.setText(chatMessageModel.getRepliedModel().getMessage(ChatActivity.langKey));
                if(chatMessageModelArrayList.get(position).getReceived()==0)
                {
                    sentMessageRepliedViewHolder.sentSuccess.setImageResource(R.drawable.ic_baseline_access_time_24);
                }else{
                    sentMessageRepliedViewHolder.sentSuccess.setImageResource(R.drawable.ic_baseline_done_white_24);
                }
                break;
            case ITEM_RECEIVED_REPLIED:
                ReceivedMessageRepliedHolder receivedMessageRepliedHolder = (ReceivedMessageRepliedHolder) holder;
                receivedMessageRepliedHolder.receivedMessage.setText(chatMessageModel.getMessage(ChatActivity.langKey));
                receivedMessageRepliedHolder.repliedToMessage.setText(chatMessageModel.getRepliedModel().getMessage(ChatActivity.langKey));
//                receivedMessageRepliedHolder.senderName.setText(userName.getOrDefault(senderId, ""));
                try {
                    if(!alreadyHandled)
                    {
                        receivedMessageRepliedHolder.senderImage.setImageBitmap(usersBitmap.get(senderId));
                        receivedMessageRepliedHolder.senderName.setText(userName.get(senderId));
                    }
                }catch (Exception e)
                {
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageModelArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessageModelArrayList.get(position).getSender_id().equals(uid))
        {
            if(chatMessageModelArrayList.get(position).isReplied())
            {
                return ITEM_SENT_REPLIED;
            }
            return ITEM_SENT;
        }
        else {
            if(chatMessageModelArrayList.get(position).isReplied())
            {
                return ITEM_RECEIVED_REPLIED;
            }
            return ITEM_RECEIVED;
        }
    }

    public static class SentMessageViewHolder extends RecyclerView.ViewHolder{

        TextView sentMessage;
        ImageView successSentImage;
        public SentMessageViewHolder(SentMessageLayoutBinding binding) {
            super(binding.getRoot());
            sentMessage = binding.sentMessage;
            successSentImage = binding.sentSuccess;
        }
    }

    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{

        TextView receivedMessage, receiveMessageUser;
        ShapeableImageView senderImage;

        public ReceivedMessageViewHolder(ReceiveMessageLayoutBinding binding) {
            super(binding.getRoot());
            receivedMessage = binding.receiveMessage;
            senderImage = binding.senderImage;
            receiveMessageUser = binding.receiveMessageUserName;
        }
    }

    public static class SentMessageRepliedViewHolder extends RecyclerView.ViewHolder
    {
        TextView sentMessage, repliedToMessage, repliedToUserName;
        ImageView sentSuccess;
        public SentMessageRepliedViewHolder(SentMessageRepliedLayoutBinding binding) {
            super(binding.getRoot());
            sentMessage = binding.sentMessage;
            repliedToMessage = binding.receiveMessage;
            repliedToUserName = binding.repliedToName;
            sentSuccess = binding.sentSuccess;
        }
    }

    public static class ReceivedMessageRepliedHolder extends RecyclerView.ViewHolder
    {
        TextView receivedMessage, repliedToMessage, senderName;
        ImageView senderImage;
        public ReceivedMessageRepliedHolder(ReceivedMessageRepliedLayoutBinding binding) {
            super(binding.getRoot());
            receivedMessage = binding.receiveMessage;
            repliedToMessage = binding.sentMessage;
            senderName = binding.receiveMessageUserName;
            senderImage = binding.senderImage;
        }
    }
}
