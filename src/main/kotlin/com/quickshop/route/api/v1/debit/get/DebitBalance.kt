package com.quickshop.route.api.v1.debit.get

import com.quickshop.database.form.MessageForm
import com.quickshop.database.statement.LedgerServiceImpl
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import jakarta.inject.Inject

@Controller("/api/v1/debit-balance")
class DebitBalance(@Inject private val ledgerService: LedgerServiceImpl) {

    @Get("/")
    suspend fun getDebitBalance(@QueryValue fullName: String): MutableHttpResponse<out Any>? {
        val balance = ledgerService.getDebitBalance(fullName)
        return if (balance != null) {
            HttpResponse.ok(balance)
        } else {
            HttpResponse.badRequest(MessageForm("Failed"))
        }
    }

}