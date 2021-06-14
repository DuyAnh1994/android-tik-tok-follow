package com.solar.dev.tiktok.app.ui.tiktok.order

import android.content.Intent
import android.view.View
import androidx.fragment.app.viewModels
import com.dev.anhnd.mybaselibrary.adapter.BaseAdapter
import com.dev.anhnd.mybaselibrary.fragment.BaseFragment
import com.dev.anhnd.mybaselibrary.utils.app.observer
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.FragmentOrderBinding
import com.solar.dev.tiktok.app.model.api.response.OrderStatus
import com.solar.dev.tiktok.app.model.app.Request
import com.solar.dev.tiktok.app.repository.TikTokRepository
import com.solar.dev.tiktok.app.ui.callback.ItemListener
import com.solar.dev.tiktok.app.ui.customview.AppBarTikTok
import com.solar.dev.tiktok.app.ui.tiktok.common.TikTokActivity
import com.solar.dev.tiktok.app.ui.tiktok.getstar.common.GetStarActivity
import com.solar.dev.tiktok.app.utils.*
import com.solar.dev.tiktok.app.utils.Constant.STATUS_CODE_SUCCESS

class OrderFragment : BaseFragment<FragmentOrderBinding, TikTokActivity>(
), AppBarTikTok.AppBarTikTokListener, ItemListener.OrderListener {

    private val adapter by lazy {
        BaseAdapter<OrderStatus>(R.layout.item_order).apply {
            listener = this@OrderFragment
        }
    }

    private val tikTikRepository by lazy {
        TikTokRepository.getInstance()
    }

    private val orderVM: OrderViewModel by viewModels {
        InjectorUtils.providerTikTokViewModelFactory()
    }


    override fun getLayoutId() = R.layout.fragment_order

    override fun setUp() {
        binding.appBar.apply {
            listener = this@OrderFragment
            setTitle(R.drawable.txt_orders)
            setStars(getStarsOfApp())
        }
        initPullToRefresh()
    }

    override fun observerViewModel() {
        observer(orderVM.listOrderStatus) {
            it?.let { res ->
                if (res.statusCode == STATUS_CODE_SUCCESS) {
                    val content = res.content
                    if (content != null) {
                        binding.tvMessage.visibility = View.GONE
                        val list = content.orderStatus
//                        activity.getBadge()?.let { badge ->
//                            badge.text = list.size.toString()
//                        }
                        activity.setBadge(list.size)
                        adapter.data = list
                        binding.rvOrder.scheduleLayoutAnimation()
                        refreshComplete()
                    } else {
//                        activity.getBadge()?.let { badge ->
//                            badge.text = "0"
//                        }
                        activity.setBadge(0)
                        binding.tvMessage.visibility = View.VISIBLE
                    }
                } else {
//                    activity.getBadge()?.let { badge ->
//                        badge.text = "0"
//                    }
                    activity.setBadge(0)
                    binding.tvMessage.visibility = View.VISIBLE
                    adapter.data = null
                }
            }
        }
    }

    override fun onClickBack(v: View) {
        activity.onBackPressed()
    }

    override fun onClickGetStars(v: View) {
        startActivity(Intent(activity, GetStarActivity::class.java))
        activity.animNext()
    }

    override fun onClickItem(v: View, item: Request) {

    }

    private fun initPullToRefresh() {
        binding.rvOrder.adapter = adapter
        DataUtils.initPullToRefreshViewPager(binding.pullToRefresh, {
            checkCanRefresh()
        }, {
            getListOrderStatus()
        })
    }

    private fun checkCanRefresh(): Boolean {
        return if (adapter.data == null || adapter.data?.size == 0) {
            true
        } else {
            binding.rvOrder.computeVerticalScrollOffset() == 0
        }
    }

    private fun getListOrderStatus() {
        orderVM.callAPI04(getDeiceId(), {}, {}, {
            refreshComplete()
        })
    }

    private fun refreshComplete() {
        binding.pullToRefresh.refreshComplete()
    }
}