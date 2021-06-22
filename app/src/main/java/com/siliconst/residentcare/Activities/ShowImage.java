package com.siliconst.residentcare.Activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.siliconst.residentcare.R;
import com.siliconst.residentcare.Utils.AppConfig;

public class ShowImage extends AppCompatActivity {

    ImageView image;

    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        image = findViewById(R.id.image);
        url=getIntent().getStringExtra("url");
        Glide.with(this).load(AppConfig.BASE_URL_Image_ATTATCHMENTS+url).into(image);

    }
}
