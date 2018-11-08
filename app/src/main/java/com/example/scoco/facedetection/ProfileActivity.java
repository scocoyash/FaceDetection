package com.example.scoco.facedetection;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = ProfileActivity.class.getSimpleName();
    TextView uniqueIdTextView;
    TextView digitalIdentitiesTextView;
    TextView contactTextView;
    TextView unameTextView;
    Button updatedetailsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Log.d(TAG, "Activity Started");
        Bundle receiveBundle = this.getIntent().getExtras();
        String uniqueId  = receiveBundle.getString("id");
        Log.d(TAG, "ID received from Intent " + uniqueId);


        digitalIdentitiesTextView = (TextView) findViewById(R.id.email);
        contactTextView = (TextView) findViewById(R.id.contact);
        unameTextView = (TextView) findViewById(R.id.uname);
        updatedetailsButton = (Button) findViewById(R.id.updateDetails);
        uniqueIdTextView = (TextView) findViewById(R.id.uniqueid);

        fetchDetailsFromChain(uniqueId);

        uniqueIdTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Copy to clipboard invoked");
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Simple Text", uniqueIdTextView.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Text View Copied to Clipboard",
                        Toast.LENGTH_LONG).show();
            }

        });

        unameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Changing Uname");
                ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.uname_switcher);
                switcher.showNext();
                unameTextView.setText("value");
                updatedetailsButton.setVisibility(View.VISIBLE);
            }
        });

        contactTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Changing contact");
                ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.contact_switcher);
                switcher.showNext();
                contactTextView.setText("value");
                updatedetailsButton.setVisibility(View.VISIBLE);
            }
        });

        updatedetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Update Detail button pressed");
                updateDetailsOnChain();
            }
        });

    }

    protected void fetchDetailsFromChain(String uniqueId){
        //TODO : Make api call to python server with unique ID
        Log.d(TAG, "fetching details from chain Started");
        uniqueIdTextView.setText("");
        digitalIdentitiesTextView.setText("");
        contactTextView.setText("");
        unameTextView.setText("");
    }

    protected void updateDetailsOnChain(){
        Log.d(TAG, "Updating Details on Chain");
        String contact = (String) contactTextView.getText();
        String uname = (String) unameTextView.getText();

        //TODO: Update data to python server
    }
}
