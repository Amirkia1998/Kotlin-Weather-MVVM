package com.amirkia.weather.model

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.amirkia.weather.view.MainActivity

class DatabaseHelper(context: MainActivity) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "weather.db"
        private const val DATABASE_VERSION = 3

        private const val TABLE_NAME = "cities"
        private const val COLUMN_ID = "_id"
        private const val COLUMN_CITY_NAME = "city_name"
        private const val COLUMN_DATE = "date"

    }


    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME " +
                "($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_CITY_NAME TEXT, " +
                "$COLUMN_DATE TEXT)"
        db.execSQL(createTableQuery)

        // Insert initial data
        val initialData = arrayOf(
            arrayOf("new york", "2023-05-20"),
            arrayOf("london", "2023-05-19"),
            arrayOf("tokyo", "2023-05-18")
        )

        initialData.forEach { (city, date) ->
            val insertQuery = "INSERT INTO $TABLE_NAME ($COLUMN_CITY_NAME, $COLUMN_DATE) " +
                    "VALUES ('$city', '$date')"
            db.execSQL(insertQuery)
        }
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop the existing table
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")

        // Recreate the table
        onCreate(db)
    }


    fun getCityNames(): ArrayList<Pair<String, String>> {
        val cityNames = ArrayList<Pair<String, String>>()

        val db = readableDatabase
        val query = "SELECT $COLUMN_CITY_NAME, $COLUMN_DATE FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val cityName = cursor.getString(with(cursor) { getColumnIndex(COLUMN_CITY_NAME) })
                val searchDate = cursor.getString(with(cursor) { getColumnIndex(COLUMN_DATE) })
                cityNames.add(Pair(cityName, searchDate))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return cityNames
    }


    fun insertCity(cityName: String, searchDate: String) {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_CITY_NAME, cityName)
            put(COLUMN_DATE, searchDate)
        }
        db.insert(TABLE_NAME, null, contentValues)
        db.close()
    }

    fun clearTable() {

        // Get a writable database instance
        val db = writableDatabase

        // Clear the table by deleting all rows
        db.delete(TABLE_NAME, null, null)

        // Close the database connection
        db.close()

        // Notify the user or perform any other actions after clearing the table

    }




}
