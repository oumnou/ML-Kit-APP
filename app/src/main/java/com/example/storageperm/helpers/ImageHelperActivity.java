package com.example.storageperm.helpers;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ImageHelperActivity extends AppCompatActivity {
    private int REQUEST_PICK_IMAGE = 1000;
    private int REQUEST_CAPTURE_IMAGE = 1001;
    private ImageView inputImageView;
    private TextView outputTextView;

    private ImageLabeler imageLabeler;
    private File photoFile;

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

        startActivityForResult(intent,REQUEST_PICK_IMAGE);
    }

    public void onStartCamera(View view){
        // create a file to share with Camera
        photoFile = createPhotoFile();

        Uri fileUri = FileProvider.getUriForFile(this,"com.iago.fileprovider",photoFile);

        // create an Intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
        // startActivityForResult
        startActivityForResult(intent,REQUEST_CAPTURE_IMAGE);
    }

    private File createPhotoFile(){
        File photoFileDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"ML_IMAGE_HELPER");

        if(!photoFileDir.exists()){
            photoFileDir.mkdir();
        }

        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(photoFileDir.getPath() + File.separator + name);

        return file;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if(requestCode == REQUEST_PICK_IMAGE){

                Uri uri = data.getData();
                Bitmap bitmap = loadFromUri(uri);
                inputImageView.setImageBitmap(bitmap);
                runClassification(bitmap);

            } else if(requestCode == REQUEST_CAPTURE_IMAGE){
                Log.d("ML", "received callback from camera ");
                Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                inputImageView.setImageBitmap(bitmap);
                 runClassification(bitmap);
            }
       }
    }

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

    protected void runClassification(Bitmap bitmap){
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

    protected TextView getOutputTextView(){
        return outputTextView;
    }
    protected ImageView getInputImageView(){
        return inputImageView;
    }

    protected void drawDetectionResult(List<BoxWithLabel>boxes, Bitmap bitmap){

        Bitmap outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
        Canvas canvas =  new Canvas(outputBitmap);
        Paint penRect = new Paint();
        penRect.setColor(Color.GREEN);
        penRect.setStyle(Paint.Style.STROKE);
        penRect.setStrokeWidth(8f);

        Paint penLabel = new Paint();
        penLabel.setColor(Color.YELLOW);
        penLabel.setStyle(Paint.Style.FILL_AND_STROKE);
        penLabel.setTextSize(96f);

        for (BoxWithLabel boxWithLabel : boxes){
            canvas.drawRect(boxWithLabel.rect, penRect);

            // Rect
            Rect labelSize = new Rect(0,0,0,0);
            penLabel.getTextBounds(boxWithLabel.label, 0, boxWithLabel.label.length(), labelSize);
            float fontSize = penLabel.getTextSize() * boxWithLabel.rect.width() / labelSize.width();

            if( fontSize < penLabel.getTextSize()){
                penLabel.setTextSize(fontSize);
            }
            canvas.drawText(boxWithLabel.label, boxWithLabel.rect.left, boxWithLabel.rect.top + labelSize.height(),penLabel);

        }
        getInputImageView().setImageBitmap(outputBitmap);
    }


}


/*
 URI stands for Uniform Resource Identifier.
 In Android Studio, a URI is a string of characters that identifies a particular resource, such as a file or web page.
 A URI can be used to locate a resource, identify a specific portion of a resource, or provide information about the resource.
 */