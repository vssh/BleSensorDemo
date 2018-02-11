package com.vssh.blesensordemo;

import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;

import com.vssh.blesensordemo.ui.SensorTemperaturesFragment;

public class MainActivity extends AppCompatActivity {
    private SensorTemperaturesFragment sensorTemperaturesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorTemperaturesFragment = new SensorTemperaturesFragment();
        if (Build.VERSION.SDK_INT >= 21) {
            Slide slideLeft = new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START, getResources().getConfiguration().getLayoutDirection()));
            sensorTemperaturesFragment.setEnterTransition(slideLeft);
        }
        loadSensorFragment();
    }

    public void loadSensorFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, sensorTemperaturesFragment);
        transaction.commit();
    }
}
