package com.memoryDiary.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.memoryDiary.Activity.Memory.AddMemoryActivity;
import com.memoryDiary.Adapter.MemoryAdapter;
import com.memoryDiary.Entity.Memory;
import com.memoryDiary.Holder.UserDataHolder;
import com.memoryDiary.R;

import java.util.ArrayList;
import java.util.List;

public class MemoryFragment extends Fragment {

    private View mView;
    private Toolbar mToolbar;
    private FloatingActionButton mFabAdd;
    private RecyclerView memoryRecyclerView;
    private MemoryAdapter adapter;
    private List<Memory> memories;
    private DatabaseReference mData;

//    public static MemoryFragment newInstance() {
//        MemoryFragment mFragment = new MemoryFragment();
//        return mFragment;
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_memory, container, false);
        initFields();
        initFireBase();
        return mView;
    }

    /**
     * Initialization the connection of the fields in xml file to their activities.
     */
    private void initFields() {
        mToolbar = mView.findViewById(R.id.memory_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        setHasOptionsMenu(true);
//        setTitle(R.id.memory_toolbar);
        mFabAdd = mView.findViewById(R.id.memory_add_floating_button);
        memories = new ArrayList<>();
        memoryRecyclerView = mView.findViewById(R.id.memory_recyclerview);
        memoryRecyclerView.setHasFixedSize(true);
        memoryRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));
        adapter = new MemoryAdapter(getActivity(), memories);
        memoryRecyclerView.setAdapter(adapter);

        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMemoryActivity();
            }
        });
    }

    private void initFireBase(){
        mData = FirebaseDatabase.getInstance().getReference();
    }

    private void initRecyclerView() {
        mData.child("Diary").child(UserDataHolder.getUserDataHolder().getUser().getUid()).child("Memories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                memories.clear();
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    Memory memory = data.getValue(Memory.class);
                    Log.d("test", memory.toString());
                    memories.add(memory);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    /**
     * Used to specify the options menu for an activity
     * @param menu a given menu to be displayed.
     * @return true to be displayed.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_memory_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    /**
     * Checked what was chosen - adding or searching in the personal diary.
     * @param item an item that has been selected.
     * @return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.memory_toolbar_search){
            Toast.makeText(getActivity(), "Search", Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId() == R.id.memory_toolbar_settings){
            Toast.makeText(getActivity(), "Settings", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void addMemoryActivity() {
        Intent intent = new Intent(getActivity(), AddMemoryActivity.class);
        startActivity(intent);
    }

}