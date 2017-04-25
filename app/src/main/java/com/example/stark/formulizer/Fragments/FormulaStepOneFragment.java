package com.example.stark.formulizer.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.stark.formulizer.Listeners.OnFormulaAddNextStepListener;
import com.example.stark.formulizer.Listeners.OnNavigationBarListener;
import com.example.stark.formulizer.Models.FormulaModel;
import com.example.stark.formulizer.Models.SharingTest;
import com.example.stark.formulizer.R;
import com.example.stark.formulizer.Utilities.Constraints;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by Stark on 06-03-2017.
 */

public class FormulaStepOneFragment extends ButterKnifeFragment implements BlockingStep {

    private static final String LAYOUT_RESOURCE_ID_ARG_KEY = "messageResourceId";

    private String fname="",fcompany="",fdescription = "",ftype="",fbase="",faccess="";
    boolean firstTime = true;
    FormulaModel model = new FormulaModel();
    @BindView(R.id.formula_add_name) MaterialEditText fName;
    @BindView(R.id.formula_add_company) MaterialBetterSpinner fCompany;
    @BindView(R.id.formula_add_base) MaterialBetterSpinner fBase;
    @BindView(R.id.formula_add_type) MaterialBetterSpinner fType;
    @BindView(R.id.formula_add_access) Switch fAccess;
    @BindView(R.id.formula_add_description) MaterialEditText fDescription;

    @Nullable
    private OnNavigationBarListener OnNavigationBarListener;
    private OnFormulaAddNextStepListener OnFormulaAddNextStepListener;

    public static FormulaStepOneFragment newInstance(@LayoutRes int layoutResId) {
        Bundle args = new Bundle();
        args.putInt(LAYOUT_RESOURCE_ID_ARG_KEY, layoutResId);
        FormulaStepOneFragment fragment = new FormulaStepOneFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNavigationBarListener) {
            OnNavigationBarListener = (OnNavigationBarListener) context;
        }
        if(context instanceof OnFormulaAddNextStepListener){
            OnFormulaAddNextStepListener = (OnFormulaAddNextStepListener) context;
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String[] comapnyList = {"Esdee","Nippon","Premila","Bilux"};
        ArrayAdapter<String> companyListAdapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_dropdown_item_1line, comapnyList);
        fCompany.setAdapter(companyListAdapter);

        String[] baseList = {"PU","FastSet","NC","Epoxy","Enamel"};
        ArrayAdapter<String> baseAdapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_dropdown_item_1line, baseList);
        fBase.setAdapter(baseAdapter);

        String[] typeList = {"Custom","Standard"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_dropdown_item_1line, typeList);
        fType.setAdapter(typeAdapter);
        fAccess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) fAccess.setText(getContext().getResources().getText(R.string.formula_Public));
                else fAccess.setText(getContext().getResources().getText(R.string.formula_Private));
            }
        });
        if(fAccess.isChecked()) fAccess.setText(getContext().getResources().getText(R.string.formula_Public));
        else fAccess.setText(getContext().getResources().getText(R.string.formula_Private));
        if (savedInstanceState != null) {
            fname = savedInstanceState.getString(Constraints.FNAME_KEY);
            fcompany = savedInstanceState.getString(Constraints.FCOMPANY_KEY);
            fbase = savedInstanceState.getString(Constraints.FBASE_KEY);
            ftype = savedInstanceState.getString(Constraints.FTYPE_KEY);
            faccess = savedInstanceState.getString(Constraints.FACCESS_KEY);
            fdescription = savedInstanceState.getString(Constraints.FDESCRIPTION_KEY);
            //fCompany.setSelection(companyListAdapter.getPosition(fcompany));
        }
        fName.setText("Silkey Silver");
        fCompany.setText(comapnyList[0]);
        fBase.setText(baseList[0]);
        fType.setText(typeList[0]);
        fDescription.setText("Demo Description!");
        updateNavigationBar();
    }

    @Override
    protected int getLayoutResId() {
        return getArguments().getInt(LAYOUT_RESOURCE_ID_ARG_KEY);
    }

    @Override
    public VerificationError verifyStep() {
        return isEverythingValid() ? null : new VerificationError("Please Enter All Details ");
    }

    private boolean isEverythingValid() {
        boolean valid;
            if (fName.getText().toString().isEmpty()) {
                fName.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake_error));
                fName.setError(getContext().getResources().getText(R.string.formula_name_error));
                valid = false;
            }
            else if(fCompany.getText().toString().isEmpty()){
                fCompany.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake_error));
                fCompany.setError(getContext().getResources().getText(R.string.formula_company_error));
                valid = false;
            }
            else if(fBase.getText().toString().isEmpty()){
                fBase.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake_error));
                fBase.setError(getContext().getResources().getText(R.string.formula_base_error));
                valid = false;
            }
            else if(fType.getText().toString().isEmpty()){
                fType.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake_error));
                fType.setError(getContext().getResources().getText(R.string.formula_type_error));
                valid = false;
            }
            else {
                valid = true;
            }
        return valid;
    }

    @Override
    public void onSelected() {
        updateNavigationBar();
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }


    private void updateNavigationBar() {
        if (OnNavigationBarListener != null) {
            OnNavigationBarListener.onChangeEndButtonsEnabled(isEverythingValid());
        }
    }

    private void updateDataForNextStep(FormulaModel newFormula){
        if(OnFormulaAddNextStepListener != null){
            OnFormulaAddNextStepListener.onFormulaAddNextStep(newFormula);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Constraints.FNAME_KEY, fname);
        outState.putString(Constraints.FCOMPANY_KEY, fcompany);
        outState.putString(Constraints.FBASE_KEY, fbase);
        outState.putString(Constraints.FTYPE_KEY, ftype);
        outState.putString(Constraints.FACCESS_KEY, faccess);
        outState.putString(Constraints.FDESCRIPTION_KEY, fdescription);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        firstTime = false;
        isEverythingValid();
        model.setName(fName.getText().toString());
        model.setCompany(fCompany.getText().toString());
        model.setBase(fBase.getText().toString());
        model.setType(fType.getText().toString());
        model.setAccess(fAccess.getText().toString());
        model.setDesc(fDescription.getText().toString());

        updateDataForNextStep(model);
        callback.goToNextStep();
    }
}
