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
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ixxc.myuit.API.APIManager;
import com.ixxc.myuit.Adapter.UserItemAdapter;
import com.ixxc.myuit.Model.User;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {
    ImageView iv_user;
    TextView tv_username;
    RecyclerView rv_user_item;
    Context ctx;
    User user;

    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();


        return false;
    });

    public UserFragment() { }

    public UserFragment(Context context) {
        this.ctx = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        iv_user = view.findViewById(R.id.iv_user);
        tv_username = view.findViewById(R.id.tv_username_1);
        rv_user_item = view.findViewById(R.id.rv_user_info);

        user = User.getUser();

        tv_username.setText(user.getDisplayName());

        List<String> items = new ArrayList<>();
        items.add("Account");
        items.add("Users");
        items.add("Realm");
        items.add("Roles");

        UserItemAdapter adapter = new UserItemAdapter(ctx, items);
        adapter.setClickListener(new UserItemAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 1) {
                    ctx.startActivity(new Intent(ctx, UsersActivity.class));
                }
                else if (position == 2) {
                    ctx.startActivity(new Intent(ctx, RealmActivity.class));
                }
                else if (position == 3) {
                    ctx.startActivity(new Intent(ctx, RoleActivity.class));
                }
            }

        });

        rv_user_item.setLayoutManager(new LinearLayoutManager(ctx));
        rv_user_item.setAdapter(adapter);

        new Thread(() -> {
//            Message msg = handler.obtainMessage();
//            Bundle bundle = new Bundle();
//            bundle.putString("USER", User.getUser().username);
//            msg.setData(bundle);
//            handler.sendMessage(msg);
        }).start();

        super.onViewCreated(view, savedInstanceState);
    }
}