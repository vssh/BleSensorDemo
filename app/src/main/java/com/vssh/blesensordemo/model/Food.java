package com.vssh.blesensordemo.model;

import android.os.Handler;

/**
 * Created by varun on 11.02.18.
 */

public class Food {
    // in Kg
    private float weight = 0;
    // in C
    private float temperature = 20;
    // specific heat capacity assume same as water
    private int heatCap = 4200;
    // assumed value; would depend on conductivity, volume, surface area and relative conductivities of media
    private int heatLossCofficient = 4;
    // in seconds
    private int calculationInterval = 1;
    // in C
    private int ambientTemperature = 20;

    private GourmetSensor sensor;
    private Handler calcHandler;
    private Runnable calcRunnable;

    private HeatTransferInterface heatTransferInterface;

    public Food(float weight, int temperature, HeatTransferInterface heatTransferInterface, GourmetSensor sensor) {
        this.weight = weight;
        this.temperature = temperature;
        this.heatTransferInterface = heatTransferInterface;
        this.sensor = sensor;
        calcHandler = new Handler();
        calcRunnable = new Runnable() {
            @Override
            public void run() {
                calculateNewTemp();
                calcHandler.postDelayed(calcRunnable, calculationInterval*1000);
            }
        };

        sensor.setColdJunctionTemperature((short) 30);

        calcHandler.postDelayed(calcRunnable, calculationInterval*1000);
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void calculateNewTemp() {
        // all calculations in Joules as interval is 1 sec

        // get input energy from cooktop
        double inputEnergy = (double) heatTransferInterface.getInputHeat();
        // get energy lost to environment
        double energyLoss = heatLossCofficient*(temperature-ambientTemperature);

        double deltaEnergy = inputEnergy - energyLoss;

        // get change in temperature
        float deltaTemp = (float) deltaEnergy/(weight*heatCap);

        float newTemp = temperature + deltaTemp;

        heatTransferInterface.setNewTemp(newTemp);

        temperature = newTemp;

        // set all temperatures the same. small variations can be simulated easily with randomizing a small added component
        sensor.setTemperature1((short) temperature);
        sensor.setTemperature2((short) temperature);
        sensor.setTemperature3((short) temperature);
        sensor.setTemperature4((short) temperature);
        sensor.setTemperature5((short) temperature);

    }

    public void clear() {
        calcHandler.removeCallbacks(calcRunnable);
    }

    public interface HeatTransferInterface {
        int getInputHeat();
        void setNewTemp(float temperature);
    }
}
