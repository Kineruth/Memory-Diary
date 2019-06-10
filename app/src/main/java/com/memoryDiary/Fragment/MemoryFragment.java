package com.memoryDiary.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.memoryDiary.Activity.Memory.AddMemoryActivity;
import com.memoryDiary.Activity.Start.LoginActivity;
import com.memoryDiary.Adapter.DiaryAdapter;
import com.memoryDiary.Entity.Diary;
import com.memoryDiary.Entity.Memory;
import com.memoryDiary.Holder.DiaryDataHolder;
import com.memoryDiary.Holder.MemoryDataHolder;
import com.memoryDiary.Holder.UserDataHolder;
import com.memoryDiary.R;

public class MemoryFragment extends Fragment {

    private View mView;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabSearch, fabAdd, fabLogout;
    private RecyclerView memoryRecyclerView;
    private DiaryAdapter diaryAdapter;
    private Diary memories;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;

    public static MemoryFragment newInstance() {
        MemoryFragment mFragment = new MemoryFragment();
        return mFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_memory, container, false);
        initFields();
        initFireBase();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initRecyclerView();
    }

    /**
     * Initialization the connection of the fields in xml file to their activities.
     */
    private void initFields() {
        memoryRecyclerView = mView.findViewById(R.id.memory_recyclerview);
        memoryRecyclerView.setHasFixedSize(true);
        memoryRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));
        memories = new Diary();
        diaryAdapter = new DiaryAdapter(getActivity(), memories);
        memoryRecyclerView.setAdapter(diaryAdapter);

        fabMenu = mView.findViewById(R.id.add_memory_fab_menu);
        fabSearch = mView.findViewById(R.id.add_memory_fab_search);
        fabAdd = mView.findViewById(R.id.add_memory_fab_add);
        fabLogout = mView.findViewById(R.id.add_memory_fab_logout);

        fabMenu.bringToFront();
        //color when not pressed
        fabAdd.setColorNormal(getResources().getColor(R.color.babyBlue));
        fabSearch.setColorNormal(getResources().getColor(R.color.babyBlue));
        fabLogout.setColorNormal(getResources().getColor(R.color.babyBlue));
        //color when pressed
        fabAdd.setColorPressed(getResources().getColor(R.color.red));
        fabSearch.setColorPressed(getResources().getColor(R.color.red));
        fabLogout.setColorPressed(getResources().getColor(R.color.red));

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchActivity();
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMemoryActivity();
            }
        });

        fabLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Logout")
                        .setMessage("Sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                logoutActivity();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // user doesn't want to logout
                            }
                        })
                        .show();

            }
        });
    }

    private void initFireBase(){
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Initializing a recycle view.
     */
    public void initRecyclerView() {
        mData.child("Diary").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

            /**
             * It is triggered once when the listener is attached,
             * and again every time the data, including children, changes.
             * @param dataSnapshot the data.
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                memories.clearMemories();
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    Memory memo = data.getValue(Memory.class);
                    memories.addMemory(memo);
                }
                diaryAdapter.notifyDataSetChanged();
            }

            /**
             * when an error occurs.
             * @param databaseError the errors.
             */
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /**
     * When clicked on the add FAB will open the add new memory activity.
     */
    private void addMemoryActivity() {
        Intent intent = new Intent(this.getActivity(), AddMemoryActivity.class);
        startActivity(intent);
    }

    /**
     * Connects to the settings activity and starts it.
     */
//    private void SettingsActivity() {
//        Intent intent = new Intent(this.getActivity(), SettingsActivity.class);
//        startActivity(intent);
//    }

    /**
     * Connects to login activity and starts it.
     * An intent - basically a message to say you did or want something to happen.
     */
    private void logoutActivity() {
        clearDataHolderes();
        mAuth.signOut();
        Intent intent = new Intent(this.getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void searchActivity(){
        Toast.makeText(getActivity(), "search", Toast.LENGTH_SHORT).show();
    }

    private void clearDataHolderes() {
        UserDataHolder.getUserDataHolder().clearUser();
        DiaryDataHolder.getDiaryDataHolder().clearDiary();
        MemoryDataHolder.getMemoryDataHolder().clearMemory();
    }



}