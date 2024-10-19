package com.quickshop.database.table

import org.jetbrains.exposed.sql.Table

object LEDGER: Table("ledger") {

    val LEDGER_ID = text("ledger_id").uniqueIndex()
    val FULL_NAME = text("full_name")
    val CREATED_AT = text("created_at")
    val KIND = integer("kind")
    val CONTENT = text("content")

}