package com.memoryDiary.Activity.Memory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.memoryDiary.Activity.Start.ShowCaptureActivity;
import com.memoryDiary.Entity.Memory;
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

public class AddMemoryActivity extends AppCompatActivity implements Validator.ValidationListener  {
    @NotEmpty()
    private EditText descriptionAddText;
    @NotEmpty()
    private EditText memoTitleAddText;
    private ImageView memoryImageView;
    private DatabaseReference mData;
    private StorageReference memoryImageRef;
    private String key;
    private Uri imageUri = null;
    private Validator validator;
    private static boolean valIsDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memory);

//        Bundle extras = savedInstanceState.
        initFields();
        initFireBase(); //failed to init firebase
        initValidator();
    }

    private void initFields(){
        memoTitleAddText = findViewById(R.id.add_memory_title);
        memoryImageView = findViewById(R.id.add_memory_image);
        descriptionAddText = findViewById(R.id.add_memory_description);
        descriptionAddText.setMovementMethod(new ScrollingMovementMethod());
        findViewById(R.id.add_memory_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnAddMemory();
            }
        });
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            byte[] picture = extras.getByteArray("capture");
            if(picture != null){
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                Bitmap rotatedBitmap = rotate(decodedBitmap);
                memoryImageView.setImageBitmap(rotatedBitmap);
            }
        }
        memoryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnMemoryImage();
            }
        });
    }

    private void initFireBase(){
        mData = FirebaseDatabase.getInstance().getReference();
        key = mData.child("Diary").child(UserDataHolder.getUserDataHolder().getUser().getUid()).push().getKey();
        memoryImageRef = FirebaseStorage.getInstance().getReference().child("Memory").child(UserDataHolder.getUserDataHolder().getUser().getUid());
    }

    private void initValidator(){
        validator = new Validator(this);
        validator.setValidationListener(this);
    }


    private Bitmap rotate(Bitmap decodedBitmap) {
        int w = decodedBitmap.getWidth();
        int h = decodedBitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(90);

        return Bitmap.createBitmap(decodedBitmap, 0, 0, w, h, matrix, true);
    }

    private void clickOnMemoryImage() {
        Toast.makeText(this, "choose1", Toast.LENGTH_LONG).show();
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
                .setAllViewTitle("All photos")
                .textOnNothingSelected("No picture selected")
                .startAlbum();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Define.ALBUM_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            imageUri = (Uri)data.getParcelableArrayListExtra(Define.INTENT_PATH).get(0);
            Picasso.get().load(imageUri.toString()).into(memoryImageView);
        }
    }

    private void clickOnAddMemory() {
        validator.validate();
        if(valIsDone && imageUri != null){
            final Memory memory = new Memory(key,
                    memoTitleAddText.getText().toString(),
                    descriptionAddText.getText().toString(),
                    Calendar.getInstance().getTimeInMillis(),
                    "");
            final StorageReference filePath = memoryImageRef.child(key + ".jpg");
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            memory.setImagePath(uri.toString());
                            mData.child("Diary").child(UserDataHolder.getUserDataHolder().getUser().getUid())
                                    .child(key).setValue(memory).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        else if (!valIsDone){
            Toast.makeText(this, "No picture selected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        valIsDone = false;
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
        valIsDone = true;
    }
}
