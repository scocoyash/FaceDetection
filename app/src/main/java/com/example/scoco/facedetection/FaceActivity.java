package com.example.scoco.facedetection;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.File;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class FaceActivity extends AppCompatActivity {

    private static final String TAG = FaceActivity.class.getSimpleName();

    ImageView imageView;
    Button camera;
    File mImageFile;
    String accountName;
    String accountEmail;
    String profile;
    static boolean profileCreatedFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);

        imageView = findViewById(R.id.imageView);

        camera = findViewById(R.id.camera);

        profileCreatedFlag = false;

        Bundle receiveBundle = this.getIntent().getExtras();
        accountName = receiveBundle.getString("accountName");
        accountEmail = receiveBundle.getString("accountEmail");
        //profile = receiveBundle.getString("profile");

        Log.d(TAG, "Received Data from SignInIntent: " + accountName + " " + accountEmail + " " + profile);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Opens camera dialog
                EasyImage.openCamera(FaceActivity.this, 100);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                Toast.makeText(FaceActivity.this, "Image picker error", Toast.LENGTH_SHORT).show();
                Log.d("FaceActivity", "onImagePickerError: " + "Image picker error");
            }

            @Override
            public void onImagePicked(final File imageFile, EasyImage.ImageSource source, int type) {

                mImageFile = imageFile;

                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(new BitmapFactory().decodeFile(imageFile.getAbsolutePath()));

                FirebaseVisionFaceDetectorOptions options =
                        new FirebaseVisionFaceDetectorOptions.Builder()
                                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.NO_LANDMARKS)
                                .setClassificationMode(FirebaseVisionFaceDetectorOptions.NO_CLASSIFICATIONS)
                                .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                                .build();


                FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                        .getVisionFaceDetector(options);

                Task<List<FirebaseVisionFace>> result =
                        detector.detectInImage(image)
                                .addOnSuccessListener(
                                        new OnSuccessListener<List<FirebaseVisionFace>>() {
                                            @Override
                                            public void onSuccess(List<FirebaseVisionFace> faces) {

                                                Log.d(TAG, "Faces Detected by Firebase : " + faces.size());

                                                if(faces.size() > 1){
                                                    // Error
                                                    Log.d(TAG, "Mutiple faces Detected");
                                                    Toast.makeText(FaceActivity.this, "Mutiple faces Detected. Please submit single picture", Toast.LENGTH_LONG).show();
                                                }
                                                else if(faces.size() == 1){
//                                                    FirebaseVisionFace face = faces.get(0);
//                                                    Rect bounds = face.getBoundingBox();
                                                    Log.d(TAG, "Single face Detected");
                                                    //TODO 1: Send the image to our Python Server
                                                    profileCreatedFlag = true;
                                                    Bundle sendBundle = new Bundle();
                                                    sendBundle.putString("accountName", accountName);
                                                    sendBundle.putString("accountEmail", accountEmail);
                                                    //sendBundle.putString("profile", profile);

                                                    Intent mainActivity = new Intent(FaceActivity.this, MainActivity.class)
                                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    mainActivity.putExtras(sendBundle);
                                                    startActivity(mainActivity);
                                                    //Toast.makeText(FaceActivity.this, "Single Face Detetion Successful", Toast.LENGTH_LONG).show();
                                                }else{
                                                    Log.d(TAG, "No Face Detected ");
                                                }
                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                                Log.d("FaceActivity", "onFailure: " + "Firebase failed to detect face");
                                                Toast.makeText(FaceActivity.this, "Firebase failed to detect face", Toast.LENGTH_LONG).show();
                                            }
                                        });

            }

        });
    }

}
