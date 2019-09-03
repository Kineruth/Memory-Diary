package com.memoryDiary.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.memoryDiary.R;

public class TagsFragment extends Fragment {

    private View view;
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

    private void initFields(){

    }

    /**
     * Gets fireBase instances & references.
     */
    private void initFireBase(){
        this.fbAuth = FirebaseAuth.getInstance();
        this.fbData = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        initRecyclerView();
    }
}
