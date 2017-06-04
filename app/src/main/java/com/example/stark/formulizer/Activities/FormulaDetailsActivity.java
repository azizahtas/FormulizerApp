package com.example.stark.formulizer.Activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.stark.formulizer.Controllers.FormulizerClient;
import com.example.stark.formulizer.Models.FormulaModel;
import com.example.stark.formulizer.Models.GeneralResponseModel;
import com.example.stark.formulizer.Models.TinterModel;
import com.example.stark.formulizer.R;
import com.example.stark.formulizer.Services.FormulaService;
import com.example.stark.formulizer.Utilities.Constraints;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormulaDetailsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    Calendar now = Calendar.getInstance();
    DatePickerDialog dpd ;
    Gson gs;
    TextView totalValue;
    MaterialEditText totalTinter;
    ColorStateList state ;
    FormulaService formulaService;
    FormulizerClient formulizerClient;

    MaterialDialog.Builder confirmDialogBuilder;
    MaterialDialog.Builder progressDialogBuilder;
    MaterialDialog confirmDialog;
    MaterialDialog progressDialog;

    boolean formulaDisabled = true;
    boolean detailsDisabled = true;
    boolean IS_INITIAL = true;
    boolean NOT_INITIAL = false;


    FormulaModel selectedFormula = null;
    @BindView(R.id.formula_details_date) TextView formulaDate;
    @BindView(R.id.formula_details_name) MaterialEditText Name;
    @BindView(R.id.formula_details_base) MaterialBetterSpinner Base;
    @BindView(R.id.formula_details_company) MaterialBetterSpinner Company;
    @BindView(R.id.formula_details_type) MaterialBetterSpinner Type;
    @BindView(R.id.formula_details_access) Switch Access;
    @BindView(R.id.formula_details_desc) MaterialEditText Desc;
    @BindView(R.id.formula_details_owner_abbrivation) TextView OwnerAbbr;
    @BindView(R.id.formula_details_owner_name) TextView Owner;
    @BindView(R.id.formula_details_table) TableLayout tinterTable;
    @BindView(R.id.formula_details_addrow) Button AddRowButton;
    @BindView(R.id.formula_details_toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formula_details);
        ButterKnife.bind(this);
        state = getResources().getColorStateList(R.color.edit_text_selector);
        formulizerClient = new FormulizerClient(this);
        formulaService = formulizerClient.getClient().create(FormulaService.class);
        confirmDialogBuilder = new MaterialDialog.Builder(this)
                .title(R.string.dialog_delete_title)
                .content(R.string.dialog_delete_content)
                .positiveText(R.string.dialog_delete_positive)
                .negativeText(R.string.dialog_delete_negative);
        progressDialogBuilder = new MaterialDialog.Builder(this)
                .title(R.string.dialog_delete_progress_title)
                .content(R.string.dialog_delete_progress_content)
                .progress(true, 0);
        AddRowButton.setTextColor(state);


        setSupportActionBar(toolbar);
        gs = new Gson();
        Intent caller = getIntent();
        if(caller.hasExtra(Constraints.FORMULA_MODEL)){
            selectedFormula = gs.fromJson(caller.getStringExtra(Constraints.FORMULA_MODEL),FormulaModel.class);
        }
        else{
            showToast("Something Went wrong! Please Select Another Formula!");
            finish();
        }
        Access.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) Access.setText(getResources().getText(R.string.formula_Public));
                else Access.setText(getResources().getText(R.string.formula_Private));
            }
        });
        formulaDate.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        String[] comapnyList = {"Esdee","Nippon","Premila","Bilux"};
        ArrayAdapter<String> companyListAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, comapnyList);
        Company.setAdapter(companyListAdapter);

        String[] baseList = {"PU","FastSet","NC","Epoxy","Enamel"};
        ArrayAdapter<String> baseAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, baseList);
        Base.setAdapter(baseAdapter);

        String[] typeList = {"Custom","Standard"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, typeList);
        Type.setAdapter(typeAdapter);
        initData();
    }

    @OnClick(R.id.EditFormulaButton)
    public void EditFormulaClicked(){
        formulaDisabled = !formulaDisabled;
        disableFormula(formulaDisabled);
    }

    @OnClick(R.id.EditDetailsButton)
    public void EditDetailsClicked(){
        detailsDisabled = !detailsDisabled;
        disableDetails(detailsDisabled);
    }

    @OnClick(R.id.formula_details_addrow)
    public void addRowClicked(){
        TableRow lastRow ;
        int count = tinterTable.getChildCount();
        if(count > 0){
            lastRow = (TableRow) tinterTable.getChildAt(count-2);
            MaterialEditText tinter = (MaterialEditText) lastRow.getVirtualChildAt(0);
            MaterialEditText weight = (MaterialEditText) lastRow.getVirtualChildAt(1);
            if(tinter.getText().toString().isEmpty()){
                tinter.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
                tinter.setError(getResources().getText(R.string.formula_tinter_error));
            }
            else if(weight.getText().toString().isEmpty()){
                weight.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
                weight.setError(getResources().getText(R.string.formula_weight_error));
            }
            else addRow("","",count-1,NOT_INITIAL);
        }
        else{
            addRow("","",-1,NOT_INITIAL);
        }
        convertToOneLiter();
    }

    @OnClick(R.id.formula_details_save_action)
    public void Save(){
        if(isTintersValid() && isDetailsValid())
        {
           initialiseDataToBeSaved();
            Call<GeneralResponseModel<String>> call = formulaService.saveFormula(selectedFormula.getId(),selectedFormula);
            call.enqueue(new Callback<GeneralResponseModel<String>>() {
                @Override
                public void onResponse(Call<GeneralResponseModel<String>> call, Response<GeneralResponseModel<String>> response) {
                    if (response.body().isSuccess()) {
                        showToast(getResources().getString(R.string.formula_save_success));
                        finish();
                    }
                    else{
                        showToast(getResources().getString(R.string.formula_save_error));
                    }
                }

                @Override
                public void onFailure(Call<GeneralResponseModel<String>> call, Throwable t) {
                    showToast(getResources().getString(R.string.error_server_offline));
                }
            });
        }
    }

    @OnClick(R.id.formula_details_delete_action)
    public void Delete(){
        confirmDialogBuilder
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        progressDialog = progressDialogBuilder.build();
                        progressDialog.show();
                        Call<GeneralResponseModel<String>> call = formulaService.deleteFormula(selectedFormula.getId());
                        call.enqueue(new Callback<GeneralResponseModel<String>>() {
                            @Override
                            public void onResponse(Call<GeneralResponseModel<String>> call, Response<GeneralResponseModel<String>> response) {
                                if(response.isSuccessful()){
                                    showToast(getResources().getString(R.string.formula_delete_success));
                                    progressDialog.dismiss();
                                    finish();
                                }
                                else{
                                    showToast(getResources().getString(R.string.formula_delete_error));
                                    progressDialog.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(Call<GeneralResponseModel<String>> call, Throwable t) {
                                showToast(getResources().getString(R.string.error_server_offline));
                                progressDialog.dismiss();
                            }
                        });
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                });
        confirmDialog = confirmDialogBuilder.build();
        confirmDialog.show();
    }

    private boolean isDetailsValid(){
        boolean valid;
        if (Name.getText().toString().isEmpty()) {
            Name.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            Name.setError(getResources().getText(R.string.formula_name_error));
            valid = false;
        }
        else if(Company.getText().toString().isEmpty()){
            Company.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            Company.setError(getResources().getText(R.string.formula_company_error));
            valid = false;
        }
        else if(Base.getText().toString().isEmpty()){
            Base.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            Base.setError(getResources().getText(R.string.formula_base_error));
            valid = false;
        }
        else if(Type.getText().toString().isEmpty()){
            Type.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            Type.setError(getResources().getText(R.string.formula_type_error));
            valid = false;
        }
        else {
            valid = true;
        }
        return valid;
    }

    private boolean isTintersValid(){
        boolean valid = true;
        int count = tinterTable.getChildCount();
        if(count<=0){
            addRow("","",0,IS_INITIAL);
            return false;
        }
        else if(count>0){
            TableRow lastRow = (TableRow) tinterTable.getChildAt(count-2);
            MaterialEditText tinter = (MaterialEditText) lastRow.getVirtualChildAt(0);
            MaterialEditText weight = (MaterialEditText) lastRow.getVirtualChildAt(1);
            if(tinter.getText().toString().isEmpty() && weight.getText().toString().isEmpty() && count >1)
            {
                tinterTable.removeView(lastRow);
                return true;
            }
            if(tinter.getText().toString().isEmpty()){
                tinter.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
                tinter.setError(getResources().getText(R.string.formula_tinter_error));
                valid = false;
            }
            else if(weight.getText().toString().isEmpty()){
                weight.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
                weight.setError(getResources().getText(R.string.formula_weight_error));
                valid = false;
            }
            else valid = true;
        }
        return valid;
    }

    public void initData(){
        if(this.selectedFormula != null){
            DateTime myDate = new DateTime(selectedFormula.getDate());
            dpd = DatePickerDialog.newInstance(
                    FormulaDetailsActivity.this,
                    myDate.getYear(),
                    myDate.getMonthOfYear(),
                    myDate.getDayOfMonth()
            );

            OwnerAbbr.setText(String.valueOf(selectedFormula.getUserName().charAt(0)));
            Owner.setText(selectedFormula.getUserName());
            Name.setText(selectedFormula.getName());
            toolbar.setTitle(selectedFormula.getName());
            Base.setText(selectedFormula.getBase());
            Company.setText(selectedFormula.getCompany());
            Type.setText(selectedFormula.getType().equals("S")?"Standard":"Custom");
            formulaDate.setText(myDate.getMonthOfYear()+"/"+myDate.getDayOfMonth()+"/"+myDate.getYear());
            if(selectedFormula.getAccess().equals(getResources().getString(R.string.formula_Public))){
                Access.setChecked(true);
                Access.setText(getResources().getString(R.string.formula_Public));
            }
            else{
                Access.setChecked(false);
                Access.setText(getResources().getString(R.string.formula_Private));
            }
            Desc.setText(selectedFormula.getDesc());
            for (int i=0;i<selectedFormula.getTintersCount();i++){
                double total =selectedFormula.getTinters().get(i).getQty();
                addRow(selectedFormula.getTinters().get(i).getTinter(),String.format(Locale.ENGLISH,"%.2f",total),i,IS_INITIAL);
            }
            addTotalRow();
            disableFormula(true);
            disableDetails(true);

        }
        else showToast(getResources().getString(R.string.formula_select_error));
    }
    private void initialiseDataToBeSaved(){
        selectedFormula.setName(Name.getText().toString());
        selectedFormula.setBase(Base.getText().toString());
        selectedFormula.setCompany(Company.getText().toString());
        selectedFormula.setType(Type.getText().toString());
        selectedFormula.setDate(formulaDate.getText().toString());
        selectedFormula.setAccess(Access.getText().toString());
        selectedFormula.setDesc(Desc.getText().toString());

        List<TinterModel> newTinters = new ArrayList<>(tinterTable.getChildCount());
        double sum = 0;
        convertToOneLiter();
        for (int i=0;i<tinterTable.getChildCount()-1;i++){
            TableRow row = (TableRow) tinterTable.getChildAt(i);
            MaterialEditText t = (MaterialEditText) row.getVirtualChildAt(0);
            MaterialEditText q = (MaterialEditText) row.getVirtualChildAt(1);
            double qval = Double.parseDouble(q.getText().toString());
            newTinters.add(new TinterModel(t.getText().toString(),qval,null));
            sum += qval;
        }
        selectedFormula.setTinters(newTinters);
        selectedFormula.settWeight(sum);
        totalValue.setText(String.format(Locale.ENGLISH,"%.2f",sum));
    }
    private void convertToOneLiter(){
        calculateTotal(1000);
    }

    private void addRow(String T, String W, final int index, boolean Initial ){
        TableRow tr = new TableRow(this);

        MaterialEditText tinter = new MaterialEditText(this);
        tinter.setHint(getResources().getString(R.string.formula_Tinter));
        tinter.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f));
        tinter.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        tinter.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        tinter.setTextColor(state);

        MaterialEditText weight = new MaterialEditText(this);
        weight.setHint(getResources().getString(R.string.formula_Weight));
        weight.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.4f));
        weight.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        weight.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        weight.setTextColor(state);

        final ImageButton delete = new ImageButton(this);
        delete.setBackground(getResources().getDrawable(R.drawable.ic_delete_black_24dp));
        delete.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.1f));
        delete.setId(index);
        delete.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                View row =(View) v.getParent();
                ViewGroup con = (ViewGroup) row.getParent();
                con.removeView(row);
                con.invalidate();
                if(selectedFormula.getTintersCount()-1 <= v.getId()){
                    selectedFormula.removeTinterAt(v.getId());
                }
            }
            });
        tinter.setText(T);
        weight.setText(W);
        tr.addView(tinter);
        tr.addView(weight);
        tr.addView(delete);
        tr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if(!Initial){
            TinterModel newTinter = new TinterModel();
            addRowInSelectedFormula(newTinter);
            tinterTable.addView(tr,tinterTable.getChildCount()-1);
        }
        else{
            tinterTable.addView(tr);
        }
    }

    private void addRowInSelectedFormula(TinterModel model){
        selectedFormula.addTinter(model);
    }
    private void addTotalRow(){
        TableRow tr = new TableRow(this);

        TextView totalLable = new TextView(this);
        totalLable.setText(getResources().getString(R.string.formula_label_total));
        totalLable.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));
        totalLable.setTextSize(20);
        totalLable.setTextColor(state);

        totalValue = new TextView(this);
        totalValue.setText(getResources().getString(R.string.formula_label_total));
        totalValue.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));
        totalValue.setTextSize(20);
        totalValue.setTextColor(state);

        totalTinter = new MaterialEditText(this);
        totalTinter.setHint("Formula For");
        totalTinter.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));
        totalTinter.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        totalTinter.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        totalTinter.setTextColor(state);

        ImageButton show = new ImageButton(this);
        show.setBackground(getResources().getDrawable(R.drawable.ic_clear_white_18dp));
        show.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.1f));
        show.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateTotal(Double.parseDouble(totalTinter.getText().toString()));
            }
        });

        calculateTotal(0);
        tr.addView(totalLable);
        tr.addView(totalValue);
        tr.addView(totalTinter);
        tr.addView(show);
        tinterTable.addView(tr);

    }
    private void calculateTotal(double value){
        double sum = 0;
        if(value < 1) {
            for (int i = 0; i < selectedFormula.getTintersCount(); i++) {
                sum += selectedFormula.getTinters().get(i).getQty();
            }
        }
        else if(value >= 1){
            for (int i = 0; i < selectedFormula.getTintersCount(); i++) {
                TableRow row = (TableRow) tinterTable.getChildAt(i);
                try {
                    MaterialEditText qty = (MaterialEditText) row.getVirtualChildAt(1);
                    double qtyVal = selectedFormula.getTinters().get(i).getQty();
                    double total = ((qtyVal / 1000) * value);
                    qty.setText(String.format(Locale.ENGLISH, "%.2f", total));
                    sum += total;
                }
                catch (ClassCastException ex){
                    Log.e("Exception Class", row.getVirtualChildAt(1).getClass().toString() + "Value Of I ="+i);
                }
            }
        }
        totalValue.setText(String.format(Locale.ENGLISH,"%.2f",sum));
        totalTinter.setText(String.format(Locale.ENGLISH,"%.2f",sum));
    }
    private void disableDetails(boolean b){
        Name.setEnabled(!b);
        Base.setEnabled(!b);
        Base.setClickable(!b);
        Company.setEnabled(!b);
        Company.setClickable(!b);
        Type.setEnabled(!b);
        Type.setClickable(!b);
        Access.setEnabled(!b);
        Desc.setEnabled(!b);
        formulaDate.setEnabled(!b);
    }
    private void disableFormula(boolean b){
        for(int i=0;i<tinterTable.getChildCount();i++){
            TableRow row = (TableRow) tinterTable.getChildAt(i);
            row.getVirtualChildAt(0).setEnabled(!b);
            row.getVirtualChildAt(1).setEnabled(!b);
            row.getVirtualChildAt(2).setEnabled(!b);
        }
        AddRowButton.setEnabled(!b);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
       formulaDate.setText(monthOfYear+"/"+dayOfMonth+"/"+year);
    }

    private void showToast(String message) {
        Toast t = Toast.makeText(FormulaDetailsActivity.this,message,Toast.LENGTH_LONG);
        t.show();
    }
}
