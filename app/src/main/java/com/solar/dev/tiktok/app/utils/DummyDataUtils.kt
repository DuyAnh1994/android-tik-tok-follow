package com.solar.dev.tiktok.app.utils

import com.dev.anhnd.mybaselibrary.utils.app.getApplication
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.model.app.*
import com.solar.dev.tiktok.app.ui.tiktok.buystar.BuyStarAdapter

object DummyDataUtils {

    val requestLikes = listOf(
        Request(name = getApplication().getString(R.string.likes), order = 10, amount = 20, type = Constant.TYPE_REQUEST_LIKES, title = R.drawable.ic_like_title_request, background = R.drawable.bg_request_like),
        Request(name = getApplication().getString(R.string.likes), order = 20, amount = 40, type = Constant.TYPE_REQUEST_LIKES, title = R.drawable.ic_like_title_request, background = R.drawable.bg_request_like),
        Request(name = getApplication().getString(R.string.likes), order = 50, amount = 200, type = Constant.TYPE_REQUEST_LIKES, title = R.drawable.ic_like_title_request, background = R.drawable.bg_request_like),
        Request(name = getApplication().getString(R.string.likes), order = 100, amount = 400, type = Constant.TYPE_REQUEST_LIKES, title = R.drawable.ic_like_title_request, background = R.drawable.bg_request_like),
        Request(name = getApplication().getString(R.string.likes), order = 200, amount = 800, type = Constant.TYPE_REQUEST_LIKES, title = R.drawable.ic_like_title_request, background = R.drawable.bg_request_like),
        Request(name = getApplication().getString(R.string.likes), order = 300, amount = 1200, type = Constant.TYPE_REQUEST_LIKES, title = R.drawable.ic_like_title_request, background = R.drawable.bg_request_like),
        Request(name = getApplication().getString(R.string.likes), order = 400, amount = 1600, type = Constant.TYPE_REQUEST_LIKES, title = R.drawable.ic_like_title_request, background = R.drawable.bg_request_like),
        Request(name = getApplication().getString(R.string.likes), order = 500, amount = 2000, type = Constant.TYPE_REQUEST_LIKES, title = R.drawable.ic_like_title_request, background = R.drawable.bg_request_like)
    )

    val requestFollowers = listOf(
        Request(name = getApplication().getString(R.string.follower), order = 10, amount = 20, type = Constant.TYPE_REQUEST_FOLLOWERS, title = R.drawable.ic_follow_title_request, background = R.drawable.bg_request_follow),
        Request(name = getApplication().getString(R.string.follower), order = 20, amount = 40, type = Constant.TYPE_REQUEST_FOLLOWERS, title = R.drawable.ic_follow_title_request, background = R.drawable.bg_request_follow),
        Request(name = getApplication().getString(R.string.follower), order = 50, amount = 200, type = Constant.TYPE_REQUEST_FOLLOWERS, title = R.drawable.ic_follow_title_request, background = R.drawable.bg_request_follow),
        Request(name = getApplication().getString(R.string.follower), order = 100, amount = 400, type = Constant.TYPE_REQUEST_FOLLOWERS, title = R.drawable.ic_follow_title_request, background = R.drawable.bg_request_follow),
        Request(name = getApplication().getString(R.string.follower), order = 200, amount = 800, type = Constant.TYPE_REQUEST_FOLLOWERS, title = R.drawable.ic_follow_title_request, background = R.drawable.bg_request_follow),
        Request(name = getApplication().getString(R.string.follower), order = 300, amount = 1200, type = Constant.TYPE_REQUEST_FOLLOWERS, title = R.drawable.ic_follow_title_request, background = R.drawable.bg_request_follow),
        Request(name = getApplication().getString(R.string.follower), order = 400, amount = 1600, type = Constant.TYPE_REQUEST_FOLLOWERS, title = R.drawable.ic_follow_title_request, background = R.drawable.bg_request_follow),
        Request(name = getApplication().getString(R.string.follower), order = 500, amount = 2000, type = Constant.TYPE_REQUEST_FOLLOWERS, title = R.drawable.ic_follow_title_request, background = R.drawable.bg_request_follow)
    )

    private const val thumbnail = "https://p16-tiktok-va-h2.ibyteimg.com/img/musically-maliva-obj/1662653085904902~c5_720x720.jpeg"

