package com.swarawan.fixerone.module.converter.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.swarawan.fixerone.cache.GlobalCache;
import com.swarawan.fixerone.module.converter.model.Rate;
import com.swarawan.fixerone.network.NetworkFactory;
import com.swarawan.fixerone.network.NetworkService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by rioswarawan on 12/12/17.
 */

public class ConverterPresenter {

    private CompositeSubscription subscription;
    private NetworkService service;
    private OnConvertListener listener;

    public ConverterPresenter(OnConvertListener listener) {
        this.service = NetworkFactory.create();
        this.subscription = new CompositeSubscription();
        this.listener = listener;
    }

    public void destroy() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public void getBase(String source) {

        listener.onShowDialog();
        subscription.add(service.getBase(source)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Rate>() {
                    @Override
                    public void onCompleted() {
                        listener.onHideDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onError(e.getMessage());
                        listener.onHideDialog();
                    }

                    @Override
                    public void onNext(Rate rate) {
                        listener.onBaseFetched(rate.rates);
                    }
                }));
    }

    public void getCountryCodes() {
        String countryCodesJson = GlobalCache.read(GlobalCache.KEY_COUNTRY_CODE, String.class);
        if (!countryCodesJson.equals("")) {
            Type type = new TypeToken<List<String>>() {
            }.getType();
            List<String> countryCodes = new Gson().fromJson(countryCodesJson, type);
            listener.onLatestFetched("", countryCodes);
        } else {
            getLatest();
        }
    }

    private void getLatest() {
        listener.onShowDialog();
        subscription.add(service.getLatest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Rate>() {
                    @Override
                    public void onCompleted() {
                        listener.onHideDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onError(e.getMessage());
                        listener.onHideDialog();
                    }

                    @Override
                    public void onNext(Rate rate) {
                        List<String> countryCodes = new ArrayList<>();
                        for (Map.Entry<String, Double> entry : rate.rates.entrySet()) {
                            countryCodes.add(entry.getKey());
                        }
                        GlobalCache.write(GlobalCache.KEY_COUNTRY_CODE, new Gson().toJson(countryCodes), String.class);
                        listener.onLatestFetched(rate.date, countryCodes);
                    }
                }));
    }
}
