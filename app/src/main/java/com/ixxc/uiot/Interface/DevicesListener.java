package com.ixxc.uiot.Interface;

import android.view.View;

import com.ixxc.uiot.Model.Device;

public interface DevicesListener {
    void onItemClicked (View v, Device device);

    void onItemLongClicked (View v, Device device);
}
