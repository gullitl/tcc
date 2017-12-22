package com.hba.fetokisystems.houseberryapp.dao;

import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

/**
 * Created by Plamedi L. Lusembo on 10/5/2017.
 */

public class ConnectionFactory {
    private static ConnectionFactory uniqueInstance;

    public ConnectionFactory() {
    }

    public static synchronized ConnectionFactory getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new ConnectionFactory();
        }
        return uniqueInstance;
    }

    public SQLiteDatabase abreNovaConexao(Context context) {
        return new OpenHelperDao(context).getWritableDatabase();
    }

}
