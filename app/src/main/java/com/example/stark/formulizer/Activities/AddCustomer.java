package com.example.stark.formulizer.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.stark.formulizer.Controllers.FormulizerClient;
import com.example.stark.formulizer.Models.CustomerDetailsModel;
import com.example.stark.formulizer.Models.GeneralResponseModel;
import com.example.stark.formulizer.R;
import com.example.stark.formulizer.Services.CustomerService;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Stark on 06-03-2017.
 */

public class AddCustomer extends AppCompatActivity {

    @BindView(R.id.customer_add_name) MaterialEditText customerName;
    @BindView(R.id.customer_add_contact) MaterialEditText customerContact;

    CustomerDetailsModel newCustomer;
    FormulizerClient formulizerClient;
    CustomerService customerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        formulizerClient = new FormulizerClient(AddCustomer.this);
        customerService = formulizerClient.getClient().create(CustomerService.class);

        newCustomer = new CustomerDetailsModel();
        ButterKnife.bind(this);
    }

    @OnClick(R.id.customer_add_save_action)
    public void Save(){
        String cName = customerName.getText().toString();
        String cContact = customerContact.getText().toString();

        if(cName.isEmpty()){
            customerName.setError(getResources().getText(R.string.customer_name_error));
            customerName.startAnimation(AnimationUtils.loadAnimation(AddCustomer.this, R.anim.shake_error));
        }
        else if(cContact.isEmpty()){
            customerContact.setError(getResources().getText(R.string.customer_contact_error));
            customerContact.startAnimation(AnimationUtils.loadAnimation(AddCustomer.this, R.anim.shake_error));
        }
        else if(cContact.length()>10 || cContact.length()<10){
            customerContact.setError(getResources().getText(R.string.customer_contact_10_error));
            customerContact.startAnimation(AnimationUtils.loadAnimation(AddCustomer.this, R.anim.shake_error));
        }
        else {
            newCustomer.setName(cName);
            newCustomer.setContact(cContact);

            Call<GeneralResponseModel<String>> call = customerService.addCustomer(newCustomer);
            call.enqueue(new Callback<GeneralResponseModel<String>>() {
                @Override
                public void onResponse(Call<GeneralResponseModel<String>> call, Response<GeneralResponseModel<String>> response) {
                    if (response.body().isSuccess()) {
                        showToast(getResources().getString(R.string.customer_save_success));
                        finish();
                    }
                    else{
                        showToast(getResources().getString(R.string.customer_save_error));
                    }
                }

                @Override
                public void onFailure(Call<GeneralResponseModel<String>> call, Throwable t) {
                    showToast(getResources().getString(R.string.error_server_offline));
                }
            });
         }
    }
    @OnClick(R.id.customer_add_cancel_action)
    public void Cancel(){
        finish();
    }
    private void showToast(String message) {
        Toast t = Toast.makeText(AddCustomer.this,message,Toast.LENGTH_LONG);
        t.show();
    }
}
