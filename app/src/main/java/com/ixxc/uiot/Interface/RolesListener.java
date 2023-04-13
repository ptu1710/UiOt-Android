package com.ixxc.uiot.Interface;

import android.view.View;

import com.ixxc.uiot.Model.Role;

public interface RolesListener {
    void onItemClicked (View v, Role role, boolean isChecked);
}
