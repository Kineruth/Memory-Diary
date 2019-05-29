package com.memoryDiary.Activity.Memory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.memoryDiary.Entity.Memory;
import com.memoryDiary.Holder.MemoryDataHolder;
import com.memoryDiary.R;
import com.squareup.picasso.Picasso;

public class ShowMemoryActivity extends AppCompatActivity {

    private TextView descriptionText;
    private TextView titleText;
    private ImageView memoryImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_memory);
        initFields();
    }

    private void initFields(){
        titleText = findViewById(R.id.show_memory_title);
        memoryImageView = findViewById(R.id.show_memory_image);
        descriptionText = findViewById(R.id.show_memory_description);
        descriptionText.setMovementMethod(new ScrollingMovementMethod());
        initMemoryDetails();
    }

    private void initMemoryDetails() {
        Memory memory = MemoryDataHolder.getMemoryDataHolder().getMemory();
        titleText.setText(memory.getMemoryTitle());
        descriptionText.setText(memory.getDescription());
        Picasso.get().load(memory.getImagePath()).into(memoryImageView);
    }
}

