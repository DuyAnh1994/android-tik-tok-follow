package com.solar.dev.tiktok.app.ui.tiktok.follow.recent

import android.view.View
import androidx.fragment.app.viewModels
import com.dev.anhnd.mybaselibrary.adapter.BaseAdapter
import com.dev.anhnd.mybaselibrary.fragment.BaseFragment
import com.dev.anhnd.mybaselibrary.utils.app.observer
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.FragmentFollowRecentBinding
import com.solar.dev.tiktok.app.model.app.RecentUser
import com.solar.dev.tiktok.app.repository.TikTokRepository
import com.solar.dev.tiktok.app.ui.callback.ItemListener
import com.solar.dev.tiktok.app.ui.customview.AppBarTikTok
import com.solar.dev.tiktok.app.ui.dialog.InvalidUrlDialog
import com.solar.dev.tiktok.app.ui.tiktok.common.TikTokActivity
import com.solar.dev.tiktok.app.ui.tiktok.follow.prepare.FollowViewModel
import com.solar.dev.tiktok.app.utils.InjectorUtils

class FollowRecentFragment : BaseFragment<FragmentFollowRecentBinding, TikTokActivity>(),
    AppBarTikTok.AppBarTikTokListener,
    ItemListener.OrderRecentUserListener {

    private val followVM: FollowViewModel by viewModels {
        InjectorUtils.providerLikeAndFollowViewModelFactory()
    }
    private val tikTokRepository by lazy {
        TikTokRepository.getInstance()
    }
    private val adapter by lazy {
        BaseAdapter<RecentUser>(R.layout.item_order_recent_user).apply {
            listener = this@FollowRecentFragment
        }
    }
    private lateinit var recentUser: MutableList<RecentUser>

    override fun getLayoutId() = R.layout.fragment_follow_recent

    override fun setUp() {
        binding.appBar.apply {
            listener = this@FollowRecentFragment
            setTitle(getString(R.string.all_user))
            visibilityStars(View.INVISIBLE)
        }
        binding.rvOrderRecent.adapter = adapter
    }

    override fun observerViewModel() {
        observer(tikTokRepository.getAllUser()) {
            it?.let { list ->
                recentUser = list as MutableList<RecentUser>
                adapter.data = recentUser.reversed()
            }
        }
    }

    override fun onClickBack(v: View) {
        super.onClickBack(v)
        onBackScreen()
    }

    override fun onClickItem(v: View, item: RecentUser) {
        crawlData(item.urlFull)
    }

    private fun crawlData(url: String) {
        followVM.fetchUser(url, {
            // loading -> show loading
            activity.loading(true)
        }, {
            // error -> turn off loading, show dialog
            activity.loading(false)
            InvalidUrlDialog().show(childFragmentManager, "InvalidUrlDialog")
        }, {
            // success -> turn off loading, insert to database, next screen
            activity.loading(false)
            navigateTo(R.id.action_followRecentFragment_to_followDetailFragment)
        })
    }
}