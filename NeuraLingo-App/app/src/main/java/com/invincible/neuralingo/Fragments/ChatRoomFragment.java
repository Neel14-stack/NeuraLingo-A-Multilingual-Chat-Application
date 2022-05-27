package com.invincible.neuralingo.Fragments;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.invincible.neuralingo.Adapters.ChatRoomAdapter;
import com.invincible.neuralingo.Adapters.LanguageSelectAdapter;
import com.invincible.neuralingo.ChatActivity;
import com.invincible.neuralingo.IntegrationActivity;
import com.invincible.neuralingo.Interfaces.PositionInterface;
import com.invincible.neuralingo.MainActivityBottomTab;
import com.invincible.neuralingo.Model.ChatRoomModel;
import com.invincible.neuralingo.R;
import com.invincible.neuralingo.databinding.CreateChatRoomLayoutBinding;
import com.invincible.neuralingo.databinding.FragmentChatRoomBinding;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChatRoomFragment extends Fragment implements MainActivityBottomTab.FloatingActionInterface {

    private Context context;
    private DatabaseReference databaseReference;
    String uid;
    FragmentChatRoomBinding binding;
    ChatRoomAdapter adapter;
    private LinkedList<ChatRoomModel> chatRoomModelLinkedList;
    final int PICK_PHOTO = 77;
    SharedPreferences sharedPref;
    CreateChatRoomLayoutBinding createChatRoomLayoutBinding;
    String createChatRoomImg;
    final String LOG_TAG = ChatRoomFragment.class.getSimpleName();

    public ChatRoomFragment(Context context)
    {
        this.context = context;
    }

    public static ChatRoomFragment newInstance(Context context) {
        Bundle args = new Bundle();
        ChatRoomFragment fragment = new ChatRoomFragment(context);
        fragment.setArguments(args);
        fragment.databaseReference = FirebaseDatabase.getInstance(context.getResources().getString(R.string.database_url)).getReference();
        fragment.uid = FirebaseAuth.getInstance().getUid();
        fragment.chatRoomModelLinkedList = new LinkedList<>();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatRoomBinding.inflate(inflater, container, false);
        adapter = new ChatRoomAdapter(context, chatRoomModelLinkedList, uid, databaseReference, new PositionInterface() {
            @Override
            public void getClickedModel(ChatRoomModel chatRoomModel) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("chatRoomModel", chatRoomModel);
                startActivity(intent);
            }

            @Override
            public void onJoin(ChatRoomModel chatRoomModel) {
                databaseReference.child("Users").child(uid).child("JoinedRooms").child(chatRoomModel.getRoomName()).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        chatRoomModel.setJoined(true);
                        chatRoomModelLinkedList.add(chatRoomModel);
                        adapter.notifyItemInserted(chatRoomModelLinkedList.size());
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("chatRoomModel", chatRoomModel);
                        startActivity(intent);
                    }
                });
            }
        });
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.item_seperator)));
        binding.chatRoomRecycleView.addItemDecoration(dividerItemDecoration);
        binding.chatRoomRecycleView.setAdapter(adapter);
        binding.searchBar.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                adapter.filterItems(chatRoomModelLinkedList);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e(LOG_TAG, query);
                LinkedList<ChatRoomModel> smallChatRoomList = new LinkedList<>();
                adapter.filterItems(smallChatRoomList);
                if(!query.equals(""))
                {
                    databaseReference.child("Rooms").child(query).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                ChatRoomModel chatRoomModel = new ChatRoomModel();
                                chatRoomModel.setRoomName(dataSnapshot.child("Profile").child("roomName").getValue(String.class));
                                chatRoomModel.setImageString(dataSnapshot.child("Profile").child("image").getValue(String.class));
                                for(DataSnapshot langSnapshot: dataSnapshot.child("Profile").child("lang").getChildren())
                                {
                                    chatRoomModel.getLanguage().add(langSnapshot.getValue(String.class));
                                }
                                databaseReference.child("Users").child(uid).child("JoinedRooms").child(query).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        if(!dataSnapshot.exists())
                                        {
                                            chatRoomModel.setJoined(false);
                                        }else {
                                            chatRoomModel.setJoined(true);
                                        }
                                        smallChatRoomList.add(chatRoomModel);
                                        adapter.notifyDataSetChanged();
                                    }
                                });

                            }
                            else{
                                Snackbar.make(binding.getRoot(), "No Room Exist with this Id", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                LinkedList<ChatRoomModel> filterList = new LinkedList<>();
                for(ChatRoomModel chatRoomModel: chatRoomModelLinkedList)
                {
                    if(chatRoomModel.getRoomName().toLowerCase().contains(newText.toLowerCase(Locale.ROOT)))
                    {
                        filterList.add(chatRoomModel);
                    }
                }
                adapter.filterItems(filterList);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        binding.refreshRooms.setRefreshing(true);
        grabJoinedRooms();
        binding.refreshRooms.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                System.out.println("Refreshing Rooms");
                grabJoinedRooms();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onProfileEditClicked() {

    }

    @Override
    public void onAddRoomClicked() {
        createChatRoomLayoutBinding = CreateChatRoomLayoutBinding.inflate(getLayoutInflater());
        binding.getRoot().setAlpha((float) 0.2);
        System.out.println(Arrays.toString(LanguageSelectAdapter.selected));
        showCreateRoomWindow();
    }

    private void grabJoinedRooms()
    {
        chatRoomModelLinkedList.clear();
        adapter.notifyDataSetChanged();
        databaseReference.child("Users").child(uid).child("JoinedRooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot roomSnapshot : snapshot.getChildren())
                {
                    ChatRoomModel chatRoomModel = new ChatRoomModel();
                    try {
                        databaseReference.child("Rooms").child(roomSnapshot.getKey()).child("Profile").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
//                                Log.e(LOG_TAG, dataSnapshot.child("roomName").getValue(String.class));
                                chatRoomModel.setRoomName(dataSnapshot.child("roomName").getValue(String.class));
                                chatRoomModel.setImageString(dataSnapshot.child("image").getValue(String.class));
                                chatRoomModel.setJoined(true);
                                for(DataSnapshot langSnapshot: dataSnapshot.child("lang").getChildren())
                                {
                                    chatRoomModel.getLanguage().add(langSnapshot.getValue(String.class));
                                }
                                databaseReference.child("Rooms").child(roomSnapshot.getKey()).orderByChild("Chats").limitToLast(1).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        System.out.println(dataSnapshot.child("sender").getValue(String.class));
                                        chatRoomModel.setLastMessages(dataSnapshot.child("deu-eng").getValue(String.class));
//                                        Log.e(LOG_TAG, chatRoomModel.getLastMessages());
                                        chatRoomModelLinkedList.add(chatRoomModel);
                                        adapter.notifyItemInserted(chatRoomModelLinkedList.size());
                                    }
                                });
                            }
                        });
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
                binding.refreshRooms.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void showCreateRoomWindow()
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        LanguageSelectAdapter languageSelectAdapter = new LanguageSelectAdapter(context);
        createChatRoomLayoutBinding.languageRecyclerView.setAdapter(languageSelectAdapter);
        createChatRoomLayoutBinding.imageEdit.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_PHOTO);
        });
        PopupWindow popupWindow = new PopupWindow(createChatRoomLayoutBinding.getRoot(), (int) (width * 0.8), (int) (height * 0.5), true);
        createChatRoomLayoutBinding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(LOG_TAG, "Create button Clicked");
                if(createChatRoomLayoutBinding.name.getText() != null && !createChatRoomLayoutBinding.name.getText().toString().equals("") && createChatRoomImg != null)
                {
                    createChatRoomLayoutBinding.progressCircular.setAlpha(1);
                    createChatRoomLayoutBinding.next.setVisibility(View.GONE);
                    String roomName = createChatRoomLayoutBinding.name.getText().toString();
                    HashMap<String, String> data = new HashMap<>();
                    data.put("roomName", roomName);
                    data.put("image", createChatRoomImg);
                    ArrayList<String> languages = new ArrayList<>();
                    for(int i=0; i<LanguageSelectAdapter.selected.length; i++)
                    {
                        if(languages.size()==2)
                        {
                            Snackbar.make(binding.getRoot(), "Please Select only 2 Language", Snackbar.LENGTH_SHORT).show();
                            break;
                        }
                        if(LanguageSelectAdapter.selected[i])
                        {
                            languages.add(LanguageSelectAdapter.languages[i]);
                        }
                    }
                    databaseReference.child("Rooms").child(roomName).child("Profile").child("roomName").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                createChatRoomLayoutBinding.name.setError("This Room Already Exists");
                                createChatRoomLayoutBinding.progressCircular.setAlpha(0);
                                createChatRoomLayoutBinding.next.setVisibility(View.VISIBLE);
                            }else {
                                databaseReference.child("Rooms").child(roomName).child("Profile").setValue(data);
                                databaseReference.child("Rooms").child(roomName).child("Profile").child("lang").setValue(languages);
                                databaseReference.child("Users").child(uid).child("Profile").child("name").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        String userName = dataSnapshot.getValue(String.class);
                                        databaseReference.child("Rooms").child(roomName).child("Users").child(uid).setValue(userName);
                                        databaseReference.child("Users").child(uid).child("JoinedRooms").child(roomName).setValue(true);
                                        Snackbar.make(binding.getRoot(), "Room Created Successfully", Snackbar.LENGTH_SHORT).show();
                                        createChatRoomLayoutBinding.progressCircular.setAlpha(0);
                                        popupWindow.dismiss();
                                    }
                                });
                            }
                        }
                    });
                }else
                {
                    Snackbar.make(binding.getRoot(), "Please Select both Name and Image", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        createChatRoomLayoutBinding.closeWindow.setOnClickListener(view -> {
            popupWindow.dismiss();
            binding.getRoot().setAlpha(1);
        });
        popupWindow.setOnDismissListener(() -> binding.getRoot().animate().alpha(1));
        popupWindow.setElevation(100);
        popupWindow.showAtLocation(binding.chatRoomRecycleView, Gravity.CENTER, 0, 0);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getX() < 0 || motionEvent.getX() > (int) (width * 0.8)) return true;
                if (motionEvent.getY() < 0 || motionEvent.getY() > (int) (height * 0.5)) return true;
                return false;
            }
        });
        popupWindow.update();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == PICK_PHOTO)
        {
            if(data == null)
            {
                return;
            }
            sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            Bitmap profileBitmap;
            InputStream inputStream = null;
            try {
                inputStream = context.getContentResolver().openInputStream(data.getData());
                profileBitmap = BitmapFactory.decodeStream(inputStream);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                profileBitmap.compress(Bitmap.CompressFormat.JPEG, 15, outputStream);
                createChatRoomLayoutBinding.appIcon.setImageBitmap(profileBitmap);
                byte[] imageBytes = outputStream.toByteArray();
                Bitmap smallBitmap = IntegrationActivity.decodeSampledBitmap(imageBytes, 50, 50);
                outputStream = new ByteArrayOutputStream();
                smallBitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
                imageBytes = outputStream.toByteArray();
                createChatRoomImg = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }


}
