
package com.project.internship_task_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.widget.LinearLayout;

import com.project.Adaptors.CustomAdaptorDeviceInfo;
import com.project.internship_task_1.databinding.ActivityDeviceInfoBinding;

import java.io.File;
import java.util.ArrayList;

public class ActivityDeviceInfo extends AppCompatActivity {

    ActivityDeviceInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeviceInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerviewInfo.setLayoutManager(linearLayoutManager);

        CustomAdaptorDeviceInfo customAdaptorDeviceInfo = new CustomAdaptorDeviceInfo(getDetails(), getStatus());
        binding.recyclerviewInfo.setAdapter(customAdaptorDeviceInfo);

        binding.toolbarDeviceInfo.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static ArrayList<String> getDetails() {
        ArrayList<String> details = new ArrayList<>();
        try {
            details.add("OS version: ");
            details.add("API Level: ");
            details.add("Device: ");
            details.add("Model: ");
            details.add("Product: ");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            details.add("VERSION RELEASE : ");
            details.add("VERSION INCREMENTAL : ");
            details.add("VERSION SDK NUMBER : ");
            details.add("BOARD : ");
            details.add("BOOTLOADER : ");
            details.add("BRAND : ");
            details.add("CPU_ABI : ");
            details.add("CPU_ABI2 : ");
            details.add("DISPLAY : ");
            details.add("FINGERPRINT : ");
            details.add("HARDWARE : ");
            details.add("HOST : ");
            details.add("ID : ");
            details.add("MANUFACTURER : ");
            details.add("MODEL : ");
            details.add("PRODUCT : ");
            details.add("SERIAL : ");
            details.add("TAGS : ");
            details.add("TIME : ");
            details.add("TYPE : ");
            details.add("UNKNOWN : ");
            details.add("USER : ");
            details.add("Total RAM : ");
            details.add("Total Internal Memory Size : ");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return details;
    }

    private ArrayList<String> getStatus() {
        ArrayList<String> status = new ArrayList<>();

        try {

            status.add(System.getProperty("os.version"));
            status.add(android.os.Build.VERSION.SDK);
            status.add(android.os.Build.DEVICE.toString());
            status.add(android.os.Build.MODEL.toString());
            status.add(android.os.Build.PRODUCT.toString());
            status.add(Build.VERSION.RELEASE);
            status.add(Build.VERSION.INCREMENTAL);
            status.add(String.valueOf(Build.VERSION.SDK_INT));
            status.add(Build.BOARD);
            status.add(Build.BOOTLOADER);
            status.add(Build.BRAND);
            status.add(Build.CPU_ABI);
            status.add(Build.CPU_ABI2);
            status.add(Build.DISPLAY);
            status.add(Build.FINGERPRINT);
            status.add(Build.HARDWARE);
            status.add(Build.HOST);
            status.add(Build.ID);
            status.add(Build.MANUFACTURER);
            status.add(Build.MODEL);
            status.add(Build.PRODUCT);
            status.add(Build.SERIAL);
            status.add(Build.TAGS);
            status.add(String.valueOf(Build.TIME));
            status.add(Build.TYPE);
            status.add(Build.UNKNOWN);
            status.add(Build.USER);

            ActivityManager actManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            actManager.getMemoryInfo(memInfo);
            long totalMemory = memInfo.totalMem / (1024 * 1024);

            totalMemory = Math.round(totalMemory / 1024);
            status.add(String.valueOf(totalMemory));

            status.add(getTotalInternalMemorySize());

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return status;
    }


    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return formatSize(totalBlocks * blockSize);
    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = " MB";
            size /= 1024;
            if (size >= 1024) {
                suffix = " GB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size/1024));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();

    }
}
