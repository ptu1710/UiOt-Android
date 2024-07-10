package com.ixxc.uiot.Interface;

import android.view.View;

public interface RecyclerViewItemListener {
    void onItemClicked (View v, Object item);

    void onItemLongClicked (View v, Object item);
}
