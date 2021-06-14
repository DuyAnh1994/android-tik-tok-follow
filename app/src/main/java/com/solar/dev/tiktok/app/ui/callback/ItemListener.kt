package com.solar.dev.tiktok.app.ui.callback

import android.view.View
import com.dev.anhnd.mybaselibrary.adapter.BaseAdapter
import com.solar.dev.tiktok.app.model.app.*

class ItemListener {

    interface RequestListener : BaseAdapter.ListItemListener {

        fun onClickItem(v: View, item: Request)
    }

    interface OrderListener : BaseAdapter.ListItemListener {

        fun onClickItem(v: View, item: Request)
    }

    interface OrderRecentVideoListener : BaseAdapter.ListItemListener {

        fun onClickItem(v: View, item: RecentVideo)
    }

    interface OrderRecentUserListener : BaseAdapter.ListItemListener {

        fun onClickItem(v: View, item: RecentUser)
    }

    interface EditVideoListener : BaseAdapter.ListItemListener {

        fun onClickItem(v: View, item: Video)
    }

    interface GalleryListener : BaseAdapter.ListItemListener {

        fun onClickItem(v: View, item: Gallery)
    }
}