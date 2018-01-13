package com.bosphere.stepsliderdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bosphere.stepslider.OnSliderPositionChangeListener;
import com.bosphere.stepslider.StepSlider;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StepSlider slider = findViewById(R.id.slider);
        slider.setOnSliderPositionChangeListener(new OnSliderPositionChangeListener() {
            @Override
            public void onPositionChanged(int position) {
                // catch user movement here
            }
        });
    }
}
