package com.job.skillhand;

//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.job.skillhand.utils.Util;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {
    AutoCompleteTextView atvCountrySelection;
    RelativeLayout lin_next;
    String[] countryList ;
    ImageView imgvArr;
    boolean isSelect = false;
    Util util;
    String country_name = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initViews();
    }
    public void initViews(){
        util = new Util(DashboardActivity.this);
        atvCountrySelection = findViewById(R.id.sp_select_country);
        lin_next = findViewById(R.id.lin_next);
        imgvArr = findViewById(R.id.imgv_arr_down);
        countryList = getResources().getStringArray(R.array.country_name);
        atvCountrySelection.setAdapter(new ArrayAdapter<String>(DashboardActivity.this,R.layout.dropdown_item,
                R.id.text1, countryList));

        atvCountrySelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atvCountrySelection.showDropDown();
            }
        });

        atvCountrySelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                country_name = (String) parent.getItemAtPosition(pos);
                isSelect = true;

            }
        });

        lin_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelect){
                    Intent regIntent = new Intent(DashboardActivity.this,RegisterActivity.class);
                    regIntent.putExtra("contry_name",country_name);
                    startActivity(regIntent);
                }else {
                    util.showFullyCustomToast("Please select a country");
                }

            }
        });

    }
}
