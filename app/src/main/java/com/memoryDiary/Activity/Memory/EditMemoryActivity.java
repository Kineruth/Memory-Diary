package com.memoryDiary.Activity.Memory;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.memoryDiary.Entity.Memory;
import com.memoryDiary.Holder.MemoryDataHolder;
import com.memoryDiary.Holder.UserDataHolder;
import com.memoryDiary.R;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;
import com.sangcomz.fishbun.define.Define;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

public class EditMemoryActivity extends AppCompatActivity implements Validator.ValidationListener{
    @NotEmpty()
    private EditText descriptionText;
    @NotEmpty()
    private EditText titleText;
    private ImageView memoImageView;
    private String currentImagePath;
    private DatabaseReference mData;
    private StorageReference memoImageRef;
    private String mUid;
    private Uri imageUri = null;
    private Validator validator;
    private static boolean valIsDone;
    private Memory memory;
//    private StorageReference filePath;
    private FloatingActionButton fabDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memory);

        initFields();
        initFireBase();
        initValidator();
    }

    private void initFields(){
        this.titleText = findViewById(R.id.edit_memory_title);
        this.memoImageView = findViewById(R.id.edit_memory_image);
        this.descriptionText = findViewById(R.id.edit_memory_description);
        this.fabDone = findViewById(R.id.edit_memory_fabDone);
        this.descriptionText.setMovementMethod(new ScrollingMovementMethod());
        initMemoryDetails();
        this.memoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnMemoryImage();
            }
        });

        this.fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnDone();
            }
        });
    }

    /**
     * mData - a reference to the project
     * mUid - gets memory Uid
     * memoImageRef - goes to diary->user node (all of his memories)
     */
    private void initFireBase(){
        this.mData = FirebaseDatabase.getInstance().getReference();
        this.mUid = this.memory.getUid();
        this.currentImagePath = this.memory.getImagePath();
        this.memoImageRef = FirebaseStorage.getInstance().getReference().child("Diary").child(UserDataHolder.getUserDataHolder().getUser().getUid());
    }

    private void initValidator(){
        this.validator = new Validator(this);
        this.validator.setValidationListener(this);
    }

    private void initMemoryDetails() {
        this.memory = MemoryDataHolder.getMemoryDataHolder().getMemory();
        this.titleText.setText(this.memory.getMemoryTitle());
        this.descriptionText.setText(this.memory.getDescription());
        Picasso.get().load(this.memory.getImagePath()).into(this.memoImageView);
    }

    private void clickOnMemoryImage() {
        FishBun.with(this).setImageAdapter(new GlideAdapter())
                .setMinCount(1)
                .setMaxCount(1)
                .setActionBarColor(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"), true)
                .setActionBarTitleColor(Color.parseColor("#000000"))
                .setAlbumSpanCount(1, 2)
                .setButtonInAlbumActivity(true)
                .setCamera(false)
                .exceptGif(true)
                .setReachLimitAutomaticClose(false)
                .setHomeAsUpIndicatorDrawable(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp))
                .setOkButtonDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check_black_24dp))
                .setActionBarTitle("Albums")
                .setAllViewTitle("All Photos")
                .textOnNothingSelected("No picture selected")
                .startAlbum();
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Define.ALBUM_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            this.imageUri = (Uri)data.getParcelableArrayListExtra(Define.INTENT_PATH).get(0);
            Picasso.get().load(this.imageUri.toString()).into(this.memoImageView);
        }
    }



    private void clickOnDone() {
        this.validator.validate();
        //if everything valid & chose a new image - imageURI won't be null
        if(this.valIsDone) {
            final Memory memo = new Memory(this.mUid,
                    this.titleText.getText().toString(),
                    this.descriptionText.getText().toString(),
                    Calendar.getInstance().getTimeInMillis(),
                    "");
            this.memory.setAll(memo); //sets the new edited memory

            //user changed picture - need to delete old one and replace with new
            if(this.imageUri != null){
                this.memoImageRef.child(this.mUid + ".jpg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        uploadNewImage();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("EditMemory", "Failed to delete image from firebase");
                    }
                });
            }
            else{ //no need to update storage
                this.memory.setImagePath(this.currentImagePath);
                mData.child("Diary").child(UserDataHolder.getUserDataHolder().getUser().getUid())
                        .child(mUid).setValue(memory).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                    }
                });
            }
        }
        else{
            Toast.makeText(this, "No picture selected", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadNewImage(){
        // goes to the user storage for pictures and overwrites a picture if needed
        final StorageReference filePath = this.memoImageRef.child(this.mUid + ".jpg");
        filePath.putFile(this.imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        memory.setImagePath(uri.toString());
                        mData.child("Diary").child(UserDataHolder.getUserDataHolder().getUser().getUid())
                                .child(mUid).setValue(memory).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        this.valIsDone = false;
        for(ValidationError error: errors){
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if(view instanceof EditText){
                ((EditText)view).setError(message);
            }
            else{
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onValidationSucceeded() {
        this.valIsDone = true;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}