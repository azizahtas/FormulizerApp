package com.example.stark.formulizer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.stark.formulizer.Adapters.FromulaStepAdapter;
import com.example.stark.formulizer.Controllers.FormulizerClient;
import com.example.stark.formulizer.Fragments.FormulaPreviewFragment;
import com.example.stark.formulizer.Fragments.FormulaStepTwoFragment;
import com.example.stark.formulizer.Listeners.OnFormulaAddNextStepListener;
import com.example.stark.formulizer.Listeners.OnNavigationBarListener;
import com.example.stark.formulizer.Models.FormulaModel;
import com.example.stark.formulizer.Models.GeneralResponseModel;
import com.example.stark.formulizer.R;
import com.example.stark.formulizer.Services.FormulaService;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFormula extends AppCompatActivity implements OnFormulaAddNextStepListener,StepperLayout.StepperListener,OnNavigationBarListener {

    protected FormulaModel newFormula;
    private static final String CURRENT_STEP_POSITION_KEY = "position";
    private int SelectedStep = 0;
    @BindView(R.id.commonStepperLayout)
    protected StepperLayout mStepperLayout;
    FormulizerClient formulizerClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        formulizerClient = new FormulizerClient(AddFormula.this);
        newFormula = new FormulaModel();
        setTitle("Add Formula (Wizzard)");

        setContentView(getLayoutResId());
       // mStepperLayout =(StepperLayout) findViewById(R.id.commonStepperLayout);

        ButterKnife.bind(this);
        int startingStepPosition = savedInstanceState != null ? savedInstanceState.getInt(CURRENT_STEP_POSITION_KEY) : 0;
        mStepperLayout.setAdapter(new FromulaStepAdapter(getSupportFragmentManager(),this), startingStepPosition);

        mStepperLayout.setListener(this);
    }
    protected int getLayoutResId() {
        return R.layout.activity_add_formula;
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_STEP_POSITION_KEY, mStepperLayout.getCurrentStepPosition());
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onBackPressed() {
        final int currentStepPosition = mStepperLayout.getCurrentStepPosition();
        if (currentStepPosition > 0) {
            mStepperLayout.setCurrentStepPosition(currentStepPosition - 1);
        } else {
            finish();
        }
    }
    @Override
    public void onChangeEndButtonsEnabled(boolean enabled) {
        mStepperLayout.setNextButtonVerificationFailed(!enabled);
        mStepperLayout.setCompleteButtonVerificationFailed(!enabled);
    }

    @Override
    public void onCompleted(View completeButton) {
        Toast.makeText(this, "onCompleted!", Toast.LENGTH_SHORT).show();
        double sum = 0;
        for (int i=0;i<newFormula.getTintersCount();i++)
        {
            sum += newFormula.getTinters().get(i).getQty();
        }
        newFormula.settWeight(sum);

        FormulaService formulaService = formulizerClient.getClient().create(FormulaService.class);
        Call<GeneralResponseModel<String>> call = formulaService.addFormula(newFormula);
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

    @Override
    public void onError(VerificationError verificationError) {
        Toast.makeText(this, "onError! Cannot Complete", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStepSelected(int newStepPosition) {
        SelectedStep = newStepPosition;
        Toast.makeText(this, "onStepSelected! -> " + newStepPosition, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReturn() {
        finish();
    }

    @Override
    public void onFormulaAddNextStep(FormulaModel newFormula) {
        this.newFormula = newFormula;
        FragmentManager manager = getSupportFragmentManager();


        if(SelectedStep == 0){
            FormulaStepTwoFragment stepTwo = (FormulaStepTwoFragment) manager.getFragments().get(1);
            stepTwo.updateData(newFormula);
        }
        else if(SelectedStep == 1){
            FormulaPreviewFragment prev = (FormulaPreviewFragment) manager.getFragments().get(2);
            prev.updateData(newFormula);
        }
        Toast.makeText(AddFormula.this,"Going to Next Step",Toast.LENGTH_LONG).show();
    }
    private void showToast(String message) {
        Toast t = Toast.makeText(AddFormula.this,message,Toast.LENGTH_LONG);
        t.show();
    }
}