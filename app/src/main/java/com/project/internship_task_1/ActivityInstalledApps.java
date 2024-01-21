package com.project.internship_task_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.project.Adaptors.CustomAdaptorApps;
import com.project.Adaptors.UninstallInterface;
import com.project.Helper.AppsModel;
import com.project.internship_task_1.databinding.ActivityInstalledAppsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivityInstalledApps extends AppCompatActivity {
    ActivityInstalledAppsBinding binding;
    EditText searchid = null;
    ArrayList<AppsModel> apps_installed = new ArrayList();
    CustomAdaptorApps customAdaptorApps = null;

    ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = ActivityInstalledAppsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        super.onCreate(savedInstanceState);

        mExecutorService.execute(() -> {
//            initialize();
//            getallapps();
            handler.post(() -> {
//                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//
//                customAdaptorApps = new CustomAdaptorApps(getApplicationContext(),apps_installed, "Update", null);
//                binding.recyclerView.setAdapter(customAdaptorApps);
//                binding.recyclerView.setLayoutManager(linearLayoutManager);
//
//                searchid.addTextChangedListener(new TextWatcher() {
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        // Call back the Adapter with current character to Filter
//                        customAdaptorApps.getFilter().filter(s.toString());
//                    }
//
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//
//                    }
//                });
            });
        });

        binding.t1.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void initialize() {
        searchid = findViewById(R.id.search_id);
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    public void getallapps() {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        // get list of all the apps installed
        List<ResolveInfo> ril = getPackageManager().queryIntentActivities(mainIntent, 0);
        String name = null;
        Drawable icon = null;
        int i = 0;
        for (ResolveInfo ri : ril) {
            if (!isSystemPackage(ri)) {
                if (ri.activityInfo != null) {
                    Resources res = null;
                    try {
                        res = getPackageManager().getResourcesForApplication(ri.activityInfo.applicationInfo);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (ri.activityInfo.labelRes != 0) {
                        name = res.getString(ri.activityInfo.labelRes);
                        icon = res.getDrawable(ri.activityInfo.getIconResource());
                    } else {
                        name = ri.activityInfo.applicationInfo.loadLabel(
                                getPackageManager()).toString();
                        icon = ri.activityInfo.applicationInfo.loadIcon(getPackageManager());
                    }
                    AppsModel appsModel = new AppsModel(icon, name, ri.activityInfo.packageName, getVersionOfApp(ri.activityInfo.packageName));
                    apps_installed.add(appsModel);
                    i++;
                }
            }
        }
    }

    public boolean isSystemPackage(ResolveInfo resolveInfo) {
        return ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    private String getVersionOfApp(String packageName) {
        String v_name = "";
        String k = "";

        try {
            v_name = this.getPackageManager().getPackageInfo(packageName, 0).versionName;
            int count = 0;
            if(v_name!=null) {
                for (int i = 0; i < v_name.length(); i++) {
                    if (count == 7) {
                        break;
                    }
                    k = k + v_name.charAt(i);
                    count++;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        return k;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}