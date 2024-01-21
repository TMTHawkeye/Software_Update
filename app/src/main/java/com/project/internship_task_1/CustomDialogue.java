package com.project.internship_task_1;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class CustomDialogue extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button exit, rateus;

        public CustomDialogue(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_dialogue);
            exit = (Button) findViewById(R.id.exit_id);
            rateus = (Button) findViewById(R.id.rate_us_id);
            exit.setOnClickListener(this);
            rateus.setOnClickListener(this);




        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.exit_id:
                    c.finishAffinity();
                    break;
                case R.id.rate_us_id:

                    try{
                        c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+c.getPackageName())));
                    }
                    catch (ActivityNotFoundException e){
                        c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+c.getPackageName())));
                    }
                    dismiss();
                    break;
                default:
                    break;
            }
            dismiss();
        }





}
