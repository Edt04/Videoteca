package com.example.videoteca

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.room.Room
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
        private const val  COLUMN_STATE = "state"


        // Tabella Rented Movies
        const val TABLE_RENTED_MOVIES = "rented_movies"
        const val COLUMN_RENTAL_ID = "rental_id"
        const val COLUMN_USER_ID="username"
        const val COLUMN_FILM_ID = "film_id"
        const val COLUMN_RENTAL_DATE = "rental_date"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_MOVIES (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TITLE TEXT," +
                "$COLUMN_GENRE TEXT," +
                "$COLUMN_YEAR INTEGER," +
                "$COLUMN_IMAGE TEXT," +
                "$COLUMN_DESCRIPTION TEXT,"+
                "$COLUMN_STATE INTEGER DEFAULT 1)")
        db?.execSQL(createTable)

        val createRentedMoviesTable =   ("CREATE TABLE $TABLE_RENTED_MOVIES(" +
                "$COLUMN_RENTAL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USER_ID TEXT," +
                "$COLUMN_FILM_ID TEXT," +
                "$COLUMN_RENTAL_DATE TEXT)"
                )
        db?.execSQL(createRentedMoviesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("ALTER TABLE $TABLE_MOVIES ADD COLUMN $COLUMN_STATE INTEGER DEFAULT 1")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RENTED_MOVIES")
        onCreate(db)
    }


    fun addMovie(title: String, genre: String, year: Int, imageUrl: String, description: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_GENRE, genre)
            put(COLUMN_YEAR, year)
            put(COLUMN_IMAGE, imageUrl)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_STATE,1)
        }


        val result = db.insert(TABLE_MOVIES, null, values)
        if (result == -1L) {
            Log.e("MovieDatabaseHelper", "Error adding movie: $title")
        }
        return result
    }
    fun insertRentedMovie(userId: String, filmId: String,  rentalDate: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_FILM_ID, filmId)
            put(COLUMN_RENTAL_DATE, rentalDate)
        }
        return db.insert(TABLE_RENTED_MOVIES, null, values)
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
                    imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    state = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATE)) == 1
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
                    imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    state = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATE)) == 1
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
        db.delete(TABLE_MOVIES, "$COLUMN_TITLE = ?", arrayOf(film.toString()))
        db.close()
    }
    fun getAllNewMovies():List<Film>{
        val movies = mutableListOf<Film>()
        val db = this.readableDatabase

        // Corretto utilizzo di rawQuery
        val cursor = db.rawQuery("SELECT * FROM movies ORDER BY $COLUMN_ID DESC LIMIT 10", null)

        if (cursor.moveToFirst()) {
            do {
                val movie = Film(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    genre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENRE)),
                    year = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_YEAR)),
                    imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    state = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATE)) == 1

                )
                movies.add(movie)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return movies
    }

    fun changeState(title: String, newState: Boolean): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_STATE, if (newState) 1 else 0) // Convertiamo il booleano in un intero (1 per true, 0 per false)
        }

        // Aggiornare la colonna `state` per il film con il titolo specificato
        val result = db.update(
            TABLE_MOVIES,
            contentValues,
            "$COLUMN_TITLE = ?",
            arrayOf(title)
        )

        db.close()
        return result > 0 // Se una o più righe sono state aggiornate, l'operazione è stata un successo
    }
    fun getRentedFilmsByUser(user: String): List<Film> {
        val rentedFilms = mutableListOf<Film>()
        val db = readableDatabase
        val query = "SELECT * FROM rented_movies WHERE username = ?"
        val cursor = db.rawQuery(query, arrayOf(user.toString()))

        if (cursor.moveToFirst()) {
            do {
                val film = Film(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    genre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENRE)),
                    year = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_YEAR)),
                    imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    state = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATE)) == 1

                )
                rentedFilms.add(film)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return rentedFilms
    }
    fun searchRentedMovies(query: String): List<Film> {
        val rentedMovies = mutableListOf<Film>()
        val db = this.readableDatabase

        // Utilizzo di rawQuery per cercare film
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_RENTED_MOVIES WHERE $COLUMN_TITLE LIKE ? OR $COLUMN_GENRE LIKE ?",
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
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    state = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATE)) == 1
                )
                rentedMovies.add(movie)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return rentedMovies
    }


}
