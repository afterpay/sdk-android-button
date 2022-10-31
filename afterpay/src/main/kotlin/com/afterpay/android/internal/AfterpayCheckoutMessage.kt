package com.afterpay.android.internal

import com.afterpay.android.model.ShippingAddress
import com.afterpay.android.model.ShippingOption
import com.afterpay.android.model.ShippingOptionUpdate
import com.afterpay.android.model.ShippingOptionUpdateErrorResult
import com.afterpay.android.model.ShippingOptionUpdateResult
import com.afterpay.android.model.ShippingOptionUpdateSuccessResult
import com.afterpay.android.model.ShippingOptionsErrorResult
import com.afterpay.android.model.ShippingOptionsResult
import com.afterpay.android.model.ShippingOptionsSuccessResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal sealed class AfterpayCheckoutMessage {

    abstract val meta: AfterpayCheckoutMessageMeta

    @Serializable
    internal data class AfterpayCheckoutMessageMeta(val requestId: String)

    companion object {

        fun fromShippingOptionsResult(
            result: ShippingOptionsResult,
            meta: AfterpayCheckoutMessageMeta,
        ): AfterpayCheckoutMessage = when (result) {
            is ShippingOptionsErrorResult -> CheckoutErrorMessage(meta, result.error.name)
            is ShippingOptionsSuccessResult -> ShippingOptionsMessage(meta, result.shippingOptions)
        }

        fun fromShippingOptionUpdateResult(
            result: ShippingOptionUpdateResult?,
            meta: AfterpayCheckoutMessageMeta,
        ): AfterpayCheckoutMessage = when (result) {
            is ShippingOptionUpdateErrorResult -> CheckoutErrorMessage(meta, result.error.name)
            is ShippingOptionUpdateSuccessResult -> ShippingOptionUpdateMessage(
                meta,
                result.shippingOptionUpdate,
            )
            null -> EmptyPayloadMessage(meta)
        }
    }
}

@Serializable
@SerialName("onMessage")
internal data class CheckoutLogMessage(
    override val meta: AfterpayCheckoutMessageMeta,
    val payload: CheckoutLog,
) : AfterpayCheckoutMessage() {

    @Serializable
    internal data class CheckoutLog(
        val severity: String,
        val message: String,
    )
}

@Serializable
@SerialName("onError")
internal data class CheckoutErrorMessage(
    override val meta: AfterpayCheckoutMessageMeta,
    val error: String,
) : AfterpayCheckoutMessage()

@Serializable
@SerialName("onShippingAddressChange")
internal data class ShippingAddressMessage(
    override val meta: AfterpayCheckoutMessageMeta,
    val payload: ShippingAddress,
) : AfterpayCheckoutMessage()

@Serializable
@SerialName("onShippingOptionChange")
internal data class ShippingOptionMessage(
    override val meta: AfterpayCheckoutMessageMeta,
    val payload: ShippingOption,
) : AfterpayCheckoutMessage()

@Serializable
@SerialName("onShippingOptionUpdateChange")
internal data class ShippingOptionUpdateMessage(
    override val meta: AfterpayCheckoutMessageMeta,
    val payload: ShippingOptionUpdate?,
) : AfterpayCheckoutMessage()

@Serializable
@SerialName("onShippingOptionsChange")
internal data class ShippingOptionsMessage(
    override val meta: AfterpayCheckoutMessageMeta,
    val payload: List<ShippingOption>,
) : AfterpayCheckoutMessage()

@Serializable
@SerialName("onEmptyPayload")
internal data class EmptyPayloadMessage(
    override val meta: AfterpayCheckoutMessageMeta,
) : AfterpayCheckoutMessage()
