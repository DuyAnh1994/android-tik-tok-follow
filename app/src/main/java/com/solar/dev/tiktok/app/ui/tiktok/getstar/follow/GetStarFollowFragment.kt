package com.solar.dev.tiktok.app.ui.tiktok.getstar.follow

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dev.anhnd.mybaselibrary.utils.app.logD
import com.dev.anhnd.mybaselibrary.utils.app.observer
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.FragmentGetStarFollowBinding
import com.solar.dev.tiktok.app.model.api.response.RecommendUser
import com.solar.dev.tiktok.app.model.app.DeliverCurrent
import com.solar.dev.tiktok.app.repository.CrawlDataRepository
import com.solar.dev.tiktok.app.repository.TikTokRepository
import com.solar.dev.tiktok.app.ui.tiktok.getstar.common.GetStarActivity
import com.solar.dev.tiktok.app.ui.tiktok.getstar.common.GetStarCommonFragment
import com.solar.dev.tiktok.app.ui.tiktok.getstar.common.GetStarFollowListener
import com.solar.dev.tiktok.app.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GetStarFollowFragment : GetStarCommonFragment<FragmentGetStarFollowBinding, GetStarActivity>(), GetStarFollowListener.DataBindingListener {

    private val TAG = GetStarFollowFragment::class.java.simpleName
    private val tikTokRepository by lazy {
        TikTokRepository.getInstance()
    }
    private var users = mutableListOf<RecommendUser>()
    private var setThumbnailInit = true
    private var currentIndex = 0
    private var intent = Intent()

    private val crawlDataRepository by lazy {
        CrawlDataRepository.getInstance()
    }

    override fun getLayoutId() = R.layout.fragment_get_star_follow

    override fun setUp() {
        binding.bindingListener = this
    }

    override fun observerViewModel() {
        // get list tuong ung like/follow
        observer(tikTokRepository.listRecommended) {
            it?.let { res ->
                if (res.statusCode == Constant.STATUS_CODE_SUCCESS) {
                    users = res.content.followerOrders
                    if (users.size > 0) {
                        if (setThumbnailInit) {
                            setThumbnailInit = false
                            logD(users[currentIndex].thumbnail)
                            binding.ivThumbnail.setThumbnailForView(users[currentIndex].thumbnail, Shape.CIRCLE)
                        }
                    } else {
                        // list tr??? v??? r???ng, set thumbnail m???c ?????nh
                        Glide.with(this)
                            .load(R.drawable.bg_user_error)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .circleCrop()
                            .into(binding.ivThumbnail)
                    }
                }
            }
        }
    }

    /**
     * - X??? l?? n??t follow now
     * 1. Ki???m tra url -> th??ng b??o n???u l???i
     * 2. Get follow ??? th???i ??i???m hi???n t???i tr?????c khi m??? app tiktok, l??u l???i ????? so s??nh khi quay tr???
     *  l???i my app
     * 3. Th???c hi???n ch???c n??ng skip ????? load ra item m???i trong list recommended s???n s??ng cho l???n ti???p
     *  theo:
     *  - TH1: current index < size - 1
     *      + t??ng index v?? set thumbnail
     *      + call API03
     *  - TH2: current index >= size -1
     *      + reset index v??? 0
     *      + call API01 ????? observer x??? l?? set thumbnail v???i ph???n t??? ?????u ti??n (index = 0) c???a m???ng
     * 4. Sau khi th??nh c??ng th?? hide loading v?? m??? app tiktok
     */
    override fun onClickFollowNow(v: View) {
        if (users.isEmpty()) {
            Toast.makeText(activity, "Not found user!", Toast.LENGTH_SHORT).show()
            return
        }
        val url = users[currentIndex].accURL
        verifyUrl(url, { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }, {
            it?.let { link ->
                Log.d(TAG, "link: $link")
                getFollowBefore(link, {
                    activity.loading(true)
                }, {
                    activity.loading(false)
                }, { deliver ->
                    activity.loading(false)
                    skip(currentIndex)
                    DataUtils.deliverCurr = deliver
                    DataUtils.FLAG_OPEN_TIK_TOK_APP_BY_USER = true
                    followNow(link)
                })
            }
        })
    }

    override fun onClickSkip(v: View) {
        skip(currentIndex)
    }

    private fun followNow(url: String) {
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
        if (users.isEmpty()) {
            callAPI01()
            return
        }
        activity.callAPI03(users[index].accURL)
        if (index < users.size - 1) {
            currentIndex++
            binding.ivThumbnail.setThumbnailForView(users[currentIndex].thumbnail, Shape.CIRCLE)
        } else {
            callAPI01()
        }
    }

    private fun callAPI01() {
        setThumbnailInit = true
        currentIndex = 0
        activity.callAPI01()
    }

    private fun getFollowBefore(url: String,
                                onLoading: () -> Unit,
                                onError: () -> Unit,
                                onSuccess: (deliver: DeliverCurrent) -> Unit = {}
    ) {
        onLoading.invoke()
        CoroutineScope(Dispatchers.IO).launch {
            val result = crawlDataRepository.fetchUserFromUrl(url)
            withContext(Dispatchers.Main) {
                if (result.verify) {
                    val deliver = DeliverCurrent(url = result.urlFull, deviceId = getDeiceId(), likeOrFollowBefore = result.follower)
                    onSuccess.invoke(deliver)
                } else {
                    onError.invoke()
                }
            }
        }
    }
}