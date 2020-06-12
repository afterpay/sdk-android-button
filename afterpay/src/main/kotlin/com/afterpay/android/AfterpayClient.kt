package com.afterpay.android

import android.content.Intent

interface AfterpayClient {
    /**
     * Creates an [Intent] that can be used to initiate an Afterpay transaction. Provide the
     * created [Intent] to [startActivityForResult][android.app.Activity.startActivityForResult]
     * to initiate the transaction.
     * @param checkoutRequest The request used to identify the transaction.
     * @return
     */
    fun createCheckoutIntent(checkoutRequest: CheckoutRequest): Intent

    /**
     * Parses the status from the checkout transaction.
     * @param intent The intent returned from
     * [startActivityForResult][android.app.Activity.startActivityForResult]
     * @return The checkout status.
     */
    fun parseCheckoutResponse(intent: Intent): CheckoutStatus
}
