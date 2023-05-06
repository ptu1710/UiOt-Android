package com.ixxc.uiot.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ixxc.uiot.Interface.RecyclerViewItemListener;
import com.ixxc.uiot.Model.Rule;
import com.ixxc.uiot.R;

import java.util.List;

public class RuleAdapter extends RecyclerView.Adapter<RuleAdapter.ViewHolder> {
    private final List<Rule> ruleList;
    private final Context cxt;
    public int checkedPos = -1;
    private final RecyclerViewItemListener ruleListener;

    public RuleAdapter(Context context, List<Rule> rules, RecyclerViewItemListener ruleListener) {
        this.cxt = context;
        this.ruleList = rules;
        this.ruleListener = ruleListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RuleAdapter.ViewHolder(LayoutInflater.from(cxt).inflate(R.layout.recycler_cardview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (checkedPos == -1) {
            holder.tv_name.setBackgroundColor(cxt.getColor(R.color.white));
        } else {
            if (checkedPos == holder.getAbsoluteAdapterPosition()) {
                holder.tv_name.setBackgroundColor(cxt.getColor(R.color.bg2));
            } else {
                holder.tv_name.setBackgroundColor(cxt.getColor(R.color.white));
            }
        }

        holder.tv_name.setText(ruleList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return ruleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_icon;
        TextView tv_name;

        ViewHolder(View itemView) {
            super(itemView);

            iv_icon = itemView.findViewById(R.id.iv_icon);
            tv_name = itemView.findViewById(R.id.tv_name);

            tv_name.setOnClickListener(view -> {
                view.setBackgroundColor(cxt.getColor(R.color.white));

                notifyItemChanged(checkedPos);
                checkedPos = -1;

                ruleListener.onItemClicked(view, getAbsoluteAdapterPosition());
            });
        }
    }
}