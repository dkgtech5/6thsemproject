package com.example.safe

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * Handles all local SQLite database operations including user authentication
 * and scan history management.
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val TAG = "DatabaseHelper"
        private const val DATABASE_NAME = "SafeGuard.db"
        private const val DATABASE_VERSION = 3
        
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_FULL_NAME = "full_name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_IS_VERIFIED = "is_verified"

        private const val TABLE_SCANS = "scans"
        private const val COLUMN_SCAN_ID = "scan_id"
        private const val COLUMN_URL = "url"
        private const val COLUMN_TIMESTAMP = "timestamp"
        private const val COLUMN_IS_SAFE = "is_safe"
        private const val COLUMN_RISK_SCORE = "risk_score"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsersTable = ("CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_FULL_NAME TEXT, " +
                "$COLUMN_EMAIL TEXT UNIQUE, " +
                "$COLUMN_PASSWORD TEXT, " +
                "$COLUMN_IS_VERIFIED INTEGER DEFAULT 0)")
        
        val createScansTable = ("CREATE TABLE $TABLE_SCANS (" +
                "$COLUMN_SCAN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_URL TEXT, " +
                "$COLUMN_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "$COLUMN_IS_SAFE INTEGER, " +
                "$COLUMN_RISK_SCORE REAL)")

        db?.execSQL(createUsersTable)
        db?.execSQL(createScansTable)
        Log.d(TAG, "Database created: $DATABASE_NAME")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            val createScansTable = ("CREATE TABLE $TABLE_SCANS (" +
                    "$COLUMN_SCAN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_URL TEXT, " +
                    "$COLUMN_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "$COLUMN_IS_SAFE INTEGER, " +
                    "$COLUMN_RISK_SCORE REAL)")
            db?.execSQL(createScansTable)
        }
        if (oldVersion < 3) {
            db?.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_IS_VERIFIED INTEGER DEFAULT 0")
        }
        Log.d(TAG, "Database upgraded from version $oldVersion to $newVersion")
    }

    fun registerUser(fullName: String, email: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_FULL_NAME, fullName)
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_PASSWORD, password)
        values.put(COLUMN_IS_VERIFIED, 0)
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        Log.d(TAG, "Register User: $email, Result: $result")
        return result
    }

    fun verifyUser(email: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_IS_VERIFIED, 1)
        val result = db.update(TABLE_USERS, values, "$COLUMN_EMAIL = ?", arrayOf(email))
        db.close()
        return result > 0
    }

    fun getAllUsers(): List<Map<String, String>> {
        val userList = mutableListOf<Map<String, String>>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS", null)

        if (cursor.moveToFirst()) {
            do {
                val user = mapOf(
                    "id" to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)).toString(),
                    "full_name" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)),
                    "email" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    "password" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                    "is_verified" to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_VERIFIED)).toString()
                )
                userList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        Log.d(TAG, "Get All Users: Found ${userList.size} users")
        return userList
    }

    fun checkEmail(email: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun loginUser(email: String, password: String): Int {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))
        
        var result = 0 // 0: Failed, 1: Success, 2: Not Verified
        if (cursor.moveToFirst()) {
            val isVerified = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_VERIFIED))
            result = if (isVerified == 1) 1 else 2
        }
        
        cursor.close()
        db.close()
        return result
    }

    fun getUserDetails(email: String): Map<String, String>? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        var userMap: Map<String, String>? = null

        if (cursor.moveToFirst()) {
            userMap = mapOf(
                "id" to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)).toString(),
                "full_name" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)),
                "email" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            )
        }
        cursor.close()
        db.close()
        return userMap
    }

    fun updatePassword(email: String, newPassword: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_PASSWORD, newPassword)
        val result = db.update(TABLE_USERS, values, "$COLUMN_EMAIL = ?", arrayOf(email))
        db.close()
        Log.d(TAG, "Update Password for $email: Result code $result")
        return result
    }

    fun saveScan(url: String, isSafe: Boolean, riskScore: Double): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_URL, url)
        values.put(COLUMN_IS_SAFE, if (isSafe) 1 else 0)
        values.put(COLUMN_RISK_SCORE, riskScore)
        val result = db.insert(TABLE_SCANS, null, values)
        db.close()
        return result
    }

    fun getRecentScans(limit: Int = 5): List<RecentScan> {
        val scanList = mutableListOf<RecentScan>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_SCANS ORDER BY $COLUMN_TIMESTAMP DESC LIMIT $limit"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val url = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL))
                val isSafe = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_SAFE)) == 1
                val timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                scanList.add(RecentScan(url, timestamp, isSafe))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return scanList
    }

    fun getScanStats(): Map<String, Int> {
        val stats = mutableMapOf("total" to 0, "safe" to 0, "threats" to 0)
        val db = this.readableDatabase
        
        // Single query for total and safe scans to optimize performance
        val cursor = db.rawQuery("SELECT COUNT(*), SUM(CASE WHEN $COLUMN_IS_SAFE = 1 THEN 1 ELSE 0 END) FROM $TABLE_SCANS", null)
        if (cursor.moveToFirst()) {
            stats["total"] = cursor.getInt(0)
            stats["safe"] = cursor.getInt(1)
            stats["threats"] = stats["total"]!! - stats["safe"]!!
        }
        cursor.close()
        db.close()
        return stats
    }

    fun getWeeklyStats(): List<Int> {
        val dailyCounts = mutableListOf(0, 0, 0, 0, 0, 0, 0) // Mon to Sun
        val db = this.readableDatabase
        
        // Query to get count of scans for the last 7 days grouped by day of week
        // SQLite: 0=Sunday, 1=Monday, ..., 6=Saturday
        val query = """
            SELECT strftime('%w', $COLUMN_TIMESTAMP) as day_of_week, COUNT(*) 
            FROM $TABLE_SCANS 
            WHERE $COLUMN_TIMESTAMP >= date('now', '-6 days')
            GROUP BY day_of_week
        """.trimIndent()

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val sqliteDay = cursor.getInt(0) // 0 (Sun) to 6 (Sat)
                val count = cursor.getInt(1)
                
                // Convert to our list index (0=Mon, 1=Tue, ..., 5=Sat, 6=Sun)
                val index = when (sqliteDay) {
                    0 -> 6 // Sunday is last
                    else -> sqliteDay - 1
                }
                if (index in 0..6) {
                    dailyCounts[index] = count
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return dailyCounts
    }

    fun clearScans() {
        val db = this.writableDatabase
        db.delete(TABLE_SCANS, null, null)
        db.close()
    }
}
