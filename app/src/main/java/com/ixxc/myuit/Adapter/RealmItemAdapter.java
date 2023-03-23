package com.ixxc.myuit.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ixxc.myuit.Model.Realm;
import com.ixxc.myuit.Model.Role;
import com.ixxc.myuit.R;

import java.util.List;

public class RealmItemAdapter extends RecyclerView.Adapter<RealmItemAdapter.ViewHolder> {
    Context context;
    List<Realm> realms;
    ViewHolder viewHolder;
    private ItemClickListener mClickListener;

    public RealmItemAdapter(Context context, List<Realm> realms) {
        this.context = context;
        this.realms = realms;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.realmset_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        viewHolder = holder;

        holder.tv_name.setText(realms.get(position).name);
        holder.tv_f_name.setText(realms.get(position).displayName);
        if (realms.get(position).enabled) {
            holder.tv_status.setText("Enabled");
        } else {
            holder.tv_status.setText("Disabled");
        }
    }

    @Override
    public int getItemCount() {
        return realms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_name, tv_f_name,tv_status;
        LinearLayout layout_3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_f_name = itemView.findViewById(R.id.tv_realm_f_name);
            tv_name = itemView.findViewById(R.id.tv_realm_name);
            tv_status = itemView.findViewById(R.id.tv_status);
            layout_3 = itemView.findViewById(R.id.add_realm_layout);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition(), realms.get(getAdapterPosition()));
            }
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position, Realm realm);
    }
}
