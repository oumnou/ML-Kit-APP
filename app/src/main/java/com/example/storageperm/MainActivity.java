package com.example.storageperm;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.storageperm.image.FlowerIdentificationActivity;
import com.example.storageperm.image.ImageClassificationActivity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void onGoToImageActivity(View view ){
        // start the image helper activity
        Intent intent = new Intent(this, ImageClassificationActivity.class);
        startActivity(intent);
    }



    public void onGoToFlowerActivity(View view ){
        // start the image helper activity
        Intent intent = new Intent(this, FlowerIdentificationActivity.class);
        startActivity(intent);
    }
 }
