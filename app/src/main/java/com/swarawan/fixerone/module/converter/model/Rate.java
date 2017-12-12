package com.swarawan.fixerone.module.converter.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by rioswarawan on 12/12/17.
 */

public class Rate {

    @SerializedName("base")
    public String base;

    @SerializedName("date")
    public String date;

    @SerializedName("rates")
    public Map<String, Double> rates;
}
