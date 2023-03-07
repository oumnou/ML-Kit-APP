package com.example.storageperm.image;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.storageperm.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions;

import java.io.File;
import java.util.List;

public class FlowerIdentificationActivity extends ImageClassificationActivity{

    private ImageLabeler imageLabeler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("TAG", "onCreate: marouaaaaaaaaaaaaaaaaaane ");

        LocalModel localModel = new LocalModel.Builder().setAssetFilePath("model_flowers.tflite").build();

        CustomImageLabelerOptions options = new CustomImageLabelerOptions.Builder(localModel)
                .setConfidenceThreshold(0.7f)
                .setMaxResultCount(5)
                .build();

        imageLabeler = ImageLabeling.getClient(options);
    }

    @Override
    protected void runClassification(Bitmap bitmap) {
        InputImage inputImage = InputImage.fromBitmap(bitmap,0);
        imageLabeler.process(inputImage).addOnSuccessListener(imageLabels -> {
            if (imageLabels.size() > 0) {
                StringBuilder builder = new StringBuilder();
                for (ImageLabel label : imageLabels){
                    builder.append(label.getText())
                            .append(" : ")
                            .append(label.getConfidence())
                            .append("\n");
                }
                getOutputTextView().setText(builder.toString());
            } else {
                getOutputTextView().setText("Could not classify");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
}
