package com.ixxc.uiot.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ixxc.uiot.Model.Device;
import com.ixxc.uiot.Model.Model;
import com.ixxc.uiot.R;

import java.util.ArrayList;
import java.util.List;

public class DeviceArrayAdapter extends ArrayAdapter<Model> {
    private final List<Model> itemsAll;
    private final List<Model> suggestions;

    public DeviceArrayAdapter(@NonNull Context context, int resource, @NonNull List<Model> list) {
        super(context, resource, list);

        this.itemsAll = new ArrayList<>(list);
        this.suggestions = new ArrayList<>();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) view = LayoutInflater.from(getContext()).inflate(R.layout.dropdown_item_1, parent, false);

        Model model = getItem(position);
        String type = model.assetDescriptor.get("name").getAsString();

        TextView tv_name = view.findViewById(R.id.tv_name);
        ImageView iv_icon = view.findViewById(R.id.iv_icon);
        tv_name.setText(type);
        Device device = new Device(type);
        iv_icon.setImageResource(device.getIconRes());

        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        public String convertResultToString(Object resultValue) {
            return ((Model) (resultValue)).assetDescriptor.get("name").getAsString();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                suggestions.addAll(itemsAll);

                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else return new FilterResults();
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Model> filteredList = (List<Model>) results.values;
            if (results.count > 0) {
                clear();
                for (Model c : filteredList) { add(c); }
                notifyDataSetChanged();
            }
        }
    };
}
