package com.example.businix.ui;

import android.graphics.Typeface;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.TypefaceCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.businix.R;
import com.google.android.material.navigation.NavigationView;

public class ActionBar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected Toolbar toolbar;
    protected DrawerLayout drawerLayout;


    protected void setTitleText(String text, float textSize, int fontFamily, int color) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            TextView titleText = new TextView(this);
            titleText.setText(text);
            titleText.setTextSize(textSize);
            Typeface typeface = ResourcesCompat.getFont(this, fontFamily);

            titleText.setTypeface(typeface);

            titleText.setTextColor(getResources().getColor(color, getResources().newTheme()));
            getSupportActionBar().setCustomView(titleText);
        }
    }

    protected void setTitleText(String text) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowCustomEnabled(true);

            TextView titleText = new TextView(this);
            titleText.setText(text);

            if (!text.isEmpty() && !text.isBlank()) {
                titleText.setTextSize(18);

                Typeface typeface = ResourcesCompat.getFont(this, R.font.medium_font);
                titleText.setTypeface(typeface);
                titleText.setTextColor(getResources().getColor(R.color.black, getResources().newTheme()));
            }


            getSupportActionBar().setCustomView(titleText);
        }
    }

    protected void setDisplayHomeAsUpEnabled(Boolean bool) {

        if (bool) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    protected void setSupportMyActionBar(String title, Boolean isBack) {
        setSupportActionBar(toolbar);
        setTitleText(title);
        setDisplayHomeAsUpEnabled(isBack);
        getSupportActionBar().setTitle("");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_items, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.opt_menu) {
            if (drawerLayout != null)
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
//                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
        }
        if (itemId == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_logout) {
            finish();
        }
        return false;
    }
}
