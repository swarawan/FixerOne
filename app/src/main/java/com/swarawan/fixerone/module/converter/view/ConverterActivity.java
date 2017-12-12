package com.swarawan.fixerone.module.converter.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.swarawan.fixerone.R;
import com.swarawan.fixerone.cache.GlobalCache;
import com.swarawan.fixerone.module.converter.presenter.ConverterPresenter;
import com.swarawan.fixerone.module.converter.presenter.OnConvertListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by rioswarawan on 12/12/17.
 */

public class ConverterActivity extends AppCompatActivity {

    private ProgressDialog dialog;
    private ConverterPresenter presenter;
    private ArrayAdapter<String> adapter;
    private List<String> data;
    private Map<String, Double> rates;
    private boolean isDarkTheme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDarkTheme = GlobalCache.read(GlobalCache.KEY_DARK_THEME, Boolean.class);
        setTheme(isDarkTheme ? R.style.AppThemeDark : R.style.AppThemeDefault);
        setContentView(R.layout.activity_converter);

        initView();

        presenter = new ConverterPresenter(onConvertListener);
        presenter.getCountryCodes();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_theme:
                changeTheme();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeTheme() {
        GlobalCache.write(GlobalCache.KEY_DARK_THEME, !isDarkTheme, Boolean.class);
        Intent intent = new Intent(this, ConverterActivity.class);
        startActivity(intent);
        finish();
    }

    private void initView() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);

        data = new ArrayList<>();
        rates = new HashMap<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        ((Spinner) findViewById(R.id.spinner_target)).setAdapter(adapter);
        ((Spinner) findViewById(R.id.spinner_source)).setAdapter(adapter);
        ((Spinner) findViewById(R.id.spinner_source)).setOnItemSelectedListener(onItemSelectedListener);

        findViewById(R.id.layout).setBackgroundResource(isDarkTheme ? R.color.background2 : R.color.background1);
        ((EditText) findViewById(R.id.input_source)).addTextChangedListener(valueTextWatcher);
    }

    private TextWatcher valueTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String source = ((Spinner) findViewById(R.id.spinner_source)).getSelectedItem().toString();
            String target = ((Spinner) findViewById(R.id.spinner_target)).getSelectedItem().toString();
            String value = s.toString();

            if (value.equals("") || rates.size() == 0) {
                ((EditText) findViewById(R.id.input_target)).setText("");
            } else if (source.equals(target)) {
                ((EditText) findViewById(R.id.input_target)).setText(value);
            } else {
                Double targetVal = rates.get(target);
                Double numVal = Double.valueOf(value);
                Double result = numVal * targetVal;
                String stringResult = String.format("%f", result, Locale.US);
                ((EditText) findViewById(R.id.input_target)).setText(stringResult);
            }
        }
    };

    private OnConvertListener onConvertListener = new OnConvertListener() {
        @Override
        public void onShowDialog() {
            if (!dialog.isShowing()) {
                dialog.show();
            }
        }

        @Override
        public void onHideDialog() {
            if (dialog.isShowing()) {
                dialog.hide();
            }
        }

        @Override
        public void onLatestFetched(String date, List<String> countryCodes) {
            if (!date.equals("")) {
                ((TextView) findViewById(R.id.text_latests_date)).setText(date);
            }

            data.addAll(countryCodes);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onBaseFetched(Map<String, Double> newRates) {
            rates.clear();
            for (Map.Entry<String, Double> entry : newRates.entrySet()) {
                rates.put(entry.getKey(), entry.getValue());
            }
        }

        @Override
        public void onError(String message) {
            Toast.makeText(ConverterActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String newSource = data.get(position);
            presenter.getBase(newSource);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}
