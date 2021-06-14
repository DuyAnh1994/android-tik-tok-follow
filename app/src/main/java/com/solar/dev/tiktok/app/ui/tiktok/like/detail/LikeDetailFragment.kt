package com.solar.dev.tiktok.app.ui.tiktok.like.detail

import android.content.Intent
import android.view.View
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.dev.anhnd.mybaselibrary.adapter.BaseAdapter
import com.dev.anhnd.mybaselibrary.fragment.BaseFragment
import com.dev.anhnd.mybaselibrary.utils.app.observer
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.FragmentLikeDetailBinding
import com.solar.dev.tiktok.app.model.api.body.OrderLike
import com.solar.dev.tiktok.app.model.app.Request
import com.solar.dev.tiktok.app.repository.CrawlDataRepository
import com.solar.dev.tiktok.app.repository.TikTokRepository
import com.solar.dev.tiktok.app.ui.callback.ItemListener
import com.solar.dev.tiktok.app.ui.customview.AppBarTikTok
import com.solar.dev.tiktok.app.ui.dialog.*
import com.solar.dev.tiktok.app.ui.tiktok.common.TikTokActivity
import com.solar.dev.tiktok.app.ui.tiktok.common.TikTokViewModel
import com.solar.dev.tiktok.app.ui.tiktok.getstar.common.GetStarActivity
import com.solar.dev.tiktok.app.ui.tiktok.like.prepare.LikeViewModel
import com.solar.dev.tiktok.app.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class LikeDetailFragment : BaseFragment<FragmentLikeDetailBinding, TikTokActivity>(),
    AppBarTikTok.AppBarTikTokListener,
    ItemListener.RequestListener,
    OrderLikeListener,
    NotEnoughListener {

    private val TAG = LikeDetailFragment::class.java.simpleName

    private val likeVM: LikeViewModel by viewModels {
        InjectorUtils.providerLikeAndFollowViewModelFactory()
    }
    private val adapter by lazy {
        BaseAdapter<Request>(R.layout.item_request).apply {
            listener = this@LikeDetailFragment
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

    private lateinit var orderLikeDialog: OrderLikeDialog
    private lateinit var notEnoughDialog: NotEnoughDialog
    private lateinit var successfullyDialog: SuccessfullyDialog
    private lateinit var itemView: View


    override fun getLayoutId() = R.layout.fragment_like_detail

    override fun setUp() {
        binding.appBar.apply {
            listener = this@LikeDetailFragment
            setStars(getStarsOfApp())
        }

        binding.rvRequestLike.adapter = adapter
        adapter.data = DummyDataUtils.requestLikes
    }

    override fun observerViewModel() {
        observer(crawlDataRepository.video) {
            it?.let { tikTokVideo ->
                binding.appBar.setTitle(tikTokVideo.name)
                Glide.with(this).load(tikTokVideo.thumbnail).into(binding.ivThumbnail)
                binding.tvCountLike.text = String.format(CalculatorUtils.abbreviateNumber(tikTokVideo.like) + " Likes")
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
                listener = this@LikeDetailFragment
            }
            notEnoughDialog.show(childFragmentManager, "NotEnoughDialog")
        } else {
            orderLikeDialog = OrderLikeDialog(item.order, item.amount).apply {
                listener = this@LikeDetailFragment
            }
            orderLikeDialog.show(childFragmentManager, "OrderLikeDialog")
        }
    }

    override fun onClickCancel(v: View) {
        orderLikeDialog.dismiss()
    }

    override fun onClickConfirm(v: View, order: Int) {
        orderLikeDialog.dismiss()
        val tikTokVideo = crawlDataRepository.video.value
        tikTokVideo?.let { video ->
            val orderLike = OrderLike(
                videoLink = video.urlFull,
                deviceId = getDeiceId(),
                orderNum = order,
                name = video.name,
            )
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val file = TikTokUtils.getFile(video.thumbnail)
                    val obj = TikTokUtils.getObject(orderLike)
                    withContext(Dispatchers.Main) {
                        callAPI07(file, obj, order)
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

    private fun callAPI07(file: MultipartBody.Part, orderLike: RequestBody, order: Int) {
        likeVM.callAPI07(file, orderLike, {
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