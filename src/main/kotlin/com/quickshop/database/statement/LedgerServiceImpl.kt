package com.quickshop.database.statement

import com.quickshop.database.service.LedgerService
import com.quickshop.database.table.LEDGER
import com.quickshop.database.DatabaseFactory.queryTask
import com.quickshop.database.record.DebitBalance
import com.quickshop.database.record.Ledger
import com.quickshop.route.api.v1.debit.post.DeductRecord
import com.quickshop.route.api.v1.debit.post.DepositRecord
import com.quickshop.util.ShiftTo.toBSha256
import com.quickshop.util.ShiftTo.toHex
import io.micronaut.context.annotation.Bean
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.intOrNull
import org.jetbrains.exposed.sql.*
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Bean
class LedgerServiceImpl : LedgerService {

    // ฟังก์ชันสำหรับสร้างบัญชีผู้ใช้ใหม่
    override suspend fun createUser(fullName: String, nickName: String, email: String, createdAt: String): Boolean = queryTask {
        try {
            val id = "${UUID.randomUUID()}$fullName$nickName$email$createdAt".toBSha256().toHex()

            LEDGER.insert {
                it[LEDGER_ID] = id
                it[FULL_NAME] = fullName
                it[CREATED_AT] = createdAt
                it[KIND] = 0
                it[CONTENT] = Json.encodeToString(
                    mapOf(
                        "nick_name" to nickName,
                        "email" to email
                    )
                )
            }
            true
        } catch (e: Exception) {
            LOG.error("Error during user creation: ${e.message}")
            false
        }
    }



    override suspend fun deposit(fullName: String, amount: Int, createdAt: String): Boolean = queryTask {
        try {

            val id = "${UUID.randomUUID()}$fullName$amount$createdAt".toBSha256().toHex()

            /**
             * INSERT INTO LEDGER (full_name, created_at, kind, content)
             * VALUES (:fullName, :createdAt, 1, '{"amount": :amount}')
             */
            LEDGER.insert {
                it[LEDGER_ID] = id
                it[FULL_NAME] = fullName
                it[CREATED_AT] = createdAt
                it[KIND] = 1  // kind = 1 สำหรับการฝากเงิน
                it[CONTENT] = Json.encodeToString(mapOf("amount" to amount))
            }
            true
        } catch (e: Exception) {
            LOG.error("Error during deposit: ${e.message}")
            false
        }
    }

    override suspend fun deduct(fullName: String, price: Int, createdAt: String): Boolean = queryTask {
        try {

            val id = "${UUID.randomUUID()}$fullName$price$createdAt".toBSha256().toHex()

            /**
             * INSERT INTO LEDGER (full_name, created_at, kind, content)
             * VALUES (:fullName, :createdAt, 2, '{"price": :price}')
             */
            LEDGER.insert {
                it[LEDGER_ID] = id
                it[FULL_NAME] = fullName
                it[CREATED_AT] = createdAt
                it[KIND] = 2  // kind = 2 สำหรับการตัดเงินจากการซื้อสินค้า
                it[CONTENT] = Json.encodeToString(mapOf("price" to price))
            }
            true
        } catch (e: Exception) {
            LOG.error("Error during deduct: ${e.message}")
            false
        }
    }

    override suspend fun getDebitBalance(fullName: String): DebitBalance? = queryTask {
        /**
         * SELECT SUM(CAST(content->>'amount' AS INTEGER))
         * FROM LEDGER
         * WHERE full_name = :fullName AND kind = 1
         */
        val deposits = LEDGER
            .selectAll().where { (LEDGER.FULL_NAME eq fullName) and (LEDGER.KIND eq 1) }
            .mapNotNull { it[LEDGER.CONTENT].toAmount() }
            .sum()

        /**
         * SELECT SUM(CAST(content->>'price' AS INTEGER))
         * FROM LEDGER
         * WHERE full_name = :fullName AND kind = 2
         */
        val spends = LEDGER
            .selectAll().where { (LEDGER.FULL_NAME eq fullName) and (LEDGER.KIND eq 2) }
            .mapNotNull { it[LEDGER.CONTENT].toPrice() }
            .sum()

        // กำหนด createdAt เป็นเวลาปัจจุบันในรูปแบบ MM/dd/yyyy
        val createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))


        DebitBalance(createdAt, fullName, deposits, spends, deposits - spends)
    }



    override suspend fun getUserInfo(fullName: String): Ledger? = queryTask {
        /**
         * SELECT * FROM LEDGER
         * WHERE full_name = :fullName AND kind = 0
         */
        LEDGER.selectAll()
            .where { (LEDGER.FULL_NAME eq fullName) and (LEDGER.KIND eq 0) }
            .map { rowToLedger(it) }
            .singleOrNull()
    }


    private fun rowToLedger(row: ResultRow): Ledger {
        return Ledger(
            row[LEDGER.FULL_NAME],
            row[LEDGER.CREATED_AT],
            row[LEDGER.KIND],
            row[LEDGER.CONTENT]
        )
    }


    suspend fun getUserDeducts(fullName: String): List<DeductRecord> = queryTask {
        /**
         * SELECT created_at, content->>'price' AS price
         * FROM LEDGER
         * WHERE full_name = :fullName AND kind = 2
         */
        LEDGER
            .selectAll().where { (LEDGER.FULL_NAME eq fullName) and (LEDGER.KIND eq 2) }
            .mapNotNull {
                val price = it[LEDGER.CONTENT].toPrice()
                price?.let { amt ->
                    DeductRecord(
                        createdAt = it[LEDGER.CREATED_AT],  // วันที่ตัดเงิน
                        price = amt                          // จำนวนเงินที่ตัด
                    )
                }
            }
    }



    // ฟังก์ชันสำหรับดึงรายการฝากเงินของผู้ใช้
    suspend fun getUserDeposits(fullName: String): List<DepositRecord> = queryTask {
        /**
         * SELECT created_at, content->>'amount' AS amount
         * FROM LEDGER
         * WHERE full_name = :fullName AND kind = 1
         */
        LEDGER
            .selectAll().where { (LEDGER.FULL_NAME eq fullName) and (LEDGER.KIND eq 1) }
            .mapNotNull {
                val amount = it[LEDGER.CONTENT].toAmount()
                amount?.let { amt ->
                    DepositRecord(
                        createdAt = it[LEDGER.CREATED_AT],  // วันที่ฝาก
                        amount = amt                        // จำนวนเงินที่ฝาก
                    )
                }
            }
    }


    private fun String.toAmount(): Int? {
        return try {
            Json.parseToJsonElement(this).jsonObject["amount"]?.jsonPrimitive?.intOrNull
        } catch (e: Exception) {
            null
        }
    }

    private fun String.toPrice(): Int? {
        return try {
            Json.parseToJsonElement(this).jsonObject["price"]?.jsonPrimitive?.intOrNull
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(LedgerServiceImpl::class.java)
    }

}
