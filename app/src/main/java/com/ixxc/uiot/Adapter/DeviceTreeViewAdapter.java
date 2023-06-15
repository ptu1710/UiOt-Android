package com.ixxc.uiot.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeViewAdapter;
import com.amrdeveloper.treeview.TreeViewHolder;
import com.amrdeveloper.treeview.TreeViewHolderFactory;
import com.ixxc.uiot.DeviceInfoActivity;
import com.ixxc.uiot.Model.Device;
import com.ixxc.uiot.R;
import com.ixxc.uiot.Utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class DeviceTreeViewAdapter extends TreeViewAdapter {
    List<TreeNode> treeNodes = new ArrayList<>();
    List<Device> deviceList;

    public DeviceTreeViewAdapter(TreeViewHolderFactory factory, List<Device> devices) {
        super(factory);
        deviceList = devices;
        InitNodes(devices);
        updateTreeNodes(treeNodes);
    }

    private void InitNodes(List<Device> devices) {
        Deque<Device> deviceQueue = new ArrayDeque<>(devices);

        List<Device> deviceSkipped = new ArrayList<>();

        for (Device device : deviceQueue) {
            deviceQueue.remove(device);

            if (device.path.size() <= 1) {
                treeNodes.add(new TreeNode(device, R.layout.device_layout));
            } else {
                TreeNode parentNode = getParentNode(treeNodes, device.getParent());

                if (parentNode == null) deviceSkipped.add(device);
                else parentNode.addChild(new TreeNode(device, R.layout.device_layout));
            }
        }

        if (!deviceSkipped.isEmpty()) InitNodes(deviceSkipped);
    }

    private TreeNode getParentNode(List<TreeNode> roots, Device parentDevice) {
        for (TreeNode node : roots) {
            if (node.getValue() == parentDevice) return node;

            if (node.getChildren().size() > 0) {
                TreeNode child = getParentNode(node.getChildren(), parentDevice);
                if (child != null) return child;
            }
        }

        return null;
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull TreeViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    public static class MyViewHolder extends TreeViewHolder {
        private final Context ctx;
        private final TextView tv_name;
        private final ImageView iv_expand, iv_icon, iv_go;
        private final CardView cv_device;

        public MyViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            this.ctx = context;

            tv_name = itemView.findViewById(R.id.tv_name);
            iv_expand = itemView.findViewById(R.id.iv_expand_3);
            iv_icon = itemView.findViewById(R.id.iv_icon);
            iv_go = itemView.findViewById(R.id.iv_go);
            cv_device = itemView.findViewById(R.id.cv_device);
        }

        @Override
        public void bindTreeNode(TreeNode node) {
            super.bindTreeNode(node);

            Device device = (Device) node.getValue();
            if (device == null) return;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    Utils.dpToPx(ctx, 54));

            int left = Utils.dpToPx(ctx, 16 * (node.getLevel() + 1));
            int right = Utils.dpToPx(ctx, 16);
            int others = Utils.dpToPx(ctx, 6);

            params.setMargins(left, others, right, others);
            cv_device.setLayoutParams(params);

            if (node.getChildren().size() == 0) {
                iv_expand.setVisibility(View.INVISIBLE);
            } else {
                iv_expand.setVisibility(View.VISIBLE);

                if (node.isExpanded()) iv_expand.setRotation(90);
                else iv_expand.setRotation(0);
            }

            tv_name.setText(device.name);
            iv_icon.setImageDrawable(device.getIconDrawable(ctx));

            iv_go.setOnClickListener(view -> {
                Intent toDetails = new Intent(ctx, DeviceInfoActivity.class);
                toDetails.putExtra("DEVICE_ID", device.id);
                ctx.startActivity(toDetails);
            });

//            if (selectedPosition != -1 && (getAbsoluteAdapterPosition() >= selectedPosition + 1 && getAbsoluteAdapterPosition() <= selectedPosition + node.getChildren().size())) {
//                itemView.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.devices_rv_anim));
//                selectedPosition = -1;
//            }
        }
    }
}
