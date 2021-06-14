package com.solar.dev.tiktok.app.model.app

import com.solar.dev.tiktok.app.R

data class BuyStar(val star: Int = 0,
                   val description: String = "",
                   val price: String = "0",
                   val type: Int = 0,
                   val sale: Int = 0,
                   val saleType: Sale = Sale.NORMAL,
                   val saleContent: String = "",
                   val icon: Int = R.drawable.ic_stars_price,
                   val background: Int = 0
)

enum class Sale {
    NORMAL, SAVE, BEST_DEAL
}