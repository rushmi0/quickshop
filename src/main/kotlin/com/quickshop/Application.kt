package com.quickshop

import com.quickshop.database.DatabaseFactory
import com.quickshop.database.Environment
import io.micronaut.runtime.Micronaut

fun main(args: Array<String>) {
    Micronaut.build()
        .args(*args)
        .start()

    DatabaseFactory.initialize()
}

