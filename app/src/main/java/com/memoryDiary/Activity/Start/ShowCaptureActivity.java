package com.memoryDiary.Activity.Start;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.memoryDiary.Activity.Memory.AddMemoryActivity;
import com.memoryDiary.R;

public class ShowCaptureActivity extends AppCompatActivity {

    FloatingActionButton mFab ;
    byte[] picture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_capture);
        mFab = findViewById(R.id.show_capture_floating_button);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        picture = extras.getByteArray("capture");

        if(picture != null){
            ImageView image = findViewById(R.id.imageViewCaptured);

            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            Bitmap rotatedBitmap = rotate(decodedBitmap);
            image.setImageBitmap(rotatedBitmap);

            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(this, "Add Memo", Toast.LENGTH_SHORT).show();
                    addMemoryActivity();
                }
            });
        }
    }

    private Bitmap rotate(Bitmap decodedBitmap) {
        int w = decodedBitmap.getWidth();
        int h = decodedBitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(90);

        return Bitmap.createBitmap(decodedBitmap, 0, 0, w, h, matrix, true);
    }
    /**
     * When clicked on the add FAB will open the add new memory activity.
     */
    private void addMemoryActivity() {
        Intent intent = new Intent(this, AddMemoryActivity.class);
        intent.putExtra("capture", picture);
        startActivity(intent);
    }
}
