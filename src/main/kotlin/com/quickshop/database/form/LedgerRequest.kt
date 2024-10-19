package com.quickshop.database.form

import io.micronaut.serde.annotation.Serdeable

// Data class หลักที่รับ JSON เข้ามา
@Serdeable.Serializable
@Serdeable.Deserializable
data class LedgerRequest(
    val nick_name: String,
    val created_at: String,
    val kind: Int,
    val content: Content
)

// สร้าง sealed class เพื่อจัดการกับเนื้อหาที่ต่างกันใน content ตาม kind
@Serdeable.Serializable
@Serdeable.Deserializable
sealed class Content {

    @Serdeable.Serializable
    @Serdeable.Deserializable
    data class AccountInfo(
        val full_name: String,
        val email: String
    ) : Content()

    @Serdeable.Serializable
    @Serdeable.Deserializable
    data class DebitTransaction(
        val amount: Double,
        val description: String
    ) : Content()

    @Serdeable.Serializable
    @Serdeable.Deserializable
    data class CreditTransaction(
        val amount: Double,
        val description: String,
        val due_date: String
    ) : Content()

    @Serdeable.Serializable
    @Serdeable.Deserializable
    data class CreditPayment(
        val amount: Double,
        val description: String
    ) : Content()

}
