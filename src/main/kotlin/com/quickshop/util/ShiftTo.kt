package com.quickshop.util

import java.security.MessageDigest

object ShiftTo {

    /**
     * ฟังก์ชัน toHex ใช้ในการแปลง ByteArray เป็นสตริงที่เป็นเลขฐาน 16
     * @return สตริงที่เป็นเลขฐาน 16
     */
    fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }


    /**
     * ฟังก์ชัน toSha256 ใช้ในการคำนวณ Hash SHA-256 ของ ByteArray
     * @return อาร์เรย์ไบต์
     */
    fun ByteArray.toSha256(): ByteArray = MessageDigest.getInstance("SHA-256").digest(this)


    fun String.toBSha256(): ByteArray = toByteArray().toSha256()

}