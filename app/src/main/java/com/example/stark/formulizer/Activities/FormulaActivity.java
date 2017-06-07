package com.example.stark.formulizer.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stark.formulizer.Models.FormulaModel;
import com.example.stark.formulizer.Models.TinterModel;
import com.example.stark.formulizer.R;
import com.example.stark.formulizer.Utilities.Constraints;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FormulaActivity extends AppCompatActivity {

    Gson gs;
    FormulaModel selectedFormula;
    TextView totalValue;

    @BindView(R.id.formula_details_table) TableLayout tinterTable;
    @BindView(R.id.formula_details_toolbar) Toolbar toolbar;
    @BindView(R.id.formula_weight_calc) MaterialEditText totalTinter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formula);
        ButterKnife.bind(this);
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
        initData();
    }
    @OnClick(R.id.formula_calculate)
    public void Calculate(){
        calculateTotal(Double.parseDouble(totalTinter.getText().toString()));
    }

    public void initData(){
        if(this.selectedFormula != null){
            toolbar.setTitle(selectedFormula.getName());
            for (int i=0;i<selectedFormula.getTintersCount();i++){
                double total =selectedFormula.getTinters().get(i).getQty();
                addRow(selectedFormula.getTinters().get(i).getTinter(),String.format(Locale.ENGLISH,"%.2f",total));
            }
            addTotalRow();
        }
        else showToast(getResources().getString(R.string.formula_select_error));
    }
    private void addRow(String T, String W){
        TableRow tr = new TableRow(this);

        MaterialEditText tinter = new MaterialEditText(this);
        tinter.setHint(getResources().getString(R.string.formula_Tinter));
        tinter.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f));
        tinter.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        tinter.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        tinter.setTextColor(getResources().getColor(R.color.cpb_white));
        tinter.setEnabled(false);

        MaterialEditText weight = new MaterialEditText(this);
        weight.setHint(getResources().getString(R.string.formula_Weight));
        weight.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.4f));
        weight.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        weight.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        weight.setTextColor(getResources().getColor(R.color.cpb_white));
        weight.setEnabled(false);

        tinter.setText(T);
        weight.setText(W);
        tr.addView(tinter);
        tr.addView(weight);
        tr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        tinterTable.addView(tr);
    }
    private void addTotalRow(){
        TableRow tr = new TableRow(this);

        TextView totalLable = new TextView(this);
        totalLable.setText(getResources().getString(R.string.formula_label_total));
        totalLable.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));
        totalLable.setTextSize(20);
        totalLable.setTextColor(getResources().getColor(R.color.cpb_white));

        totalValue = new TextView(this);
        totalValue.setText(getResources().getString(R.string.formula_label_total));
        totalValue.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));
        totalValue.setTextSize(20);
        totalValue.setTextColor(getResources().getColor(R.color.cpb_white));

        calculateTotal(0);
        tr.addView(totalLable);
        tr.addView(totalValue);
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
                    Log.e("Exception Class", row.getVirtualChildAt(1).getClass().toString() + "Value Of I = "+i);
                }
            }
        }
        totalValue.setText(String.format(Locale.ENGLISH,"%.2f",sum));
        totalTinter.setText(String.format(Locale.ENGLISH,"%.2f",sum));
    }
    private void showToast(String message) {
        Toast t = Toast.makeText(FormulaActivity.this,message,Toast.LENGTH_LONG);
        t.show();
    }
}
