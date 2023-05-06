package com.ixxc.uiot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ixxc.uiot.Model.User;

import java.util.Arrays;
import java.util.List;

public class CreateRuleFragment_2 extends Fragment {
    CreateRuleActivity parentActivity;
    AutoCompleteTextView act_actions;

    public CreateRuleFragment_2() { }

    public CreateRuleFragment_2(CreateRuleActivity parentActivity) {
        this.parentActivity = parentActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_rule_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        InitViews(view);
        InitVars();
        InitEvents();

        super.onViewCreated(view, savedInstanceState);
    }

    private void InitViews(View view) {
        act_actions = view.findViewById(R.id.act_actions);
    }

    private void InitVars() {
        List<String> actions = Arrays.asList("Push notification", "Email");
        act_actions.setAdapter(new ArrayAdapter<>(parentActivity, R.layout.dropdown_item, actions));
    }

    private void InitEvents() {
        act_actions.setOnItemClickListener((adapterView, view, i, l) -> {
            if (i == 0) parentActivity.rule.setRuleAction("notification");

            parentActivity.rule.setTargetIds(User.getMe().id);
        });
    }
}