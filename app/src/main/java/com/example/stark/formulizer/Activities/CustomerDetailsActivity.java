package com.example.stark.formulizer.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stark.formulizer.Adapters.FormulaListAdapter;
import com.example.stark.formulizer.Adapters.FromulaCardAdapter;
import com.example.stark.formulizer.Controllers.FormulizerClient;
import com.example.stark.formulizer.Models.CustomerListModel;
import com.example.stark.formulizer.Models.CustomerModel;
import com.example.stark.formulizer.Models.FormulaModel;
import com.example.stark.formulizer.Models.GeneralResponseModel;
import com.example.stark.formulizer.R;
import com.example.stark.formulizer.Services.CustomerService;
import com.example.stark.formulizer.Utilities.Constraints;
import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerDetailsActivity extends AppCompatActivity implements FormulaListAdapter.FormulaListAdapterOnClickHandler {
    CustomerListModel selectedCustomer;
    LinearLayoutManager layoutManager;
    FormulizerClient formulizerClient;
    CustomerService customerService;
    FormulaListAdapter fAdapter;
    MenuItem mSearchaction;
    private EditText editSearch;

    @BindView(R.id.customer_formulas_recyclerview) RecyclerView customerFormulaRv;
    @BindView(R.id.customer_details_toolbar) Toolbar toolbar;

    List<FormulaModel> customerFormulas;
    boolean search=false;
    private boolean isSearchOpened = false;

    Gson gs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);
        ButterKnife.bind(this);
        selectedCustomer = new CustomerListModel();
        toolbar.setTitleTextColor(getResources().getColor(R.color.cpb_white));
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);
        formulizerClient = new FormulizerClient(this);
        customerService = formulizerClient.getClient().create(CustomerService.class);
        fAdapter = new FormulaListAdapter(this);
        layoutManager = new LinearLayoutManager(CustomerDetailsActivity.this,LinearLayoutManager.VERTICAL,false);
        customerFormulaRv.setLayoutManager(layoutManager);
        customerFormulaRv.setHasFixedSize(true);
        customerFormulaRv.setAdapter(fAdapter);

        gs = new Gson();
        Intent callerIntent = getIntent();
        if(callerIntent.hasExtra(Constraints.CUSTOMER_LIST_MODEL)){
            selectedCustomer = gs.fromJson(callerIntent.getStringExtra(Constraints.CUSTOMER_LIST_MODEL),CustomerListModel.class);
            toolbar.setTitle(selectedCustomer.getName());
            getData(1,selectedCustomer.getId());
        }
        else{
            showToast("Something Went wrong! Please Select Another Formula!");
            finish();
        }
    }

    public void getData(int page,String cId) {
       // Call<GeneralResponseModel<CustomerModel>> call = customerService.getCustomerFormulas(cId,page);
        Call<GeneralResponseModel<CustomerModel>> call = customerService.getCustomerFormulas(cId);
        call.enqueue(new Callback<GeneralResponseModel<CustomerModel>>() {
            @Override
            public void onResponse(Call<GeneralResponseModel<CustomerModel>> call, Response<GeneralResponseModel<CustomerModel>> response) {
                if(response.body()!=null){
                    customerFormulas = response.body().getData().get(0).getCustomerFormulas();
                    //customerContact.setText(response.body().getData().get(0).getContact());
                    Log.e("Customer Formulas : ",response.body().getData().get(0).getCustomerFormulas().size()+"");
                    fAdapter.setFormulaData(customerFormulas);
                    //numOfpagesOnServer = response.body().getData().getPages();
                    //currentPage = response.body().getData().getPage();
                    // Load complete
                    //swipeLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<GeneralResponseModel<CustomerModel>> call, Throwable t) {
                // Log error here since request failed
                Log.e("From Formula Fragment", t.toString());
                showToast("Server Not Accessable! Make Sure Your Connected To Internet!");
            }
        });
    }
    public void getSearchData(final String term){
        Call<GeneralResponseModel<FormulaModel>> call = customerService.searchCustomerFormulas(selectedCustomer.getId(),term);
        call.enqueue(new Callback<GeneralResponseModel<FormulaModel>>() {
            @Override
            public void onResponse(Call<GeneralResponseModel<FormulaModel>> call, Response<GeneralResponseModel<FormulaModel>> response) {
               if(response.isSuccessful()) {
                   customerFormulas = response.body().getData();
                   Log.e("Searched Data Size : ", response.body().getData().size() + "");
                   if(customerFormulas.size() >0){
                       fAdapter.setFormulaData(customerFormulas);
                   }
                   else{
                       showToast("Nothing Found with the Name "+term);
                   }

               }
            }

            @Override
            public void onFailure(Call<GeneralResponseModel<FormulaModel>> call, Throwable t) {
            // Log error here since request failed
                Log.e("From Formula Fragment", t.toString());
                showToast("Server Not Accessible! Make Sure Your Connected To Internet!");
            }
        });
    }
    private void showToast(String message) {
        Toast t = Toast.makeText(CustomerDetailsActivity.this,message,Toast.LENGTH_LONG);
        t.show();
    }
    protected void handleMenuSearch(){
        ActionBar action = getSupportActionBar();
        if(isSearchOpened){
            action.setDisplayShowCustomEnabled(false);
            action.setDisplayShowTitleEnabled(true);

            //hides keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editSearch.getWindowToken(),0);

            //add the search icon in the action bar
            mSearchaction.setIcon(getResources().getDrawable(R.drawable.ic_search_white_18dp));

            isSearchOpened = false;
            toolbar.setTitle(selectedCustomer.getName());
        }
        else{
            action.setDisplayShowCustomEnabled(true);
            //Custom view in action bar
            action.setCustomView(R.layout.searchbar);
            action.setDisplayShowTitleEnabled(false);

            editSearch = (EditText) action.getCustomView().findViewById(R.id.editSearch);
            editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(actionId == EditorInfo.IME_ACTION_SEARCH){
                        Search(v.getText().toString());
                        return true;
                    }
                    return false;
                }
            });
            editSearch.requestFocus();

            //open the keyboard focused in the editSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editSearch,InputMethodManager.SHOW_IMPLICIT);

            //add the close icon
            mSearchaction.setIcon(getResources().getDrawable(R.drawable.ic_clear_white_18dp));

            isSearchOpened = true;
        }
    }
    protected void Search(String term){
        if(!term.isEmpty()){
            search = true;
            getSearchData(term);
        }
    }
    @OnClick(R.id.add_Customer_Formulas)
    public void AddFormulas(){
        Intent addFormulaIntent = new Intent(CustomerDetailsActivity.this, CustomerFormulasActivity.class);
        Gson b = new Gson();
        String content = b.toJson(selectedCustomer);
        addFormulaIntent.putExtra(Constraints.CUSTOMER_LIST_MODEL, content);
        startActivity(addFormulaIntent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer_details,menu);
        return true;
    }
    @Override
    public void onListItemClicked(FormulaModel formula) {
        Intent detailsIntent = new Intent(CustomerDetailsActivity.this, FormulaDetailsActivity.class);
        Gson b = new Gson();
        String content = b.toJson(formula);
        detailsIntent.putExtra(Constraints.FORMULA_MODEL, content);
        showToast("You Clicked "+formula.getName());
        startActivity(detailsIntent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_search:
                handleMenuSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchaction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }
}
