package com.hba.fetokisystems.houseberryapp.dao;

/**
 * Created by Plamedi L. Lusembo on 10/2/2017.
 */

public class ScriptDLL {

    public static String getCreateTableUsuario() {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS Usuario (");
        sql.append("cdUsuario INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,");
        sql.append("email VARCHAR (250) NOT NULL DEFAULT (''),");
        sql.append("senha VARCHAR (250) NOT NULL DEFAULT (''),");
        sql.append("nome VARCHAR (250) NOT NULL DEFAULT (''));");

        return sql.toString();
    }

    public static String getCreateTableServidorConfig() {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ServidorConfig (");
        sql.append("cdServidorConfic INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,");
        sql.append("ipServidor VARCHAR (250) NOT NULL DEFAULT (''),");
        sql.append("porta INTEGER NOT NULL);");

        return sql.toString();
    }


}
