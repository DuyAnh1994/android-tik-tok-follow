package com.solar.dev.tiktok.app.ui.tiktok.common

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.plusAssign
import androidx.navigation.ui.setupWithNavController
import com.dev.anhnd.mybaselibrary.activity.BaseActivity
import com.dev.anhnd.mybaselibrary.utils.app.getAppColor
import com.dev.anhnd.mybaselibrary.utils.app.observer
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.ActivityTikTokBinding
import com.solar.dev.tiktok.app.repository.TikTokRepository
import com.solar.dev.tiktok.app.ui.dialog.LoadingDialog
import com.solar.dev.tiktok.app.utils.*
import java.io.File

class TikTokActivity : BaseActivity<ActivityTikTokBinding>() {

    private val tikTokRepository by lazy {
        TikTokRepository.getInstance()
    }
    private val tikTokVM: TikTokViewModel by viewModels {
        InjectorUtils.providerTikTokViewModelFactory()
    }
    private var notificationBadge: View? = null
    private var oldPosition: Int = 1
    private lateinit var navigator: KeepStateNavigator
    private val loadingDialog by lazy {
        LoadingDialog(this)
    }

    override fun getLayoutId() = R.layout.activity_tik_tok

    private val timer = object : CountDownTimer(3000, 1000) {
        override fun onTick(p0: Long) {

        }

        override fun onFinish() {
            binding.ivPopup.visibility = View.GONE
        }
    }

    @SuppressLint("RestrictedApi")
    override fun setUp() {
        createFolderInternal()
        val navController = getMainNavController()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)!!
        navigator = KeepStateNavigator(this, navHostFragment.childFragmentManager, R.id.nav_host_fragment)

        navController.navigatorProvider += navigator
        navController.setGraph(R.navigation.nav_graph_tik_tok)
        navigator.apply {
            enterAnim(R.anim.enter_slide_left_to_right)
            exitAnim(R.anim.exit_slide_right_to_left)
            popEnterAnim(R.anim.enter_slide_right_to_left)
            popExitAnim(R.anim.exit_slide_left_to_right)
        }
        binding.bottomNav.setupWithNavController(navController)
        binding.bottomNav.setOnNavigationItemSelectedListener {
            var newPosition = 0
            when (it.title) {
                getString(R.string.likes) -> {
                    newPosition = 1
                }
                getString(R.string.follower) -> {
                    newPosition = 2
                }
                getString(R.string.buy_stars) -> {
                    newPosition = 3
                }
                getString(R.string.orders) -> {
                    newPosition = 4
                }
            }
            if (oldPosition == newPosition) {
                return@setOnNavigationItemSelectedListener false
            }
            if (navController.graph.findNode(it.itemId) != null) {
                navController.navigate(it.itemId, null, getNavOption(newPosition))
                return@setOnNavigationItemSelectedListener true
            }
            false
        }
        AnimUtils.doBounceAnimation(binding.ivPopup)
//        getBadge()
        timer.start()
        tikTokVM.callAPI04(getDeiceId(), {
            loading(true)
        }, {
            loading(false)
        }, {
            loading(false)
        })
    }

    private fun getNavOption(newPosition: Int): NavOptions? {
        if (oldPosition > newPosition) {
            navigator.state = StateAnim.LEFT_TO_RIGHT
        }
        if (oldPosition < newPosition) {
            navigator.state = StateAnim.RIGHT_TO_LEFT
        }
        oldPosition = newPosition
        return null
    }

    private fun getMainNavController(): NavController {
        return findNavController(R.id.nav_host_fragment)
    }

    fun getDestination() = binding.bottomNav
    fun getItemCopy() = binding.ivItemCopy

    override fun observerViewModel() {
        observer(tikTokVM.listOrderStatus) {
            it?.let { res ->
                if (res.statusCode == Constant.STATUS_CODE_SUCCESS) {
                    val content = res.content
                    if (content != null) {
                        val list = content.orderStatus
//                        getBadge()?.let { badge ->
//                            badge.text = list.size.toString()
//                        }
                        setBadge(list.size)
                    } else {
//                        getBadge()?.let { badge ->
//                            badge.text = "0"
//                        }
                    }
                } else {
//                    getBadge()?.let { badge ->
//                        badge.text = "0"
//                    }
                }
            }
        }
    }

    fun loading(loading: Boolean) {
        if (loading) {
            if (!loadingDialog.isAdded) {
                loadingDialog.show(supportFragmentManager, "LoadingDialog")
            }
        } else {
            if (loadingDialog.isAdded) {
                loadingDialog.dismiss()
            }
        }
    }

    private fun createFolderInternal() {
        val internalPackage = filesDir.parent
        val folderName = Constant.FOLDER_INTERNAL_IMAGE_RECENT
        val bitmapRecent = File(internalPackage, folderName)
        if (!bitmapRecent.exists()) {
            bitmapRecent.mkdirs()
        }
    }

    fun getBadge(): TextView? {
        val menuView = binding.bottomNav.getChildAt(0) as BottomNavigationMenuView
        val itemView = menuView.getChildAt(3) as BottomNavigationItemView
        notificationBadge = LayoutInflater.from(this).inflate(R.layout.layout_badge, menuView, false)
        val tvBadge = notificationBadge?.findViewById<TextView>(R.id.tvNotificationsBadge)
        tvBadge?.text = 0.toString()
        itemView.addView(notificationBadge)
        return tvBadge
    }

    fun setBadge(count: Int) {
        val menu = binding.bottomNav.menu
        val badge = binding.bottomNav.getOrCreateBadge(menu.getItem(3).itemId)
        badge.apply {
            isVisible = true
            number = count
            verticalOffset = 10
            backgroundColor = getAppColor(R.color.colorOrange)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        animPrevious()
    }
}