package com.example.videoteca

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MovieDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "videoteca.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_MOVIES = "movies"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_GENRE = "genre"
        private const val COLUMN_YEAR = "year"
        private const val COLUMN_IMAGE = "image"
        private const val COLUMN_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_MOVIES (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TITLE TEXT," +
                "$COLUMN_GENRE TEXT," +
                "$COLUMN_YEAR INTEGER," +
                "$COLUMN_IMAGE INTEGER," +
                "$COLUMN_DESCRIPTION TEXT)")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIES")
        onCreate(db)
    }

    fun insertMovie(title: String, genre: String, year: Int, imageResId: Int, description: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_GENRE, genre)
            put(COLUMN_YEAR, year)
            put(COLUMN_IMAGE, imageResId)
            put(COLUMN_DESCRIPTION, description)
        }
        db.insert(TABLE_MOVIES, null, contentValues)
        db.close()
    }

    fun getAllMovies(): List<Film> {
        val movies = mutableListOf<Film>()
        val db = this.readableDatabase

        // Corretto utilizzo di rawQuery
        val cursor = db.rawQuery("SELECT * FROM $TABLE_MOVIES", null)

        if (cursor.moveToFirst()) {
            do {
                val movie = Film(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    genre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENRE)),
                    year = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_YEAR)),
                    imageResId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                )
                movies.add(movie)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return movies
    }

    fun searchMovies(query: String): List<Film> {
        val movies = mutableListOf<Film>()
        val db = this.readableDatabase

        // Utilizzo di rawQuery per cercare film
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_MOVIES WHERE $COLUMN_TITLE LIKE ? OR $COLUMN_GENRE LIKE ?",
            arrayOf("%$query%", "%$query%")
        )

        if (cursor.moveToFirst()) {
            do {
                val movie = Film(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    genre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENRE)),
                    year = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_YEAR)),
                    imageResId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                )
                movies.add(movie)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return movies
    }

    fun deleteMovie(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_MOVIES, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
    }
}
