package com.ixxc.myuit.Interface;

import android.view.View;

import com.ixxc.myuit.Model.Device;

public interface UsersListener {
    void onItemClicked (View v, int pos);

    void onItemLongClicked (View v, int pos);
}
