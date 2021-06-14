package com.solar.dev.tiktok.app.ui.tiktok.follow.detail

import android.content.Intent
import android.view.View
import androidx.fragment.app.viewModels
import com.dev.anhnd.mybaselibrary.adapter.BaseAdapter
import com.dev.anhnd.mybaselibrary.fragment.BaseFragment
import com.dev.anhnd.mybaselibrary.utils.app.observer
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.FragmentFollowDetailBinding
import com.solar.dev.tiktok.app.model.api.body.OrderFollower
import com.solar.dev.tiktok.app.model.app.Request
import com.solar.dev.tiktok.app.repository.CrawlDataRepository
import com.solar.dev.tiktok.app.repository.TikTokRepository
import com.solar.dev.tiktok.app.ui.callback.ItemListener
import com.solar.dev.tiktok.app.ui.customview.AppBarTikTok
import com.solar.dev.tiktok.app.ui.dialog.*
import com.solar.dev.tiktok.app.ui.tiktok.common.TikTokActivity
import com.solar.dev.tiktok.app.ui.tiktok.common.TikTokViewModel
import com.solar.dev.tiktok.app.ui.tiktok.follow.prepare.FollowViewModel
import com.solar.dev.tiktok.app.ui.tiktok.getstar.common.GetStarActivity
import com.solar.dev.tiktok.app.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FollowDetailFragment : BaseFragment<FragmentFollowDetailBinding, TikTokActivity>(),
    AppBarTikTok.AppBarTikTokListener,
    ItemListener.RequestListener,
    OrderFollowListener, NotEnoughListener {

    private val followVM: FollowViewModel by viewModels {
        InjectorUtils.providerLikeAndFollowViewModelFactory()
    }
    private val adapter by lazy {
        BaseAdapter<Request>(R.layout.item_request).apply {
            listener = this@FollowDetailFragment
        }
    }
    private val tikTokRepository by lazy {
        TikTokRepository.getInstance()
    }
    private val crawlDataRepository by lazy {
        CrawlDataRepository.getInstance()
    }
    private val tikTokVM: TikTokViewModel by viewModels {
        InjectorUtils.providerTikTokViewModelFactory()
    }

    private lateinit var orderFollowDialog: OrderFollowDialog
    private lateinit var notEnoughDialog: NotEnoughDialog
    private lateinit var itemView: View

    override fun getLayoutId() = R.layout.fragment_follow_detail

    override fun setUp() {
        binding.appBar.apply {
            listener = this@FollowDetailFragment
            setStars(getStarsOfApp())
        }
        binding.rvRequestFollow.adapter = adapter
        adapter.data = DummyDataUtils.requestFollowers
    }

    override fun observerViewModel() {
        observer(crawlDataRepository.user) {
            it?.let { tikTokUser ->
                binding.appBar.setTitle(tikTokUser.name)
                binding.profileUser.apply {
                    setCover(activity, tikTokUser.thumbnail)
                    setFollowing(String.format(CalculatorUtils.abbreviateNumber(tikTokUser.following)))
                    setFollower(String.format(CalculatorUtils.abbreviateNumber(tikTokUser.follower)))
                    setLikes(String.format(CalculatorUtils.abbreviateNumber(tikTokUser.likes)))
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
        onBackScreen()
    }

    override fun onClickGetStars(v: View) {
        startActivity(Intent(activity, GetStarActivity::class.java))
        activity.animNext()
    }

    override fun onClickItem(v: View, item: Request) {
        itemView = v
        if (item.amount > getStarsOfApp()) {
            notEnoughDialog = NotEnoughDialog().apply {
                listener = this@FollowDetailFragment
            }
            notEnoughDialog.show(childFragmentManager, "NotEnoughDialog")
        } else {
            orderFollowDialog = OrderFollowDialog(item.order, item.amount).apply {
                listener = this@FollowDetailFragment
            }
            orderFollowDialog.show(childFragmentManager, "OrderFollowDialog")
        }
    }

    override fun onClickCancel(v: View) {
        orderFollowDialog.dismiss()
    }

    override fun onClickConfirm(v: View, order: Int) {
        orderFollowDialog.dismiss()
        val tikTokUser = crawlDataRepository.user.value
        tikTokUser?.let { user ->
            val orderFollow = OrderFollower(
                accountLink = user.urlFull,
                deviceId = getDeiceId(),
                orderNum = order,
                name = user.name
            )
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val file = TikTokUtils.getFile(user.thumbnail)
                    val obj = TikTokUtils.getObject(orderFollow)
                    withContext(Dispatchers.Main) {
                        callAPI08(file, obj, order)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onClickOk(v: View) {
        notEnoughDialog.dismiss()
        startActivity(Intent(activity, GetStarActivity::class.java))
        activity.animNext()
    }

    private fun callAPI08(file: MultipartBody.Part, orderFollow: RequestBody, order: Int) {
        followVM.callAPI08(file, orderFollow, {
            activity.loading(true)
        }, {
            activity.loading(false)
        }, {
            activity.loading(false)
            AnimUtils.animateView(itemView, activity.getItemCopy(), activity.getDestination(), {
                tikTokVM.callAPI04(getDeiceId(), {}, {}, {})
            }, {
                SuccessfullyDialog(order).show(childFragmentManager, "SuccessfullyDialog")
            })
        })
    }
}