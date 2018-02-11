package com.vssh.blesensordemo;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.vssh.blesensordemo.connectors.GattConnector;

/**
 * Created by varun on 10.02.18.
 */

public class GourmetSensor {
    private short coldJunctionTemperature = 30;
    private short temperature1 = 30;
    private short temperature2 = 30;
    private short temperature3 = 30;
    private short temperature4 = 30;
    private short temperature5 = 30;
    private short batteryVoltage = 0;
    // standard gatt characteristic measurement_interval (2a21) supports a min value of 1sec
    // less than that, it would be better to use a custom characteristic instead
    private short updateInterval = 1;

    private GattConnector gattConnector;
    private Handler updateNotificationHandler;
    private Runnable updateNotificationRunnable;

    public GourmetSensor(Context context) {
        gattConnector = new GattConnector(context, new SensorUpdateIntervalListener() {
            @Override
            public void setUpdateInterval(short updateInterval) {
                setUpdateInterval(updateInterval);
            }
        });
        updateNotificationHandler = new Handler();
        updateNotificationRunnable = new Runnable() {
            @Override
            public void run() {
                notifyTempValues();
                updateNotificationHandler.postDelayed(updateNotificationRunnable, updateInterval*1000);
            }
        };
        setUpdateInterval((short) 1);
        gattConnector.startAdvertising();
    }

    public short getColdJunctionTemperature() {
        return coldJunctionTemperature;
    }

    public void setColdJunctionTemperature(short coldJunctionTemperature) {
        this.coldJunctionTemperature = coldJunctionTemperature;
        Log.d("GourmetSensor", "set ColdJuncTemp: "+coldJunctionTemperature);
        setGattTempValues();
    }

    public short getTemperature1() {
        return temperature1;
    }

    public void setTemperature1(short temperature1) {
        this.temperature1 = temperature1;
        Log.d("GourmetSensor", "set Temp1: "+temperature1);
        setGattTempValues();
    }

    public short getTemperature2() {
        return temperature2;
    }

    public void setTemperature2(short temperature2) {
        this.temperature2 = temperature2;
        Log.d("GourmetSensor", "set Temp2: "+temperature2);
        setGattTempValues();
    }

    public short getTemperature3() {
        return temperature3;
    }

    public void setTemperature3(short temperature3) {
        this.temperature3 = temperature3;
        Log.d("GourmetSensor", "set Temp3: "+temperature3);
        setGattTempValues();
    }

    public short getTemperature4() {
        return temperature4;
    }

    public void setTemperature4(short temperature4) {
        this.temperature4 = temperature4;
        Log.d("GourmetSensor", "set Temp4: "+temperature4);
        setGattTempValues();
    }

    public short getTemperature5() {
        return temperature5;
    }

    public void setTemperature5(short temperature5) {
        this.temperature5 = temperature5;
        Log.d("GourmetSensor", "set Temp5: "+temperature5);
        setGattTempValues();
    }

    public short getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(short batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
        setGattTempValues();
    }

    public short getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(short updateInterval) {
        this.updateInterval = updateInterval;
        Log.d("GourmetSensor", "set interval: "+updateInterval);
        gattConnector.setTempUpdateRateinSec(updateInterval);

        updateNotificationHandler.removeCallbacks(updateNotificationRunnable);
        if(updateInterval > 0) {
            updateNotificationHandler.postDelayed(updateNotificationRunnable, updateInterval*1000);
        }
    }

    public void notifyTempValues() {
        gattConnector.sendTempNotification();
    }

    private void setGattTempValues() {
        gattConnector.setTempValues(coldJunctionTemperature, temperature1, temperature2, temperature3, temperature4, temperature5, batteryVoltage);
    }

    public void clear() {
        gattConnector.clear();
        setUpdateInterval((short) 0);
    }

    public interface SensorUpdateIntervalListener {
        void setUpdateInterval(short updateInterval);
    }
}
