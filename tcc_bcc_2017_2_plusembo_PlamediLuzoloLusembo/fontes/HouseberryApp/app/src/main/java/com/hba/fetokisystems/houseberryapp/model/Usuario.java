package com.hba.fetokisystems.houseberryapp.model;

/**
 * Created by Plamedi L. Lusembo on 10/2/2017.
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Plamedi L. Lusembo
 */
public class Usuario {

    private int cdUsuario;
    private String email;
    private String senha;
    private String nome;

    public Usuario() {
    }

    public int getCdUsuario() {
        return cdUsuario;
    }

    public void setCdUsuario(int cdUsuario) {
        this.cdUsuario = cdUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.cdUsuario;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        return this.getCdUsuario() == other.getCdUsuario();
    }

    @Override
    public String toString() {
        return "[Id=" + getCdUsuario() + " Nome=" + getNome() + " E-mail=" + getEmail() + "]";
    }

}

