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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.memoryDiary.Activity.Main.MainActivity;
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
    private Toolbar mToolbar;
    private FloatingActionButton mFabAdd;
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
        mToolbar = mView.findViewById(R.id.fragment_memory_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        setHasOptionsMenu(true);
        mToolbar.setTitle("");
//        mToolbar.setBackgroundColor(@android:color/transparent);
//        setTitle(R.id.memory_toolbar);
        mFabAdd = mView.findViewById(R.id.memory_add_floating_button);
        memoryRecyclerView = mView.findViewById(R.id.memory_recyclerview);
        memoryRecyclerView.setHasFixedSize(true);
        memoryRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));
        memories = new Diary();
        diaryAdapter = new DiaryAdapter(getActivity(), memories);
        memoryRecyclerView.setAdapter(diaryAdapter);

        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMemoryActivity();
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
     * Used to specify the options menu for an activity
     * @param menu a given menu to be displayed.
     * @return true to be displayed.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_memory_menu, menu);
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
        if(item.getItemId() == R.id.search_option) {
            Toast.makeText(getActivity(), "Search", Toast.LENGTH_SHORT).show();
//            SearchActivity();
        }
        if(item.getItemId() == R.id.settings_option) {
            Toast.makeText(getActivity(), "Settings", Toast.LENGTH_SHORT).show();
//            SettingsActivity();
        }
        if(item.getItemId() == R.id.logout_option){
            loginActivity();
        }
        return true;
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
    private void loginActivity() {
        clearDataHolderes();
        mAuth.signOut();
        Intent intent = new Intent(this.getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void SearchActivity(){

    }

    private void clearDataHolderes() {
        UserDataHolder.getUserDataHolder().clearUser();
        DiaryDataHolder.getDiaryDataHolder().clearDiary();
        MemoryDataHolder.getMemoryDataHolder().clearMemory();
    }



}