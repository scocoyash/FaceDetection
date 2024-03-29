package com.example.scoco.facedetection;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


import com.example.scoco.facedetection.models.UserModel;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView title;
    private TextView subtitle;
    private UserModel userModel;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle receiveBundle = this.getIntent().getExtras();
        userModel = receiveBundle.getParcelable(UserModel.class.getSimpleName());
        //profile = receiveBundle.getString("profile");

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        title = (TextView) headerView.findViewById(R.id.nav_header_title);
        subtitle = (TextView) headerView.findViewById(R.id.nav_header_subtitle);

        title.setText(userModel.userName);
        subtitle.setText(userModel.userEmail);

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profile) {
            Log.d(TAG, "Profile Tab Clicked");
            Bundle sendBundle = new Bundle();
            sendBundle.putString("id", "106dsffsdfsdhthvsbntb");

            Intent i = new Intent(MainActivity.this, ProfileActivity.class);
            i.putExtras(sendBundle);
            startActivity(i);

        }
        else if (id == R.id.qr)
        {
            Log.d(TAG, "QR Clicked");
            //TODO Get the id from sqlite db
            Intent i = new Intent(MainActivity.this, QrActivity.class);
            String uniqueId = "fa592519821e2e0e31ea43cdd2beb20b";
            i.putExtra(QrActivity.EXTRA_UNIQUEID, uniqueId);
            startActivity(i);
        }
        else if (id == R.id.events)
        {
            Log.d("Events clicked", "Events Clicked");
            Intent i = new Intent(MainActivity.this, EventsActivity.class);
            startActivity(i);

        }
        else if (id == R.id.previousEvents) {
            Log.d(TAG, "Previous Events Clicked");
        }
        else if (id == R.id.sign_out) {
            Log.d(TAG, "Sign Out Clicked");
            signout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signout(){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        changeActivity();
                    }
                });
        LoginManager.getInstance().logOut();
    }

    private void changeActivity() {
        Intent signInActivity = new Intent(MainActivity.this, SignInActivity.class);
        finishAffinity();
        startActivity(signInActivity);
    }
}
