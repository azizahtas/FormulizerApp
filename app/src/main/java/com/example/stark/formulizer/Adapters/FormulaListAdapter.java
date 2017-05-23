package com.example.stark.formulizer.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stark.formulizer.Models.FormulaModel;
import com.example.stark.formulizer.R;

import java.util.List;

/**
 * Created by aziz_ on 24-04-2017.
 */

public class FormulaListAdapter extends RecyclerView.Adapter<FormulaListAdapter.FormulaListViewHolder> {


    private Context context;
    List<FormulaModel> formulas;
    private final FormulaListAdapter.FormulaListAdapterOnClickHandler mClickHandler;


    public interface FormulaListAdapterOnClickHandler{
        void onListItemClicked(FormulaModel formula);
    }

    public FormulaListAdapter(FormulaListAdapter.FormulaListAdapterOnClickHandler clickHandler){
        this.mClickHandler = clickHandler;
    }

    public class FormulaListViewHolder extends RecyclerView.ViewHolder{
        //@BindView(R.id.formula_item_owner_abbrivation) TextView ownerAbbrivation;
        TextView formulaName;

        public FormulaListViewHolder(View itemView) {
            super(itemView);
            // ButterKnife.bind(itemView);
            formulaName= (TextView) itemView.findViewById(R.id.formula_name);
        }
        void bind(final FormulaModel model, final FormulaListAdapterOnClickHandler handler){
            String Formula = model.getName() + " - " + model.getBase() + " - " + model.getCompany();
            formulaName.setText(Formula);
            formulaName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handler.onListItemClicked(model);
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
    public FormulaListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        int customerListItemLayoutId = R.layout.formula_listview_item;
        LayoutInflater inflater = LayoutInflater.from(this.context);

        View view = inflater.inflate(customerListItemLayoutId, parent, false);

        FormulaListAdapter.FormulaListViewHolder viewHolder = new FormulaListAdapter.FormulaListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FormulaListViewHolder holder, int position) {
        if(formulas!=null){
            holder.bind(formulas.get(position),mClickHandler);
        }
    }
    public void setFormulaData(List<FormulaModel> formulaData){
        formulas = formulaData;
        notifyDataSetChanged();
    }
}
