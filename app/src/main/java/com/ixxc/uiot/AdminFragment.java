package com.ixxc.uiot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ixxc.uiot.Adapter.UserItemAdapter;
import com.ixxc.uiot.Model.User;

import java.util.ArrayList;
import java.util.List;

public class AdminFragment extends Fragment {
    HomeActivity homeActivity;
    ImageView iv_user;
    TextView tv_username;
    RecyclerView rv_admin_item;
    Context ctx;
    User me;

    public AdminFragment() { }

    public AdminFragment(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
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
        ctx = homeActivity;

        iv_user = view.findViewById(R.id.iv_user);
        tv_username = view.findViewById(R.id.tv_username_1);
        rv_admin_item = view.findViewById(R.id.rv_admin_item);

        me = User.getMe();

        tv_username.setText(me.getDisplayName());

        List<String> items = new ArrayList<>();
        items.add("Account");
        if (me.canWriteAdmin()) {
            items.add("Users");
            items.add("Realm");
            items.add("Roles");
        }
        items.add("Sign out");

        UserItemAdapter adapter = new UserItemAdapter(ctx, items);
        adapter.setClickListener((view1, position) -> {
            switch (position) {
                case 0:
                    Toast.makeText(ctx, "Feature under development!", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    if (me.canWriteAdmin()) {
                        ctx.startActivity(new Intent(ctx, UsersActivity.class));
                    } else {
                        User.setMe(null);
                        ctx.startActivity(new Intent(ctx, LoginActivity.class));
                        homeActivity.finish();
                    }
                    break;
                case 2:
                    ctx.startActivity(new Intent(ctx, RealmActivity.class));
                    break;
                case 3:
                    ctx.startActivity(new Intent(ctx, RoleActivity.class));
                    break;
                case 4:
                    User.setMe(null);
                    ctx.startActivity(new Intent(ctx, LoginActivity.class));
                    homeActivity.finish();
                    break;
                default:
                    Toast.makeText(ctx, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        rv_admin_item.setLayoutManager(new LinearLayoutManager(ctx));
        rv_admin_item.setAdapter(adapter);

        super.onViewCreated(view, savedInstanceState);
    }
}