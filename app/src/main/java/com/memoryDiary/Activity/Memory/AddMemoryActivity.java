package com.memoryDiary.Activity.Memory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
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
    private EditText descriptionText;
    @NotEmpty()
    private EditText titleText;
    private ImageView memoImageView;
    private DatabaseReference mData;
    private StorageReference memoImageRef;
    private String key;
    private Uri imageUri = null;
    private Validator validator;
    private static boolean valIsDone;
    private FloatingActionButton fabDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memory);

        initFields();
        initFireBase();
        initValidator();
    }

    private void initFields(){
        this.titleText = findViewById(R.id.add_memory_title);
        this.memoImageView = findViewById(R.id.add_memory_image);
        this.descriptionText = findViewById(R.id.add_memory_description);
        this.descriptionText.setMovementMethod(new ScrollingMovementMethod());
        this.fabDone = findViewById(R.id.add_memory_fabDone);
        this.fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnDone();
            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            byte[] picture = extras.getByteArray("capture");
            if(picture != null){
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                Bitmap rotatedBitmap = rotate(decodedBitmap);
                this.memoImageView.setImageBitmap(rotatedBitmap);
            }
        }
        this.memoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnMemoryImage();
            }
        });
    }

    private void initFireBase(){
        this.mData = FirebaseDatabase.getInstance().getReference();
        this.key = mData.child("Diary").child(UserDataHolder.getUserDataHolder().getUser().getUid()).push().getKey();
        this.memoImageRef = FirebaseStorage.getInstance().getReference().child("Diary").child(UserDataHolder.getUserDataHolder().getUser().getUid());
    }

    private void initValidator(){
        this.validator = new Validator(this);
        this.validator.setValidationListener(this);
    }


    private Bitmap rotate(Bitmap decodedBitmap) {
        int w = decodedBitmap.getWidth();
        int h = decodedBitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(90);

        return Bitmap.createBitmap(decodedBitmap, 0, 0, w, h, matrix, true);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // succeeded in opening album & choosing a photo
        if(requestCode == Define.ALBUM_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            this.imageUri = (Uri)data.getParcelableArrayListExtra(Define.INTENT_PATH).get(0);
            Picasso.get().load(this.imageUri.toString()).into(this.memoImageView);
        }
    }

    private void clickOnDone() {
        this.validator.validate();
        if(this.valIsDone && this.imageUri != null){
            final Memory memory = new Memory(key,
                    this.titleText.getText().toString(),
                    this.descriptionText.getText().toString(),
                    Calendar.getInstance().getTimeInMillis(),
                    "");
            final StorageReference filePath = this.memoImageRef.child(this.key + ".jpg");
            filePath.putFile(this.imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
        else if (!this.valIsDone){
            Toast.makeText(this, "No picture selected", Toast.LENGTH_LONG).show();
        }
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
}
