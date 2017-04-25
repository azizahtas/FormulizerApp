package com.example.stark.formulizer.Services;

import com.example.stark.formulizer.Models.FormulaModel;
import com.example.stark.formulizer.Models.GeneralResponseModel;
import com.example.stark.formulizer.Models.PagedGeneralResponseModel;
import com.example.stark.formulizer.Utilities.Constraints;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Stark on 04-03-2017.
 */

public interface FormulaService {
    @GET("formula/search/"+ Constraints.option_formula_personal+"/{term}/{page}")
    Call<PagedGeneralResponseModel<FormulaModel>> searchPagedFormulasPersonal(@Path("term") String Term,@Path("page") int Page);

    @GET("formula/search/"+ Constraints.option_formula_community+"/{term}/{page}")
    Call<PagedGeneralResponseModel<FormulaModel>> searchPagedFormulasCommunity(@Path("term") String Term,@Path("page") int Page);

    @GET("formula/page/{page}")
    Call<PagedGeneralResponseModel<FormulaModel>> getPagedFormulas(@Path("page") int Page);

    @POST("formula")
    Call<GeneralResponseModel<String>> addFormula( @Body FormulaModel newFormula);

    @PUT("formula/Id/{id}")
    Call<GeneralResponseModel<String>> saveFormula(@Path("id") String id,@Body FormulaModel newFormula);

    @DELETE("formula/Id/{id}")
    Call<GeneralResponseModel<String>> deleteFormula(@Path("id") String id);
}
