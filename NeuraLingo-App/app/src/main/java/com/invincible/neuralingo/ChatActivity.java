package com.invincible.neuralingo;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.invincible.neuralingo.Adapters.ChatMessagesAdapter;
import com.invincible.neuralingo.Api.ApiMainClass;
import com.invincible.neuralingo.Interfaces.ApiCallbacks;
import com.invincible.neuralingo.ItemTouchHelper.MessageSwipeController;
import com.invincible.neuralingo.ItemTouchHelper.SwipeControllerActions;
import com.invincible.neuralingo.Model.ApiDataModel;
import com.invincible.neuralingo.Model.ChatMessageModel;
import com.invincible.neuralingo.Model.ChatRoomModel;
import com.invincible.neuralingo.Model.RepliedChatMessageModel;
import com.invincible.neuralingo.databinding.ActivityChatBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    ChatMessagesAdapter adapter;
    DatabaseReference databaseReference;
    String uid;
    String roomName = "";
    ChatRoomModel chatRoomModel;
    Menu languageMenu;
    String selected, nonSelected;
    boolean replyingEnabled = false;
    ChatMessageModel replyingToChatMessage = new ChatMessageModel();
    ArrayList<String> messagePendingQueue = new ArrayList<>();
    ArrayList<ChatMessageModel> chatMessageModelArrayList = new ArrayList<>();
    public static Map<String, String> shortLangMap = new HashMap<>();
    public static String langKey, revLangKey;
    static String LOG_TAG = ChatActivity.class.getSimpleName();
    static {
        shortLangMap.put("English", "eng");
        shortLangMap.put("German", "deu");
        shortLangMap.put("Spanish", "spa");
        shortLangMap.put("Hindi", "hin");
        shortLangMap.put("Bengali", "ben");
        shortLangMap.put("French", "fra");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        super.setContentView(binding.getRoot());
        selected = getString(R.string.english);
        PreferenceManager.getDefaultSharedPreferences(this);
        uid = FirebaseAuth.getInstance().getUid();
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.real_matte_black));
        chatRoomModel = (ChatRoomModel) getIntent().getSerializableExtra("chatRoomModel");
        roomName = chatRoomModel.getRoomName();
        byte[] bytes = Base64.decode(chatRoomModel.getImageString(), Base64.DEFAULT);
        binding.chatRoomIcon.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        databaseReference = FirebaseDatabase.getInstance(getResources().getString(R.string.database_url)).getReference();

        databaseReference = databaseReference.child("Rooms").child(roomName);
        binding.chatRoomName.setText(roomName);

        binding.chatRecyclerViewLayout.cancelReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replyingEnabled = false;
                binding.chatRecyclerViewLayout.replyLayout.setVisibility(View.GONE);
            }
        });

        MessageSwipeController messageSwipeController = new MessageSwipeController(getApplicationContext(), new SwipeControllerActions() {
            @Override
            public void showReplyUI(int position) {
                replyingEnabled = true;
                String userName = "";
                String userId = chatMessageModelArrayList.get(position).getSender_id();
                replyingToChatMessage = chatMessageModelArrayList.get(position);
                replyingToChatMessage.setPosition(position);
                if(ChatMessagesAdapter.userName.get(userId) != null)
                {
                    userName = ChatMessagesAdapter.userName.get(userId);
                }
                binding.chatRecyclerViewLayout.replyLayout.setVisibility(View.VISIBLE);
                binding.chatRecyclerViewLayout.repliedMessage.setText(chatMessageModelArrayList.get(position).getMessage(ChatActivity.langKey));
                binding.chatRecyclerViewLayout.userNameReplied.setText(userName);
                if(ChatMessagesAdapter.usersBitmap.get(userId)!= null)
                {
                    binding.chatRecyclerViewLayout.replyingToUserImage.setImageBitmap(ChatMessagesAdapter.usersBitmap.get(userId));
                }
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(messageSwipeController);
        itemTouchHelper.attachToRecyclerView(binding.chatRecyclerViewLayout.chatRecyclerView);


//        binding.chatRecyclerViewLayout.chatRecyclerView.scrollToPosition(chatMessageModelArrayList.size());
        binding.backNavigate.setOnClickListener(view -> finish());
        binding.chatRecyclerViewLayout.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = getInputText();
                if(!message.equals("")) {
                    sendMessage(message);

                }
                binding.chatRecyclerViewLayout.messageEdit.setText("");
                binding.chatRecyclerViewLayout.messageInputLayout.setActivated(false);
            }
        });
        setSupportActionBar(binding.toolbar);

