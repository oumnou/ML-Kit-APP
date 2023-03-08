package com.example.storageperm.image;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.storageperm.helpers.BoxWithLabel;
import com.example.storageperm.helpers.ImageHelperActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;

import java.util.ArrayList;
import java.util.List;

public class ObjectDetectionActivity extends ImageHelperActivity {

    private ObjectDetector objectDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Multiple object detection in static image
        ObjectDetectorOptions options =
                new ObjectDetectorOptions.Builder()
                        .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
                        .enableMultipleObjects()
                        .enableClassification()
                        .build();

        objectDetector = ObjectDetection.getClient(options);
    }

    @Override
    protected void runClassification(Bitmap bitmap) {
        InputImage inputImage = InputImage.fromBitmap(bitmap,0);
        objectDetector.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<DetectedObject>>() {
                    @Override
                    public void onSuccess(List<DetectedObject> detectedObjects) {
                        if(!detectedObjects.isEmpty()){
                            StringBuilder builder = new StringBuilder();
                            List<BoxWithLabel> boxes = new ArrayList<>();

                            for (DetectedObject object : detectedObjects){
                                if(!object.getLabels().isEmpty()){
                                    String label = object.getLabels().get(0).getText();
                                    builder.append(label).append(" : ")
                                            .append(object.getLabels().get(0).getConfidence())
                                            .append("\n");
                                    boxes.add(new BoxWithLabel(object.getBoundingBox(), label));
                                    Log.d("ObjectDetection","object detected : "+ label );


                                }else{
                                    builder.append("Unknown").append("\n");
                                }
                            }
                            getOutputTextView().setText(builder.toString());
                            drawDetectionResult(boxes, bitmap);
                        }else{
                            getOutputTextView().setText("Could not detect");

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
}
