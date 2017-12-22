package com.hba.fetokisystems.houseberryapp.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Plamedi L. Lusembo on 10/2/2017.
 */

public class OpenHelperDao extends SQLiteOpenHelper {

    private static final String nomebd = "Hbdb";

    public OpenHelperDao(Context context) {
        super(context, nomebd, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ScriptDLL.getCreateTableUsuario());
        sqLiteDatabase.execSQL(ScriptDLL.getCreateTableServidorConfig());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
