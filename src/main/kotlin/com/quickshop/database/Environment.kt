package com.quickshop.database


import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv

import io.micronaut.context.annotation.Bean

@Bean
object Environment {

    private val dotenv: Dotenv = dotenv {
        directory = "." // root path
        filename = ".env"
    }

    // Database settings
    val DATABASE_NAME: String by lazy { dotenv["DATABASE_NAME"] }
    val DATABASE_URL: String by lazy { dotenv["DATABASE_URL"] }
    val DATABASE_PORT: String by lazy { dotenv["DATABASE_PORT"] }
    val DATABASE_USERNAME: String by lazy { dotenv["DATABASE_USERNAME"] }
    val DATABASE_PASSWORD: String by lazy { dotenv["DATABASE_PASSWORD"] }

}