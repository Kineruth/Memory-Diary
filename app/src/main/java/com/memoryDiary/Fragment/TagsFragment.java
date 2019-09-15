package com.memoryDiary.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.memoryDiary.Adapter.TagsAdapter;
import com.memoryDiary.Entity.Memory;
import com.memoryDiary.Entity.Tags;
import com.memoryDiary.R;

public class TagsFragment extends Fragment {

    private View view;
    private Tags memories;
    private TagsAdapter tagsAdapter;
    private RecyclerView tagsRecyclerView;

    private FirebaseAuth fbAuth;
    private DatabaseReference fbData;

    public static TagsFragment newInstance() {
        TagsFragment fragment = new TagsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_tags, container, false);
        initFields();
        initFireBase();
        return this.view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initRecyclerView();
    }

    private void initFields(){
        this.tagsRecyclerView =  this.view.findViewById(R.id.tags_recyclerView);
        // sets layout manager for RecyclerView, to be able to draw the layout properly.
        LinearLayoutManager manager = new LinearLayoutManager(this.getActivity());
        this.tagsRecyclerView.setLayoutManager(manager);
        this.tagsRecyclerView.setHasFixedSize(true);
        this.tagsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));
        this.memories = new Tags();
        this.tagsAdapter = new TagsAdapter(getActivity(), this.memories);
        this.tagsRecyclerView.setAdapter(this.tagsAdapter);
    }

    /**
     * Gets fireBase instances & references.
     */
    private void initFireBase(){
        this.fbAuth = FirebaseAuth.getInstance();
        this.fbData = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Initializing a recycle view.
     */
    public void initRecyclerView() {
        this.fbData.child("Tags").child(this.fbAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

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
                tagsAdapter.notifyDataSetChanged();
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
}
