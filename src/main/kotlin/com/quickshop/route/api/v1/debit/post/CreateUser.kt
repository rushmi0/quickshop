package com.quickshop.route.api.v1.debit.post

import com.quickshop.database.form.MessageForm
import com.quickshop.database.statement.LedgerServiceImpl
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.serde.annotation.Serdeable
import io.micronaut.http.HttpResponse
import org.slf4j.LoggerFactory

@Serdeable.Serializable
@Serdeable.Deserializable
data class CreateUserRequest(
    val full_name: String,
    val createdAt: String,
    val content: UserContent
)

@Serdeable.Serializable
@Serdeable.Deserializable
data class UserContent(
    val nick_name: String,
    val email: String
)

@Controller("/api/v1/debit")
class CreateUser(private val ledgerService: LedgerServiceImpl) {

    @Post("/create")
    suspend fun createUser(@Body payload: CreateUserRequest): MutableHttpResponse<out Any> {

        LOG.info("Creating user ${payload.full_name}")

        // เรียกใช้ LedgerService เพื่อสร้างผู้ใช้ใหม่
        val result = ledgerService.createUser(
            fullName = payload.full_name,
            nickName = payload.content.nick_name,
            email = payload.content.email,
            createdAt = payload.createdAt
        )

        // ตอบกลับ HTTP Response ตามผลลัพธ์
        return if (result) {
            HttpResponse.ok(MessageForm("Success"))
        } else {
            HttpResponse.badRequest(MessageForm("Failed"))
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(CreateUser::class.java)
    }

}
