package com.memoryDiary.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
        this.mView = inflater.inflate(R.layout.fragment_memory, container, false);
        initFields();
        initFireBase();
        return this.mView;
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
        this.memoryRecyclerView = this.mView.findViewById(R.id.memory_recyclerview);
        // sets layout manager for RecyclerView, to be able to draw the layout properly.
        LinearLayoutManager manager = new LinearLayoutManager(this.getActivity());
        this.memoryRecyclerView.setLayoutManager(manager);
        this.memoryRecyclerView.setHasFixedSize(true);
        this.memoryRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));
        this.memories = new Diary();
        this.diaryAdapter = new DiaryAdapter(getActivity(), memories);
        this.memoryRecyclerView.setAdapter(diaryAdapter);

        this.fabMenu = this.mView.findViewById(R.id.add_memory_fab_menu);
        this.fabSearch = this.mView.findViewById(R.id.add_memory_fab_search);
        this.fabAdd = this.mView.findViewById(R.id.add_memory_fab_add);
        this.fabLogout = this.mView.findViewById(R.id.add_memory_fab_logout);

        this.fabMenu.bringToFront();
        //color when not pressed
        this.fabAdd.setColorNormal(getResources().getColor(R.color.babyBlue));
        this.fabSearch.setColorNormal(getResources().getColor(R.color.babyBlue));
        this.fabLogout.setColorNormal(getResources().getColor(R.color.babyBlue));
        //color when pressed
        this.fabAdd.setColorPressed(getResources().getColor(R.color.red));
        this.fabSearch.setColorPressed(getResources().getColor(R.color.red));
        this.fabLogout.setColorPressed(getResources().getColor(R.color.red));

        this.fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchActivity();
            }
        });

        this.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMemoryActivity();
            }
        });

        this.fabLogout.setOnClickListener(new View.OnClickListener() {
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
        this.mAuth = FirebaseAuth.getInstance();
        this.mData = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Initializing a recycle view.
     */
    public void initRecyclerView() {
        this.mData.child("Diary").child(this.mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

            /**
             * It is triggered once when the listener is attached,
             * and again every time the data, including children, changes.
             * @param dataSnapshot the data.
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChildren())
                    return;
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

//    /**
//     * Connects to the settings activity and starts it.
//     */
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
        this.mAuth.signOut();
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