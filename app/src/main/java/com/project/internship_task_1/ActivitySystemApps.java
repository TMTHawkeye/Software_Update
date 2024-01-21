package com.project.internship_task_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import android.widget.EditText;
import android.widget.Toolbar;

import com.project.Adaptors.CustomAdaptorApps;
import com.project.Helper.AppsModel;
import com.project.internship_task_1.databinding.ActivitySystemAppsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivitySystemApps extends AppCompatActivity {
    ArrayList<String> packageName = new ArrayList();
    Toolbar toolbar;
    String ri;
    CustomAdaptorApps customAdaptorApps=null;

    EditText searchid=null;

    ArrayList<AppsModel> system_apps = new ArrayList();

    ActivitySystemAppsBinding binding;

    ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySystemAppsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String title=getIntent().getStringExtra("toolbarTitle");

        binding.t1.titleToolbar.setText(title);


        binding.t1.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mExecutorService.execute(() -> {
            initialize();
            getSystemApps();
            handler.post(() -> {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                binding.recyclerView.setLayoutManager(linearLayoutManager);
                customAdaptorApps = new CustomAdaptorApps(getApplicationContext(),system_apps, "Update", null);
                binding.recyclerView.setAdapter(customAdaptorApps);

                searchid.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // Call back the Adapter with current character to Filter
                        customAdaptorApps.getFilter().filter(s.toString());
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            });
        });
    }

    private void initialize() {
        searchid = findViewById(R.id.search_id);
    }

    public void getSystemApps() {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> ril = getPackageManager().queryIntentActivities(mainIntent, 0);
        String name = null;
        Drawable icon = null;
        int i = 0;
        for (ResolveInfo ri : ril) {
            if (isSystemPackage(ri)) {
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

                    system_apps.add(appsModel);
                    i++;
                }
            }

        }
    }

    private String getVersionOfApp(String packageName) {
        String v_name = "";
        try {
            v_name = this.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String k = "";
        int count = 0;
        for (int i = 0; i < v_name.length(); i++) {
            if (count == 7) {
                break;
            }
            k = k + v_name.charAt(i);
            count++;
        }
        return k;
    }

    public boolean isSystemPackage(ResolveInfo resolveInfo) {
        return ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}