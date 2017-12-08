package com.harit.elgamalandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by harit on 12/2/2017.
 */


public class DBHelper extends SQLiteOpenHelper {


    // Logcat tag
    private static final String LOG = DBHelper.class.getName();


    public final static int DATABASE_VERSION = 1;
    public final static String DATABASE_NAME = "Elgamal.db";

    //table and column for fragment_inventory
    private static final String PUBLIC_KEY_TABLE_NAME = "publickey";
    private static final String PUBLIC_KEY_COLUMN_NAME_P = "p";
    private static final String PUBLIC_KEY_COLUMN_NAME_G = "g";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    private static final String SQL_CREATE_PUBLIC_KEY_TABLE =
            "CREATE TABLE " + PUBLIC_KEY_TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY," +
                    PUBLIC_KEY_COLUMN_NAME_G + " INTEGER, "+
                    PUBLIC_KEY_COLUMN_NAME_P + " INTEGER)";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(SQL_CREATE_PUBLIC_KEY_TABLE);
        this.createPublicKey(db, 1, 257);
        this.createPublicKey(db, 2, 263);
        this.createPublicKey(db, 3, 269);
        this.createPublicKey(db, 4, 271);
        this.createPublicKey(db, 5, 277);
        this.createPublicKey(db, 6, 281);
        this.createPublicKey(db, 7, 283);
        this.createPublicKey(db, 8, 293);
        this.createPublicKey(db, 9, 307);
        this.createPublicKey(db, 10, 311);
        this.createPublicKey(db, 11, 313);
        this.createPublicKey(db, 12, 317);
        this.createPublicKey(db, 13, 331);
        this.createPublicKey(db, 14, 337);
        this.createPublicKey(db, 15, 347);
        this.createPublicKey(db, 16, 349);
        this.createPublicKey(db, 17, 353);
        this.createPublicKey(db, 18, 359);
        this.createPublicKey(db, 19, 367);
        this.createPublicKey(db, 20, 373);
        this.createPublicKey(db, 21, 379);
        this.createPublicKey(db, 22, 383);
        this.createPublicKey(db, 23, 389);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + PUBLIC_KEY_TABLE_NAME);
        // create new tables
        onCreate(db);
    }

    public void createPublicKey(SQLiteDatabase db, int g, int p){
        ContentValues values = new ContentValues();
        values.put(PUBLIC_KEY_COLUMN_NAME_G, g);
        values.put(PUBLIC_KEY_COLUMN_NAME_P, p);
        // insert row
        db.insert(PUBLIC_KEY_TABLE_NAME, null, values);
    }


    /**
     * getting all inventories
     * */
    public int getPrimeBasedOnIndex(int index) {
        int prime = 0;
        String selectQuery = "SELECT  * FROM " + PUBLIC_KEY_TABLE_NAME+" WHERE "+
                PUBLIC_KEY_COLUMN_NAME_G+" = "+index;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            prime = c.getInt((c.getColumnIndex(PUBLIC_KEY_COLUMN_NAME_P)));
        }

        return  prime;
    }

}