package com.vssh.blesensordemo.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.vssh.blesensordemo.model.GourmetSensor;

/**
 * Created by varun on 11.02.18.
 */

public class SensorTemperaturesViewModel extends AndroidViewModel {
    MutableLiveData<GourmetSensor> sensor;


    public SensorTemperaturesViewModel(@NonNull Application application) {
        super(application);

        sensor = new MutableLiveData<>();
        sensor.setValue(new GourmetSensor(this.getApplication()));
    }

    void setUpdateInterval(short interval) {
        sensor.getValue().setUpdateInterval(interval);
    }

    void setColdJunctionTemp(double temp) {
        sensor.getValue().setColdJunctionTemperature(temp);
    }

    void setTemp1(double temp) {
        sensor.getValue().setTemperature1(temp);
    }

    void setTemp2(double temp) {
        sensor.getValue().setTemperature2(temp);
    }

    void setTemp3(double temp) {
        sensor.getValue().setTemperature3(temp);
    }

    void setTemp4(double temp) {
        sensor.getValue().setTemperature4(temp);
    }

    void setTemp5(double temp) {
        sensor.getValue().setTemperature5(temp);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        sensor.getValue().clear();
    }
}
