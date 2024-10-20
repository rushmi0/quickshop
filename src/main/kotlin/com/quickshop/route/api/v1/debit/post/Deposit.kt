package com.quickshop.route.api.v1.debit.post

import com.quickshop.database.form.MessageForm
import com.quickshop.database.statement.LedgerServiceImpl
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Inject
import org.slf4j.LoggerFactory

@Serdeable.Serializable
@Serdeable.Deserializable
data class DepositData(
    val fullName: String,
    val createdAt: String,
    val amount: Int
)

@Serdeable.Serializable
data class DepositRecord(
    val createdAt: String,  // วันที่ฝากเงิน
    val amount: Int         // จำนวนเงินที่ฝาก
)

@Controller("api/v1/debit")
class Deposit(@Inject private val ledgerService: LedgerServiceImpl) {


    @Get("/deposits/{fullName}", produces = [MediaType.APPLICATION_JSON])
    suspend fun depositsList(fullName: String): MutableHttpResponse<out Any>? {
        return try {
            val deposits = ledgerService.getUserDeposits(fullName)
            HttpResponse.ok(deposits)
        } catch (e: Exception) {
            HttpResponse.serverError(MessageForm("Failed"))
        }
    }


    @Post("/deposit", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    suspend fun deposit(@Body payload: DepositData): MutableHttpResponse<out Any>? {
        return if (ledgerService.deposit(payload.fullName, payload.amount, payload.createdAt)) {

            HttpResponse.ok(MessageForm("Success"))
        } else {
            HttpResponse.badRequest(MessageForm("Failed"))
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(Deposit::class.java)
    }

}
