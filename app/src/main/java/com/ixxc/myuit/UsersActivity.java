package com.ixxc.myuit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ixxc.myuit.API.APIManager;
import com.ixxc.myuit.Adapter.AttributesAdapter;
import com.ixxc.myuit.Adapter.UserAdapter;
import com.ixxc.myuit.Adapter.UserItemAdapter;
import com.ixxc.myuit.Model.Role;
import com.ixxc.myuit.Model.User;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {
    Toolbar toolbar;
    ActionBar actionBar;
    TextView tv_forbidden;
    RecyclerView rv_users;

    List<User> userList;

    List<String> userNameList;

    Handler handler = new Handler(message -> {
        Bundle bundle = message.getData();
        int code = bundle.getInt("CODE");

        if (code == 403) {
            tv_forbidden.setVisibility(View.VISIBLE);
        } else if (code == 200) {
            rv_users.setVisibility(View.VISIBLE);
            userList = User.getUsersList();
            showUsers();
        } else {
            // Something went wrong here
            Toast.makeText(this, "Request failed with unknown error!", Toast.LENGTH_SHORT).show();
        }

        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        InitVars();
        InitViews();

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Users");
        actionBar.setDisplayHomeAsUpEnabled(true);

        new Thread(() -> {
            String queryString = "{\"realmPredicate\": {\"name\": \"master\"}}";
            JsonParser jsonParser = new JsonParser();
            JsonObject query = (JsonObject)jsonParser.parse(queryString);

            Log.d(GlobalVars.LOG_TAG, query.toString());

            int code = APIManager.queryUsers(query);

            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("CODE", code);
            message.setData(bundle);
            handler.sendMessage(message);
        }).start();
    }

    private void InitVars() {
        userNameList = new ArrayList<>();
    }

    private void InitViews() {
        tv_forbidden = findViewById(R.id.tv_forbidden);
        rv_users = findViewById(R.id.rv_users);
        toolbar = findViewById(R.id.action_bar);
    }

    private void showUsers() {
        for (User user : userList) {
            userNameList.add(user.getDisplayName());
        }

        UserAdapter adapter = new UserAdapter(this, userNameList);
        adapter.setClickListener(new UserAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(UsersActivity.this, UserInfoActivity.class);
                intent.putExtra("USER_ID", userList.get(position).id);
                startActivity(intent);
            }
        });

        rv_users.setLayoutManager(new LinearLayoutManager(this));
        rv_users.setAdapter(adapter);

        rv_users.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}