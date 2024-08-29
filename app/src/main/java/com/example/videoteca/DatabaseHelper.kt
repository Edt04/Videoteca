package com.example.videoteca
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.videoteca.User

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    companion object {
        const val DATABASE_NAME = "Login.db"
        const val TABLE_NAME = "users"
        const val COL_1 = "ID"
        const val COL_2 = "USERNAME"
        const val COL_3 = "PASSWORD"
        const val COL_4 = "EMAIL"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_NAME (" +
                    "$COL_1 INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_2 TEXT," +
                    "$COL_3 TEXT," +
                    "$COL_4 TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertData(username: String, password: String, email: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL_2, username)
            put(COL_3, password)
            put(COL_4, email)
        }
        val result = db.insert(TABLE_NAME, null, contentValues)
        return result != -1L
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $COL_2 = ? AND $COL_3 = ?",
            arrayOf(username, password)
        )
        return cursor.count > 0
    }

    fun getUser(username: String): User? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COL_2 = ?",
            arrayOf(username),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            val userEmail = cursor.getString(cursor.getColumnIndexOrThrow(COL_4))
            cursor.close()
            User(username, userEmail)
        } else {
            cursor.close()
            null
        }
    }

    fun validatePassword(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COL_3),
            "$COL_2 = ? AND $COL_3 = ?",
            arrayOf(username, password),
            null,
            null,
            null
        )

        val isValid = cursor.count > 0
        cursor.close()
        return isValid
    }

    fun updatePassword(username: String, newPassword: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_3, newPassword)
        }
        val rowsAffected = db.update(TABLE_NAME, values, "$COL_2= ?", arrayOf(username))
        return rowsAffected > 0
    }
}
