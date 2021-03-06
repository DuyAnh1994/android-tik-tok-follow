package com.solar.dev.tiktok.app.ui.tiktok.getstar.like

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dev.anhnd.mybaselibrary.utils.app.observer
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.FragmentGetStarLikeBinding
import com.solar.dev.tiktok.app.model.api.response.RecommendVideo
import com.solar.dev.tiktok.app.model.app.DeliverCurrent
import com.solar.dev.tiktok.app.repository.CrawlDataRepository
import com.solar.dev.tiktok.app.repository.TikTokRepository
import com.solar.dev.tiktok.app.ui.tiktok.getstar.common.GetStarActivity
import com.solar.dev.tiktok.app.ui.tiktok.getstar.common.GetStarCommonFragment
import com.solar.dev.tiktok.app.ui.tiktok.getstar.common.GetStarLikeListener
import com.solar.dev.tiktok.app.utils.*
import com.solar.dev.tiktok.app.utils.Constant.STATUS_CODE_SUCCESS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GetStarLikeFragment : GetStarCommonFragment<FragmentGetStarLikeBinding, GetStarActivity>(), GetStarLikeListener.DataBindingListener {

    private val TAG = GetStarLikeFragment::class.java.simpleName
    private val tikTokRepository by lazy {
        TikTokRepository.getInstance()
    }
    private val crawlDataRepository by lazy {
        CrawlDataRepository.getInstance()
    }
    private var videos = mutableListOf<RecommendVideo>()
    private var setThumbnailInit = true
    private var currentIndex = 0
    private var intent = Intent()
    private val isAppInstalled = appInstalledOrNot(PACKAGE_APP_TIK_TOK)

    override fun getLayoutId() = R.layout.fragment_get_star_like

    override fun setUp() {
        binding.bindingListener = this
    }

    override fun observerViewModel() {
        observer(tikTokRepository.listRecommended) {
            it?.let { res ->
                if (res.statusCode == STATUS_CODE_SUCCESS) {
                    videos = res.content.likeVideoOrders
                    if (videos.size > 0) {
                        if (setThumbnailInit) {
                            setThumbnailInit = false
                            binding.ivThumbnail.setThumbnailForView(videos[currentIndex].thumbnail, Shape.SQUARE)
                        }
                    } else {
                        // list tr??? v??? r???ng, set thumbnail m???c ?????nh
                        Glide.with(this)
                            .load(R.drawable.bg_video_error)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .centerCrop()
                            .into(binding.ivThumbnail)
                    }
                }
            }
        }
    }

    /**
     * - X??? l?? n??t like now
     * 1. Ki???m tra url -> th??ng b??o n???u l???i
     * 2. Get like ??? th???i ??i???m hi???n t???i tr?????c khi m??? app tiktok, l??u l???i ????? so s??nh khi quay tr???
     *  l???i my app
     * 3. Th???c hi???n ch???c n??ng skip ????? load ra item m???i trong list recommended s???n s??ng cho l???n ti???p
     *  theo:
     *  - TH1: current index < size-1
     *      + t??ng index v?? set thumbnail
     *      + call API03
     *  - TH2: current index >= size-1
     *      + reset index v??? 0
     *      + call API01 ????? observer x??? l?? set thumbnail v???i ph???n t??? ?????u ti??n (index = 0) c???a m???ng
     * 4. Sau khi th??nh c??ng th?? hide loading v?? m??? app tiktok
     */
    override fun onClickLikeNow(v: View) {
        if (videos.isEmpty()) {
            Toast.makeText(activity, "Not found video!", Toast.LENGTH_SHORT).show()
            return
        }
        val url = videos[currentIndex].videoURL
        verifyUrl(url, { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }, {
            it?.let { link ->
                getLikeBefore(link, {
                    Log.d(TAG, "link: $link")
                    activity.loading(true)
                }, {
                    activity.loading(false)
                }, { deliver ->
                    activity.loading(false)
                    skip(currentIndex)
                    DataUtils.deliverCurr = deliver
                    DataUtils.FLAG_OPEN_TIK_TOK_APP_BY_VIDEO = true
                    likeNow(link)
                })
            }
        })
    }

    override fun onClickSkip(v: View) {
        skip(currentIndex)
    }

    private fun likeNow(url: String) {
        try {
            intent.apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(url)
            }
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun skip(index: Int) {
        if (videos.isEmpty()) {
            callAPI01()
            return
        }
        activity.callAPI02((videos[index].videoURL))
        if (index < videos.size - 1) {
            currentIndex++
            binding.ivThumbnail.setThumbnailForView(videos[currentIndex].thumbnail, Shape.SQUARE)
        } else {
            callAPI01()
        }
    }

    private fun callAPI01() {
        setThumbnailInit = true
        currentIndex = 0
        activity.callAPI01()
    }

    private fun getLikeBefore(url: String,
                              onLoading: () -> Unit,
                              onError: () -> Unit,
                              onSuccess: (deliver: DeliverCurrent) -> Unit = {}
    ) {
        onLoading.invoke()
        CoroutineScope(Dispatchers.IO).launch {
            val result = crawlDataRepository.fetchVideoFromUrl(url)
            withContext(Dispatchers.Main) {
                if (result.verify) {
                    val deliver = DeliverCurrent(url = result.urlFull, deviceId = getDeiceId(), likeOrFollowBefore = result.like)
                    onSuccess.invoke(deliver)
                } else {
                    onError.invoke()
                }
            }
        }
    }

    companion object {
        const val PACKAGE_APP_TIK_TOK = "com.ss.android.ugc.trill"
    }
}