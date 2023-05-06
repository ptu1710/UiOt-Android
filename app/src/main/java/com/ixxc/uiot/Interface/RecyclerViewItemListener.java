package com.ixxc.uiot.Interface;

import android.view.View;

public interface RecyclerViewItemListener {
    void onItemClicked (View v, int pos);

    void onItemLongClicked (View v, int pos);
}
