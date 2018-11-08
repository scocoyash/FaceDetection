package com.example.scoco.facedetection;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class FaceActivity extends AppCompatActivity {

    private static final String TAG = FaceActivity.class.getSimpleName();

    private ImageView imageView;
    private Bitmap bitmap;
    private String accountName;
    private String accountEmail;
    private String profile;
    static boolean profileCreatedFlag;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);

        imageView = findViewById(R.id.imageView);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.progressBar);

        profileCreatedFlag = false;

        Bundle receiveBundle = this.getIntent().getExtras();
        accountName = receiveBundle.getString("accountName");
        accountEmail = receiveBundle.getString("accountEmail");
        //profile = receiveBundle.getString("profile");

        Log.d(TAG, "Received Data from SignInIntent: " + accountName + " " + accountEmail + " " + profile);
        openDialog();

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

                bitmap = new BitmapFactory().decodeFile(imageFile.getAbsolutePath());
                imageView.setImageBitmap(bitmap);

                sendDataToFirebase(imageFile);
                //TODO: send Data to Python Server
                //sendDataToServer();
            }

        });
    }

    private void sendDataToFirebase(final File imageFile){

        mLoadingIndicator.setVisibility(View.VISIBLE);
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
                                            mLoadingIndicator.setVisibility(View.INVISIBLE);
                                            changeActivity();
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

    private void changeActivity(){
        Bundle sendBundle = new Bundle();
        sendBundle.putString("accountName", accountName);
        sendBundle.putString("accountEmail", accountEmail);
        //sendBundle.putString("profile", profile);

        Intent mainActivity = new Intent(FaceActivity.this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mainActivity.putExtras(sendBundle);
        startActivity(mainActivity);
    }

    private void openDialog() {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        // Set Custom Title
        TextView title = new TextView(this);
        // Title Properties
        title.setText("Disclaimer!");
        title.setPadding(10, 10, 10, 10);   // Set Position
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        alertDialog.setCustomTitle(title);

        // Set Message
        TextView msg = new TextView(this);
        // Message Properties
        msg.setText("\n Please allow camera to click your latest Picture! \n This will help in creating your unique identity");
        msg.setGravity(Gravity.CENTER_HORIZONTAL);
        msg.setTextColor(Color.BLACK);
        alertDialog.setView(msg);

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"Click a Pic!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Perform Action on Button
                Log.d(TAG, "Clicking Picture ");
                EasyImage.openCamera(FaceActivity.this, 100);
            }
        });


        new Dialog(getApplicationContext());
        alertDialog.show();

        // Set Properties for OK Button
        final Button camera = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) camera.getLayoutParams();
        neutralBtnLP.gravity = Gravity.CENTER;
        //okBT.setPadding(50, 10, 10, 10);   // Set Position
        camera.setTextColor(Color.BLUE);
        camera.setLayoutParams(neutralBtnLP);

    }

    private void sendDataToServer(){

        mLoadingIndicator.setVisibility(View.VISIBLE);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        String URL ="http://192.168.1.010";
        //sending image to server
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                if(s.equals("true")){
                    Toast.makeText(FaceActivity.this, "Uploaded Successful", Toast.LENGTH_LONG).show();
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    changeActivity();
                }
                else{
                    Toast.makeText(FaceActivity.this, "Some error occurred!", Toast.LENGTH_LONG).show();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(FaceActivity.this, "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();;
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("image", imageString);
                parameters.put("email", accountEmail);
                return parameters;
            }
        };

        RequestQueue rQueue = Volley.newRequestQueue(FaceActivity.this);
        rQueue.add(request);
    }

}
