package com.invincible.neuralingo.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.invincible.neuralingo.Interfaces.PositionInterface;
import com.invincible.neuralingo.Model.ChatRoomModel;
import com.invincible.neuralingo.databinding.ChatRoomItemLayoutBinding;

import java.util.LinkedList;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomItemHolder> {

    private Context context;
    String uid;
    DatabaseReference databaseReference;
    ChatRoomItemLayoutBinding binding;
    LinkedList<ChatRoomModel> chatRoomModelLinkedList;
    PositionInterface positionInterface;

    public ChatRoomAdapter(Context context, LinkedList<ChatRoomModel> chatRoomModelLinkedList, String uid, DatabaseReference databaseReference, PositionInterface positionInterface)
    {
        this.context = context;
        this.chatRoomModelLinkedList = chatRoomModelLinkedList;
        this.uid = uid;
        this.databaseReference = databaseReference;
        this.positionInterface = positionInterface;
    }

    @NonNull
    @Override
    public ChatRoomItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ChatRoomItemLayoutBinding.inflate(LayoutInflater.from(this.context), parent, false);
        return new ChatRoomItemHolder(binding);
    }

    public void filterItems(LinkedList<ChatRoomModel> chatRoomModels)
    {
        this.chatRoomModelLinkedList = chatRoomModels;
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomItemHolder holder, int position) {
        String roomNameString = chatRoomModelLinkedList.get(position).getRoomName();
        holder.roomName.setText(roomNameString);
        byte[] bytes = Base64.decode(chatRoomModelLinkedList.get(position).getImageString(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        holder.roomIcon.setImageBitmap(bitmap);
        holder.lastMessage.setText(chatRoomModelLinkedList.get(position).getLastMessages());
        if(chatRoomModelLinkedList.get(position).isJoined())
        {
            holder.joinButton.setVisibility(View.GONE);
            holder.constraintLayout.setOnClickListener(view -> positionInterface.getClickedModel(chatRoomModelLinkedList.get(position)));
        }else {
            holder.joinButton.setVisibility(View.VISIBLE);
        }
        holder.joinButton.setOnClickListener(view -> positionInterface.onJoin(chatRoomModelLinkedList.get(position)));

    }


    @Override
    public int getItemCount() {
        return chatRoomModelLinkedList.size();
    }

    public static class ChatRoomItemHolder extends RecyclerView.ViewHolder
    {
        TextView roomName, lastMessage;
        ShapeableImageView roomIcon;
        MaterialButton joinButton;
        ConstraintLayout constraintLayout;

        public ChatRoomItemHolder(ChatRoomItemLayoutBinding binding) {
            super(binding.getRoot());
            roomIcon = binding.chatRoomIcon;
            roomName = binding.chatRoomName;
            joinButton = binding.joinButton;
            lastMessage = binding.lastMessage;
            constraintLayout = binding.getRoot();
        }
    }
}
