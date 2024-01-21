package com.project.internship_task_1;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.Helper.AppsModel;
import com.project.internship_task_1.databinding.ActivityDeviceInfoBinding;
import com.project.internship_task_1.databinding.ActivityLatestUpdateBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivityLatestUpdate extends AppCompatActivity {

    ActivityLatestUpdateBinding binding;
    RecyclerView recyclerView;
    int i = 0;
    String playstoreDate = "";
    AppsModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLatestUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        model = getIntent().getParcelableExtra("model");
        String updated_btn = "";
        updated_btn = getIntent().getStringExtra("button_update");

        if (updated_btn.equals("Update Now")) {
            binding.updateButton.setText(updated_btn);

            binding.updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                     binding.progressBarCheckUpdate.setVisibility(View.VISIBLE);
//                     checkPlaystoreVersion(model);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + model.getPackageName())));

                }
            });
        } else if (updated_btn.equals("App up to date")) {
//            binding.updateButton.setText(updated_btn);
            binding.updateButton.setVisibility(View.GONE);
            binding.textviewIdUpdate.setText(updated_btn);
            binding.textviewIdUpdate.setVisibility(View.VISIBLE);
        }
        String pkg = model.getPackageName();
        Drawable icon = null;
        try {
            icon = getApplicationContext().getPackageManager().getApplicationIcon(pkg);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        binding.iconIdLatest.setImageDrawable(icon);
        binding.titleApp.setText(model.getAppname());
        binding.updatedDate.setText(getLastUpdateDate());
        binding.version.setText(model.getCurrent_version());

        binding.launch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchApp = getPackageManager().getLaunchIntentForPackage(model.getPackageName());
                startActivity(launchApp);
            }
        });

        binding.uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri packageUri = Uri.parse("package:" + model.getPackageName());
                Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
                uninstallIntent.putExtra(Intent.EXTRA_RETURN_RESULT, false);
                new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
                startActivityForResult(uninstallIntent, 1);
            }
        });

        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

            }
        });


        binding.toolbarLatest.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.permissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager pm=getPackageManager();
                PackageInfo packageInfo=null;
                try {
                     packageInfo = pm.getPackageInfo(model.getPackageName(), PackageManager.GET_PERMISSIONS);
                    //Get Permissions
                    String[] requestedPermissions = packageInfo.requestedPermissions;
                    if(requestedPermissions != null) {

                        CustomDialoguePermissions cdd = new CustomDialoguePermissions(ActivityLatestUpdate.this, Arrays.asList(requestedPermissions));
                        cdd.show();

//                        Intent intent=new Intent(getApplicationContext(),PermissionsActivity.class);
//                        intent.putExtra("permissions_list",requestedPermissions);
//                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(ActivityLatestUpdate.this, "No permission granted to this app", Toast.LENGTH_SHORT).show();
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }


            }
        });
    }


    private String getLastUpdateDate() {
        PackageManager pm = getApplicationContext().getPackageManager();
//        ApplicationInfo appInfo = null;
        PackageInfo pk = null;


        try {
            pk = pm.getPackageInfo(model.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        long updateTimeInMilliseconds = 0; // install time is conveniently provided in milliseconds

//        String appFile = appInfo.sourceDir;

        updateTimeInMilliseconds = pk.lastUpdateTime;
//        updateTimeInMilliseconds = new File(appFile).getAbsoluteFile().lastModified();


        Date date = new Date(updateTimeInMilliseconds);
        SimpleDateFormat df2 = new SimpleDateFormat("MMM d, yyyy");
        String dateText = df2.format(date);
        System.out.println("***********Installed app version Date : " + dateText);
//        this.version = dateText;

        return dateText;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}