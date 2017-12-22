package com.hba.fetokisystems.houseberryapp.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hba.fetokisystems.houseberryapp.model.Usuario;

/**
 * Created by Plamedi L. Lusembo on 10/2/2017.
 */

public class UsuarioDao {

    private SQLiteDatabase conn;
    private static UsuarioDao uniqueInstance;

    public UsuarioDao(SQLiteDatabase conn) {
        this.conn = conn;
    }

    public static synchronized UsuarioDao getInstance(SQLiteDatabase conn) {
        if (uniqueInstance == null) {
            uniqueInstance = new UsuarioDao(conn);
        }
        return uniqueInstance;
    }

    public Usuario selecionaUsuario(String email, String senha) {

        Cursor crs = conn.rawQuery("SELECT * FROM Usuario WHERE email='"+email +"' AND senha='"+senha+"'", null);

        if(crs.moveToFirst()) {
            Usuario usuario = new Usuario();
            usuario.setCdUsuario(crs.getInt(0));
            usuario.setEmail(crs.getString(1));
            usuario.setSenha(crs.getString(2));
            usuario.setNome(crs.getString(3));

            return usuario;
        }

        return null;

    }

    public boolean insereUsuario(Usuario usuario) {
        ContentValues cv = new ContentValues();
        cv.put("email", usuario.getEmail());
        cv.put("senha", usuario.getSenha());
        cv.put("nome", usuario.getNome());

        long resultado = conn.insert("Usuario", null, cv);

        return resultado != -1;

    }

    public boolean removerTodoUsuario() {
        int resultado = conn.delete("Usuario", "1", null);

        conn.execSQL("DELETE FROM sqlite_sequence WHERE name='Usuario'");

        return resultado != 0;
    }


}
