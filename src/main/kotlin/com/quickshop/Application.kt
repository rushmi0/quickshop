package com.quickshop

import com.quickshop.database.DatabaseFactory
import io.micronaut.runtime.Micronaut

fun main(args: Array<String>) {
    Micronaut.build()
        .args(*args)
        .start()

    DatabaseFactory.initialize()
}

