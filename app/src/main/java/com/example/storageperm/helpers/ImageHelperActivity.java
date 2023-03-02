package com.example.storageperm.helpers;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.storageperm.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.IOException;
import java.util.List;


public class ImageHelperActivity extends AppCompatActivity {
    private int REQUEST_PICK_IMAGE = 1000;
    private ImageView inputImageView;
    private TextView outputTextView;

    private ImageLabeler imageLabeler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_helper);

        inputImageView = findViewById(R.id.imageViewInput);
        outputTextView = findViewById(R.id.textViewOutput);

        imageLabeler = ImageLabeling.getClient(new ImageLabelerOptions
                .Builder()
                .setConfidenceThreshold(0.7f)
                .build());

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
                runClassification(bitmap);



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

    private void runClassification(Bitmap bitmap){
        InputImage inputImage = InputImage.fromBitmap(bitmap,0);
        imageLabeler.process(inputImage).addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
            @Override
            public void onSuccess(List<ImageLabel> imageLabels) {
                if (imageLabels.size() > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (ImageLabel label : imageLabels){
                        builder.append(label.getText())
                                .append(" : ")
                                .append(label.getConfidence())
                                .append("\n");
                    }
                    outputTextView.setText(builder.toString());
                } else {
                    outputTextView.setText("Could not classify");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

}
