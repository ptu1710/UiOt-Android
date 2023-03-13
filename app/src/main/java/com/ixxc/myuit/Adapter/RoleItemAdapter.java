package com.ixxc.myuit.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ixxc.myuit.Model.Role;
import com.ixxc.myuit.R;

import java.util.List;

public class RoleItemAdapter extends RecyclerView.Adapter<RoleItemAdapter.ViewHolder> {

    Context context;
    List<Role> roles;
    private UserItemAdapter.ItemClickListener mClickListener;

    public RoleItemAdapter(Context context, List<Role> roles) {
        this.context = context;
        this.roles = roles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.role_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tv_Name_val.setText(roles.get(position).name);
        holder.tv_Description_val.setText(roles.get(position).description);

    }

    @Override
    public int getItemCount() {
        return roles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_Name_val,tv_Description_val;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_Description_val= itemView.findViewById(R.id.tv_description_val);
            tv_Name_val= itemView.findViewById(R.id.tv_name_val);
            itemView.setOnClickListener((View.OnClickListener) this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public void setClickListener(UserItemAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
