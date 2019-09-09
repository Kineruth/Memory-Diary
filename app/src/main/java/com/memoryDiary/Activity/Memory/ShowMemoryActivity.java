package com.memoryDiary.Activity.Memory;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import com.github.clans.fab.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.ContactsContract;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.memoryDiary.Entity.Memory;
import com.memoryDiary.Holder.MemoryDataHolder;
import com.memoryDiary.Holder.UserDataHolder;
import com.memoryDiary.R;
import com.squareup.picasso.Picasso;

public class ShowMemoryActivity extends AppCompatActivity {

    private static final int RESULT_PICK_CONTACT =1;
    private TextView descriptionText;
    private TextView titleText;
    private ImageView memoImageView;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabEdit, fabDelete, fabShare;
    private Memory memory;
    private String memoryUid, imagePath, taggedNumber, taggedUserKey, taggedMemoryKey;

    private FirebaseAuth fbAuth;
    private DatabaseReference fbData;

    public ShowMemoryActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_memory);
        initFields();
        initFireBase();
    }

    private void initFields(){
        this.titleText = findViewById(R.id.show_memory_title);
        this.memoImageView = findViewById(R.id.show_memory_image);
        this.descriptionText = findViewById(R.id.show_memory_description);
        this.descriptionText.setMovementMethod(new ScrollingMovementMethod());
        this.fabMenu = findViewById(R.id.show_memory_fab_menu);
        this.fabEdit = findViewById(R.id.show_memory_fab_edit);
        this.fabDelete = findViewById(R.id.show_memory_fab_delete);
        this.fabShare = findViewById(R.id.show_memory_fab_share);

        this.fabMenu.bringToFront();
        //color when not pressed
        this.fabEdit.setColorNormal(getResources().getColor(R.color.babyBlue));
        this.fabDelete.setColorNormal(getResources().getColor(R.color.babyBlue));
        this.fabShare.setColorNormal(getResources().getColor(R.color.babyBlue));

        //color when pressed
        this.fabEdit.setColorPressed(getResources().getColor(R.color.maroon));
        this.fabDelete.setColorPressed(getResources().getColor(R.color.maroon));
        this.fabShare.setColorPressed(getResources().getColor(R.color.maroon));
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
        this.fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareActivity();
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
     * shares current memory with other users.
     * need to check if the chosen phone number is a user, if so-
     * find his uid and save it
     * create a clone the memory with new uid and pass it to chosen user tag diary.
     * Tags -> chosen user uid -> cloned memory.
     * (same as: Diary -> user uid -> original memory
     */
    private void shareActivity(){
        Intent in = new Intent (Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult (in, RESULT_PICK_CONTACT);
    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try{
            String phoneNo = null;
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null,null,null);
            cursor.moveToFirst();
            int phoneIndex = cursor.getColumnIndex (ContactsContract.CommonDataKinds.Phone.NUMBER);

            phoneNo = cursor.getString(phoneIndex);
            if(phoneNo.length() == 10){ //need to replace with regex?
                this.taggedNumber = "+972" + phoneNo.substring(1,10);
            }
            else{
                this.taggedNumber = phoneNo;
            }
            searchUserByPhone();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchUserByPhone(){
        this.fbData.child("Users");
        this.fbData.orderByChild("phoneNumber")
                .equalTo(this.taggedNumber)
                .addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot child: dataSnapshot.getChildren()){
                            String key = child.getKey();
                            taggedUserKey = key;
                            Log.e("search Key: ", key);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("search User : ", "Failed to get user from firebase");
                    }
                });
        sendMemory();

    }

    private void sendMemory(){
        this.taggedMemoryKey = fbData.child("Tags").child(this.taggedUserKey).push().getKey();
        final Memory memoCopy = new Memory(this.memory);
        memoCopy.setMemoryId(this.taggedMemoryKey);

        this.fbData.child("Tags").child(this.taggedUserKey)
                .child(this.taggedMemoryKey).setValue(memoCopy).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        }
        else{
            Toast.makeText (this, "Failed To pick contact", Toast.LENGTH_SHORT).show ();
        }
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

