package com.ixxc.myuit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ixxc.myuit.Adapter.UserItemAdapter;
import com.ixxc.myuit.Model.User;

import java.util.ArrayList;
import java.util.List;

public class AdminFragment extends Fragment {
    ImageView iv_user;
    TextView tv_username;
    RecyclerView rv_admin_item;
    Context ctx;
    User user;

    public AdminFragment() { }

    public AdminFragment(Context context) {
        this.ctx = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        iv_user = view.findViewById(R.id.iv_user);
        tv_username = view.findViewById(R.id.tv_username_1);
        rv_admin_item = view.findViewById(R.id.rv_admin_item);

        user = User.getMe();

        tv_username.setText(user.getDisplayName());

        List<String> items = new ArrayList<>();
        items.add("Account");
        items.add("Users");
        items.add("Realm");
        items.add("Roles");

        UserItemAdapter adapter = new UserItemAdapter(ctx, items);
        adapter.setClickListener((view1, position) -> {
            switch (position) {
                case 1:
                    ctx.startActivity(new Intent(ctx, UsersActivity.class));
                    break;
                case 2:
                    ctx.startActivity(new Intent(ctx, RealmActivity.class));
                    break;
                case 3:
                    ctx.startActivity(new Intent(ctx, RoleActivity.class));
                    break;
                default:
                    break;
            }
        });

        rv_admin_item.setLayoutManager(new LinearLayoutManager(ctx));
        rv_admin_item.setAdapter(adapter);

        super.onViewCreated(view, savedInstanceState);
    }
}