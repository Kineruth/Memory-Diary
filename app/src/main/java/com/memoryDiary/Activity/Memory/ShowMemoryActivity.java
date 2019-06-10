package com.memoryDiary.Activity.Memory;

import android.content.DialogInterface;
import android.os.Bundle;
import com.github.clans.fab.FloatingActionButton;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.memoryDiary.Entity.Memory;
import com.memoryDiary.Holder.MemoryDataHolder;
import com.memoryDiary.R;
import com.squareup.picasso.Picasso;

public class ShowMemoryActivity extends AppCompatActivity {

    private TextView descriptionText;
    private TextView titleText;
    private ImageView memoryImageView;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabEdit, fabDelete;

    public ShowMemoryActivity() {
    }

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
        fabMenu = findViewById(R.id.show_memory_fab_menu);
        fabEdit = findViewById(R.id.show_memory_fab_edit);
        fabDelete = findViewById(R.id.show_memory_fab_delete);

        fabMenu.bringToFront();
        //color when not pressed
        fabEdit.setColorNormal(getResources().getColor(R.color.babyBlue));
        fabDelete.setColorNormal(getResources().getColor(R.color.babyBlue));
        //color when pressed
        fabEdit.setColorPressed(getResources().getColor(R.color.red));
        fabDelete.setColorPressed(getResources().getColor(R.color.red));
        initMemoryDetails();

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editActivity();
            }
        });
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteActivity();
            }
        });
    }

    private void initMemoryDetails() {
        Memory memory = MemoryDataHolder.getMemoryDataHolder().getMemory();
        titleText.setText(memory.getMemoryTitle());
        descriptionText.setText(memory.getDescription());
        Picasso.get().load(memory.getImagePath()).into(memoryImageView);
    }

    private void editActivity(){
        //forward to edit memory layout/class
        Toast.makeText(getApplicationContext(), "edit", Toast.LENGTH_SHORT).show();
    }

    private void deleteActivity(){

        new AlertDialog.Builder(this)
                .setTitle("Delete Memory")
                .setMessage("Sure you want to delete this memory? ")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        //delete the memory from everywhere
                        Toast.makeText(getApplicationContext(), "deleting", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // user doesn't want to logout
                    }
                })
                .show();
    }
}

