package com.afterpay.android.view

import com.afterpay.android.Afterpay
import com.afterpay.android.R
import java.util.Locale

enum class AfterpayColorScheme {
    BLACK_ON_MINT,
    MINT_ON_BLACK,
    WHITE_ON_BLACK,
    BLACK_ON_WHITE;

    internal companion object {
        val DEFAULT: AfterpayColorScheme get() = BLACK_ON_MINT
    }
}

private val locale: Locale
    get() = Afterpay.configuration?.locale ?: Locale.US

internal val AfterpayColorScheme.badgeDrawable: Int
    get() = if (locale == Locale.UK) {
        when (this) {
            AfterpayColorScheme.BLACK_ON_MINT -> R.drawable.clearpay_badge_black_on_mint
            AfterpayColorScheme.MINT_ON_BLACK -> R.drawable.clearpay_badge_mint_on_black
            AfterpayColorScheme.WHITE_ON_BLACK -> R.drawable.clearpay_badge_white_on_black
            AfterpayColorScheme.BLACK_ON_WHITE -> R.drawable.clearpay_badge_black_on_white
        }
    } else {
        when (this) {
            AfterpayColorScheme.BLACK_ON_MINT -> R.drawable.afterpay_badge_black_on_mint
            AfterpayColorScheme.MINT_ON_BLACK -> R.drawable.afterpay_badge_mint_on_black
            AfterpayColorScheme.WHITE_ON_BLACK -> R.drawable.afterpay_badge_white_on_black
            AfterpayColorScheme.BLACK_ON_WHITE -> R.drawable.afterpay_badge_black_on_white
        }
    }

internal val AfterpayColorScheme.payNowButtonDrawable: Int
    get() = if (locale == Locale.UK) {
        when (this) {
            AfterpayColorScheme.BLACK_ON_MINT -> R.drawable.clearpay_button_pay_now_black_on_mint
            AfterpayColorScheme.MINT_ON_BLACK -> R.drawable.clearpay_button_pay_now_mint_on_black
            AfterpayColorScheme.WHITE_ON_BLACK -> R.drawable.clearpay_button_pay_now_white_on_black
            AfterpayColorScheme.BLACK_ON_WHITE -> R.drawable.clearpay_button_pay_now_black_on_mint
        }
    } else {
        when (this) {
            AfterpayColorScheme.BLACK_ON_MINT -> R.drawable.afterpay_button_pay_now_black_on_mint
            AfterpayColorScheme.MINT_ON_BLACK -> R.drawable.afterpay_button_pay_now_mint_on_black
            AfterpayColorScheme.WHITE_ON_BLACK -> R.drawable.afterpay_button_pay_now_white_on_black
            AfterpayColorScheme.BLACK_ON_WHITE -> R.drawable.afterpay_button_pay_now_black_on_mint
        }
    }
