package com.tuturing.api.paymentmethod.api

import com.tuturing.api.paymentmethod.dto.resource.PaymentCardDto
import com.stripe.model.Customer
import com.stripe.model.PaymentMethod
import com.stripe.model.SetupIntent
import com.stripe.net.RequestOptions
import java.util.HashMap

/**
 * https://stripe.com/docs/payments/setup-intents
 */
class StripeClient(val request: RequestOptions) {

    // TODO Create Stripe Customer object
    fun createCustomerProfile(input: PaymentCardDto): Customer {
        val customerParams: MutableMap<String, Any> = HashMap()
        customerParams["name"] = input.nameOnCard
        customerParams["shipping"] = input.billingAddress

        val params: MutableMap<String, Any> = HashMap()
        params["customer"] = customerParams

        return Customer.create(params, request)
    }

    fun createPaymentMethod(cardNumber: String, cvv: String, expirationMonth: Long, expirationYear: Long): PaymentMethod {
        val card: MutableMap<String, Any> = HashMap()
        card["number"] = cardNumber
        card["exp_month"] = expirationMonth
        card["exp_year"] = expirationYear
        card["cvc"] = cvv

        val params: MutableMap<String, Any> = HashMap()
        params["type"] = "card"
        params["card"] = card

        return PaymentMethod.create(params, request)
    }

    fun validatePaymentCard(input: String): SetupIntent {
        val params: MutableMap<String, Any> = HashMap()

        params["payment_method"] = input
        params["confirm"] = true

        return SetupIntent.create(params, request)
    }

    // TODO Needed to create stripe customer profile for monthly billing subscriptions
    private fun confirmSetupIntent(id: String, input: String): SetupIntent? {
        val intent: SetupIntent = SetupIntent.retrieve(id)

        val params: MutableMap<String, Any> = HashMap()
        params["payment_method"] = input

        /*
        To create a new Customer and attach the PaymentMethod in one API call.
        val customerParams: MutableMap<String, Any> = HashMap()
        customerParams["payment_method"] = intent.getPaymentMethod()
        Customer.create(customerParams)
        */

        return intent.confirm(params)
    }
}
