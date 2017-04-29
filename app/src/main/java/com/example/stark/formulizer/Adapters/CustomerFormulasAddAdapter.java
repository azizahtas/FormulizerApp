package com.example.stark.formulizer.Adapters;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stark.formulizer.Models.FormulaModel;
import com.example.stark.formulizer.R;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by aziz_ on 29-04-2017.
 */

public class CustomerFormulasAddAdapter extends RecyclerView.Adapter<CustomerFormulasAddAdapter.FormulaViewHolder> {

    private Context context;
    List<FormulaModel> formulas;
    private final CustomerFormulasAddAdapterOnClickHandler mClickHandler;
    Calendar cal = Calendar.getInstance();
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

    DateTimeZone utc = DateTimeZone.UTC;


    public interface CustomerFormulasAddAdapterOnClickHandler{
        void onListItemClicked(FormulaModel formula);
        void onListItemAdd(FormulaModel formula);
    }

    public CustomerFormulasAddAdapter(CustomerFormulasAddAdapterOnClickHandler clickHandler){
        this.mClickHandler = clickHandler;
    }

    public class FormulaViewHolder extends RecyclerView.ViewHolder{
        //@BindView(R.id.formula_item_owner_abbrivation) TextView ownerAbbrivation;
        TextView ownerAbbrivation;
        TextView ownerName;
        TextView formulaName;
        TextView company;
        TextView base;
        TextView type;
        TextView date;
        FloatingActionButton addFormula;

        public FormulaViewHolder(View itemView) {
            super(itemView);
            // ButterKnife.bind(itemView);
            ownerAbbrivation= (TextView) itemView.findViewById(R.id.formula_item_owner_abbrivation);
            ownerName= (TextView) itemView.findViewById(R.id.formula_item_owner_name);
            formulaName= (TextView) itemView.findViewById(R.id.formula_item_name);
            company= (TextView) itemView.findViewById(R.id.formula_item_company);
            base= (TextView) itemView.findViewById(R.id.formula_item_base);
            type= (TextView) itemView.findViewById(R.id.formula_item_type);
            date= (TextView) itemView.findViewById(R.id.formula_item_date);
            addFormula= (FloatingActionButton) itemView.findViewById(R.id.customer_formula_item_add);
        }
        void bind(final FormulaModel model, final CustomerFormulasAddAdapterOnClickHandler handler){
            ownerAbbrivation.setText(model.getUserName().charAt(0)+"");
            ownerName.setText(model.getUserName());
            formulaName.setText(model.getName());
            company.setText(model.getCompany());
            base.setText(model.getBase());
            type.setText(model.getType().equals("C")?"Custom":"Standard");
            DateTime myDate = new DateTime(model.getDate());
            date.setText(myDate.getDayOfMonth()+"/"+myDate.getMonthOfYear()+"/"+myDate.getYear());
            formulaName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handler.onListItemClicked(model);
                }
            });
            addFormula.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handler.onListItemAdd(model);
                }
            });


        }

    }

    @Override
    public int getItemCount() {
        if(formulas != null) return formulas.size();
        else return 0;
    }

    @Override
    public CustomerFormulasAddAdapter.FormulaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        int customerListItemLayoutId = R.layout.customer_formulas_add_list_item;
        LayoutInflater inflater = LayoutInflater.from(this.context);

        View view = inflater.inflate(customerListItemLayoutId, parent, false);

        CustomerFormulasAddAdapter.FormulaViewHolder viewHolder = new CustomerFormulasAddAdapter.FormulaViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomerFormulasAddAdapter.FormulaViewHolder holder, int position) {
        if(formulas!=null){
            holder.bind(formulas.get(position),mClickHandler);
        }
    }
    public void setFormulaData(List<FormulaModel> formulaData){
        formulas = formulaData;
        notifyDataSetChanged();
    }
}
