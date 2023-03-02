package com.example.storageperm.helpers;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.os.strictmode.Violation;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.storageperm.R;

import java.io.IOException;


public class ImageHelperActivity extends AppCompatActivity {
    private int REQUEST_PICK_IMAGE = 1000;
    private ImageView inputImageView;
    private TextView outputTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_helper);

        inputImageView = findViewById(R.id.imageViewInput);
        outputTextView = findViewById(R.id.textViewOutput);

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(ImageHelperActivity.class.getSimpleName(),"grant result for " + permissions[0]+" is " + grantResults[0]);}

    public void onPickImage(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); //type of images

        startActivityForResult(intent,1001);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if (resultCode == RESULT_OK) {
          //  if(resultCode == REQUEST_PICK_IMAGE){
                Uri uri = data.getData();
                Bitmap bitmap = loadFromUri(uri);

                inputImageView.setImageBitmap(bitmap);



            }
      //  }
    //}

    private Bitmap loadFromUri(Uri uri){
        Bitmap bitmap = null;
        try{
            ContentResolver m = getContentResolver();
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1){
                ImageDecoder.Source source = ImageDecoder.createSource(m,uri);
                bitmap = ImageDecoder.decodeBitmap(source);

            }else{
                bitmap = MediaStore.Images.Media.getBitmap(m,uri);
            }
        }catch (IOException e){
            e.printStackTrace();
            }

        return bitmap;
    }
}
