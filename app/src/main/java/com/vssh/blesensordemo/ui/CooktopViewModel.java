package com.vssh.blesensordemo.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.vssh.blesensordemo.model.Cooktop;
import com.vssh.blesensordemo.model.Food;
import com.vssh.blesensordemo.model.GourmetSensor;

/**
 * Created by varun on 11.02.18.
 */

public class CooktopViewModel extends AndroidViewModel{
    private GourmetSensor sensor;
    Food food;
    Cooktop cooktop;
    MutableLiveData<Float> foodTemperature = new MutableLiveData<>();

    public CooktopViewModel(@NonNull Application application) {
        super(application);

        cooktop = new Cooktop();
        sensor = new GourmetSensor(this.getApplication());
        foodTemperature.setValue(0f);
        food = new Food(0.5f, 30, new Food.HeatTransferInterface() {
            @Override
            public int getInputHeat() {
                return cooktop.getOutputAfterLosses();
            }

            @Override
            public void setNewTemp(float temperature) {
                foodTemperature.setValue(temperature);
            }
        }, sensor);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        food.clear();
        sensor.clear();
    }
}
