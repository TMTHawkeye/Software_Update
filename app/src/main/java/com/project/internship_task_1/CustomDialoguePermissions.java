package com.project.internship_task_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.project.Adaptors.CustomAdaptorPermissions;

import java.util.List;

public class CustomDialoguePermissions extends Dialog {
    public Activity c;
    public Dialog d;
    List<String> permissions_list;

    RecyclerView recyclerView;
    ImageView btn;


    public CustomDialoguePermissions(@NonNull Activity a, List<String> list_permissions) {
        super(a);
        this.c=a;
        this.permissions_list=list_permissions;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_dialogue_permissions);
        Window window =getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);


        btn=findViewById(R.id.btnClose);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        recyclerView=findViewById(R.id.recyclerView_permissions);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        CustomAdaptorPermissions customAdaptorPermissions=new CustomAdaptorPermissions(permissions_list);
        recyclerView.setAdapter(customAdaptorPermissions);
    }
}