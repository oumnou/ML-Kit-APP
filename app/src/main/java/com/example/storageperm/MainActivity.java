package com.example.storageperm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.storageperm.helpers.ImageHelperActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onGoToImageActivity(View view ){
        // start the image helper activity
        Intent intent = new Intent(this, ImageHelperActivity.class);
        startActivity(intent);
    }
 }
