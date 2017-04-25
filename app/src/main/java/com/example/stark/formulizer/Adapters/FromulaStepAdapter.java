package com.example.stark.formulizer.Adapters;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.example.stark.formulizer.Fragments.FormulaPreviewFragment;
import com.example.stark.formulizer.Fragments.FormulaStepOneFragment;
import com.example.stark.formulizer.Fragments.FormulaStepTwoFragment;
import com.example.stark.formulizer.R;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

public class FromulaStepAdapter extends AbstractFragmentStepAdapter {
    public FromulaStepAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        //Override this method to set Step title for the Tabs, not necessary for other stepper types
        return new StepViewModel.Builder(context)
                .setTitle(R.string.formula_stepOne_title)
                .create();
    }

    @Override
    public Step createStep(int position) {
        switch (position) {
            case 0:
                return FormulaStepOneFragment.newInstance(R.layout.formula_step_one_fragment);
            case 1:
                return FormulaStepTwoFragment.newInstance(R.layout.formula_step_two_fragment);
            case 2:
                return FormulaPreviewFragment.newInstance(R.layout.formula_step_preview_fragment);
            default:
                throw new IllegalArgumentException("Unsupported position: " + position);
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}