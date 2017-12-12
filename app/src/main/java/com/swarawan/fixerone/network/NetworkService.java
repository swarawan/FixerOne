package com.swarawan.fixerone.network;

import com.swarawan.fixerone.module.converter.model.Rate;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by rioswarawan on 12/12/17.
 */

public interface NetworkService {

    @GET("/latest")
    Observable<Rate> getLatest();

    @GET("/latest")
    Observable<Rate> getBase(@Query("base") String base);
}
