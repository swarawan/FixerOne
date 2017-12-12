package com.swarawan.fixerone.module.converter.presenter;

import com.swarawan.fixerone.module.converter.model.Rate;

import java.util.List;
import java.util.Map;

/**
 * Created by rioswarawan on 12/12/17.
 */

public interface OnConvertListener {

    void onShowDialog();

    void onHideDialog();

    void onLatestFetched(String date, List<String> countryCodes);

    void onBaseFetched(Map<String, Double> rates);

    void onError(String message);
}
