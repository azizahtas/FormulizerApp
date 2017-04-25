package com.example.stark.formulizer.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.stark.formulizer.Activities.AddCustomer;
import com.example.stark.formulizer.Adapters.CustomerAdapter;
import com.example.stark.formulizer.Controllers.FormulizerClient;
import com.example.stark.formulizer.Activities.CustomerDetailsActivity;
import com.example.stark.formulizer.Models.CustomerListModel;
import com.example.stark.formulizer.Models.CustomerModel;
import com.example.stark.formulizer.Models.GeneralResponseModel;
import com.example.stark.formulizer.Models.PagedGeneralResponseModel;
import com.example.stark.formulizer.R;
import com.example.stark.formulizer.Services.CustomerService;
import com.example.stark.formulizer.Utilities.Constraints;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Stark on 02-03-2017.
 */

public class CustomerFragment extends Fragment implements Serializable,
        CustomerAdapter.CustomerAdapterOnClickHandler{
    FormulizerClient fclient ;
    private CustomerAdapter cAdapter;
    RecyclerView customerListRV;
    View fragmentView;
    private LinearLayoutManager layoutManager;
    private FloatingActionButton mSharedFab;
    private SwipeRefreshLayout swipeLayout;
    Context context;
    CustomerService customerService;

    private int numOfpagesOnServer = 0;
    public int currentPage = 1;
    boolean search=false;
    List<CustomerListModel> customers = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fclient = new FormulizerClient(container.getContext());
        this.context = container.getContext();
        fragmentView =  inflater.inflate(R.layout.fragment, container, false);

        customerListRV = (RecyclerView) fragmentView.findViewById(R.id.my_recycler_view);
        swipeLayout = (SwipeRefreshLayout) fragmentView.findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
      //  customerListRV = (RecyclerView) container.findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(container.getContext(),LinearLayoutManager.VERTICAL,false);
        customerService = fclient.getClient().create(CustomerService.class);
        getData(1);
        cAdapter = new CustomerAdapter(this);
        customerListRV.setLayoutManager(layoutManager);
        customerListRV.setHasFixedSize(true);
        customerListRV.setAdapter(cAdapter);
        return fragmentView;
    }

    void refreshItems() {
        getData(1);
    }
    public void getData(int page){
        Call<PagedGeneralResponseModel<CustomerListModel>> call = customerService.getAllPagedCustomers(page);
        call.enqueue(new Callback<PagedGeneralResponseModel<CustomerListModel>>() {
            @Override
            public void onResponse(Call<PagedGeneralResponseModel<CustomerListModel>> call, Response<PagedGeneralResponseModel<CustomerListModel>> response) {
                if(response.body()!=null){
                    customers = response.body().getData().getDocs();
                    Log.e("Customer Data Size : ",response.body().getData().getDocs().size()+"");
                    numOfpagesOnServer = response.body().getData().getPages();
                    currentPage = response.body().getData().getPage();
                    cAdapter.setCustomerData(customers);
                    // Load complete
                    swipeLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<PagedGeneralResponseModel<CustomerListModel>> call, Throwable t) {
                Log.e("From Customer Fragment", t.toString());
                Toast.makeText(context,"Server Not Accessable! Make Sure Your Connected To Internet!",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void getSearchData(String term, int page){
        Call<PagedGeneralResponseModel<CustomerListModel>> call = customerService.searchPagedCustomers(term,page);
        call.enqueue(new Callback<PagedGeneralResponseModel<CustomerListModel>>() {
            @Override
            public void onResponse(Call<PagedGeneralResponseModel<CustomerListModel>> call, Response<PagedGeneralResponseModel<CustomerListModel>> response) {
                customers = response.body().getData().getDocs();
                Log.e("Data Size : ",response.body().getData().getDocs().size()+"");
                cAdapter.setCustomerData(customers);
            }

            @Override
            public void onFailure(Call<PagedGeneralResponseModel<CustomerListModel>> call, Throwable t) {
// Log error here since request failed
                Log.e("From Formula Fragment", t.toString());
                Toast.makeText(context,"Server Not Accessable! Make Sure Your Connected To Internet!",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void shareFab(FloatingActionButton fab){
        if(fab == null){
            if(mSharedFab !=null){
                mSharedFab.setOnClickListener(null);
            }
            mSharedFab = null;
        }
        else{
            mSharedFab = fab;
            mSharedFab.setImageResource(R.drawable.ic_customer_add_white_18dp);
            mSharedFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent customer_add_edit_Intent = new Intent(context,AddCustomer.class);
                    startActivity(customer_add_edit_Intent);
                }
            });
        }
    }
    public void searchCustomer(String term, int page){
        search = true;
        getSearchData(term,page);
    }
    @Override
    public void onListItemClick(CustomerListModel customer) {
        Intent detailsIntent = new Intent(context, CustomerDetailsActivity.class);
        Gson b = new Gson();
        String content = b.toJson(customer);
        detailsIntent.putExtra(Constraints.CUSTOMER_LIST_MODEL, content);
        startActivity(detailsIntent);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mSharedFab = null;
    }
}
