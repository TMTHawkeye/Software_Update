package com.project.internship_task_1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.project.Adaptors.CustomAdaptorApps;
import com.project.Adaptors.UninstallInterface;
import com.project.Helper.AppsModel;
import com.project.internship_task_1.databinding.ActivityUninstallAppsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivityUninstallApps extends AppCompatActivity implements UninstallInterface {

    ActivityUninstallAppsBinding binding;
    EditText searchid = null;
    CustomAdaptorApps customAdaptorApps = null;

    ArrayList<AppsModel> apps_uninstalled = new ArrayList<>();
    ArrayList<AppsModel> apps_uninstalled2;
    int pos;

    ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUninstallAppsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String title = getIntent().getStringExtra("toolbarTitle");

        binding.t1.titleToolbar.setText(title);


        binding.t1.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mExecutorService.execute(() -> {
            initialize();

            getallapps();
            handler.post(() -> {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                binding.recyclerViewUninstallApps.setLayoutManager(linearLayoutManager);

                customAdaptorApps = new CustomAdaptorApps(getApplicationContext(),apps_uninstalled, "Uninstall", this);
                binding.recyclerViewUninstallApps.setAdapter(customAdaptorApps);

                // Add Text Change Listener to EditText
                searchid.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // Call back the Adapter with current character to Filter
                        customAdaptorApps.getFilter().filter(s.toString());
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            });
        });
    }


    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        // There are no request codes
                        apps_uninstalled2.remove(apps_uninstalled2.get(pos));
                        CustomAdaptorApps customAdaptorApps = new CustomAdaptorApps(getApplicationContext(),apps_uninstalled2, "Uninstall", ActivityUninstallApps.this);
                        binding.recyclerViewUninstallApps.setAdapter(customAdaptorApps);
                    }
                }
            });

    private void initialize() {
        searchid = findViewById(R.id.search_id);
    }


    public void getallapps() {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

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

                    apps_uninstalled.add(appsModel);
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
    public void deleteApp(ArrayList<AppsModel> appsModel, int position) {
        apps_uninstalled2 = new ArrayList<>();
        apps_uninstalled2 = appsModel;
        pos = position;
        Log.d("TAG", "onClick:" + appsModel.get(position));
        Intent intent = null;

        Uri packageUri = Uri.parse("package:" + appsModel.get(position).getPackageName());
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
        uninstallIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
        someActivityResultLauncher.launch(uninstallIntent);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}