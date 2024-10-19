package com.quickshop.route.api.v1.debit.post

import com.quickshop.database.form.MessageForm
import com.quickshop.database.statement.LedgerServiceImpl
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
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


@Controller("api/v1/debit")
class Deduct(@Inject private val ledgerService: LedgerServiceImpl) {

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
