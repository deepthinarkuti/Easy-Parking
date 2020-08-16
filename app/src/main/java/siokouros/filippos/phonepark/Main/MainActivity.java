package siokouros.filippos.phonepark.Main;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Math.toIntExact;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import siokouros.filippos.phonepark.Manifest;
import siokouros.filippos.phonepark.Model.Parking;
import siokouros.filippos.phonepark.Model.User;
import siokouros.filippos.phonepark.R;
import siokouros.filippos.phonepark.StartUp.LogIn;
import siokouros.filippos.phonepark.StartUp.SignUp;
import siokouros.filippos.phonepark.StartUp.StartUpActivity;

public class MainActivity extends AppCompatActivity implements AboutFragment.OnFragmentInteractionListener, ParkingFragment.OnFragmentInteractionListener
        , MyCarsFragment.OnFragmentInteractionListener, RegisterCarFragment.OnFragmentInteractionListener, ParkMyCarFragment.OnFragmentInteractionListener
        , PaymentFragment.OnFragmentInteractionListener, ExtendFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener {
    private static final String TAG = "MainActivity";
    private final int LOCATION_PERMISSION_CODE = 1;
    private final int LOCATION_GPS = 2;


    TextView mainName, mainEmail;
    NavigationView navigationViev;
    Button parkMyCarButton;
    ImageButton addCarButton;

    private DrawerLayout mDrawerLayout;
    FragmentManager manager;
    User user;

    //Firebase stuff
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    FirebaseAuth.AuthStateListener authStateListener;
    String userID;

    String hours, minutes, startTime;

    boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase stuff
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = auth.getCurrentUser();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged: Singed in  " + user.getUid());
                    Toast.makeText(MainActivity.this, "Successfully signed in with " + user.getUid(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onAuthStateChanged: Signed_out");
                    Toast.makeText(MainActivity.this, "Succeddfully signed out.", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(MainActivity.this, StartUpActivity.class);
                    startActivity(myIntent);
                }
            }
        };


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final FragmentManager manager = getSupportFragmentManager();


        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View v = navigationView.getHeaderView(0);


        mainEmail = v.findViewById(R.id.nav_email);
        mainName = v.findViewById(R.id.nav_name);
        parkMyCarButton = findViewById(R.id.park_my_car_button);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowCustomEnabled(false);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);


        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationViev = findViewById(R.id.nav_view);
        navigationViev.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(false);

                mDrawerLayout.closeDrawers();
                FragmentManager manager = getSupportFragmentManager();

                switch (item.getItemId()) {
                    case R.id.nav_myAccount:
                        ParkingFragment parkingFragment = new ParkingFragment();
                        manager.beginTransaction()
                                .replace(R.id.main_frame_fragment, parkingFragment).addToBackStack(null)
                                .commit();
                        break;

                    case R.id.nav_Settings:
                        SettingsFragment settingsFragment = new SettingsFragment();
                        manager.beginTransaction()
                                .replace(R.id.main_frame_fragment, settingsFragment).addToBackStack(null)
                                .commit();
                        break;

                    case R.id.nav_about:
                        AboutFragment aboutFragment = new AboutFragment();
                        manager.beginTransaction()
                                .replace(R.id.main_frame_fragment, aboutFragment).addToBackStack(null)
                                .commit();
                        break;

                    case R.id.nav_myCars:
                        MyCarsFragment myCarsFragment = new MyCarsFragment();
                        manager.beginTransaction()
                                .replace(R.id.main_frame_fragment, myCarsFragment).addToBackStack(null)
                                .commit();
                        break;

                    case R.id.nav_logout:
                        FirebaseAuth.getInstance().signOut();
                        logout();
                        break;

                }
                return true;
            }
        });

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You have already granted this persmissions ", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }


    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Permission needed to get location for parking")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void logout() {
        Intent myIntent = new Intent(this, StartUpActivity.class);
        startActivity(myIntent);
    }

    private void showData(DataSnapshot dataSnapshot) {


        manager = getSupportFragmentManager();


        for (DataSnapshot ds : dataSnapshot.child("Users").getChildren()) {
            if (ds.getKey().equals(userID)) {
                User user = new User();
                user.setName((Objects.requireNonNull(ds.getValue(User.class))).getName());
                user.setSurname((Objects.requireNonNull(ds.getValue(User.class))).getSurname());
                user.setEmail((Objects.requireNonNull(ds.getValue(User.class))).getEmail());
                mainEmail.setText(user.getEmail());
                mainName.setText(user.getName() + " " + user.getSurname());

                if (Integer.parseInt(ds.child("numberOfCars").getValue(String.class)) <= 0) {
                    RegisterCarFragment registerCarFragment = new RegisterCarFragment();
                    manager.beginTransaction()
                            .replace(R.id.main_frame_fragment, registerCarFragment).addToBackStack(null)
                            .commit();

                } else {
                    ParkingFragment parkingFragment = new ParkingFragment();
                    manager.beginTransaction()
                            .replace(R.id.main_frame_fragment, parkingFragment).addToBackStack(null)
                            .commit();
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            return;
        }
        finish();
        Intent myIntent = new Intent(this, StartUpActivity.class);
        startActivity(myIntent);
    }


    public void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    Fragment getCurrentFragment()
    {
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.main_frame_fragment);
        return currentFragment;
    }

    /*@Override
    public void onBackPressed() {
        FrameLayout fl = findViewById(R.id.main_frame_fragment);
        if (fl.getChildCount()==1) {
            super.onBackPressed();

            if (fl.getChildCount() == 0) {
                if (doubleBackToExitPressedOnce) {
                    finish();
                }

                this.doubleBackToExitPressedOnce = true;
                Fragment visibleFragment=getCurrentFragment();

                Toast.makeText(this, visibleFragment.getTag()*//*"Please click back again to exit"*//*, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }

        } else if (fl.getChildCount() == 0) {
            ParkingFragment parkingFragment = new ParkingFragment();
            manager.beginTransaction()
                    .replace(R.id.main_frame_fragment, parkingFragment).addToBackStack(null)
                    .commit();
        } else {
            super.onBackPressed();
        }
    }*/


//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return true;
//    }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            hideKeyboard(this);
            switch (item.getItemId()) {
                case android.R.id.home:
                    mDrawerLayout.openDrawer(GravityCompat.START);
                    return true;
            }


            return true;
        }


        @Override
        public void onFragmentInteraction (Uri uri){

        }
    }
