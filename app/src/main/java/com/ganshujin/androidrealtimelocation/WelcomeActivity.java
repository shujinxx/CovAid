package com.ganshujin.androidrealtimelocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout layoutDot;
    private TextView[] dotstv;
    private int[]layouts;
    private Button btnSkip;
    private Button btnNext;
    private MyPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!isFirstTimeStartApp()) {
            openDashboard();
            finish();
        }

        setStatusBarTransparent();
        setContentView(R.layout.activity_welcome);

        viewPager = (ViewPager)findViewById(R.id.view_pager);
        layoutDot = findViewById(R.id.dotLayout);
        btnNext = findViewById(R.id.btn_next);
        btnSkip = findViewById(R.id.btn_skip);


        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openDashboard(); }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPage = viewPager.getCurrentItem()+1;
                if(currentPage < layouts.length)
                {
                    viewPager.setCurrentItem(currentPage);
                }
                else
                {
                    openDashboard();
                }
            }
        });

        layouts = new int[]{R.layout.slider_1,R.layout.slider_2,R.layout.slider_3,R.layout.slider_4};
        pagerAdapter = new MyPagerAdapter(layouts,getApplicationContext());
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == layouts.length-1){
                    btnNext.setText("START");
                    btnSkip.setVisibility(View.GONE);
                }
                else
                {
                    btnNext.setText("NEXT");
                    btnSkip.setVisibility(View.VISIBLE);
                }
                setDotStatus(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setDotStatus(0);

    }

    private boolean isFirstTimeStartApp(){
        SharedPreferences ref = getApplicationContext().getSharedPreferences("AndroidRealtimeLocationApp", Context.MODE_PRIVATE);
        return ref.getBoolean("FirstTimeStartApp", true);
    }

    private void setFirstTimeStartStatus(boolean stt) {
        SharedPreferences ref = getApplicationContext().getSharedPreferences("AndroidRealtimeLocationApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ref.edit();
        editor.putBoolean("FirstTimeStartApp", stt);
        editor.commit();
    }

    private void setDotStatus(int page){
        layoutDot.removeAllViews();
        dotstv = new TextView[layouts.length];
        for (int i =0; i<dotstv.length; i++){
            dotstv[i] = new TextView(this);
            dotstv[i].setText(Html.fromHtml("&#8226;"));
            dotstv[i].setTextSize(30);
            dotstv[i].setTextColor(Color.parseColor("#A9B4BB"));
            layoutDot.addView(dotstv[i]);
        }

        if(dotstv.length>0){
            dotstv[page].setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    private void setStatusBarTransparent()
    {
        if (Build.VERSION.SDK_INT>=21)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            Window window =getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void openDashboard() {
        setFirstTimeStartStatus(false);
        Intent intent = new Intent(WelcomeActivity.this, Dashboard.class);
        startActivity(intent);
    }

}
