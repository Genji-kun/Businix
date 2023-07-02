package com.example.businix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IntroduceActivity extends AppCompatActivity {

    ViewPager slideViewPager;
    Button btnSkip;
    LinearLayout btnBack, btnNext, dotList;

    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);

        btnSkip = (Button) findViewById(R.id.btnSkip);
        btnBack = (LinearLayout) findViewById(R.id.btnback);
        btnNext = (LinearLayout) findViewById(R.id.btnNext);

        btnBack.setOnClickListener(v -> {
            if (getItem(0) > 0) {
                slideViewPager.setCurrentItem(getItem(-1), true);
            }
        });

        btnNext.setOnClickListener(v -> {
            if (getItem(0) < 3) {
                slideViewPager.setCurrentItem(getItem(1), true);
            } else {
                Intent i = new Intent(IntroduceActivity.this, SignUpActivity.class);
                startActivity(i);
                finish();
            }
        });
        btnSkip.setOnClickListener(v -> {
            Intent i = new Intent(IntroduceActivity.this, SignUpActivity.class);
            startActivity(i);
            finish();
        });

        slideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        dotList = (LinearLayout) findViewById(R.id.dotList);

        viewPagerAdapter = new ViewPagerAdapter(this);
        slideViewPager.setAdapter(viewPagerAdapter);

        setUpIndicator(0);
        slideViewPager.addOnPageChangeListener(viewListener);
    }

    public void setUpIndicator(int position) {
        dots = new TextView[4];
        dotList.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.gray, getApplicationContext().getTheme()));
            dotList.addView(dots[i]);
        }

        dots[position].setTextColor(getResources().getColor(R.color.mediumPurple, getApplicationContext().getTheme()));
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            setUpIndicator(position);

            if (position > 0) {
                btnBack.setVisibility(View.VISIBLE);
            } else {
                btnBack.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
        }
    };

    private int getItem(int i) {
        return slideViewPager.getCurrentItem() + i;
    }
}