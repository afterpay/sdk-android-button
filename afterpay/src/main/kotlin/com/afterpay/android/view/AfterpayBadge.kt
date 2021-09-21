package com.afterpay.android.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView.ScaleType.FIT_CENTER
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.use
import com.afterpay.android.Afterpay
import com.afterpay.android.R
import com.afterpay.android.internal.coloredDrawable
import com.afterpay.android.internal.dp

private const val MIN_WIDTH: Int = 64

class AfterpayBadge @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    var colorScheme: AfterpayColorScheme = AfterpayColorScheme.DEFAULT
        set(value) {
            field = value
            update()
        }

    init {
        contentDescription = resources.getString(Afterpay.brand.serviceNameDescription)
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
        isFocusable = true
        scaleType = FIT_CENTER
        adjustViewBounds = true
        minimumWidth = MIN_WIDTH.dp

        context.theme.obtainStyledAttributes(attrs, R.styleable.Afterpay, 0, 0).use { attributes ->
            colorScheme = AfterpayColorScheme.values()[
                attributes.getInteger(
                    R.styleable.Afterpay_afterpayColorScheme,
                    AfterpayColorScheme.DEFAULT.ordinal
                )
            ]
        }
    }

    private fun update() {
        setImageDrawable(
            context.coloredDrawable(
                drawableResId = Afterpay.brand.badgeFg,
                colorResId = colorScheme.foregroundColorResId
            )
        )

        background = context.coloredDrawable(
            R.drawable.afterpay_badge_bg,
            colorScheme.backgroundColorResId
        )

        invalidate()
        requestLayout()
    }
}
