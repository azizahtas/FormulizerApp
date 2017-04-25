package com.example.stark.formulizer.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.stark.formulizer.Models.CustomerListModel;
import com.example.stark.formulizer.R;
import com.example.stark.formulizer.Utilities.Constraints;
import com.google.gson.Gson;

public class CustomerFormulasActivity extends AppCompatActivity {

    Gson gs;
    CustomerListModel selectedCustomer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_formulas);

        gs= new Gson();
        Intent caller = getIntent();
        if(caller.hasExtra(Constraints.CUSTOMER_LIST_MODEL)){
            selectedCustomer = gs.fromJson(caller.getStringExtra(Constraints.CUSTOMER_LIST_MODEL),CustomerListModel.class);
        }

    }
}
