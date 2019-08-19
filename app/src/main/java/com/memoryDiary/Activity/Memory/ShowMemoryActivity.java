package com.memoryDiary.Activity.Memory;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.github.clans.fab.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.memoryDiary.Entity.Memory;
import com.memoryDiary.Holder.MemoryDataHolder;
import com.memoryDiary.Holder.UserDataHolder;
import com.memoryDiary.R;
import com.squareup.picasso.Picasso;

public class ShowMemoryActivity extends AppCompatActivity {

    private TextView descriptionText;
    private TextView titleText;
    private ImageView memoImageView;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabEdit, fabDelete;
    private Memory memory;
    private String memoryUid, imagePath;

    public ShowMemoryActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_memory);
        initFields();
    }

    private void initFields(){
        this.titleText = findViewById(R.id.show_memory_title);
        this.memoImageView = findViewById(R.id.show_memory_image);
        this.descriptionText = findViewById(R.id.show_memory_description);
        this.descriptionText.setMovementMethod(new ScrollingMovementMethod());
        this.fabMenu = findViewById(R.id.show_memory_fab_menu);
        this.fabEdit = findViewById(R.id.show_memory_fab_edit);
        this.fabDelete = findViewById(R.id.show_memory_fab_delete);

        this.fabMenu.bringToFront();
        //color when not pressed
        this.fabEdit.setColorNormal(getResources().getColor(R.color.babyBlue));
        this.fabDelete.setColorNormal(getResources().getColor(R.color.babyBlue));
        //color when pressed
        this.fabEdit.setColorPressed(getResources().getColor(R.color.red));
        this.fabDelete.setColorPressed(getResources().getColor(R.color.red));
        initMemoryDetails();

        this.fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMemoryActivity();
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
        DatabaseReference memoDR = FirebaseDatabase.getInstance().getReference("Diary").child(UserDataHolder.getUserDataHolder().getUser().getUid()).child(memoryUid);
        memoDR.removeValue();
        MemoryDataHolder.getMemoryDataHolder().clearMemory();
        StorageReference memoImageRef = FirebaseStorage.getInstance().getReference().child("Diary").child(UserDataHolder.getUserDataHolder().getUser().getUid());
        memoImageRef.child(this.memoryUid + ".jpg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("EditMemory", "Failed to delete image from firebase");
            }
        });
    }

    /**
     * When clicked on the add FAB will open the add new memory activity.
     */
    private void editMemoryActivity() {
        Intent intent = new Intent(this, EditMemoryActivity.class);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "edit", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initFields();
    }
}

