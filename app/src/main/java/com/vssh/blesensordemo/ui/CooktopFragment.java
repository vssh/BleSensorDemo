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

import com.vssh.blesensordemo.R;
import com.vssh.blesensordemo.databinding.FragmentCooktopBinding;

/**
 * Created by varun on 11.02.18.
 */

public class CooktopFragment extends Fragment {
    private FragmentCooktopBinding binding;
    private CooktopViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cooktop, null, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(CooktopViewModel.class);

        binding.powerLabel.setText(getString(R.string.cooktop_power, viewModel.cooktop.getCurrentOutput()));
        binding.weightLabel.setText(getString(R.string.food_weight, viewModel.food.getWeight()));

        binding.weight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                viewModel.food.setWeight(((float) i/1000));
                binding.weightLabel.setText(getString(R.string.food_weight, ((float) i/1000)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.power.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                viewModel.cooktop.setCurrentOutput(i);
                binding.powerLabel.setText(getString(R.string.cooktop_power, i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        viewModel.foodTemperature.observe(this, new Observer<Float>() {
            @Override
            public void onChanged(@Nullable Float aFloat) {
                binding.temp.setText(Float.toString(aFloat));
            }
        });
    }
}