    val order = listOf(
        Order(count = 10, delivered = 1, type = 0, cover = R.drawable.ic_follow_small, thumbnail = thumbnail, name = "a"),
        Order(count = 20, delivered = 2, type = 1, cover = R.drawable.ic_like_small, thumbnail = thumbnail, name = "b"),
        Order(count = 30, delivered = 3, type = 0, cover = R.drawable.ic_follow_small, thumbnail = thumbnail, name = "c"),
        Order(count = 40, delivered = 4, type = 1, cover = R.drawable.ic_like_small, thumbnail = thumbnail, name = "d"),
        Order(count = 50, delivered = 5, type = 0, cover = R.drawable.ic_follow_small, thumbnail = thumbnail, name = "e"),
        Order(count = 60, delivered = 6, type = 1, cover = R.drawable.ic_like_small, thumbnail = thumbnail, name = "f"),
        Order(count = 70, delivered = 7, type = 0, cover = R.drawable.ic_follow_small, thumbnail = thumbnail, name = "g"),
        Order(count = 80, delivered = 8, type = 1, cover = R.drawable.ic_follow_small, thumbnail = thumbnail, name = "h"),
        Order(count = 90, delivered = 9, type = 1, cover = R.drawable.ic_like_small, thumbnail = thumbnail, name = "i"),
        Order(count = 100, delivered = 10, type = 0, cover = R.drawable.ic_follow_small, thumbnail = thumbnail, name = "k"),
        Order(count = 110, delivered = 11, type = 1, cover = R.drawable.ic_like_small, thumbnail = thumbnail, name = "l"),
        Order(count = 120, delivered = 12, type = 0, cover = R.drawable.ic_follow_small, thumbnail = thumbnail, name = "m"),
        Order(count = 130, delivered = 13, type = 1, cover = R.drawable.ic_like_small, thumbnail = thumbnail, name = "n"),
        Order(count = 140, delivered = 14, type = 0, cover = R.drawable.ic_follow_small, thumbnail = thumbnail, name = "o"),
        Order(count = 150, delivered = 15, type = 1, cover = R.drawable.ic_like_small, thumbnail = thumbnail, name = "p")
    )

    val recentVideo = listOf(
        RecentVideo(name = "a", urlFull = "a.com", thumbnail = thumbnail),
        RecentVideo(name = "b", urlFull = "b.com", thumbnail = thumbnail),
        RecentVideo(name = "c", urlFull = "c.com", thumbnail = thumbnail),
        RecentVideo(name = "d", urlFull = "d.com", thumbnail = thumbnail),
        RecentVideo(name = "e", urlFull = "e.com", thumbnail = thumbnail),
        RecentVideo(name = "f", urlFull = "f.com", thumbnail = thumbnail),
        RecentVideo(name = "g", urlFull = "g.com", thumbnail = thumbnail),
        RecentVideo(name = "h", urlFull = "h.com", thumbnail = thumbnail)
    )

    val recentUser = listOf(
        RecentUser(name = "a", urlFull = "a.com", thumbnail = thumbnail),
        RecentUser(name = "b", urlFull = "b.com", thumbnail = thumbnail),
        RecentUser(name = "c", urlFull = "c.com", thumbnail = thumbnail),
        RecentUser(name = "d", urlFull = "d.com", thumbnail = thumbnail),
        RecentUser(name = "e", urlFull = "e.com", thumbnail = thumbnail),
        RecentUser(name = "f", urlFull = "f.com", thumbnail = thumbnail),
        RecentUser(name = "g", urlFull = "g.com", thumbnail = thumbnail),
        RecentUser(name = "h", urlFull = "h.com", thumbnail = thumbnail)
    )

    val buyStar = listOf(
        BuyStar(star = 50, price = "46.000đ", type = BuyStarAdapter.SALE, saleType = Sale.NORMAL),
        BuyStar(star = 150, price = "121.000đ", type = BuyStarAdapter.SALE, saleType = Sale.SAVE, sale = 17),
        BuyStar(star = 350, price = "243.000đ", type = BuyStarAdapter.SALE, saleType = Sale.SAVE, sale = 29),
        BuyStar(star = 750, price = "455.000đ", type = BuyStarAdapter.SALE, saleType = Sale.BEST_DEAL, saleContent = "Best Deal"),
        BuyStar(star = 2000, price = "1.200.000đ", type = BuyStarAdapter.SALE, saleType = Sale.SAVE, sale = 33),
        BuyStar(type = BuyStarAdapter.PRO)
    )

    val gallery = listOf(
        Gallery(id = 1, name = "My collage", uriFirstVideo = null, count = 17),
        Gallery(id = 1, name = "My collage", uriFirstVideo = null, count = 17),
        Gallery(id = 1, name = "My collage", uriFirstVideo = null, count = 17),
        Gallery(id = 1, name = "My collage", uriFirstVideo = null, count = 17),
        Gallery(id = 1, name = "My collage", uriFirstVideo = null, count = 17),
        Gallery(id = 1, name = "My collage", uriFirstVideo = null, count = 17),
        Gallery(id = 1, name = "My collage", uriFirstVideo = null, count = 17),
        Gallery(id = 1, name = "My collage", uriFirstVideo = null, count = 17),
        Gallery(id = 1, name = "My collage", uriFirstVideo = null, count = 17),
        Gallery(id = 1, name = "My collage", uriFirstVideo = null, count = 17),
        Gallery(id = 1, name = "My collage", uriFirstVideo = null, count = 17)
    )

    val ratioCrop = mutableListOf(
        "Free size",
        "1:1",
        "4:5",
        "4:3",
        "9:16",
        "16:9",
        "4:5",
        "2:3",
        "3:2"
    )
}