package com.solar.dev.tiktok.app.utils

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator

@Navigator.Name("keep_state_fragment") // `keep_state_fragment` is used in navigation xml
class KeepStateNavigator(
    private val context: Context,
    private val manager: FragmentManager, // Should pass childFragmentManager.
    private val containerId: Int
) : FragmentNavigator(context, manager, containerId) {

    private var mEnterAnim = 0
    private var mExitAnim = 0
    private var mPopEnterAnim = 0
    private var mPopExitAnim = 0

    var state = StateAnim.LEFT_TO_RIGHT

    fun enterAnim(enterAnim: Int) {
        mEnterAnim = enterAnim
    }

    fun exitAnim(exitAnim: Int) {
        mExitAnim = exitAnim
    }

    fun popEnterAnim(popEnterAnim: Int) {
        mPopEnterAnim = popEnterAnim
    }

    fun popExitAnim(popExitAnim: Int) {
        mPopExitAnim = popExitAnim
    }

    override fun navigate(destination: Destination,
                          args: Bundle?,
                          navOptions: NavOptions?,
                          navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        val tag = destination.id.toString()
        val transaction = manager.beginTransaction()
        var initialNavigate = false
        val currentFragment = manager.primaryNavigationFragment
        if (currentFragment != null) {
            transaction.detach(currentFragment)
        } else {
            initialNavigate = true
        }
        if (state == StateAnim.LEFT_TO_RIGHT) {
            transaction.setCustomAnimations(mExitAnim, mEnterAnim, mPopExitAnim, mPopEnterAnim)
        } else if (state == StateAnim.RIGHT_TO_LEFT) {
            transaction.setCustomAnimations(mEnterAnim, mExitAnim, mPopEnterAnim, mPopExitAnim)
        }
        var fragment = manager.findFragmentByTag(tag)
        if (fragment == null) {
            val className = destination.className
            fragment = manager.fragmentFactory.instantiate(context.classLoader, className)
            transaction.add(containerId, fragment, tag)
        } else {
            transaction.attach(fragment)
        }
        transaction.setPrimaryNavigationFragment(fragment)
        transaction.setReorderingAllowed(true)

        transaction.commitNow()

        return if (initialNavigate) {
            destination
        } else {
            null
        }
    }
}

enum class StateAnim {
    LEFT_TO_RIGHT, RIGHT_TO_LEFT
}