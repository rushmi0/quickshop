package com.quickshop.database

import com.quickshop.database.table.LEDGER
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    @Inject
    lateinit var ENV: Environment

    @JvmStatic
    fun initialize() {

        if (!::ENV.isInitialized) {
            throw IllegalStateException("ENV has not been initialized")
        }

        Database.connect(hikariConfig())
        transaction {
            SchemaUtils.create(LEDGER)
        }
    }

    private fun hikariConfig(): HikariDataSource {

        val config = HikariConfig().apply {

            driverClassName = "org.postgresql.Driver"

            // กำหนด่าสำหรับการเชื่อมต่อกับฐานข้อมูล
            jdbcUrl = "${ENV.DATABASE_URL}:${ENV.DATABASE_PORT}/${ENV.DATABASE_NAME}"
            username = ENV.DATABASE_USERNAME
            password = ENV.DATABASE_PASSWORD

            minimumIdle = 2
            maximumPoolSize = 10

            isAutoCommit = false

            idleTimeout = 30000
            keepaliveTime = 30000
            maxLifetime = 1800000
            leakDetectionThreshold = 30000
            validationTimeout = 3000

            transactionIsolation = "TRANSACTION_REPEATABLE_READ"

            validate()
        }

        // สร้างและคืนค่าอ็อบเจกต์ HikariDataSource ที่กำหนดค่า
        return HikariDataSource(config)
    }


    suspend fun <T> queryTask(block: () -> T): T = withContext(Dispatchers.IO) {
        transaction {
            //addLogger(StdOutSqlLogger)
            block()
        }
    }

}