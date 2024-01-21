package com.project.internship_task_1;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.Adaptors.CustomAdaptorApps;
import com.project.Helper.AppsModel;
import com.project.internship_task_1.databinding.ActivityScanAllAppsBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivityScanAllApps extends AppCompatActivity {
    ArrayList<AppsModel> app_arraylist = new ArrayList<>();
    ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    final Handler handler = new Handler(Looper.getMainLooper());
    Toolbar toolbar;
    EditText searchid = null;
    CustomAdaptorApps customAdaptorApps;


    private int progressStatus = 1;


    ActivityScanAllAppsBinding binding;
    int available_update_counter;

    int count = 0;
    private int size_of_apps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanAllAppsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = findViewById(R.id.t2);
        binding.progress.setProgress(0);

        binding.t2.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app_arraylist = null;
                available_update_counter = 0;
                count = 0;
                finish();
            }
        });


        mExecutorService.execute(() -> {
            initialize();
            getAllAppsFromDevice();
            handler.post(() -> {
                binding.animationView.setVisibility(View.GONE);
                binding.progress.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                binding.scannnedIcon.setVisibility(View.GONE);
                binding.availableUpdates.setVisibility(View.GONE);
                binding.scannedAppName.setVisibility(View.GONE);
                binding.scanning.setVisibility(View.GONE);
                binding.updatingOutOfTotal.setVisibility(View.GONE);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                binding.recyclerView.setLayoutManager(linearLayoutManager);

                customAdaptorApps = new CustomAdaptorApps(getApplicationContext(), app_arraylist, "update_after_scan", null);
                binding.recyclerView.setAdapter(customAdaptorApps);

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

    private void initialize() {
        searchid = findViewById(R.id.search_id);
    }


    public void getAllAppsFromDevice() {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> ril = getPackageManager().queryIntentActivities(mainIntent, 0);
        size_of_apps = ril.size();
        binding.progress.setMax(size_of_apps);

        System.out.println("**************Total apps are : " + ril.size());
        String name;
        Drawable icon;
        for (ResolveInfo ri : ril) {

            if (ri.activityInfo != null) {
                // get package
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

                try {
                    Drawable finalIcon = icon;
                    String finalName = name;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (count <= size_of_apps) {
                                binding.updatingOutOfTotal.setText((count++) + "/" + size_of_apps + " apps have been scanned");

                                binding.scannnedIcon.setImageDrawable(finalIcon);
                                binding.scannedAppName.setText(finalName);
//                            progressStatus += 1;
                                progressStatus += progressStatus * ril.size() / 100;
                                binding.progress.incrementProgressBy(progressStatus);
                            }
//                            binding.progress.setProgress(progressStatus);

                        }
                    });


                    PackageInfo pkginfo = getPackageManager().getPackageInfo(ri.activityInfo.packageName, 0);
                    AppsModel appsModel = new AppsModel(icon, name, ri.activityInfo.packageName, getVersionOfApp(ri.activityInfo.packageName));
                    checkPlaystoreVersion(appsModel, pkginfo.lastUpdateTime);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }

    }


    public void checkPlaystoreVersion(AppsModel model, Long currentdate) {

        String newdate = null;
        try {
            Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + model.getPackageName() + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();

            if (document != null) {
                Elements element = document.getElementsContainingOwnText("Updated on");
                for (Element ele : element) {
                    if (ele.siblingElements() != null) {
                        Elements sibElemets = ele.siblingElements();
                        for (Element sibElemet : sibElemets) {
                            newdate = sibElemet.text();
                            System.out.println("+++++++++new Version : " + newdate);
                            break;
                        }
                    }
                }
            }

            compareVersions(model, newdate, currentdate);

        } catch (Exception e) {
            Log.e("error", e.toString());
        }
    }

    public String getVersionOfApp(String packageName) {
        String v_name = "";
        String k = "";

        try {
            v_name = this.getPackageManager().getPackageInfo(packageName, 0).versionName;
            int count = 0;
            if (v_name != null) {
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

    private void compareVersions(AppsModel model, String playdate, Long currentdate) {
        try {
            Date playstore_app_date = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                    .parse(playdate);
            long playstore_miliseconds = playstore_app_date.getTime();

            if (currentdate < playstore_miliseconds) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.availableUpdates.setText("Available updates are : " + available_update_counter++);
//                        binding.updatingOutOfTotal.setText((count++) + " apps have been scanned");
                    }
                });
                app_arraylist.add(model);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}