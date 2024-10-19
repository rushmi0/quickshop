package com.quickshop.database.statement

import com.quickshop.database.DatabaseFactory.queryTask
import com.quickshop.database.form.Content
import com.quickshop.database.form.LedgerRequest
import com.quickshop.database.service.LedgerService
import com.quickshop.database.table.LEDGER
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class LedgerServiceImpl : LedgerService {

    override suspend fun saveLedger(ledgerRequest: LedgerRequest): Boolean {
        return try {
            queryTask {
                // บันทึกข้อมูลลงในฐานข้อมูล
                LEDGER.insert {
                    it[NICK_NAME] = ledgerRequest.nick_name
                    it[CREATED_AT] = ledgerRequest.created_at
                    it[KIND] = ledgerRequest.kind
                    it[CONTENT] = ledgerRequest.content.toString() // อาจต้องแปลง content เป็น JSON string ก่อนบันทึก
                }
            }
            true
        } catch (e: Exception) {
            // การจัดการข้อผิดพลาดสามารถปรับแต่งเพิ่มเติมได้
            println("Error saving ledger: ${e.message}")
            false
        }
    }

    override suspend fun getLedger(kind: Int, nickName: String): List<LedgerRequest> {
        return queryTask {
            try {
                // เริ่มต้นสร้างคำสั่ง SQL
                val query = LEDGER.select {
                    (LEDGER.KIND eq kind) and (LEDGER.NICK_NAME eq nickName)
                }

                // การค้นหาข้อมูลจากฐานข้อมูล
                query.map { row ->
                    LedgerRequest(
                        nick_name = row[LEDGER.NICK_NAME],
                        created_at = row[LEDGER.CREATED_AT], // เปลี่ยนเป็น CREATED_AT เพื่อให้ตรงกับการประกาศใน LEDGER.kt
                        kind = row[LEDGER.KIND],
                        content = parseContent(row[LEDGER.CONTENT]) // แปลง content จาก JSON string เป็น object
                    )
                }
            } catch (e: Exception) {
                println("Error fetching ledger: ${e.message}")
                emptyList()
            }
        }
    }

    override fun parseContent(s: String): Content {
        TODO("Not yet implemented")
    }


}
