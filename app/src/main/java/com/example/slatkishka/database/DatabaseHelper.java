package com.example.slatkishka.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// помошна класа за дата базата

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SlatkishkaLocal.db";
    private static final int DATABASE_VERSION = 6;
    private static final String TABLE_USERS = "korisnici";
    private static final String TABLE_FAVORITES = "omileni";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sozdadiTabela = "CREATE TABLE " + TABLE_USERS +
                "(username TEXT PRIMARY KEY, password TEXT, role TEXT, name TEXT, phone TEXT, category TEXT)";
        db.execSQL(sozdadiTabela); // креира табела „Корисници“ во која се чуваат корисничкото име, лозинката и дали е клиент или бизнис
        String createFavoritesTable = "CREATE TABLE " + TABLE_FAVORITES + "(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, product_id INTEGER)";
        db.execSQL(createFavoritesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        onCreate(db);
    }


    // МЕТОДИ ЗА ТАБЕЛАТА СО КОРИСНИЦИ

    public boolean insertUser(String username, String password, String role, String name, String phone, String category) {
        SQLiteDatabase db = this.getWritableDatabase(); // да може да се запишува во дата базата
        ContentValues values = new ContentValues(); // контејнер за податоците кои треба да се внесат

        values.put("username", username);
        values.put("password", password);
        values.put("role", role);
        values.put("name", name);
        values.put("phone", phone);
        values.put("category", category);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1; // дали операцијата била успешна
    }

    public boolean removeUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase(); // да може да брише во дата базата

        long result = db.delete(TABLE_USERS, "username = ?", new String[]{username});
        return result > 0; // дали операцијата била успешнa
    }

    public boolean checkUsernamePassword(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase(); // да може да се прочита од дата базата
        // резултатите од SQL прашалникот се сместуваат во курсор
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE username = ? AND password = ?", new String[]{username, password});
        boolean postoi = cursor.getCount() > 0; // се проверува дали е пронајден корисникот
        cursor.close(); // се затвора курсорот
        return postoi;
    }

    public String getUserRole(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        // типот на пронајдениот корисник се сместува во курсорот
        Cursor cursor = db.rawQuery("SELECT role FROM " + TABLE_USERS + " WHERE username = ?", new String[]{username});
        String role = "client"; // нека default вредноста за типот биде клиент
        if (cursor.moveToFirst()) {
            role = cursor.getString(0);
        }
        cursor.close();
        return role;
    }

    public String getFullName(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM " + TABLE_USERS + " WHERE username = ?", new String[]{username});

        if (cursor.moveToFirst()) {
            String fullName = cursor.getString(0);
            cursor.close();
            return fullName;
        }

        cursor.close();
        return "Име и Презиме"; // default вредност
    }

    public String getPhone(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT phone FROM " + TABLE_USERS + " WHERE username = ?", new String[]{username});

        if (cursor.moveToFirst()) {
            String phone = cursor.getString(0);
            cursor.close();
            return phone;
        }

        cursor.close();
        return "Нема телефонски број";
    }

    // МЕТОДИ ЗА ТАБЕЛАТА СО ОМИЛЕНИ ПРОИЗВОДИ

    public boolean addFavorite(String username, int productId) {
        SQLiteDatabase db = this.getWritableDatabase(); // да може да се запишува во дата базата
        ContentValues values = new ContentValues(); // контејнер за податоците кои треба да се внесат

        values.put("username", username);
        values.put("product_id", productId);

        long result = db.insert(TABLE_FAVORITES, null, values);
        return result != -1;
    }

    public boolean removeFavorite(String username, int productId) {
        SQLiteDatabase db = this.getWritableDatabase(); // да може да се запишува во дата базата

        long result = db.delete(TABLE_FAVORITES, "username = ? AND product_id = ?", new String[]{username, String.valueOf(productId)});
        return result > 0; // дали операцијата била успешнa
    }

    public boolean isFavorite(String username, int productId) {
        SQLiteDatabase db = this.getReadableDatabase(); // да може да се прочита од дата базата
        // резултатите од SQL прашалникот се сместуваат во курсор
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVORITES + " WHERE username = ? AND product_id = ?", new String[]{username, Integer.toString(productId)});
        boolean postoi = cursor.getCount() > 0; // се проверува дали е пронајден корисникот
        cursor.close();
        return postoi;
    }
}
