package com.ixxc.uiot.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ixxc.uiot.Interface.UsersListener;
import com.ixxc.uiot.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private final List<String> userList;
    private final LayoutInflater mInflater;
    private final Context cxt;

    public int checkedPos = -1;

    private final UsersListener usersListener;

    public UserAdapter(List<String> users, UsersListener usersListener, Context context) {
        this.cxt = context;
        this.mInflater = LayoutInflater.from(context);
        this.userList = users;
        this.usersListener = usersListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.user_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (checkedPos == -1) {
            holder.tv_user.setBackgroundColor(cxt.getColor(R.color.white));
        } else {
            if (checkedPos == holder.getAdapterPosition()) {
                holder.tv_user.setBackgroundColor(cxt.getColor(R.color.bg2));
            } else {
                holder.tv_user.setBackgroundColor(cxt.getColor(R.color.white));
            }
        }

        holder.tv_user.setText(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_user;
        View v_line;

        ViewHolder(View itemView) {
            super(itemView);
            tv_user = itemView.findViewById(R.id.tv_user);
            v_line = itemView.findViewById(R.id.v_line);

            tv_user.setOnClickListener(view -> {
                view.setBackgroundColor(cxt.getColor(R.color.white));

                notifyItemChanged(checkedPos);
                checkedPos = -1;

                usersListener.onItemClicked(view, getAdapterPosition());
            });

            tv_user.setOnLongClickListener(view -> {
                usersListener.onItemLongClicked(view, getAdapterPosition());

                view.setBackgroundColor(cxt.getColor(R.color.bg2));

                if (checkedPos != getAdapterPosition()) {
                    notifyItemChanged(checkedPos);
                    checkedPos = getAdapterPosition();
                }

                return true;
            });
        }
    }

    String getItem(int id) {
        return userList.get(id);
    }
}