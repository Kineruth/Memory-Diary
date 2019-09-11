package com.memoryDiary.Activity.Memory;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.clans.fab.FloatingActionButton;
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

import java.util.Calendar;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ShowMemoryActivity extends AppCompatActivity {

    private static final int RESULT_PICK_CONTACT =1;
    private TextView descriptionText;
    private TextView titleText;
    private ImageView memoImageView;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabEdit, fabDelete, fabShare;
    private Memory memory;
    private String memoryUid, imagePath, taggedPhoneNumber, taggedUserUid, taggedMemoryUid, taggedUserName;

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

    /**
     * User wants to delete a memory
     */
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
                Log.d("ShowMemory", "Failed to delete image from firebase");
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

    /**
     * After contact gets picked - gets his phone number & name
     * Searches contact in fireBase.
     * @param data
     */
    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try{
            String phoneNo = null;
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null,null,null);
            cursor.moveToFirst();
            int phoneIndex = cursor.getColumnIndex (ContactsContract.CommonDataKinds.Phone.NUMBER);
            int nameIndex = cursor.getColumnIndex (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            this.taggedUserName = cursor.getString(nameIndex);

            if(phoneNo.length() == 10){ //need to replace with regex?
                this.taggedPhoneNumber = "+972" + phoneNo.substring(1,10);
            }
            else{
                this.taggedPhoneNumber = phoneNo;
            }
            //need to alert him if he is sure he wants to send this to the user
            // show phone number & name
            //press ok or cancel
            new AlertDialog.Builder(this)
                    .setTitle("Share Memory")
                    .setMessage("Share memory with " +  this.taggedUserName + " ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            searchUserByPhone();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        // user doesn't want to delete
                        public void onClick(DialogInterface dialog, int which) { }
                    })
                    .show();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Searches contact in fireBase:
     * if exists- adds the memory to his Tags.
     * if not- doesn't do anything (maybe gives alert?).
     * Sends copy of the memory to the chosen contact - adds it to his Tag Fragment.
     * Changes the duplicated memory uid to a new one.
     */
    private void searchUserByPhone(){


        this.fbData.child("Users")
                .orderByChild("phoneNumber")
                .equalTo(this.taggedPhoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener(){
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String key = child.getKey();
                            taggedUserUid = key;
                            Log.e("search Key: ", key);
                        }

                        taggedMemoryUid = fbData.child("Tags").child(taggedUserUid).push().getKey();
                        final Memory memoCopy = new Memory(taggedUserUid,
                                taggedMemoryUid,
                                memory.getMemoryTitle(),
                                memory.getDescription(),
                                Calendar.getInstance().getTimeInMillis(),
                                "", memory.getImageLabels());

                        final StorageReference filePathRef = FirebaseStorage.getInstance().getReferenceFromUrl(imagePath);
                        final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Diary").child(taggedUserUid);

                         filePathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String fileName = String.valueOf(memoCopy.getCreationTime());
                                DownloadManager downloadManager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                                // uri?
                                DownloadManager.Request request = new DownloadManager.Request(uri);
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDestinationInExternalFilesDir(getApplicationContext(),DIRECTORY_DOWNLOADS, fileName +".jpg");
                                downloadManager.enqueue(request);
                                Log.e("DOWNLOAD : ", "Succeeded downloading image");

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                Log.e("DOWNLOAD : ", "Failed to download image");
                            }
                        });

                        /*
                        if(isExternalStorageWritable()){
                            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//                            final File rootPath = new File(Environment.getExternalStorageDirectory(), "MemoryDiary_Downloads");
                            if (!path.exists()) {
                                path.mkdirs();
                            }
                            final File file = new File(path, memoCopy.getCreationTime()+".jpg");
                            filePathRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    // Local temp file has been created
                                    Log.e("DOWNLOAD : ", "local tem file created - " + file.getPath());

                                    Uri imgUri =
                                    filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Log.e("GOT URI : ", "onSuccess");
                                                    memoCopy.setImagePath(uri.toString());
                                                    fbData.child("Tags").child(taggedUserUid)
                                                            .child(taggedMemoryUid).setValue(memoCopy).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.e("ADD IMAGE REFERENCE: ", "onSuccess");
                                                            finish();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                    Log.e("DOWNLOAD : ", "Failed to download image");
                                }
                            });
                            } */
                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("Search_User : ", "Failed to get user from fireBase");
                    }
                });
    }


    /**
     * Sends copy of the memory to the chosen contact - adds it to his Tag Fragment.
     * Changes the duplicated memory uid to a new one.
     */
    private void sendMemory(){
        // Can't pass null for argument 'pathString' in child()
        this.taggedMemoryUid = this.fbData.child("Tags").child(this.taggedUserUid).push().getKey();
        Log.d("Send_Memory : ", this.taggedMemoryUid);
        final Memory memoCopy = new Memory(this.memory);
        memoCopy.setMemoryId(this.taggedMemoryUid);

        this.fbData.child("Tags").child(this.taggedUserUid)
                .child(this.taggedMemoryUid).setValue(memoCopy).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
            }
        });
    }

    /**
     * Gets called when the user choose a contact from his phone.
     * Only one contact can be picked.
     * @param requestCode
     * @param resultCode
     * @param data the chosen contact's data.
     */
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
//        Toast.makeText(getApplicationContext(), "edit", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initFields();
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

}

