package com.quickshop.route.api.v1.debit.get

import com.quickshop.database.form.MessageForm
import com.quickshop.database.record.Ledger
import com.quickshop.database.statement.LedgerServiceImpl
import io.micronaut.context.annotation.Bean
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Inject
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

@Serializable
data class Content(
    val nick_name: String,
    val email: String
)


@Serializable
@Serdeable.Serializable
@Serdeable.Deserializable
data class NewLedger(
    val fullName: String,
    val createdAt: String,
    val kind: Int,
    val nick_name: String,
    val email: String
)

@Bean
@Controller("api/v1/user-info")
class UserInfo @Inject constructor(private val ledgerService: LedgerServiceImpl) {

    @Get("/{fullName}")
    suspend fun getUserInfo(fullName: String): MutableHttpResponse<out Any>? {
        val userInfo: Ledger? = ledgerService.getUserInfo(fullName)
        LOG.info("fullName: $fullName, userInfo: $userInfo")

        return if (userInfo != null) {
            try {
                // แปลง JSON String ในฟิลด์ content เป็น Kotlin object ของ Content
                val contentObject = Json.decodeFromString<Content>(userInfo.content)
                LOG.info("contentObject: $contentObject")

                // สร้าง Ledger object ใหม่ที่มี content เป็น object
                val responseLedger = NewLedger(
                    fullName = userInfo.fullName,
                    createdAt = userInfo.createdAt,
                    kind = userInfo.kind,
                    nick_name = contentObject.nick_name,
                    email =contentObject.email
                )
                LOG.info("responseLedger: $responseLedger")

                HttpResponse.ok(responseLedger)
            } catch (e: Exception) {
                LOG.error("Failed to parse content: ${e.message}")
                HttpResponse.badRequest(MessageForm("Failed to parse content"))
            }
        } else {
            HttpResponse.badRequest(MessageForm("Failed"))
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(UserInfo::class.java)
    }
}
