package com.memoryDiary.Activity.Memory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.memoryDiary.Entity.Memory;
import com.memoryDiary.Holder.MemoryDataHolder;
import com.memoryDiary.Holder.UserDataHolder;
import com.memoryDiary.R;
import com.squareup.picasso.Picasso;

public class ShowTaggedActivity extends AppCompatActivity {
    private TextView descriptionText;
    private TextView titleText;
    private ImageView memoImageView;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabOk, fabDelete;
    private Memory memory;
    private String memoryUid, imagePath;

    private FirebaseAuth fbAuth;
    private DatabaseReference fbData;

    public ShowTaggedActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tagged_memory);
        initFields();
        initFireBase();
    }

    private void initFields(){
        this.titleText = findViewById(R.id.show_tagged_title);
        this.memoImageView = findViewById(R.id.show_tagged_image);
        this.descriptionText = findViewById(R.id.show_tagged_description);
        this.descriptionText.setMovementMethod(new ScrollingMovementMethod());
        this.fabMenu = findViewById(R.id.show_tagged_fab_menu);
        this.fabOk = findViewById(R.id.show_tagged_fab_ok);
        this.fabDelete = findViewById(R.id.show_tagged_fab_delete);

        this.fabMenu.bringToFront();
        //color when not pressed
        this.fabOk.setColorNormal(getResources().getColor(R.color.babyBlue));
        this.fabDelete.setColorNormal(getResources().getColor(R.color.babyBlue));

        //color when pressed
        this.fabOk.setColorPressed(getResources().getColor(R.color.maroon));
        this.fabDelete.setColorPressed(getResources().getColor(R.color.maroon));
        initMemoryDetails();

        this.fabOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okMemoryActivity();
            }
        });
        this.fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteActivity();
            }
        });
    }

    private void initMemoryDetails() {
        this.memory = MemoryDataHolder.getMemoryDataHolder().getMemory();
        this.memoryUid = this.memory.getMemoryId();
        this.imagePath = this.memory.getImagePath();
        this.titleText.setText(this.memory.getMemoryTitle());
        this.descriptionText.setText(this.memory.getDescription());
        Picasso.get().load(this.imagePath).into(this.memoImageView);
    }

    /**
     * Gets fireBase instances & references.
     */
    private void initFireBase(){
        this.fbAuth = FirebaseAuth.getInstance();
        this.fbData = FirebaseDatabase.getInstance().getReference();
    }

    private void deleteActivity(){

        new AlertDialog.Builder(this)
                .setTitle("Delete Memory")
                .setMessage("Sure you want to delete this memory? ")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMemory();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    // user doesn't want to delete
                    public void onClick(DialogInterface dialog, int which) { }
                })
                .show();
    }

    /**
     * Deletes memory from firebase database & from dataholder
     */
    private void deleteMemory(){
        DatabaseReference memoDR = FirebaseDatabase.getInstance().getReference("Tags").child(UserDataHolder.getUserDataHolder().getUser().getUid()).child(this.memoryUid);
        memoDR.removeValue();
        MemoryDataHolder.getMemoryDataHolder().clearMemory();
        //need to change that because it points to the sender - will give ERROR
        StorageReference memoImageRef = FirebaseStorage.getInstance().getReference().child("Tags").child(UserDataHolder.getUserDataHolder().getUser().getUid());
        memoImageRef.child(this.memoryUid + ".jpg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DeleteMemory", "Failed to delete image from firebase");
            }
        });
    }

    /**
     * When clicked on the ok FAB will add memory to diary
     * and will exit the activity.
     */
    private void okMemoryActivity() {
        // add to Diary
        this.fbData.child("Diary").child(UserDataHolder.getUserDataHolder().getUser().getUid())
                .child(this.memoryUid).setValue(this.memory).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
        //removes from Tags
        DatabaseReference memoDR = FirebaseDatabase.getInstance().getReference("Tags").child(UserDataHolder.getUserDataHolder().getUser().getUid()).child(this.memoryUid);
        memoDR.removeValue();
        Toast.makeText(getApplicationContext(), "Added to diary!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initFields();
    }
}


