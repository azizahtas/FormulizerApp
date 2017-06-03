package com.example.stark.formulizer.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.stark.formulizer.Activities.AddCustomer;
import com.example.stark.formulizer.Activities.FormulaDetailsActivity;
import com.example.stark.formulizer.Adapters.CustomerAdapter;
import com.example.stark.formulizer.Controllers.FormulizerClient;
import com.example.stark.formulizer.Activities.CustomerDetailsActivity;
import com.example.stark.formulizer.Models.CustomerListModel;
import com.example.stark.formulizer.Models.GeneralResponseModel;
import com.example.stark.formulizer.Models.PagedGeneralResponseModel;
import com.example.stark.formulizer.R;
import com.example.stark.formulizer.Services.CustomerService;
import com.example.stark.formulizer.Utilities.Constraints;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
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
    private SwipeRefreshLayout swipeLayout;
    Context context;
    CustomerService customerService;
    MaterialDialog.Builder confirmDialogBuilder;
    MaterialDialog.Builder progressDialogBuilder;
    MaterialDialog confirmDialog;
    MaterialDialog progressDialog;

    private int numOfpagesOnServer = 0;
    public int currentPage = 1;
    boolean search=false;
    List<CustomerListModel> customers = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        customers = new ArrayList<>();
        fclient = new FormulizerClient(container.getContext());
        this.context = container.getContext();
        fragmentView =  inflater.inflate(R.layout.fragment, container, false);
        confirmDialogBuilder = new MaterialDialog.Builder(this.context)
                .title(R.string.dialog_delete_title)
                .content(R.string.dialog_delete_content)
                .positiveText(R.string.dialog_delete_positive)
                .negativeText(R.string.dialog_delete_negative);
        progressDialogBuilder = new MaterialDialog.Builder(this.context)
                .title(R.string.dialog_delete_progress_title)
                .content(R.string.dialog_delete_progress_content)
                .progress(true, 0);
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
    public void onListItemLongClick(final String Id, final View view, final int position) {
        final PopupMenu popupMenu = new PopupMenu(context,view, Gravity.END);
        popupMenu.inflate(R.menu.menu_customer_item_options);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.customer_delete_item:
                        Delete(Id,position);
                        Toast.makeText(context,"Delete Customer with id "+Id,Toast.LENGTH_LONG).show();
                        break;
                    default: break;
                }
                return false;
            }
        });
        popupMenu.show();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void Delete(final String customerId, final int position){
        confirmDialogBuilder
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        progressDialog = progressDialogBuilder.build();
                        progressDialog.show();
                        Call<GeneralResponseModel<String>> call = customerService.deleteCustomer(customerId);
                        call.enqueue(new Callback<GeneralResponseModel<String>>() {
                            @Override
                            public void onResponse(Call<GeneralResponseModel<String>> call, Response<GeneralResponseModel<String>> response) {
                                if(response.isSuccessful()){
                                    showToast(getResources().getString(R.string.customer_delete_success));
                                    progressDialog.dismiss();
                                    customers.remove(position);
                                    cAdapter.setCustomerData(customers);
                                }
                                else{
                                    showToast(getResources().getString(R.string.customer_delete_error));
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

    private void showToast(String message) {
        Toast t = Toast.makeText(this.context,message,Toast.LENGTH_LONG);
        t.show();
    }
}
