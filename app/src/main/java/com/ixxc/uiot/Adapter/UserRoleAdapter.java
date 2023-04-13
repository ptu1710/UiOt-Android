package com.ixxc.uiot.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ixxc.uiot.Interface.RolesListener;
import com.ixxc.uiot.Model.Role;
import com.ixxc.uiot.R;

import java.util.List;

public class UserRoleAdapter extends ArrayAdapter<Role> {
    private final RolesListener rolesListener;

    public UserRoleAdapter(@NonNull Context context, int resource, @NonNull List<Role> objects, RolesListener rolesListener) {
        super(context, resource, objects);
        this.rolesListener = rolesListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) { view = LayoutInflater.from(getContext()).inflate(R.layout.user_role_item, parent, false); }

        Role role = getItem(position);

        TextView mTextView = view.findViewById(R.id.text);
        CheckBox mCheckbox = view.findViewById(R.id.checkbox);
        mTextView.setText(role.name);
        mCheckbox.setChecked(role.assigned);

        mTextView.setOnClickListener(view1 -> {
            mCheckbox.setChecked(!mCheckbox.isChecked());
            rolesListener.onItemClicked(view1, role, mCheckbox.isChecked());
        });

        mCheckbox.setOnClickListener(view1 -> rolesListener.onItemClicked(view1, role, mCheckbox.isChecked()));

        return view;
    }
}
