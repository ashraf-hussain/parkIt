package com.project.parkit.common;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SetupRetrofit {


    public Retrofit getRetrofit() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(String.valueOf(AppConstant.BASE_URL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

}
