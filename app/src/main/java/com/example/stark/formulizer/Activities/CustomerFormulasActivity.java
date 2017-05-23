package com.example.stark.formulizer.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stark.formulizer.Adapters.CustomerFormulasAddAdapter;
import com.example.stark.formulizer.Adapters.CustomerFormulasGridAdapter;
import com.example.stark.formulizer.Adapters.FromulaCardAdapter;
import com.example.stark.formulizer.Controllers.FormulizerClient;
import com.example.stark.formulizer.Listeners.EndlessRecyclerViewScrollListener;
import com.example.stark.formulizer.Models.CustomerListModel;
import com.example.stark.formulizer.Models.FormulaListModel;
import com.example.stark.formulizer.Models.FormulaModel;
import com.example.stark.formulizer.Models.FormulasListModelCollection;
import com.example.stark.formulizer.Models.GeneralResponseModel;
import com.example.stark.formulizer.Models.PagedGeneralResponseModel;
import com.example.stark.formulizer.R;
import com.example.stark.formulizer.Services.CustomerService;
import com.example.stark.formulizer.Services.FormulaService;
import com.example.stark.formulizer.Utilities.Constraints;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerFormulasActivity extends AppCompatActivity implements CustomerFormulasAddAdapter.CustomerFormulasAddAdapterOnClickHandler,CustomerFormulasGridAdapter.CustomerFormulasGridAdapterOnClickHandler {

    FormulizerClient fclient;
    CustomerService customerService;
    FormulaService formulaService;

    CustomerFormulasAddAdapter fAdapter;
    CustomerFormulasGridAdapter CfAdapter;
    private GridLayoutManager layoutManager;
    private GridLayoutManager formulasAddedlayoutManager;
    Gson gs;
    CustomerListModel selectedCustomer;
    List<FormulaModel> formulas = null;
    List<FormulaListModel> addedFormulas = null;
    List<FormulaListModel> oldFormulas = null;
    FormulasListModelCollection oldFormulasColl = null;
    MaterialEditText editSearch;
    Button clear,save;
    private SwipeRefreshLayout swipeLayout;
    private EndlessRecyclerViewScrollListener scrollListener;

    RecyclerView customerFormulasAddRV;
    RecyclerView customerFormulasAddedRV;
    public int currentPage = 1;
    boolean search=false;
    boolean firstTimeOnResume = true;
    private int numOfpagesOnServer = 0;
    private boolean isSearchOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_formulas);
        formulas = new ArrayList<>();
        addedFormulas = new ArrayList<>();
        oldFormulas = new ArrayList<>();
        //oldFormulas = new ArrayList<>();
        customerFormulasAddRV = (RecyclerView) findViewById(R.id.customer_formulas_add_rv);
        customerFormulasAddedRV = (RecyclerView) findViewById(R.id.customer_formulas_added_list_rv);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.customer_formulas_add_rv_swipe);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
        clear = (Button) findViewById(R.id.customer_formulas_clear_action);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clear();
            }
        });
        save = (Button) findViewById(R.id.customer_formulas_save_action);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Save();
            }
        });
        editSearch = (MaterialEditText) findViewById(R.id.customer_formulas_search_all);
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchFormulaPersonal(v.getText().toString(), 1);
                    return true;
                }
                return false;
            }
        });
        editSearch.requestFocus();
        layoutManager = new GridLayoutManager(CustomerFormulasActivity.this,1,LinearLayoutManager.VERTICAL,false);
        formulasAddedlayoutManager = new GridLayoutManager(CustomerFormulasActivity.this,2,LinearLayoutManager.VERTICAL,false);
        fclient = new FormulizerClient(CustomerFormulasActivity.this);
        formulaService = fclient.getClient().create(FormulaService.class);
        customerService = fclient.getClient().create(CustomerService.class);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if(page <= numOfpagesOnServer) {
                    appendData(page);
                }
                Log.e("Page Number", page + "");
                Log.e("Items Count", totalItemsCount + "");
            }
        };
        getData(1);
        fAdapter = new CustomerFormulasAddAdapter(this);
        CfAdapter = new CustomerFormulasGridAdapter(this);
        customerFormulasAddRV.setLayoutManager(layoutManager);
        customerFormulasAddRV.setHasFixedSize(true);
        customerFormulasAddRV.addOnScrollListener(scrollListener);
        customerFormulasAddRV.setAdapter(fAdapter);
        customerFormulasAddedRV.setLayoutManager(formulasAddedlayoutManager);
        customerFormulasAddedRV.setHasFixedSize(true);
        //customerFormulasAddRV.addOnScrollListener(scrollListener);
        customerFormulasAddedRV.setAdapter(CfAdapter);

        gs= new Gson();
        Intent caller = getIntent();
        if(caller.hasExtra(Constraints.CUSTOMER_LIST_MODEL) && caller.hasExtra(Constraints.CUSTOMER_FORMULAS_LIST_MODEL)){
            selectedCustomer = gs.fromJson(caller.getStringExtra(Constraints.CUSTOMER_LIST_MODEL),CustomerListModel.class);
            oldFormulasColl = gs.fromJson(caller.getStringExtra(Constraints.CUSTOMER_FORMULAS_LIST_MODEL), FormulasListModelCollection.class);
            oldFormulas = oldFormulasColl.getOldFormulas();
            addedFormulas.addAll(oldFormulas);
            CfAdapter.setFormulaData(addedFormulas);
        }
    }

    public void getData(int page) {
        Call<PagedGeneralResponseModel<FormulaModel>> call = formulaService.getPagedFormulas(page);
        call.enqueue(new Callback<PagedGeneralResponseModel<FormulaModel>>() {
            @Override
            public void onResponse(Call<PagedGeneralResponseModel<FormulaModel>> call, Response<PagedGeneralResponseModel<FormulaModel>> response) {
                if(response.body()!=null){
                    formulas = response.body().getData().getDocs();
                    Log.e("Data Size : ",response.body().getData().getDocs().size()+"");
                    fAdapter.setFormulaData(formulas);
                    numOfpagesOnServer = response.body().getData().getPages();
                    currentPage = response.body().getData().getPage();
                    // Load complete
                    swipeLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<PagedGeneralResponseModel<FormulaModel>> call, Throwable t) {
                // Log error here since request failed
                Log.e("From Formula Fragment", t.toString());
                Toast.makeText(CustomerFormulasActivity.this,"Server Not Accessable! Make Sure Your Connected To Internet!",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void appendData(final int page) {
        Call<PagedGeneralResponseModel<FormulaModel>> call = formulaService.getPagedFormulas(page);
        call.enqueue(new Callback<PagedGeneralResponseModel<FormulaModel>>() {
            @Override
            public void onResponse(Call<PagedGeneralResponseModel<FormulaModel>> call, Response<PagedGeneralResponseModel<FormulaModel>> response) {
                if(response.body()!=null && response.isSuccessful()){
                    formulas.addAll(response.body().getData().getDocs());
                    Log.e("Page No : ",page+"");
                    Log.e("Data Size Appended: ",response.body().getData().getDocs().size()+"");
                    fAdapter.setFormulaData(formulas);
                    numOfpagesOnServer = response.body().getData().getPages();
                    currentPage = response.body().getData().getPage();
                }
            }

            @Override
            public void onFailure(Call<PagedGeneralResponseModel<FormulaModel>> call, Throwable t) {
                Log.e("From Formula Fragment", t.toString());
                Toast.makeText(CustomerFormulasActivity.this,"Server Not Accessible! Make Sure Your Connected To Internet!",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void refreshItems(){
        getData(1);
    }
    public void searchFormulaPersonal(String term, int page){
        search = true;
        if(!term.equals("")) {
        getSearchData(term,page);
        }
        else {
            getData(1);
        }
    }
    public void getSearchData(String term, int page){
            Call<PagedGeneralResponseModel<FormulaModel>> call = formulaService.searchPagedFormulasPersonal(term, page);
            call.enqueue(new Callback<PagedGeneralResponseModel<FormulaModel>>() {
                @Override
                public void onResponse(Call<PagedGeneralResponseModel<FormulaModel>> call, Response<PagedGeneralResponseModel<FormulaModel>> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            formulas = response.body().getData().getDocs();
                            Log.e("Data Size : ", response.body().getData().getDocs().size() + "");
                            fAdapter.setFormulaData(formulas);
                        } else {
                            showToast("Nothing Found On Server Side!");
                        }
                    } else {
                        showToast("Something Went Wrong! Please Try Searching Again!");
                    }
                }

                @Override
                public void onFailure(Call<PagedGeneralResponseModel<FormulaModel>> call, Throwable t) {
// Log error here since request failed
                    Log.e("From Customer Formulas", t.toString());
                    Toast.makeText(CustomerFormulasActivity.this, "Server Not Accessable! Make Sure Your Connected To Internet!", Toast.LENGTH_LONG).show();
                }
            });

    }

    public void Clear(){
        addedFormulas.clear();
        CfAdapter.setFormulaData(addedFormulas);
    }
    public void Save(){
        List<String> newFormulas = new ArrayList<>(addedFormulas.size());
        for(int i=0; i <addedFormulas.size();i++){
            newFormulas.add(addedFormulas.get(i).getId());
        }
        Call<GeneralResponseModel<String>> call = customerService.addFromulas(selectedCustomer.getId(),newFormulas);
        call.enqueue(new Callback<GeneralResponseModel<String>>() {
            @Override
            public void onResponse(Call<GeneralResponseModel<String>> call, Response<GeneralResponseModel<String>> response) {
                if (response.body().isSuccess()) {
                    showToast(getResources().getString(R.string.customer_formulas_save_success));
                    finish();
                }
                else{
                    showToast(getResources().getString(R.string.customer_formulas_save_success));
                }
            }

            @Override
            public void onFailure(Call<GeneralResponseModel<String>> call, Throwable t) {
                showToast(getResources().getString(R.string.error_server_offline));
            }
        });
    }

    @Override
    public void onListItemClicked(FormulaModel formula) {
        Intent detailsIntent = new Intent(CustomerFormulasActivity.this, FormulaDetailsActivity.class);
        Gson b = new Gson();
        String content = b.toJson(formula);
        detailsIntent.putExtra(Constraints.FORMULA_MODEL, content);
        Toast.makeText(CustomerFormulasActivity.this,"You Clicked "+formula.getName(),Toast.LENGTH_SHORT).show();
        startActivity(detailsIntent);
    }
    @Override
    public void onListItemAdd(FormulaModel formula) {
        boolean b = false;
        for (int i =0; i<addedFormulas.size();i++){
            if(addedFormulas.get(i).getId().equals(formula.getId())){
                b = true;
            }
        }
        if(b){
            Toast.makeText(CustomerFormulasActivity.this,"Formula Already Present "+formula.getName(),Toast.LENGTH_SHORT).show();
        }
        else {
            addedFormulas.add(new FormulaListModel(formula.getId(),formula.getName()));
            CfAdapter.setFormulaData(addedFormulas);
        }
    }
    @Override
    public void onListItemDeleted(FormulaListModel formula) {
        addedFormulas.remove(addedFormulas.indexOf(formula));
        Toast.makeText(CustomerFormulasActivity.this,"You Deleted "+formula.getName(),Toast.LENGTH_SHORT).show();
        CfAdapter.setFormulaData(addedFormulas);
    }
    private void showToast(String message) {
        Toast t = Toast.makeText(CustomerFormulasActivity.this,message,Toast.LENGTH_LONG);
        t.show();
    }
}
