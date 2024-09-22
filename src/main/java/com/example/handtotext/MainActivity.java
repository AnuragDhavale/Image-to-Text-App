package com.example.handtotext;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.content.Context;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    Button btcam,btdev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btcam=(Button) findViewById(R.id.btcam);
        btdev=(Button) findViewById(R.id.btdev);
        btcam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cam=new Intent(MainActivity.this,CaptureRead.class);
                startActivity(cam);

            }
        });
        btdev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dev=new Intent(MainActivity.this,FileRead.class);
                startActivity(dev);

            }
        });

    }
}