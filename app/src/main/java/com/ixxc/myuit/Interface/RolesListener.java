package com.ixxc.myuit.Interface;

import android.view.View;

import com.ixxc.myuit.Model.Device;
import com.ixxc.myuit.Model.Role;

public interface RolesListener {
    void onItemClicked (View v, Role role, boolean isChecked);
}
