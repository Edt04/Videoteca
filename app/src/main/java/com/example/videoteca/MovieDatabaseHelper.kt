package com.example.videoteca

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Movie
import android.util.Log


class MovieDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "videoteca.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_MOVIES = "movies"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_GENRE = "genre"
        private const val COLUMN_YEAR = "year"
        private const val COLUMN_IMAGE = "image_url"
        private const val COLUMN_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_MOVIES (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TITLE TEXT," +
                "$COLUMN_GENRE TEXT," +
                "$COLUMN_YEAR INTEGER," +
                "$COLUMN_IMAGE TEXT," +
                "$COLUMN_DESCRIPTION TEXT," +
                "is_rented INTEGER DEFAULT 0)") // Colonna per indicare se il film è noleggiato
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIES")
        onCreate(db)
    }

    fun addMovie(
        title: String,
        genre: String,
        year: Int,
        imageUrl: String,
        description: String,
        isRented: Boolean = false
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_GENRE, genre)
            put(COLUMN_YEAR, year)
            put(COLUMN_IMAGE, imageUrl)
            put(COLUMN_DESCRIPTION, description)
            put("is_rented", if (isRented) 1 else 0) // Gestisce il valore di noleggio
        }

        val result = db.insert(TABLE_MOVIES, null, values)
        if (result == -1L) {
            Log.e("MovieDatabaseHelper", "Error adding movie: $title")
        }
        return result
    }

    fun updateMovieRentedStatus(movieId: Int, isRented: Boolean) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("is_rented", if (isRented) 1 else 0)
        }
        db.update(TABLE_MOVIES, values, "$COLUMN_ID = ?", arrayOf(movieId.toString()))
        db.close()
    }

    fun getAllMovies(): List<Film> {
        val movies = mutableListOf<Film>()
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TABLE_MOVIES", null)

        if (cursor.moveToFirst()) {
            do {
                val movie = Film(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    genre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENRE)),
                    year = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_YEAR)),
                    imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
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
                    imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                )
                movies.add(movie)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return movies
    }

    fun deleteMovie(film: String) {
        val db = this.writableDatabase
        db.delete(TABLE_MOVIES, "$COLUMN_TITLE = ?", arrayOf(film))
        db.close()
    }

    fun getAllNewMovies(): List<Film> {
        val movies = mutableListOf<Film>()
        val db = this.readableDatabase

        val cursor =
            db.rawQuery("SELECT * FROM $TABLE_MOVIES ORDER BY $COLUMN_ID DESC LIMIT 10", null)

        if (cursor.moveToFirst()) {
            do {
                val movie = Film(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    genre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENRE)),
                    year = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_YEAR)),
                    imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                )
                movies.add(movie)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return movies
    }

    fun getRentedMovies(): List<Film> {
        val rentedMovies = mutableListOf<Film>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_MOVIES WHERE is_rented = 1", null)

        if (cursor.moveToFirst()) {
            do {
                val movie = Film(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    genre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENRE)),
                    year = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_YEAR)),
                    imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                )
                rentedMovies.add(movie)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return rentedMovies
    }


    fun searchRentedMovies(query: String) {

    }
}
