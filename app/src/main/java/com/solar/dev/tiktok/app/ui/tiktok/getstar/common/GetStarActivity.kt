package com.solar.dev.tiktok.app.ui.tiktok.getstar.common

import android.os.Build
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.ViewPager
import com.dev.anhnd.mybaselibrary.activity.BaseActivity
import com.dev.anhnd.mybaselibrary.utils.app.logE
import com.dev.anhnd.mybaselibrary.utils.app.observer
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.ActivityGetStarBinding
import com.solar.dev.tiktok.app.model.api.body.DeliverAccount
import com.solar.dev.tiktok.app.model.api.body.DeliverVideo
import com.solar.dev.tiktok.app.model.api.body.RecommendedAccount
import com.solar.dev.tiktok.app.model.api.body.RecommendedVideo
import com.solar.dev.tiktok.app.model.app.DeliverCurrent
import com.solar.dev.tiktok.app.repository.CrawlDataRepository
import com.solar.dev.tiktok.app.repository.TikTokRepository
import com.solar.dev.tiktok.app.ui.customview.AppBarTikTok
import com.solar.dev.tiktok.app.ui.dialog.LoadingDialog
import com.solar.dev.tiktok.app.ui.tiktok.getstar.follow.GetStarFollowFragment
import com.solar.dev.tiktok.app.ui.tiktok.getstar.like.GetStarLikeFragment
import com.solar.dev.tiktok.app.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GetStarActivity : BaseActivity<ActivityGetStarBinding>(),
    AppBarTikTok.AppBarTikTokListener {

    private val TAG = GetStarActivity::class.java.simpleName

    private val getStarVM: GetStarViewModel by viewModels {
        InjectorUtils.providerTikTokViewModelFactory()
    }
    private val crawlDataRepository by lazy {
        CrawlDataRepository.getInstance()
    }
    private val tikTokRepository by lazy {
        TikTokRepository.getInstance()
    }
    private val loadingDialog by lazy {
        LoadingDialog(this)
    }


    private val getStarLikeFragment = GetStarLikeFragment()
    private val getStarFollowFragment = GetStarFollowFragment()

    override fun getLayoutId() = R.layout.activity_get_star

    override fun fixSingleTask() = false

    override fun setUp() {
        binding.appBar.apply {
            listener = this@GetStarActivity
            setTitle(R.drawable.txt_get_star)
            setStars(getStarsOfApp())
        }

        val adapter = ViewPagerAdapter(this, supportFragmentManager, 1)
        adapter.addFragment(getStarLikeFragment, getString(R.string.like), 0)
        adapter.addFragment(getStarFollowFragment, getString(R.string.follow), 1)


        binding.viewPager.apply {
            offscreenPageLimit = adapter.count
            setAdapter(adapter)
        }
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        for (i in 0..1) {
            binding.tabLayout.getTabAt(i)?.customView = adapter.getTabView(i)
        }

        /**
         * setup tab view
         */
        // tab view first
//        getTabView(0)?.findViewById<TextView>(R.id.tvTitle)?.text = getString(R.string.like)
        // tab view second
//        getTabView(1)?.findViewById<TextView>(R.id.tvTitle)?.text = getString(R.string.follow)

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        setTabView(0)
                    }
                    1 -> {
                        setTabView(1)
                    }
                }
            }
        })
        // vao man get star la call api nay`, co 2 viewpager
        callAPI01()
    }

    override fun observerViewModel() {
        observer(tikTokRepository.stars) {
            it?.let { stars ->
                binding.appBar.setStars(stars)
            }
        }
    }

    override fun onClickBack(v: View) {
        super.onClickBack(v)
        onBackPressed()
    }

    fun callAPI01() {
        getStarVM.callAPI01(getDeiceId(), {
            loading(true)
        }, { throwable ->
            loading(false)
            logE(throwable.message.toString())
        }, {
            loading(false)
        })
    }

    fun callAPI02(url: String) {
        val video = RecommendedVideo(
            video = url,
            deviceId = getDeiceId()
        )
        getStarVM.callAPI02(video, {}, {}, {})
    }

    fun callAPI03(url: String) {
        val user = RecommendedAccount(
            accountRecommend = url,
            deviceId = getDeiceId()
        )
        getStarVM.callAPI03(user, {}, {}, {})
    }

    private fun getTabView(index: Int): View? {
        return binding.tabLayout.getTabAt(index)?.customView
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setTabView(index: Int) {
        when (index) {
            0 -> {
                getTabView(0)?.findViewById<TextView>(R.id.tvTitle)?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_point_selected_yellow)
                getTabView(1)?.findViewById<TextView>(R.id.tvTitle)?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_point_selected_black)

                getTabView(0)?.findViewById<TextView>(R.id.tvTitle)?.setTextColor(getColor(R.color.colorTextYellow))
                getTabView(1)?.findViewById<TextView>(R.id.tvTitle)?.setTextColor(getColor(R.color.colorHint))
            }
            1 -> {
                getTabView(0)?.findViewById<TextView>(R.id.tvTitle)?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_point_selected_black)
                getTabView(1)?.findViewById<TextView>(R.id.tvTitle)?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_point_selected_purple)

                getTabView(0)?.findViewById<TextView>(R.id.tvTitle)?.setTextColor(getColor(R.color.colorHint))
                getTabView(1)?.findViewById<TextView>(R.id.tvTitle)?.setTextColor(getColor(R.color.colorTextPurple))
            }
        }
    }

    fun loading(loading: Boolean) {
        try {
            if (loading) {
                if (!loadingDialog.isAdded) {
                    loadingDialog.show(supportFragmentManager, "LoadingDialog")
                }
            } else {
                if (loadingDialog.isAdded) {
                    loadingDialog.dismiss()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        if (DataUtils.FLAG_OPEN_TIK_TOK_APP_BY_VIDEO) {
            DataUtils.FLAG_OPEN_TIK_TOK_APP_BY_VIDEO = false
            increaseStarByVideo({
                loading(true)
            }, {
                loading(false)
            }, { likeAfter ->
                loading(false)
                val likeBefore = DataUtils.deliverCurr.likeOrFollowBefore
                if (likeBefore <= 998) {
                    if (likeAfter > likeBefore) {
                        CoroutineScope(Dispatchers.IO).launch {
                            tikTokRepository.putStars(Constant.INCREASE_STARS) {
                                val deliverVideo = DeliverVideo(
                                    video = DataUtils.deliverCurr.url,
                                    deviceId = getDeiceId(),
                                    deliveredAmount = Constant.DELIVERED_AMOUNT
                                )
                                getStarVM.callAPI05(deliverVideo, {}, {}, {})
                                DataUtils.deliverCurr = DeliverCurrent()
                            }
                        }
                    }
                } else {
                    if (likeAfter >= likeBefore) {
                        CoroutineScope(Dispatchers.IO).launch {
                            tikTokRepository.putStars(Constant.INCREASE_STARS) {
                                val deliverVideo = DeliverVideo(
                                    video = DataUtils.deliverCurr.url,
                                    deviceId = getDeiceId(),
                                    deliveredAmount = Constant.DELIVERED_AMOUNT
                                )
                                getStarVM.callAPI05(deliverVideo, {}, {}, {})
                                DataUtils.deliverCurr = DeliverCurrent()
                            }
                        }
                    }
                }
            })
        }
        if (DataUtils.FLAG_OPEN_TIK_TOK_APP_BY_USER) {
            DataUtils.FLAG_OPEN_TIK_TOK_APP_BY_USER = false
            increaseStarByUser({
                loading(true)
            }, {
                loading(false)
            }, { followAfter ->
                loading(false)
                val followBefore = DataUtils.deliverCurr.likeOrFollowBefore
                if (followBefore <= 998) {
                    if (followAfter > followBefore) {
                        CoroutineScope(Dispatchers.IO).launch {
                            tikTokRepository.putStars(Constant.INCREASE_STARS) {
                                val deliverUser = DeliverAccount(
                                    account = DataUtils.deliverCurr.url,
                                    deviceId = getDeiceId(),
                                    deliveredAmount = Constant.DELIVERED_AMOUNT
                                )
                                getStarVM.callAPI06(deliverUser, {}, {}, {})
                                DataUtils.deliverCurr = DeliverCurrent()
                            }
                        }
                    }
                } else {
                    if (followAfter >= followBefore) {
                        CoroutineScope(Dispatchers.IO).launch {
                            tikTokRepository.putStars(Constant.INCREASE_STARS) {
                                val deliverUser = DeliverAccount(
                                    account = DataUtils.deliverCurr.url,
                                    deviceId = getDeiceId(),
                                    deliveredAmount = Constant.DELIVERED_AMOUNT
                                )
                                getStarVM.callAPI06(deliverUser, {}, {}, {})
                                DataUtils.deliverCurr = DeliverCurrent()
                            }
                        }
                    }
                }
            })
        }
    }

    private fun increaseStarByVideo(onLoading: () -> Unit, onError: () -> Unit, onSuccess: (like: Long) -> Unit = {}) {
        onLoading.invoke()
        CoroutineScope(Dispatchers.IO).launch {
            val result = crawlDataRepository.fetchVideoFromUrl(DataUtils.deliverCurr.url)
            withContext(Dispatchers.Main) {
                if (result.verify) {
                    onSuccess.invoke(result.like)
                } else {
                    onError.invoke()
                }
            }
        }
    }

    private fun increaseStarByUser(onLoading: () -> Unit, onError: () -> Unit, onSuccess: (follow: Long) -> Unit = {}) {
        onLoading.invoke()
        CoroutineScope(Dispatchers.IO).launch {
            val result = crawlDataRepository.fetchUserFromUrl(DataUtils.deliverCurr.url)
            withContext(Dispatchers.Main) {
                if (result.verify) {
                    onSuccess.invoke(result.follower)
                } else {
                    onError.invoke()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        animPrevious()
    }
}