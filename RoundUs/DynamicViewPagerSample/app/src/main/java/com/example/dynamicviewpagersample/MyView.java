package com.example.dynamicviewpagersample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class MyView {
    private LayoutInflater inflater;
    private View _view;
    private String value;

    public MyView(Context context, String value) {
        inflater = LayoutInflater.from(context);
        _view = inflater.inflate(R.layout.my_layout, null);
        this.value = value;
        changeValue();
    }

    private void changeValue() {
        TextView tvFirst = _view.findViewById(R.id.tvLayoutFrame);
        tvFirst.setText(value);
    }

    public View getView() {
        return _view;
    }
}
