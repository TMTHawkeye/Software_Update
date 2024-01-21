package com.project.internship_task_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.project.Adaptors.CustomAdaptorPermissions;
import com.project.internship_task_1.databinding.ActivityPermissionsBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionsActivity extends AppCompatActivity {

    String [] permissions_list;
    RecyclerView recyclerView;
    ActivityPermissionsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding=ActivityPermissionsBinding.inflate(getLayoutInflater());

        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        recyclerView=findViewById(R.id.recyclerView_permissions);

        permissions_list=getIntent().getStringArrayExtra("permissions_list");

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);


        ArrayList<String> permissions=new ArrayList<>();
        List<String> wordList = Arrays.asList(permissions_list);
        CustomAdaptorPermissions customAdaptorPermissions=new CustomAdaptorPermissions(wordList);
        recyclerView.setAdapter(customAdaptorPermissions);

        binding.toolbarDeviceInfo.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}