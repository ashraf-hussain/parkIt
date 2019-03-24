package com.project.parkit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface ParkingApiInterface {
    @GET("search")
    Call<List<ParkingModel>> getSearchData(@Query("lat") String latitude,
                                           @Query("lng") String longitude);

    @POST("{id}/reserve/")
    Call<ReservationBody> reserveSpot(@Body ReservationBody reservationBody,
                                      @Path("id") int id);

    @PATCH("{id}")
    Call<ReservationBody> updateReserveSpot(@Body ReservationBody reservationBody,
                                      @Path("id") int id);

    @GET("{id}")
    Call<ParkingModel> getReservationDetail(@Path("id") int id);
}
