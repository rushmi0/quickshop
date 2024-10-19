package com.quickshop.database.table

import org.jetbrains.exposed.sql.Table

object LEDGER: Table("ledger") {

    val LEDGER_ID = integer("ledger_id").uniqueIndex()
    val NICK_NAME = varchar("nick_name", 64)
    val CREATED_AT = varchar("created_at", 64)
    val KIND = integer("kind")
    val CONTENT = text("content")

}