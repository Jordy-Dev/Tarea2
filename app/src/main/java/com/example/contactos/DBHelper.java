package com.example.contactos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // Nombre de la base de datos
    public static final String DATABASE_NAME = "Contactos.db";

    // Nombre de la tabla y columnas
    public static final String TABLE_NAME = "contactos";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NOMBRE";
    public static final String COL_3 = "TELEFONO";

    // Constructor
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);  // Versión 1 de la base de datos
    }

    // Crear la tabla de contactos
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " TEXT, " +
                COL_3 + " TEXT)");
    }

    // Si la base de datos necesita ser actualizada
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Agregar un nuevo contacto
    public boolean addContacto(String nombre, String telefono) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO " + TABLE_NAME + " (" + COL_2 + ", " + COL_3 + ") VALUES ('" + nombre + "', '" + telefono + "')");
        return true;
    }

    // Eliminar un contacto
    public boolean deleteContacto(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COL_1 + " = " + id);
        return true;
    }

    // Obtener todos los contactos
    public Cursor getAllContactos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
}
