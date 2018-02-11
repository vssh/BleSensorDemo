package com.vssh.blesensordemo.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.vssh.blesensordemo.model.GourmetSensor;
import com.vssh.blesensordemo.R;
import com.vssh.blesensordemo.databinding.FragmentTemperatureSensorBinding;

/**
 * Created by varun on 11.02.18.
 */

public class SensorTemperaturesFragment extends Fragment {
    private FragmentTemperatureSensorBinding binding;
    private SensorTemperaturesViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_temperature_sensor, null, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(SensorTemperaturesViewModel.class);

        binding.updateInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    viewModel.setUpdateInterval((short) i);
                    binding.updateIntervalLabel.setText(getString(R.string.update_interval_seconds, i));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.coldJuncTemp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    viewModel.setColdJunctionTemp((short) i);
                    binding.coldJuncTempLabel.setText(getString(R.string.cold_junction_temperature, i));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.temp1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b) {
                    viewModel.setTemp1((short) i);
                    binding.temp1Label.setText(getString(R.string.temperature_1, i));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.temp2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b) {
                    viewModel.setTemp2((short) i);
                    binding.temp2Label.setText(getString(R.string.temperature_2, i));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.temp3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b) {
                    viewModel.setTemp3((short) i);
                    binding.temp3Label.setText(getString(R.string.temperature_3, i));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.temp4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b) {
                    viewModel.setTemp4((short) i);
                    binding.temp4Label.setText(getString(R.string.temperature_4, i));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.temp5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b) {
                    viewModel.setTemp5((short) i);
                    binding.temp5Label.setText(getString(R.string.temperature_5, i));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        viewModel.sensor.observe(this, new Observer<GourmetSensor>() {
            @Override
            public void onChanged(@Nullable GourmetSensor gourmetSensor) {
                binding.updateInterval.setProgress(gourmetSensor.getUpdateInterval());
                binding.updateIntervalLabel.setText(getString(R.string.update_interval_seconds, gourmetSensor.getUpdateInterval()));

                binding.coldJuncTemp.setProgress(gourmetSensor.getColdJunctionTemperature());
                binding.coldJuncTempLabel.setText(getString(R.string.cold_junction_temperature, gourmetSensor.getColdJunctionTemperature()));

                binding.temp1.setProgress(gourmetSensor.getTemperature1());
                binding.temp1Label.setText(getString(R.string.temperature_1, gourmetSensor.getTemperature1()));

                binding.temp2.setProgress(gourmetSensor.getTemperature2());
                binding.temp2Label.setText(getString(R.string.temperature_2, gourmetSensor.getTemperature2()));

                binding.temp3.setProgress(gourmetSensor.getTemperature3());
                binding.temp3Label.setText(getString(R.string.temperature_3, gourmetSensor.getTemperature3()));

                binding.temp4.setProgress(gourmetSensor.getTemperature4());
                binding.temp4Label.setText(getString(R.string.temperature_4, gourmetSensor.getTemperature4()));

                binding.temp5.setProgress(gourmetSensor.getTemperature5());
                binding.temp5Label.setText(getString(R.string.temperature_5, gourmetSensor.getTemperature5()));
            }
        });
    }
}
