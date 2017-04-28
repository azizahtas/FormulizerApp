package com.example.stark.formulizer.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.stark.formulizer.Models.CustomerListModel;
import com.example.stark.formulizer.Models.CustomerModel;
import com.example.stark.formulizer.R;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Stark on 02-03-2017.
 */

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>{

    private Context context;
    List<CustomerListModel> customers ;
    private final CustomerAdapterOnClickHandler mCkickHandler;

    public interface CustomerAdapterOnClickHandler {
        void onListItemClick(CustomerListModel customer);
        void onListItemLongClick(String customerId, View view, int position);
    }

    public CustomerAdapter(CustomerAdapterOnClickHandler clickHandler){ this.mCkickHandler = clickHandler;}

     public class CustomerViewHolder extends RecyclerView.ViewHolder{
         LinearLayout customerItemLayout;
        TextView formulaCountView;
        TextView customerNameView;
         CustomerListModel customer;
         public CustomerViewHolder(View itemView){
            super(itemView);
             customerItemLayout = (LinearLayout)  itemView.findViewById(R.id.customers_item_layout);
            formulaCountView = (TextView) itemView.findViewById(R.id.customer_item_formula_count);
            customerNameView = (TextView) itemView.findViewById(R.id.customer_item_name);
        }
         void bind(final int position, final CustomerAdapterOnClickHandler handler){
             customer = customers.get(position);
            formulaCountView.setText(customer.getCount()+"");
             customerNameView.setText(customer.getName());
             itemView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     handler.onListItemClick(customer);
                 }
             });
             itemView.setOnLongClickListener(new View.OnLongClickListener() {
                 @Override
                 public boolean onLongClick(View v) {
                     handler.onListItemLongClick(customer.getId(),itemView,position);
                     return true;
                 }
             });
         }

     }

    @Override
    public CustomerViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        this.context = parent.getContext();
        int customerListItemLayoutId = R.layout.customer_list_item;
        LayoutInflater inflater = LayoutInflater.from(this.context);

        View view = inflater.inflate(customerListItemLayoutId, parent, false);
        CustomerViewHolder viewHolder = new CustomerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomerViewHolder holder, int position) {
        if(customers !=null) {
           holder.bind(position,mCkickHandler);
        }
    }
    @Override
    public int getItemCount() {
        if(customers == null){return 0;} else{return customers.size();}
    }

    public void setCustomerData(List<CustomerListModel> customerData){
        customers = customerData;
        notifyDataSetChanged();
    }
}
