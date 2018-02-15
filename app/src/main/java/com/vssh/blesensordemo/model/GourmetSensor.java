package com.vssh.blesensordemo.model;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.vssh.blesensordemo.connectors.GattConnector;

/**
 * Created by varun on 10.02.18.
 */

public class GourmetSensor {
    private double coldJunctionTemperature = 30;
    private double temperature1 = 30;
    private double temperature2 = 30;
    private double temperature3 = 30;
    private double temperature4 = 30;
    private double temperature5 = 30;
    private short batteryVoltage = 0;
    // standard gatt characteristic measurement_interval (2a21) supports a min value of 1sec
    // less than that, it would be better to use a custom characteristic instead
    private short updateInterval = 1;

    private GattConnector gattConnector;
    private Handler updateNotificationHandler;
    private Runnable updateNotificationRunnable;

    private static final double[] TemperatureToVoltageNegativeLookupTable = {
            0.0,
            3.95e1,
            2.36e-2,
            -3.29e-4,
            -4.99e-6,
            -6.75e-8,
            -5.74e-10,
            -3.11e-12,
            -1.05e-14,
            -1.99e-17,
            -1.63e-20
    };

    private static final double[] TemperatureToVoltagePositiveLookupTable = {
            -1.76e1,
            3.89e1,
            1.86e-2,
            -9.95e-5,
            3.18e-7,
            5.61e-10,
            5.61e-13,
            -3.2e-16,
            9.72e-20,
            -1.21e-23
    };

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

    public double getColdJunctionTemperature() {
        return coldJunctionTemperature;
    }

    public void setColdJunctionTemperature(double coldJunctionTemperature) {
        this.coldJunctionTemperature = coldJunctionTemperature;
        Log.d("GourmetSensor", "set ColdJuncTemp: "+coldJunctionTemperature);
        setGattTempValues();
    }

    public double getTemperature1() {
        return temperature1;
    }

    public void setTemperature1(double temperature1) {
        this.temperature1 = temperature1;
        Log.d("GourmetSensor", "set Temp1: "+temperature1);
        setGattTempValues();
    }

    public double getTemperature2() {
        return temperature2;
    }

    public void setTemperature2(double temperature2) {
        this.temperature2 = temperature2;
        Log.d("GourmetSensor", "set Temp2: "+temperature2);
        setGattTempValues();
    }

    public double getTemperature3() {
        return temperature3;
    }

    public void setTemperature3(double temperature3) {
        this.temperature3 = temperature3;
        Log.d("GourmetSensor", "set Temp3: "+temperature3);
        setGattTempValues();
    }

    public double getTemperature4() {
        return temperature4;
    }

    public void setTemperature4(double temperature4) {
        this.temperature4 = temperature4;
        Log.d("GourmetSensor", "set Temp4: "+temperature4);
        setGattTempValues();
    }

    public double getTemperature5() {
        return temperature5;
    }

    public void setTemperature5(double temperature5) {
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
        short coldJuncTemp = getRawValFromTemp(coldJunctionTemperature*1000);
        double coldJunctionVoltage = convertToVoltage(coldJunctionTemperature);
        short temp1 = getRawValFromTemp(temperature1-coldJunctionVoltage);
        short temp2 = getRawValFromTemp(temperature2-coldJunctionVoltage);
        short temp3 = getRawValFromTemp(temperature3-coldJunctionVoltage);
        short temp4 = getRawValFromTemp(temperature4-coldJunctionVoltage);
        short temp5 = getRawValFromTemp(temperature5-coldJunctionVoltage);


        gattConnector.setTempValues(coldJuncTemp, temp1, temp2, temp3, temp4, temp5, batteryVoltage);
    }

    public void clear() {
        gattConnector.clear();
        setUpdateInterval((short) 0);
    }

    public interface SensorUpdateIntervalListener {
        void setUpdateInterval(short updateInterval);
    }


    /**
     * Simulate the voltages corresponding to the temperatures
     * @param temp input temperature
     * @return raw value
     */
    private short getRawValFromTemp(double temp) {
        short result = (short) (temp*(128.0/1000.0));

        // This is very probably not needed as I convert to little-endian byte order before sending
        result = Short.reverseBytes(result);

        return result;
    }

    /**
     Convert temperature to voltage.

     The function is used to convert the temperature back to voltage. Needed to
     compensate the thermocouple for the ambient temperature.

     - parameter temperature: Temperature in degree Celsius to be converted to voltage

     - returns: thermocouple voltage.
     */
    private double convertToVoltage(double temperature) {
        double ret = 0f;
        if(temperature < 0) {
            return convert(temperature, TemperatureToVoltageNegativeLookupTable);
        }
        ret = convert(temperature, TemperatureToVoltagePositiveLookupTable);
        ret += 1.19e2*Math.exp(Math.pow(temperature-126.9686, 2)*(-1.18e-4));
        return ret;
    }

    /**
     Helper function for voltage-to-temperature and temperature-to-voltage conversions.

     - parameter inputValue: Parameter to be converted (this really can be a temperature as well, as the conversion is just multiplying by the coeffcients in an array).
     - parameter lookupTable: Voltage-to-temperature (or the other way around) conversion array.
     - returns: Temperature for the voltage (or the voltagae for the temperature).
     */
    private double convert(double inputValue, double[] lookupTable) {
        double ret = 0f;
        double power = 1f;

        for (double tableVal : lookupTable) {
            ret += power * tableVal;
            power *= inputValue;
        }
        return ret;
    }
}
