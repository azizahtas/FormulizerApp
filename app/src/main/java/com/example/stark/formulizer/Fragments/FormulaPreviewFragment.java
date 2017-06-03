package com.example.stark.formulizer.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.renderscript.Double2;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.stark.formulizer.Listeners.OnNavigationBarListener;
import com.example.stark.formulizer.Models.FormulaModel;
import com.example.stark.formulizer.Models.TinterModel;
import com.example.stark.formulizer.R;
import com.example.stark.formulizer.Utilities.Constraints;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by aziz_ on 11-03-2017.
 */

public class FormulaPreviewFragment extends ButterKnifeFragment implements Step {

    private static final String LAYOUT_RESOURCE_ID_ARG_KEY = "messageResourceId";

    private String fname="",fcompany="",ftype="",fbase="",faccess="",fdesc="",fdate="";

    private FormulaModel newFormula;
    private Context context;
    @BindView(R.id.formula_preview_name) TextView fName;
    @BindView(R.id.formula_preview_company) TextView fCompany;
    @BindView(R.id.formula_preview_base) TextView fBase;
    @BindView(R.id.formula_preview_type) TextView fType;
    @BindView(R.id.formula_preview_access) TextView fAccess;
    @BindView(R.id.formula_preview_desc) TextView fDesc;
    @BindView(R.id.formula_preview_date) TextView fDate;
    @BindView(R.id.formula_preview_tinters) TableLayout tinterTable;

    @Nullable
    private OnNavigationBarListener OnNavigationBarListener;

    public static FormulaPreviewFragment newInstance(@LayoutRes int layoutResId) {
        Bundle args = new Bundle();
        args.putInt(LAYOUT_RESOURCE_ID_ARG_KEY, layoutResId);
        FormulaPreviewFragment fragment = new FormulaPreviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNavigationBarListener) {
            OnNavigationBarListener = (OnNavigationBarListener) context;
        }
        this.context = context;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState==null)System.out.println("yes It Is Null");
        else System.out.println("No its Not Null");
        if (savedInstanceState != null) {
            fname = savedInstanceState.getString(Constraints.FNAME_KEY);
            fcompany = savedInstanceState.getString(Constraints.FCOMPANY_KEY);
            fbase = savedInstanceState.getString(Constraints.FBASE_KEY);
            ftype = savedInstanceState.getString(Constraints.FTYPE_KEY);
            faccess = savedInstanceState.getString(Constraints.FACCESS_KEY);
            fdesc = savedInstanceState.getString(Constraints.FDESCRIPTION_KEY);
            fdate = savedInstanceState.getString(Constraints.FDATE_KEY);
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
      /*  boolean valid = false;
        valid = this.fName.getText().toString().isEmpty();
        return !valid;*/
        return true;
    }

    @Override
    public void onSelected() {
        updateNavigationBar();
    }

    @Override
    public void onError(@NonNull VerificationError error) {
        //fName.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake_error));
    }

    private void updateNavigationBar() {
        if (OnNavigationBarListener != null) {
            OnNavigationBarListener.onChangeEndButtonsEnabled(isEverythingValid());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Constraints.FNAME_KEY, fname);
        outState.putString(Constraints.FCOMPANY_KEY, fcompany);
        outState.putString(Constraints.FBASE_KEY, fbase);
        outState.putString(Constraints.FTYPE_KEY, ftype);
        outState.putString(Constraints.FACCESS_KEY, faccess);
        outState.putString(Constraints.FDESCRIPTION_KEY, fdesc);
        outState.putString(Constraints.FDATE_KEY, fdate);
        super.onSaveInstanceState(outState);
    }
    private void addRow(String T, double Q){
        TableRow tr = new TableRow(context);
        TextView tinter = new TextView(context);
        tinter.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.7f));
        tinter.setTextSize(18f);
        tinter.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        tinter.setText(T);

        TextView weight = new TextView(context);
        weight.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));
        weight.setTextSize(18f);
        weight.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        weight.setText(Q+"");

        tr.addView(tinter);
        tr.addView(weight);
        tr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        tinterTable.addView(tr);
    }
    public void updateData(FormulaModel newFormula){
        this.newFormula =newFormula;
        fName.setText(newFormula.getName());
        fCompany.setText(newFormula.getCompany());
        fBase.setText(newFormula.getBase());
        fType.setText(newFormula.getType());
        fAccess.setText(newFormula.getAccess());
        fDesc.setText(newFormula.getDesc());
        fDate.setText(newFormula.getDate());
        tinterTable.removeAllViews();
        for (int i = 0; i<newFormula.getTinters().size(); i++){
            addRow(newFormula.getTinters().get(i).getTinter(),newFormula.getTinters().get(i).getQty());
        }
    }


}