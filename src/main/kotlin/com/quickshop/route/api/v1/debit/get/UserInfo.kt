package com.quickshop.route.api.v1.debit.get

import com.quickshop.database.form.MessageForm
import com.quickshop.database.statement.LedgerServiceImpl
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import jakarta.inject.Inject


@Controller("api/v1/user-info")
class UserInfo(@Inject private val ledgerService: LedgerServiceImpl) {


    @Get("/")
    suspend fun getUserInfo(@QueryValue fullName: String): MutableHttpResponse<out Any>? {
        val userInfo = ledgerService.getUserInfo(fullName)
        return if (userInfo != null) {
            HttpResponse.ok(userInfo)
        } else {
            HttpResponse.badRequest(MessageForm("Failed"))
        }
    }

}