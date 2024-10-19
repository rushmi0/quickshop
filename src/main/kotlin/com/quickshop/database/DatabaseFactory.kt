package com.quickshop.database

import com.quickshop.database.table.LEDGER
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.micronaut.context.annotation.Bean
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

@Bean
object DatabaseFactory {

    @JvmStatic
    fun initialize() {

        Database.connect(hikariConfig())
        transaction {
            SchemaUtils.create(LEDGER)
        }
    }

    private fun hikariConfig(): HikariDataSource {

        val config = HikariConfig().apply {

            driverClassName = "org.sqlite.JDBC"

            // กำหนด่าสำหรับการเชื่อมต่อกับฐานข้อมูล
            jdbcUrl = "jdbc:sqlite:/home/rushmi0/items/dev/Kotlin/JVM/quickshop/src/main/kotlin/com/quickshop/database/storage.db"


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