package com.quickshop.route.api.v1

import com.quickshop.database.form.HelloForm
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import org.slf4j.LoggerFactory

@Controller("api/v1")
class Hello {


    @Post(
        "/hello",
        consumes = [MediaType.APPLICATION_JSON],
        produces = [MediaType.APPLICATION_JSON]
    )
    fun hello(
        @Header("Access-Token") access: String,
        @Body payload: HelloForm
    ): MutableHttpResponse<out Any?>? {

        return if (access == "lnwza007@rushmi0.win") {
            LOG.info("payload: $payload")
            HttpResponse.ok(HelloForm("Hello ${payload.message} From API"))
        } else {
            HttpResponse.badRequest(HelloForm("Not authorized"))
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(Hello::class.java)
    }

}