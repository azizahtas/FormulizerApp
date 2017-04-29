package com.example.stark.formulizer.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.stark.formulizer.Models.FormulaListModel;
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

public class CustomerFormulasGridAdapter extends RecyclerView.Adapter<CustomerFormulasGridAdapter.FormulaViewHolder> {

    private Context context;
    List<FormulaListModel> formulas;
    private final CustomerFormulasGridAdapter.CustomerFormulasGridAdapterOnClickHandler mClickHandler;

    public interface CustomerFormulasGridAdapterOnClickHandler{
        void onListItemDeleted(FormulaListModel formula);
    }

    public CustomerFormulasGridAdapter(CustomerFormulasGridAdapter.CustomerFormulasGridAdapterOnClickHandler clickHandler){
        this.mClickHandler = clickHandler;
    }

    public class FormulaViewHolder extends RecyclerView.ViewHolder{
        //@BindView(R.id.formula_item_owner_abbrivation) TextView ownerAbbrivation;
        TextView formulaName;
        Button formulaDelete;

        public FormulaViewHolder(View itemView) {
            super(itemView);
            // ButterKnife.bind(itemView);CustomerFormulasGridAdapterCustomerFormulasGridAdapter
            formulaName= (TextView) itemView.findViewById(R.id.customer_formula_added_name);
            formulaDelete= (Button) itemView.findViewById(R.id.customer_formula_added_action_delete);
        }
        void bind(final FormulaListModel model, final CustomerFormulasGridAdapter.CustomerFormulasGridAdapterOnClickHandler handler){
            formulaName.setText(model.getName());
            formulaDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handler.onListItemDeleted(model);
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
    public CustomerFormulasGridAdapter.FormulaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        int customerListItemLayoutId = R.layout.customer_formulas_added_list_item;
        LayoutInflater inflater = LayoutInflater.from(this.context);

        View view = inflater.inflate(customerListItemLayoutId, parent, false);

        CustomerFormulasGridAdapter.FormulaViewHolder viewHolder = new CustomerFormulasGridAdapter.FormulaViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomerFormulasGridAdapter.FormulaViewHolder holder, int position) {
        if(formulas!=null){
            holder.bind(formulas.get(position),mClickHandler);
        }
    }
    public void setFormulaData(List<FormulaListModel> formulaData){
        formulas = formulaData;
        notifyDataSetChanged();
    }
}
