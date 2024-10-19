package com.quickshop.database.service

import com.quickshop.database.form.Content
import com.quickshop.database.form.LedgerRequest

interface LedgerService {

    // ฟังก์ชันสำหรับบันทึกข้อมูล ledger
    suspend fun saveLedger(ledgerRequest: LedgerRequest): Boolean

    // ฟังก์ชันสำหรับเรียกข้อมูล ledger โดยใช้ kind และ nick_name
    suspend fun getLedger(kind: Int, nickName: String): List<LedgerRequest>
    fun parseContent(s: String): Content

}
