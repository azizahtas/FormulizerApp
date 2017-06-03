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

import com.example.stark.formulizer.Activities.AddFormula;
import com.example.stark.formulizer.Activities.FormulaDetailsActivity;
import com.example.stark.formulizer.Adapters.FromulaCardAdapter;
import com.example.stark.formulizer.Controllers.FormulizerClient;
import com.example.stark.formulizer.Listeners.EndlessRecyclerViewScrollListener;
import com.example.stark.formulizer.Models.FormulaModel;
import com.example.stark.formulizer.Models.PagedGeneralResponseModel;
import com.example.stark.formulizer.R;
import com.example.stark.formulizer.Services.FormulaService;
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

public class FormulaFragment extends Fragment implements Serializable,FromulaCardAdapter.FormulaAdapterOnClickHandler{
    FormulizerClient fclient ;
    private FromulaCardAdapter fAdapter;
    RecyclerView formulaListRV;
    View fragmentView;
    private LinearLayoutManager layoutManager;
    private FloatingActionButton mSharedFab;
    private SwipeRefreshLayout swipeLayout;
    private EndlessRecyclerViewScrollListener scrollListener;
    FormulaService formulaService;
    List<FormulaModel> formulas = null;
    public Context context;
    public int currentPage = 1;
    boolean search=false;
    boolean firstTimeOnResume = true;
    private int numOfpagesOnServer = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentView =  inflater.inflate(R.layout.fragment, container, false);
        context = this.getActivity();
        fclient = new FormulizerClient(this.context);
        formulaService = fclient.getClient().create(FormulaService.class);
        formulaListRV = (RecyclerView) fragmentView.findViewById(R.id.my_recycler_view);
        swipeLayout = (SwipeRefreshLayout) fragmentView.findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
        formulas = new ArrayList<>();

        layoutManager = new LinearLayoutManager(container.getContext(),LinearLayoutManager.VERTICAL,false);
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
        fAdapter = new FromulaCardAdapter(this);
        formulaListRV.setLayoutManager(layoutManager);
        formulaListRV.setHasFixedSize(true);
        formulaListRV.addOnScrollListener(scrollListener);
        formulaListRV.setAdapter(fAdapter);

        return fragmentView;
    }
    void refreshItems() {
        getData(1);
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
                Toast.makeText(context,"Server Not Accessable! Make Sure Your Connected To Internet!",Toast.LENGTH_LONG).show();
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
                Toast.makeText(context,"Server Not Accessible! Make Sure Your Connected To Internet!",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void getSearchData(String term, int page){
        Call<PagedGeneralResponseModel<FormulaModel>> call = formulaService.searchPagedFormulasPersonal(term,page);
        call.enqueue(new Callback<PagedGeneralResponseModel<FormulaModel>>() {
            @Override
            public void onResponse(Call<PagedGeneralResponseModel<FormulaModel>> call, Response<PagedGeneralResponseModel<FormulaModel>> response) {
                formulas = response.body().getData().getDocs();
                Log.e("Data Size : ",response.body().getData().getDocs().size()+"");
                fAdapter.setFormulaData(formulas);
            }

            @Override
            public void onFailure(Call<PagedGeneralResponseModel<FormulaModel>> call, Throwable t) {
// Log error here since request failed
                Log.e("From Formula Fragment", t.toString());
                Toast.makeText(context,"Server Not Accessable! Make Sure Your Connected To Internet!",Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       // this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.context = getContext();
        if(!firstTimeOnResume) {
            if (formulas != null) formulas.clear();
            for (int i = 1; i <= currentPage ; i++) {
                appendData(i);
            }
        }
        firstTimeOnResume = false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //this.context = getActivity();
    }

    @Override
    public void onListItemClicked(FormulaModel formula) {
        Intent detailsIntent = new Intent(context, FormulaDetailsActivity.class);
        Gson b = new Gson();
        String content = b.toJson(formula);
        detailsIntent.putExtra(Constraints.FORMULA_MODEL, content);
        Toast.makeText(context,"You Clicked "+formula.getName(),Toast.LENGTH_SHORT).show();
        startActivity(detailsIntent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void searchFormulaPersonal(String term, int page){
        search = true;
        getSearchData(term,page);
    }
}
