package com.quickshop.database.record

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
@Serdeable.Deserializable
data class Ledger(
    val fullName: String,
    val createdAt: String,
    val kind: Int,
    val content: String
)

@Serdeable.Serializable
@Serdeable.Deserializable
data class DebitBalance(
    val createdAt: String,
    val fullName: String,
    val totalDeposit: Int,
    val totalSpent: Int,
    val balance: Int
)