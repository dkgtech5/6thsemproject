package com.example.safe

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val TAG = "DatabaseHelper"
        private const val DATABASE_NAME = "SafeGuard.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_FULL_NAME = "full_name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = ("CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_FULL_NAME TEXT, " +
                "$COLUMN_EMAIL TEXT UNIQUE, " +
                "$COLUMN_PASSWORD TEXT)")
        db?.execSQL(createTableQuery)
        Log.d(TAG, "Database created: $DATABASE_NAME")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
        Log.d(TAG, "Database upgraded from version $oldVersion to $newVersion")
    }

    fun registerUser(fullName: String, email: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_FULL_NAME, fullName)
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_PASSWORD, password)
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        Log.d(TAG, "Register User: $email, Result: $result")
        return result
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
                    "password" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
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

    fun loginUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))
        val success = cursor.count > 0
        cursor.close()
        db.close()
        return success
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
}
