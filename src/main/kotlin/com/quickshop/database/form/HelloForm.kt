package com.quickshop.database.form

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
@Serdeable.Deserializable
data class HelloForm(
    val message: String
)
