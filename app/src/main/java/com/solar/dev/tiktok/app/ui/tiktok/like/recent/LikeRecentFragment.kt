package com.solar.dev.tiktok.app.ui.tiktok.like.recent

import android.view.View
import androidx.fragment.app.viewModels
import com.dev.anhnd.mybaselibrary.adapter.BaseAdapter
import com.dev.anhnd.mybaselibrary.fragment.BaseFragment
import com.dev.anhnd.mybaselibrary.utils.app.observer
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.FragmentLikeRecentBinding
import com.solar.dev.tiktok.app.model.app.RecentVideo
import com.solar.dev.tiktok.app.repository.TikTokRepository
import com.solar.dev.tiktok.app.ui.callback.ItemListener
import com.solar.dev.tiktok.app.ui.customview.AppBarTikTok
import com.solar.dev.tiktok.app.ui.dialog.InvalidUrlDialog
import com.solar.dev.tiktok.app.ui.tiktok.common.TikTokActivity
import com.solar.dev.tiktok.app.ui.tiktok.like.prepare.LikeViewModel
import com.solar.dev.tiktok.app.utils.InjectorUtils

class LikeRecentFragment : BaseFragment<FragmentLikeRecentBinding, TikTokActivity>(),
    AppBarTikTok.AppBarTikTokListener,
    ItemListener.OrderRecentVideoListener {

    private val likeVM: LikeViewModel by viewModels {
        InjectorUtils.providerLikeAndFollowViewModelFactory()
    }
    private val tikTokRepository by lazy {
        TikTokRepository.getInstance()
    }
    private val adapter by lazy {
        BaseAdapter<RecentVideo>(R.layout.item_order_recent_video).apply {
            listener = this@LikeRecentFragment
        }
    }
    private lateinit var recentVideo: MutableList<RecentVideo>


    override fun getLayoutId() = R.layout.fragment_like_recent

    override fun setUp() {
        binding.appBar.apply {
            listener = this@LikeRecentFragment
            setTitle(getString(R.string.all_video))
            visibilityStars(View.INVISIBLE)
        }
        binding.rvOrderRecent.adapter = adapter
    }

    override fun observerViewModel() {
        observer(tikTokRepository.getAllVideo()) {
            it?.let { list ->
                recentVideo = list as MutableList<RecentVideo>
                adapter.data = recentVideo.reversed()
            }
        }
    }

    override fun onClickBack(v: View) {
        super.onClickBack(v)
        onBackScreen()
    }

    override fun onClickItem(v: View, item: RecentVideo) {
        crawlData(item.urlFull)
    }

    private fun crawlData(url: String) {
        likeVM.fetchVideo(url, {
            // loading -> show loading
            activity.loading(true)
        }, {
            // error -> turn off loading, show dialog
            activity.loading(false)
            InvalidUrlDialog().show(childFragmentManager, "InvalidUrlDialog")
        }, {
            // success -> turn off loading, next screen
            activity.loading(false)
            navigateTo(R.id.action_likeRecentFragment_to_likeDetailFragment)
        })
    }

}