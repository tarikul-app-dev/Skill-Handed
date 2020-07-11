package com.job.skillhand;

//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

public class ViewPageActivity extends AppCompatActivity {
    ImageView imgBackHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_page);

        imgBackHome = findViewById(R.id.imgv_back_home);

        imgBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPageActivity.this,DashboardActivity.class);
                startActivity(intent);
                ActivityCompat.finishAffinity(ViewPageActivity.this);
            }
        });
    }
}
