package com.solar.dev.tiktok.app.ui.tiktok.like.prepare

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.dev.anhnd.mybaselibrary.adapter.BaseAdapter
import com.dev.anhnd.mybaselibrary.fragment.BaseFragment
import com.dev.anhnd.mybaselibrary.utils.app.TAG
import com.dev.anhnd.mybaselibrary.utils.app.observer
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.FragmentLikeBinding
import com.solar.dev.tiktok.app.model.app.RecentVideo
import com.solar.dev.tiktok.app.repository.TikTokRepository
import com.solar.dev.tiktok.app.ui.callback.ItemListener
import com.solar.dev.tiktok.app.ui.customview.AppBarTikTok
import com.solar.dev.tiktok.app.ui.customview.EditTextTikTok
import com.solar.dev.tiktok.app.ui.dialog.InvalidUrlDialog
import com.solar.dev.tiktok.app.ui.tiktok.common.TikTokActivity
import com.solar.dev.tiktok.app.ui.tiktok.common.TikTokViewModel
import com.solar.dev.tiktok.app.ui.tiktok.getstar.common.GetStarActivity
import com.solar.dev.tiktok.app.utils.InjectorUtils
import com.solar.dev.tiktok.app.utils.animNext
import com.solar.dev.tiktok.app.utils.getStarsOfApp

class LikeFragment : BaseFragment<FragmentLikeBinding, TikTokActivity>(),
    AppBarTikTok.AppBarTikTokListener,
    LikeListener.DataBindingListener,
    EditTextTikTok.EditTextTikTokListener,
    ItemListener.OrderRecentVideoListener {

    private val TAG = LikeFragment::class.java.simpleName

    private lateinit var recentVideo: MutableList<RecentVideo>
    private var sizeRecentCurrent = 3

    private val likeVM: LikeViewModel by viewModels {
        InjectorUtils.providerLikeAndFollowViewModelFactory()
    }
    private val tikTokRepository by lazy {
        TikTokRepository.getInstance()
    }
    private val tikTokVM: TikTokViewModel by viewModels {
        InjectorUtils.providerTikTokViewModelFactory()
    }
    private val adapter by lazy {
        BaseAdapter<RecentVideo>(R.layout.item_order_recent_video).apply {
            listener = this@LikeFragment
        }
    }

    override fun getLayoutId() = R.layout.fragment_like

    override fun setUp() {
        binding.bindingListener = this
        binding.appBar.apply {
            listener = this@LikeFragment
            setTitle(R.drawable.txt_title_likes)
            setStars(getStarsOfApp())
        }
        binding.ibConfirm.isEnabled = false
        binding.editText.listener = this
        binding.editText.setHint("Link your video")
        binding.rvOrderRecent.adapter = adapter
        binding.tvGuide.text = getString(R.string.last_video)
        tikTokVM.callVideoRecent()
    }

    override fun observerViewModel() {
        observer(tikTokRepository.getAllVideo()) {
            if (it == null) {
                binding.tvGuide.text = getString(R.string.guide_likes)
            }
            it?.let { list ->
                if (list.isEmpty()) {
                    binding.tvGuide.text = getString(R.string.guide_likes)
                } else {
                    binding.tvGuide.text = getString(R.string.last_video)
                    recentVideo = list as MutableList<RecentVideo>
                    adapter.data = recentVideo.takeLast(sizeRecentCurrent).reversed()
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
        navigateTo(R.id.action_likeFragment_to_likeRecentFragment)
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

    override fun onClickItem(v: View, item: RecentVideo) {
        crawlData(item.urlShort, true)
    }

    private fun crawlData(url: String, fromDB: Boolean = false) {
        likeVM.fetchVideo(url, {
            // loading -> show loading
            activity.loading(true)
        }, {
            // error -> turn off loading, show dialog
            activity.loading(false)
            InvalidUrlDialog().show(childFragmentManager, "InvalidUrlDialog")
        }, { recentVideo ->
            // success -> turn off loading, clear text, clear focus, insert to database, next screen
            binding.editText.clearText()
            Log.d(TAG, "crawlData: $recentVideo")
            if (!fromDB) {
                likeVM.insert(recentVideo)
            }
            activity.loading(false)
            navigateTo(R.id.action_likeFragment_to_likeDetailFragment)
        })
    }
}