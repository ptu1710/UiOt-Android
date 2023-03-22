package com.ixxc.myuit.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ixxc.myuit.Model.Role;
import com.ixxc.myuit.R;

import java.util.List;

public class RoleItemAdapter extends RecyclerView.Adapter<RoleItemAdapter.ViewHolder> {
    Context context;
    List<Role> roles;
    ViewHolder viewHolder;
    private ItemClickListener mClickListener;

    public RoleItemAdapter(Context context, List<Role> roles) {
        this.context = context;
        this.roles = roles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.roleset_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        viewHolder = holder;

        holder.tv_name.setText(roles.get(position).name);
        holder.tv_desc.setText(roles.get(position).description);
    }

    @Override
    public int getItemCount() {
        return roles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_name, tv_desc;
        LinearLayout layout_2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_desc = itemView.findViewById(R.id.tv_role_desc);
            tv_name = itemView.findViewById(R.id.tv_role_name);
            layout_2 = itemView.findViewById(R.id.add_role_layout);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition(), roles.get(getAdapterPosition()));
            }
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position, Role role);
    }
}
