package com.example.stark.formulizer.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by aziz_ on 30-04-2017.
 */

public class FormulasListModelCollection implements Serializable {
    @SerializedName("CustomerOldFormulas")
    @Expose
    private List<FormulaListModel> oldFormulas;

    public FormulasListModelCollection(List<FormulaListModel> oldFormulas) {
        this.oldFormulas = oldFormulas;
    }

    public List<FormulaListModel> getOldFormulas() {
        return oldFormulas;
    }

    public void setOldFormulas(List<FormulaListModel> oldFormulas) {
        this.oldFormulas = oldFormulas;
    }

}
