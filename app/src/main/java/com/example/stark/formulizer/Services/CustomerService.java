package com.example.stark.formulizer.Services;

import com.example.stark.formulizer.Models.CustomerDetailsModel;
import com.example.stark.formulizer.Models.CustomerListModel;
import com.example.stark.formulizer.Models.CustomerModel;
import com.example.stark.formulizer.Models.FormulaModel;
import com.example.stark.formulizer.Models.GeneralResponseModel;
import com.example.stark.formulizer.Models.PagedGeneralResponseModel;
import com.example.stark.formulizer.Utilities.Constraints;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Stark on 03-03-2017.
 */

public interface CustomerService {
    @GET("customer/search/"+Constraints.CUSTOMER+"/{term}/{page}")
    Call<PagedGeneralResponseModel<CustomerListModel>> searchPagedCustomers(@Path("term") String Term,@Path("page") int Page);

    @GET("customer/search/"+Constraints.CUSTOMER_FORMULA+"/{cid}/{term}")
    Call<GeneralResponseModel<FormulaModel>> searchCustomerFormulas(@Path("cid") String CId, @Path("term") String Term);

    @GET("customer/page/{page}")
    Call<PagedGeneralResponseModel<CustomerListModel>> getAllPagedCustomers(@Path("page") int Page);

   // @GET("customer/customerformulas/{cid}/{page}")
    @GET("customer/customerformulas/{cid}")
    Call<GeneralResponseModel<CustomerModel>> getCustomerFormulas(@Path("cid") String CId);
   //Call<GeneralResponseModel<CustomerModel>> getCustomerFormulas(@Path("cid") String CId,@Path("page") int Page);

    @POST("customer")
    Call<GeneralResponseModel<String>> addCustomer( @Body CustomerDetailsModel newFormula);
}
