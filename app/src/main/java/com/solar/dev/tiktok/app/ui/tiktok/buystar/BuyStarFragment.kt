package com.solar.dev.tiktok.app.ui.tiktok.buystar

import android.content.Intent
import android.view.View
import com.dev.anhnd.mybaselibrary.fragment.BaseFragment
import com.dev.anhnd.mybaselibrary.utils.app.logD
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.FragmentBuyStarBinding
import com.solar.dev.tiktok.app.model.app.BuyStar
import com.solar.dev.tiktok.app.ui.customview.AppBarTikTok
import com.solar.dev.tiktok.app.ui.tiktok.common.TikTokActivity
import com.solar.dev.tiktok.app.ui.tiktok.getstar.common.GetStarActivity
import com.solar.dev.tiktok.app.utils.DummyDataUtils
import com.solar.dev.tiktok.app.utils.animNext
import com.solar.dev.tiktok.app.utils.getStarsOfApp

class BuyStarFragment : BaseFragment<FragmentBuyStarBinding, TikTokActivity>(
), AppBarTikTok.AppBarTikTokListener, BuyStarAdapter.BuyStarListener {

    private val adapter by lazy {
        BuyStarAdapter().apply {
            listener = this@BuyStarFragment
        }
    }


    override fun getLayoutId() = R.layout.fragment_buy_star

    override fun setUp() {
        binding.appBar.apply {
            listener = this@BuyStarFragment
            setTitle(R.drawable.txt_buy_stars)
            setStars(getStarsOfApp())
        }
        binding.rvBuyStar.adapter = adapter
        adapter.data = DummyDataUtils.buyStar
    }

    override fun observerViewModel() {

    }

    override fun onClickBack(v: View) {
        activity.onBackPressed()
    }

    override fun onClickGetStars(v: View) {
        startActivity(Intent(activity, GetStarActivity::class.java))
        activity.animNext()
    }

    override fun onItemClick(v: View, item: BuyStar) {
        logD("$v, $item")
    }
}