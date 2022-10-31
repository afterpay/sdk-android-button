package com.example.afterpay.checkout

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afterpay.android.Afterpay
import com.afterpay.android.CancellationStatusV3
import com.afterpay.android.model.CheckoutV3Item
import com.afterpay.android.model.OrderTotal
import com.afterpay.android.view.AfterpayPaymentButton
import com.example.afterpay.NavGraph
import com.example.afterpay.R
import com.example.afterpay.checkout.CheckoutViewModel.Command
import com.example.afterpay.getDependencies
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import java.math.BigDecimal
import java.net.URL

class CheckoutFragment : Fragment() {
    private companion object {
        const val CHECKOUT_WITH_AFTERPAY = 1234
        const val CHECKOUT_WITH_AFTERPAY_V3 = 12345
    }

    private val viewModel by viewModels<CheckoutViewModel> {
        CheckoutViewModel.factory(
            totalCost = requireNotNull(arguments?.get(NavGraph.args.total_cost) as? BigDecimal),
            merchantApi = getDependencies().merchantApi,
            preferences = getDependencies().sharedPreferences,
        )
    }

    // when launching the checkout with V2, the token must be generated
    // with 'popupOriginUrl' set to 'https://static.afterpay.com' under the
    // top level 'merchant' object
    private val checkoutHandler = CheckoutHandler(
        onDidCommenceCheckout = { viewModel.loadCheckoutToken() },
        onShippingAddressDidChange = { viewModel.selectAddress(it) },
        onShippingOptionDidChange = { viewModel.selectShippingOption(it) },
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Afterpay.setCheckoutV2Handler(checkoutHandler)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.fragment_checkout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailField = view.findViewById<EditText>(R.id.cart_editText_emailAddress)
        emailField.addTextChangedListener { text ->
            viewModel.enterEmailAddress(email = text.toString())
        }

        val checkoutButton = view.findViewById<AfterpayPaymentButton>(R.id.cart_button_checkout)
        checkoutButton.setOnClickListener { viewModel.showAfterpayCheckout() }

        val totalCost = view.findViewById<TextView>(R.id.cart_totalCost)

        val expressRow = view.findViewById<View>(R.id.cart_expressRow)
        val expressCheckBox = view.findViewById<MaterialCheckBox>(R.id.cart_expressCheckBox)
        expressRow.setOnClickListener { expressCheckBox.toggle() }
        expressCheckBox.setOnCheckedChangeListener { _, checked -> viewModel.checkExpress(checked) }

        val versionRow = view.findViewById<View>(R.id.cart_v1Row)
        val versionCheckBox = view.findViewById<MaterialCheckBox>(R.id.cart_v1CheckBox)
        versionRow.setOnClickListener { versionCheckBox.toggle() }
        versionCheckBox.setOnCheckedChangeListener { _, checked -> viewModel.checkVersion(checked) }

        val buyNowRow = view.findViewById<View>(R.id.cart_buyNowRow)
        val buyNowCheckBox = view.findViewById<MaterialCheckBox>(R.id.cart_buyNowCheckBox)
        buyNowRow.setOnClickListener { buyNowCheckBox.toggle() }
        buyNowCheckBox.setOnCheckedChangeListener { _, checked -> viewModel.checkBuyNow(checked) }

        val pickupRow = view.findViewById<View>(R.id.cart_pickupRow)
        val pickupCheckBox = view.findViewById<MaterialCheckBox>(R.id.cart_pickupCheckBox)
        pickupRow.setOnClickListener { pickupCheckBox.toggle() }
        pickupCheckBox.setOnCheckedChangeListener { _, checked -> viewModel.checkPickup(checked) }

        val shippingOptionsRequiredRow = view.findViewById<View>(R.id.cart_shippingOptionsRequiredRow)
        val shippingOptionsRequiredCheckBox = view.findViewById<MaterialCheckBox>(R.id.cart_shippingOptionsRequiredCheckBox)
        shippingOptionsRequiredRow.setOnClickListener { pickupCheckBox.toggle() }
        shippingOptionsRequiredCheckBox.setOnCheckedChangeListener { _, checked ->
            viewModel.checkShippingOptionsRequired(checked)
        }

        lifecycleScope.launchWhenCreated {
            viewModel.state().collectLatest { state ->
                if (emailField.text.toString() != state.emailAddress) {
                    emailField.setText(state.emailAddress)
                }

                totalCost.text = state.totalCost
                versionCheckBox.isChecked = state.useV1
                expressCheckBox.isChecked = state.express
                buyNowCheckBox.isChecked = state.buyNow
                pickupCheckBox.isChecked = state.pickup
                shippingOptionsRequiredCheckBox.isChecked = state.shippingOptionsRequired
                checkoutButton.isEnabled = state.enableCheckoutButton
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.commands().collectLatest { command ->
                when (command) {
                    is Command.ShowAfterpayCheckoutV2 -> {
                        val intent = Afterpay.createCheckoutV2Intent(requireContext(), command.options)
                        startActivityForResult(intent, CHECKOUT_WITH_AFTERPAY)
                    }
                    is Command.ShowAfterpayCheckoutV3 -> {
                        val intent = Afterpay.createCheckoutV3Intent(
                            requireContext(),
                            consumer = command.consumer,
                            orderTotal = OrderTotal(
                                total = command.total,
                                shipping = BigDecimal.ZERO,
                                tax = BigDecimal.ZERO,
                            ),
                            items = arrayOf(
                                Item(
                                    name = "Coffee",
                                    quantity = 1u,
                                    price = BigDecimal(13.37),
                                ),
                            ),
                            buyNow = command.buyNow,
                        )
                        startActivityForResult(intent, CHECKOUT_WITH_AFTERPAY_V3)
                    }
                    is Command.ShowAfterpayCheckoutV1 -> {
                        val intent = Afterpay.createCheckoutIntent(requireContext(), command.checkoutUrl)
                        startActivityForResult(intent, CHECKOUT_WITH_AFTERPAY)
                    }
                    is Command.ProvideCheckoutTokenResult ->
                        checkoutHandler.provideTokenResult(command.tokenResult)
                    is Command.ProvideShippingOptionsResult ->
                        checkoutHandler.provideShippingOptionsResult(command.shippingOptionsResult)
                    is Command.ProvideShippingOptionUpdateResult ->
                        checkoutHandler.provideShippingOptionUpdateResult(
                            command.shippingOptionUpdateResult,
                        )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode to resultCode) {
            CHECKOUT_WITH_AFTERPAY to AppCompatActivity.RESULT_OK -> {
                val intent = checkNotNull(data) {
                    "Intent should always be populated by the SDK"
                }
                val token = checkNotNull(Afterpay.parseCheckoutSuccessResponse(intent)) {
                    "A token is always associated with a successful Afterpay transaction"
                }
                findNavController().navigate(
                    NavGraph.action.to_receipt,
                    bundleOf(NavGraph.args.checkout_token to token),
                )
            }
            CHECKOUT_WITH_AFTERPAY_V3 to AppCompatActivity.RESULT_OK -> {
                val intent = checkNotNull(data) {
                    "Intent should always be populated by the SDK"
                }
                val resultData = checkNotNull(Afterpay.parseCheckoutSuccessResponseV3(intent)) {
                    "Result data is always associated with a successful V3 Afterpay transaction"
                }
                findNavController().navigate(
                    NavGraph.action.to_details_v3,
                    bundleOf(NavGraph.args.result_data_v3 to resultData),
                )
            }
            CHECKOUT_WITH_AFTERPAY to AppCompatActivity.RESULT_CANCELED -> {
                val intent = requireNotNull(data) {
                    "Intent should always be populated by the SDK"
                }
                val status = checkNotNull(Afterpay.parseCheckoutCancellationResponse(intent)) {
                    "A cancelled Afterpay transaction always contains a status"
                }
                Snackbar.make(requireView(), "Cancelled: $status", Snackbar.LENGTH_SHORT).show()
            }
            CHECKOUT_WITH_AFTERPAY_V3 to AppCompatActivity.RESULT_CANCELED -> {
                val intent = requireNotNull(data) {
                    "Intent should always be populated by the SDK"
                }
                val pair = checkNotNull(Afterpay.parseCheckoutCancellationResponseV3(intent)) {
                    "A cancelled Afterpay transaction always contains a status"
                }
                val errorMessage = when (pair.first) {
                    CancellationStatusV3.CONFIGURATION_ERROR -> "Checkout missing configuration data"
                    CancellationStatusV3.USER_INITIATED -> "Ended by user"
                    CancellationStatusV3.REQUEST_ERROR -> pair.second?.message ?: "Unknown request error"
                }

                Snackbar.make(requireView(), "Cancelled: $errorMessage", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Example implementation of [CheckoutV3Item], which can optionally be included as part of the
     * checkout. This will not affect the total charged to the consumer, which remains the sole
     * responsibility of [OrderTotal.total]
     */
    private data class Item(
        override val name: String,
        override val quantity: UInt,
        override val price: BigDecimal,
        override val sku: String? = null,
        override val pageUrl: URL? = null,
        override val imageUrl: URL? = null,
        override val categories: List<List<String>>? = null,
        override val estimatedShipmentDate: String? = null,
    ) : CheckoutV3Item
}
