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
data class DeductData(
    val fullName: String,
    val createdAt: String,
    val price: Int
)

@Serdeable.Serializable
data class DeductRecord(
    val createdAt: String,  // วันที่ตัดเงิน
    val price: Int          // จำนวนเงินที่ตัด
)

@Controller("api/v1/debit")
class Deduct(@Inject private val ledgerService: LedgerServiceImpl) {


    @Get("/deduct/{fullName}", produces = [MediaType.APPLICATION_JSON])
    suspend fun deductList(fullName: String): MutableHttpResponse<out Any>? {
        return try {
            val deducts = ledgerService.getUserDeducts(fullName)
            HttpResponse.ok(deducts)
        } catch (e: Exception) {
            HttpResponse.serverError(MessageForm("Failed"))
        }
    }


    @Post("/deduct", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    suspend fun deduct(@Body payload: DeductData): MutableHttpResponse<out Any>? {
        return if (ledgerService.deduct(payload.fullName, payload.price, payload.createdAt)) {
            HttpResponse.ok(MessageForm("Success"))
        } else {
            HttpResponse.badRequest(MessageForm("Failed"))
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(Deduct::class.java)
    }
}
