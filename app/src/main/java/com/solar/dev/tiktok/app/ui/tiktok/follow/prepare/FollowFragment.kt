package com.solar.dev.tiktok.app.ui.tiktok.follow.prepare

import android.content.Intent
import android.view.View
import androidx.fragment.app.viewModels
import com.dev.anhnd.mybaselibrary.adapter.BaseAdapter
import com.dev.anhnd.mybaselibrary.fragment.BaseFragment
import com.dev.anhnd.mybaselibrary.utils.app.observer
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.FragmentFollowBinding
import com.solar.dev.tiktok.app.model.app.RecentUser
import com.solar.dev.tiktok.app.repository.TikTokRepository
import com.solar.dev.tiktok.app.ui.callback.ItemListener
import com.solar.dev.tiktok.app.ui.customview.AppBarTikTok
import com.solar.dev.tiktok.app.ui.customview.EditTextTikTok
import com.solar.dev.tiktok.app.ui.dialog.InvalidUrlDialog
import com.solar.dev.tiktok.app.ui.tiktok.common.TikTokActivity
import com.solar.dev.tiktok.app.ui.tiktok.getstar.common.GetStarActivity
import com.solar.dev.tiktok.app.utils.InjectorUtils
import com.solar.dev.tiktok.app.utils.animNext
import com.solar.dev.tiktok.app.utils.getStarsOfApp

class FollowFragment : BaseFragment<FragmentFollowBinding, TikTokActivity>(),
    AppBarTikTok.AppBarTikTokListener,
    FollowListener.DataBindingListener,
    EditTextTikTok.EditTextTikTokListener,
    ItemListener.OrderRecentUserListener {

    private lateinit var recentUser: MutableList<RecentUser>
    private var sizeRecentCurrent = 3

    private val followVM: FollowViewModel by viewModels {
        InjectorUtils.providerLikeAndFollowViewModelFactory()
    }

    private val adapter by lazy {
        BaseAdapter<RecentUser>(R.layout.item_order_recent_user).apply {
            listener = this@FollowFragment
        }
    }

    private val tikTokRepository by lazy {
        TikTokRepository.getInstance()
    }

    override fun getLayoutId() = R.layout.fragment_follow

    override fun setUp() {
        binding.bindingListener = this
        binding.appBar.apply {
            listener = this@FollowFragment
            setTitle(R.drawable.txt_title_follower)
            setStars(getStarsOfApp())
        }
        binding.ibConfirm.isEnabled = false
        binding.editText.apply {
            listener = this@FollowFragment
            setHint("Link your profile tiktok")
        }
        binding.rvOrderRecent.adapter = adapter
        tikTokRepository.getAllUser()
    }

    override fun observerViewModel() {
        observer(tikTokRepository.getAllUser()) {
            if (it == null) {
                binding.group.visibility = View.VISIBLE
                binding.tvGuide.visibility = View.GONE
            }
            it?.let { list ->
                if (list.isEmpty()) {
                    binding.group.visibility = View.VISIBLE
                    binding.tvGuide.visibility = View.GONE
                } else {
                    binding.group.visibility = View.GONE
                    binding.tvGuide.visibility = View.VISIBLE
                    recentUser = list as MutableList<RecentUser>
                    adapter.data = list.takeLast(sizeRecentCurrent).reversed()
                    if (list.size > 3) {
                        binding.ibLoadMore.visibility = View.VISIBLE
                    } else {
                        binding.ibLoadMore.visibility = View.GONE
                    }
                }
            }
        }
        observer(tikTokRepository.stars) {
            it?.let { stars ->
                binding.appBar.setStars(stars)
            }
        }
    }

    override fun onClickBack(v: View) {
        super.onClickBack(v)
        activity.onBackPressed()
    }

    override fun onClickGetStars(v: View) {
        startActivity(Intent(activity, GetStarActivity::class.java))
        activity.animNext()
    }

    override fun onClickConfirm(v: View) {
        val url = binding.editText.getText()
        crawlData(url)
    }

    override fun onClickSeeMore(v: View) {
        navigateTo(R.id.action_followFragment_to_followRecentFragment)
    }

    override fun onClickCross(v: View) {
        super.onClickCross(v)
        hideKeyboard()
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        super.onTextChanged(p0, p1, p2, p3)
        if (p0.toString().isNotEmpty()) {
            binding.ibConfirm.apply {
                isEnabled = true
                setImageResource(R.drawable.bt_comfirm_enable)
            }

        } else {
            binding.ibConfirm.apply {
                isEnabled = false
                setImageResource(R.drawable.bt_confirm_disable)
            }
        }
    }

    override fun onClickItem(v: View, item: RecentUser) {
        crawlData(item.urlShort, true)
    }

    private fun crawlData(url: String, fromDB: Boolean = false) {
        followVM.fetchUser(url, {
            // loading -> show loading
            activity.loading(true)
        }, {
            // error -> turn off loading, show dialog
            activity.loading(false)
            InvalidUrlDialog().show(childFragmentManager, "InvalidUrlDialog")
        }, { recentUser ->
            // success -> turn off loading, clear text, clear focus, insert to database, next screen
            binding.editText.clearText()
            if (!fromDB) {
                followVM.insert(recentUser)
            }
            activity.loading(false)
            navigateTo(R.id.action_followFragment_to_followDetailFragment)
        })
    }
}