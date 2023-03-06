package com.ixxc.myuit.Interface;

import android.view.View;

import com.ixxc.myuit.Model.Device;

public interface DevicesListener {
    void onItemClicked (Device device);

    void onItemLongClicked (View v, Device device);
}
