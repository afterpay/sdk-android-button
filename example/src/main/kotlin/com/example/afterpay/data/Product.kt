package com.example.afterpay.data

import java.math.BigDecimal
import java.util.UUID

data class Product(
    val id: UUID,
    val name: String,
    val description: String,
    val price: BigDecimal,
)
