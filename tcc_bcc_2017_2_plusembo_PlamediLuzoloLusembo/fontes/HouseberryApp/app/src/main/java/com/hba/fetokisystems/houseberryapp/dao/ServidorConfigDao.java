package com.hba.fetokisystems.houseberryapp.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hba.fetokisystems.houseberryapp.model.ServidorConfig;

/**
 * Created by Plamedi L. Lusembo on 10/2/2017.
 */

public class ServidorConfigDao {

    private SQLiteDatabase conn;
    private static ServidorConfigDao uniqueInstance;


    public ServidorConfigDao(SQLiteDatabase conn) {
        this.conn = conn;
    }

        public static synchronized ServidorConfigDao getInstance(SQLiteDatabase conn) {
        if (uniqueInstance == null) {
            uniqueInstance = new ServidorConfigDao(conn);
        }
        return uniqueInstance;
    }

    public ServidorConfig selecionaServidorConfig() {
        Cursor crs = conn.rawQuery("SELECT * FROM ServidorConfig", null);

        if(crs.moveToFirst()) {
            return new ServidorConfig(crs.getString(1), crs.getInt(2));
        }
        return null;
    }

    public boolean insereServidorConfig(ServidorConfig servidorConfig) {

        ContentValues cv = new ContentValues();
        cv.put("ipServidor", servidorConfig.getIpServidor());
        cv.put("porta", servidorConfig.getPorta());

        long resultado = conn.insert("ServidorConfig", null, cv);

        return resultado != -1;

    }

    public boolean removerTodoServidorConfig() {
        int resultado = conn.delete("ServidorConfig", "1", null);

        conn.execSQL("DELETE FROM sqlite_sequence WHERE name='ServidorConfig'");

        return resultado != 0;
    }

}