//        System.out.println(databaseReference.push().getKey());
    }

   public String getInputText()
   {
       String output;
       if(binding.chatRecyclerViewLayout.messageEdit.getText()!=null && !binding.chatRecyclerViewLayout.messageEdit.getText().toString().equals(""))
       {output = binding.chatRecyclerViewLayout.messageEdit.getText().toString();}
       else{
           output = "";
       }
       return output;
   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.language_menu, menu);
        int count = 1;
        for(String language : chatRoomModel.getLanguage())
        {
            menu.add(language);
            menu.getItem(count).setCheckable(true);
            if(menu.getItem(count).getTitle().equals(selected))
            {
                menu.getItem(count).setChecked(true);
            }else{
                nonSelected = language;
            }
            count += 1;
        }
        languageMenu = menu;
        langKey = shortLangMap.get(nonSelected)+"-"+shortLangMap.get(selected);
        revLangKey = shortLangMap.get(selected)+"-"+shortLangMap.get(nonSelected);
        System.out.println(selected + "-" + nonSelected);
        adapter = new ChatMessagesAdapter(this, chatMessageModelArrayList, uid);
        binding.chatRecyclerViewLayout.chatRecyclerView.setAdapter(adapter);
        chatListener();
        return super.onCreateOptionsMenu(languageMenu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        selected = String.valueOf(item.getTitle());
        for(String lang : chatRoomModel.getLanguage())
        {
            if(!lang.equals(selected))
            {
                nonSelected = lang;
            }
        }
        System.out.println(selected + "-" + nonSelected);
        langKey = shortLangMap.get(nonSelected)+"-"+shortLangMap.get(selected);
        revLangKey = shortLangMap.get(selected)+"-"+shortLangMap.get(nonSelected);
        adapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        for(int i=1;i<=2;i++){
            if(menu.getItem(i).getTitle().equals(selected))
            {
                menu.getItem(i).setChecked(true);
            }else{
                menu.getItem(i).setChecked(false);
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    private void sendMessage(String message)
    {
        String messageId = databaseReference.child("Chats").push().getKey();
        ApiDataModel apiDataModel = new ApiDataModel(message, messageId, roomName, uid);
        apiDataModel.addModelName(revLangKey);
        apiDataModel.setPosition(chatMessageModelArrayList.size());
        ChatMessageModel chatMessageModel = new ChatMessageModel();
        chatMessageModel.setMessageId(messageId);
        chatMessageModel.setSender_id(uid);
        chatMessageModel.setReceived(0);
        chatMessageModel.setPosition(chatMessageModelArrayList.size());
        chatMessageModel.getMessageMap().put(langKey, message);

        if(replyingEnabled)
        {
            chatMessageModel.setReplied(true);
            apiDataModel.setReplied(true);
            chatMessageModel.setRepliedModel(replyingToChatMessage);
            apiDataModel.setRepliedChatMessageModel(replyingToChatMessage);
        }
        if(ApiMainClass.obj != null)
        {
            chatMessageModelArrayList.add(chatMessageModel);
            messagePendingQueue.add(messageId);
            adapter.notifyItemInserted(apiDataModel.getPosition());
            ApiMainClass.obj.sendMessage(apiDataModel, new ApiCallbacks() {
                @Override
                public void onMessageTranslated(JSONObject jsonObject) {
                    super.onMessageTranslated(jsonObject);
                    try {
                        int position = jsonObject.getInt("position");
                        String translateKey = shortLangMap.get(selected) + "-" + shortLangMap.get(nonSelected);
                        String translatedMessage = jsonObject.getJSONObject("messageMap").getString(translateKey);
                        chatMessageModelArrayList.get(position).getMessageMap().put(translateKey, translatedMessage);
                        chatMessageModelArrayList.get(position).setReceived(1);
                        adapter.notifyItemChanged(position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    super.onError(errorMessage);
                    Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_SHORT).show();
                }
            });
        }
        int lastPosition = chatMessageModelArrayList.size();
        if(lastPosition>0)
        {
            lastPosition -= 1;
        }
        binding.chatRecyclerViewLayout.replyLayout.setVisibility(View.GONE);
        binding.chatRecyclerViewLayout.chatRecyclerView.scrollToPosition(lastPosition);
    }

    private void chatListener()
    {
        FirebaseDatabase.getInstance().goOnline();
        databaseReference.child("Chats").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e(LOG_TAG, "Adding the Child");
                if(messagePendingQueue.contains(snapshot.getKey()))
                {
                    messagePendingQueue.remove(snapshot.getKey());
                }else{
                    ChatMessageModel chatMessageModel = snapshot.getValue(ChatMessageModel.class);
                    chatMessageModel.setMessageId(snapshot.getKey());
                    chatMessageModel.setReceived(1);
//                    chatMessageModel.setSenderId(snapshot.child("sender_id").getValue(String.class));
//                    Log.e(LOG_TAG, chatMessageModel.getSenderId());
//                    chatMessageModel.getMessageMap().put(langKey, snapshot.child(langKey).getValue(String.class));
//                    chatMessageModel.getMessageMap().put(revLangKey, snapshot.child(revLangKey).getValue(String.class));
                    System.out.println(chatMessageModel.getMessageId());
                    System.out.println(chatMessageModel.getMessageMap());
                    System.out.println(chatMessageModel.getPosition());
                    System.out.println(chatMessageModel.getSender_id());
                    chatMessageModelArrayList.add(chatMessageModel);
                    adapter.notifyItemInserted(chatMessageModelArrayList.size());
                    int lastPosition = chatMessageModelArrayList.size();
                    if(lastPosition>0)
                    {
                        lastPosition -= 1;
                    }
                    binding.chatRecyclerViewLayout.chatRecyclerView.scrollToPosition(lastPosition);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
