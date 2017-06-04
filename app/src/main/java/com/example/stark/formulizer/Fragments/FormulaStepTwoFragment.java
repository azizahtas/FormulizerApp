package com.example.stark.formulizer.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.stark.formulizer.Listeners.OnFormulaAddNextStepListener;
import com.example.stark.formulizer.Listeners.OnNavigationBarListener;
import com.example.stark.formulizer.Models.FormulaModel;
import com.example.stark.formulizer.Models.TinterModel;
import com.example.stark.formulizer.R;
import com.example.stark.formulizer.Utilities.Constraints;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Stark on 06-03-2017.
 */

public class FormulaStepTwoFragment extends ButterKnifeFragment implements BlockingStep {

    private static final String LAYOUT_RESOURCE_ID_ARG_KEY = "messageResourceId";

    private String fname="",fcompany="",fdescription = "",ftype="",fbase="",faccess="";
    boolean firstTime = true;
    FormulaModel model;
    @BindView(R.id.formula_add_tinter_table)
    TableLayout tinterTable;
    int idx = 1;
    Context context;
    /*@BindView(R.id.formula_add_company)
    MaterialBetterSpinner fCompany;*/

    @Nullable
    private com.example.stark.formulizer.Listeners.OnNavigationBarListener OnNavigationBarListener;
    private com.example.stark.formulizer.Listeners.OnFormulaAddNextStepListener OnFormulaAddNextStepListener;

    public static FormulaStepTwoFragment newInstance(@LayoutRes int layoutResId) {
        Bundle args = new Bundle();
        args.putInt(LAYOUT_RESOURCE_ID_ARG_KEY, layoutResId);
        FormulaStepTwoFragment fragment = new FormulaStepTwoFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @OnClick(R.id.formula_add_tinter_fab)
    public void addRowClicked(){
        TableRow lastRow ;
        int count = tinterTable.getChildCount();
        if(count >0){
            lastRow = (TableRow) tinterTable.getChildAt(count-1);
            MaterialEditText tinter = (MaterialEditText) lastRow.getVirtualChildAt(0);
            MaterialEditText weight = (MaterialEditText) lastRow.getVirtualChildAt(1);
            if(tinter.getText().toString().isEmpty()){
                tinter.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake_error));
                tinter.setError(getContext().getResources().getText(R.string.formula_tinter_error));
            }
            else if(weight.getText().toString().isEmpty()){
                weight.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake_error));
                weight.setError(getContext().getResources().getText(R.string.formula_weight_error));
            }
            else addRow();
        }
        else{
            addRow();
        }
    }

    private void addRow(){
        TableRow tr = new TableRow(context);
        MaterialEditText tinter = new MaterialEditText(context);
        tinter.setHint("Tinter");
        tinter.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.51f));
        tinter.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        tinter.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        MaterialEditText weight = new MaterialEditText(context);
        weight.setHint("Weight");
        weight.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.41f));
        weight.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        weight.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);

        ImageButton delete = new ImageButton(context);
        delete.setBackground(getResources().getDrawable(R.drawable.ic_delete_black_24dp));
        delete.setDrawingCacheBackgroundColor(Color.RED);
        delete.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.08f));
        //delete.setId(tinterTable.getChildCount());
        Toast.makeText(context,"childrenCount "+tinterTable.getChildCount(),Toast.LENGTH_SHORT).show();
        delete.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                View row =(View) v.getParent();
                ViewGroup con = (ViewGroup) row.getParent();
                con.removeView(row);
                con.invalidate();
            }
        });
        tr.addView(tinter);
        tr.addView(weight);
        tr.addView(delete);
        tr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        tinterTable.addView(tr);
        idx++;
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
        this.context = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
        }
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
        boolean valid = true;
        int count = tinterTable.getChildCount();
        if(count<=0){
            addRow();
            return false;
        }
        else if(count>0){
            TableRow lastRow = (TableRow) tinterTable.getChildAt(count-1);
            MaterialEditText tinter = (MaterialEditText) lastRow.getVirtualChildAt(0);
            MaterialEditText weight = (MaterialEditText) lastRow.getVirtualChildAt(1);
            if(tinter.getText().toString().isEmpty() && weight.getText().toString().isEmpty() && count >1)
            {
                tinterTable.removeView(lastRow);
                return true;
            }
            if(tinter.getText().toString().isEmpty()){
                tinter.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake_error));
                tinter.setError(getContext().getResources().getText(R.string.formula_tinter_error));
                valid = false;
            }
            else if(weight.getText().toString().isEmpty()){
                weight.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake_error));
                weight.setError(getContext().getResources().getText(R.string.formula_weight_error));
                valid = false;
            }
            else valid = true;
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
        List<TinterModel> tinterModels = new ArrayList<>(tinterTable.getChildCount());
        for (int i=0; i <tinterTable.getChildCount(); i++){
            TableRow row = (TableRow) tinterTable.getChildAt(i);
            MaterialEditText t = (MaterialEditText) row.getVirtualChildAt(0);
            MaterialEditText w = (MaterialEditText) row.getVirtualChildAt(1);
            TinterModel tmodel = new TinterModel();
            tmodel.setTinter(t.getText().toString());
            tmodel.setQty(Double.parseDouble(w.getText().toString()));
            tinterModels.add(tmodel);
        }
        model.setTinters(tinterModels);
        updateDataForNextStep(model);
        callback.goToNextStep();
    }
    public void updateData(FormulaModel newFormula) {
        this.model = newFormula;
    }
}
