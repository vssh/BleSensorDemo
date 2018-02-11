package com.vssh.blesensordemo.model;

/**
 * Created by varun on 11.02.18.
 */

public class Cooktop {
    // in Watts
    public int maxOutput = 2000;
    private int currentOutput = 0;

    // simulate losses in heat transfer from cooktop to food
    private float transferEfficiency = 0.7f;

    public int getCurrentOutput() {
        return currentOutput;
    }

    public void setCurrentOutput(int currentOutput) {
        this.currentOutput = currentOutput;
    }

    public float getTransferEfficiency() {
        return transferEfficiency;
    }

    public void setTransferEfficiency(float transferEfficiency) {
        this.transferEfficiency = transferEfficiency;
    }

    public int getOutputAfterLosses() {
        return (int) (currentOutput*transferEfficiency);
    }
}
