package com.quickshop

import com.quickshop.database.DatabaseFactory
import com.quickshop.database.Environment
import io.micronaut.runtime.Micronaut
import io.micronaut.runtime.Micronaut.run

fun main(args: Array<String>) {
    val relay = Micronaut.build()
        .args(*args)
        .start()

    DatabaseFactory.ENV = relay.getBean(Environment::class.java)
    DatabaseFactory.initialize()
}

