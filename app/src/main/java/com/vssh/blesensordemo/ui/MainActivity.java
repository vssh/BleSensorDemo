package com.vssh.blesensordemo.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.vssh.blesensordemo.R;

public class MainActivity extends AppCompatActivity {
    private SensorTemperaturesFragment sensorTemperaturesFragment;
    private CooktopFragment cooktopFragment;

    private boolean showingCooktop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorTemperaturesFragment = new SensorTemperaturesFragment();
        if (Build.VERSION.SDK_INT >= 21) {
            Slide slideLeft = new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START, getResources().getConfiguration().getLayoutDirection()));
            sensorTemperaturesFragment.setEnterTransition(slideLeft);
        }

        cooktopFragment = new CooktopFragment();
        if (Build.VERSION.SDK_INT >= 21) {
            Slide slideLeft = new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.END, getResources().getConfiguration().getLayoutDirection()));
            cooktopFragment.setEnterTransition(slideLeft);
        }

        loadSensorFragment();
    }

    public void loadSensorFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, sensorTemperaturesFragment);
        transaction.commit();

        showingCooktop = false;
    }

    public void loadCooktopFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, cooktopFragment);
        transaction.commit();

        showingCooktop = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.switch_fragment) {
            if(showingCooktop) {
                loadSensorFragment();
            }
            else {
                loadCooktopFragment();
            }
        }
        return true;
    }
}
