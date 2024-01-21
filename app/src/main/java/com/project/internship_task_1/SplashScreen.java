package com.project.internship_task_1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_SCREEN=3000;

    Animation top_animation,bottom_animation,rotate_animation,bounce_animation;
    ImageView image;
    TextView logo,slogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        setContentView(R.layout.activity_splash_screen);


        top_animation= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottom_animation= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        rotate_animation=AnimationUtils.loadAnimation(this,R.anim.rotate_animation);
        bounce_animation=AnimationUtils.loadAnimation(this,R.anim.bounce_animation);

        image=findViewById(R.id.main_icon);
        logo=findViewById(R.id.logo);

        image.setAnimation(bounce_animation);
        logo.setAnimation(bounce_animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                Pair[] pairs=new Pair[2];
                pairs[0]=new Pair<View,String>(image,"logo_image");
                pairs[1]=new Pair<View,String>(logo,"logo_text");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this, pairs);
                startActivity(intent,options.toBundle());
            }
        },SPLASH_SCREEN);
    }
}