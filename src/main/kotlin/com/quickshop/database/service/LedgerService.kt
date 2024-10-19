package com.quickshop.database.service

import com.quickshop.database.record.DebitBalance
import com.quickshop.database.record.Ledger

interface LedgerService {

    // ฟังก์ชันสำหรับสร้างบัญชีผู้ใช้ใหม่
    suspend fun createUser(fullName: String, nickName: String, email: String, createdAt: String): Boolean

    // ฟังก์ชันสำหรับดึงข้อมูลบัญชีลูกค้าตาม fullName
    suspend fun getUserInfo(fullName: String): Ledger?

    // ฟังก์ชันสำหรับบันทึกข้อมูลเดบิตการฝากเงิน
    suspend fun deposit(fullName: String, amount: Int, createdAt: String): Boolean

    // ฟังก์ชันสำหรับบันทึกข้อมูลการตัดเงินสำหรับการซื้อสินค้า
    suspend fun deduct(fullName: String, price: Int, createdAt: String): Boolean

    // ฟังก์ชันสำหรับดึงข้อมูลสรุปยอดคงเหลือเดบิต
    suspend fun getDebitBalance(fullName: String): DebitBalance?
}
