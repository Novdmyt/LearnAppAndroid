package com.example.learnwordapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.learnwordapp.database.Word;
import java.util.ArrayList;
import java.util.List;



public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Database.db";

    public DatabaseHelper(Context context) {
        super(context, context.getExternalFilesDir(null).getAbsolutePath() + "/" + DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Initial creation of the database, if necessary
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrade if needed
    }

    public void createTable(SQLiteDatabase db, String tableName) {
        String createTableSQL = "CREATE TABLE " + tableName + " (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Word TEXT, " +
                "Translate TEXT)";
        db.execSQL(createTableSQL);
    }

    public boolean doesTableExist(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{tableName});
        boolean tableExists = cursor.getCount() > 0;
        cursor.close();
        return tableExists;
    }

    public List<String> getTableNames(SQLiteDatabase db) {
        List<String> tableNames = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name NOT IN ('android_metadata', 'sqlite_sequence')", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                tableNames.add(cursor.getString(0));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return tableNames;
    }

    public void insertWord(SQLiteDatabase db, String tableName, String word, String translate) {
        ContentValues values = new ContentValues();
        values.put("word", word);
        values.put("translate", translate);
        db.insert(tableName, null, values);
    }

    public List<Word> getWordsFromTable(SQLiteDatabase db, String tableName) {
        List<Word> words = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT Word, Translate FROM " + tableName, null);
        if (cursor.moveToFirst()) {
            int wordColumnIndex = cursor.getColumnIndex("Word");
            int translateColumnIndex = cursor.getColumnIndex("Translate");
            while (!cursor.isAfterLast()) {
                String word = cursor.getString(wordColumnIndex);
                String translate = cursor.getString(translateColumnIndex);
                words.add(new Word(word, translate));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return words;
    }

    public List<Word> getAllWords(SQLiteDatabase db) {
        List<Word> words = new ArrayList<>();
        List<String> tableNames = getTableNames(db);
        for (String tableName : tableNames) {
            words.addAll(getWordsFromTable(db, tableName));
        }
        return words;
    }
}


