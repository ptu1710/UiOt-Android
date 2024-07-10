package com.ixxc.uiot;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.ixxc.uiot.Model.Rule;
import com.ixxc.uiot.Utils.Util;

import java.util.Objects;

public class CreateRuleFragment_0 extends Fragment {
    CreateRuleActivity parentActivity;
    Button btn_next, btn_back;

    public CreateRuleFragment_0() {
    }

    public CreateRuleFragment_0(CreateRuleActivity parentActivity) {
        this.parentActivity = parentActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_rule_0, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText til_name = view.findViewById(R.id.til_name);
        til_name.setOnFocusChangeListener((view1, focused) -> {
            if (!focused) {
                String name = Objects.requireNonNull(til_name.getText()).toString();
                parentActivity.rule.setRuleName(name);
                Log.d(Util.LOG_TAG, "setRuleName: " + name);
            }
        });

        til_name.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                textView.clearFocus();
                // TODO: Hide keyboard
            }

            return false;
        });

        RadioButton rb_always = view.findViewById(R.id.rb_always);
        rb_always.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                JsonObject recurrence = new JsonObject();
                recurrence.addProperty("mins", 0);

                parentActivity.rule.setRecurrence(recurrence);
                Log.d(Util.LOG_TAG, "setRecurrence");
            }
        });

        RadioGroup rg_trigger = view.findViewById(R.id.rg_trigger);
        rg_trigger.setOnCheckedChangeListener((radioGroup, i) -> til_name.clearFocus());

        if (parentActivity.chose != null) {
            til_name.setText(Rule.rule_selected.getName());
        }

        btn_next = view.findViewById(R.id.btn_next);
        btn_next.setOnClickListener(view12 -> parentActivity.changeTab(1));

        btn_back = view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(view13 -> parentActivity.finish());
    }

}