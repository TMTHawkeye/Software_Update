package com.project.internship_task_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.infideap.drawerbehavior.Advance3DDrawerLayout;
import com.project.internship_task_1.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private int count = 0;
    Dialog dialog;

    public Advance3DDrawerLayout drawer;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    ActivityMainBinding binding;
    private static final int INTERNET_PERMISSION_CODE = 102;
    private static final int WRITE_PERMISSION_CODE = 103;
    private static final int REQUEST_DELETE_PACKAGES = 104;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        drawer = (Advance3DDrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setViewRotation(GravityCompat.START, 15); // set degree of Y-rotation ( value : 0 -> 45)

        drawer.setViewScale(GravityCompat.START, 0.9f); //set height scale for main view (0f to 1f)
        drawer.setViewElevation(GravityCompat.START, 20); //set main view elevation when drawer open (dimension)
        drawer.setViewScrimColor(GravityCompat.START, Color.TRANSPARENT); //set drawer overlay coloe (color)
        drawer.setContrastThreshold(3); //set maximum of contrast ratio between white text and background color.
        drawer.setRadius(GravityCompat.START, 25);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.nav_open, R.string.nav_close);


        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        Toolbar toolbar = binding.toolbarMainId.toolbar;
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setHomeAsUpIndicator(R.drawable.menu_icon_svg);
        actionbar.setDisplayHomeAsUpEnabled(true);

        checkPermission(Manifest.permission.REQUEST_DELETE_PACKAGES, REQUEST_DELETE_PACKAGES);
        checkPermission(Manifest.permission.INTERNET, INTERNET_PERMISSION_CODE);
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_PERMISSION_CODE);
//        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);


        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.scan_nav:

                        boolean connected = false;
                        ConnectivityManager connectivityManager = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        }
                        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                            //we are connected to a network
                            connected = true;
                        } else
                            connected = false;
                        if (connected) {
                            startActivity(new Intent(getApplicationContext(), ActivityScanAllApps.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "No internet Connection", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.info_nav:
                        startActivity(new Intent(getApplicationContext(), ActivityDeviceInfo.class));
                        break;
                    case R.id.nav_feedback:
                        Intent feedbackEmail = new Intent(Intent.ACTION_SEND);

                        feedbackEmail.setType("text/email");
                        feedbackEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"hammadhussaing1@gmail.com"});
                        feedbackEmail.putExtra(Intent.EXTRA_SUBJECT, "Feedback Software Update");
                        startActivity(Intent.createChooser(feedbackEmail, "Send Feedback:"));
                        break;
                    case R.id.nav_privacy:
                        Toast.makeText(MainActivity.this, "privacy policy", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.share_nav:
                        try {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                            String shareMessage = "\nLet me recommend you this application\n\n";
                            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                            startActivity(Intent.createChooser(shareIntent, "choose one"));
                        } catch (Exception e) {
                            e.toString();
                        }

                        break;

                    case R.id.nav_rateus:

                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                        }
                        break;

                    default:
                        Toast.makeText(MainActivity.this, "Default", Toast.LENGTH_SHORT).show();

                }
                return false;
            }
        });


        binding.installedAppsId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityInstalledApps.class);
                startActivity(intent);
            }
        });

        binding.systemApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivitySystemApps.class);
                intent.putExtra("toolbarTitle", "System Apps");
                startActivity(intent);
            }
        });

        binding.deviceInfoId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityDeviceInfo.class);
                startActivity(intent);
            }
        });

        binding.phoneId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent("android.settings.SYSTEM_UPDATE_SETTINGS"), 0);
            }
        });
        binding.uninstallAppsId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityUninstallApps.class);
                intent.putExtra("toolbarTitle", "Uninstalled Apps");
                startActivity(intent);
            }
        });

        binding.phoneId1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean connected = false;
                ConnectivityManager connectivityManager = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                }
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                } else
                    connected = false;
                if (connected) {
                    startActivity(new Intent(getApplicationContext(), ActivityScanAllApps.class));
                } else {
                    Toast.makeText(getApplicationContext(), "No internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        CustomDialogue cdd = new CustomDialogue(MainActivity.this);
        cdd.show();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        } else {
//            Toast.makeText(MainActivity.this, "Permission for already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == INTERNET_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Internet Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Internet Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_DELETE_PACKAGES) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Package Deletion Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Package Deletion Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode ==WRITE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Write Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Write Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}