package com.hba.fetokisystems.houseberryapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.hba.fetokisystems.houseberryapp.dao.ConnectionFactory;
import com.hba.fetokisystems.houseberryapp.dao.OpenHelperDao;
import com.hba.fetokisystems.houseberryapp.dao.ServidorConfigDao;
import com.hba.fetokisystems.houseberryapp.dao.UsuarioDao;
import com.hba.fetokisystems.houseberryapp.model.ServidorConfig;
import com.hba.fetokisystems.houseberryapp.model.Usuario;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtEmail;
    private EditText edtSenha;

    private CheckBox cbxConfiguracoes;

    private SQLiteDatabase conexao;
    private OpenHelperDao openHelperDao;

    public static boolean mostraConfiguracoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Login");

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtSenha = (EditText) findViewById(R.id.edtSenha);

        Button btnEntrar = (Button) findViewById(R.id.btnEntrar);
        btnEntrar.setOnClickListener(this);

        edtSenha.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                entra();
                return true;
            }
        });

        cbxConfiguracoes = (CheckBox) findViewById((R.id.cbxConfiguracoes));
        mostraConfiguracoes = false;

        // Pra não ter que entrar toda vez o login, sera apagado na versão final
        edtEmail.setText("plam.lusembo@gmail.com");
        edtSenha.setText("09876");

        //Usuário de teste
        //----------------

        //Remover todos os usuarios
        UsuarioDao.getInstance(ConnectionFactory.getInstance().abreNovaConexao(this))
                .removerTodoUsuario();

        //Salvar Usuario
        Usuario usuario = new Usuario();
        usuario.setEmail("plam.lusembo@gmail.com");
        usuario.setSenha("09876");
        usuario.setNome("Plamedi L. Lusembo");

        if (UsuarioDao.getInstance(ConnectionFactory.getInstance().abreNovaConexao(this)).insereUsuario(usuario)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
            dialog.setTitle("Inserção");
            dialog.setMessage("Usuário inserido com sucesso");
            dialog.setNeutralButton("OK", null);
            dialog.show();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
            dialog.setTitle("Erro");
            dialog.setMessage("Erro ao inserir usuário");
            dialog.setNeutralButton("OK", null);
            dialog.show();
        }

        //Configurações do Servidor de teste
        ServidorConfig servidorConfig = new ServidorConfig();
        servidorConfig.setIpServidor("192.168.1.36");
        servidorConfig.setPorta(9000);

        if (ServidorConfigDao.getInstance(ConnectionFactory.getInstance().abreNovaConexao(this)).insereServidorConfig(servidorConfig)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
            dialog.setTitle("Inserção");
            dialog.setMessage("Configurações do Servidor inseridos com sucesso");
            dialog.setNeutralButton("OK", null);
            dialog.show();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
            dialog.setTitle("Erro");
            dialog.setMessage("Erro ao inserir Configurações do Servidor");
            dialog.setNeutralButton("OK", null);
            dialog.show();
        }


        System.out.println("Aqui que eu quero te mostrar uma coisa");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEntrar:
                entra();
                break;
        }
    }

    private void entra() {

        boolean cancelado = false;
        View focusView = null;

        if (TextUtils.isEmpty(edtSenha.getText().toString())) {
            edtSenha.setError("SENHA: Campo obrigatório");
            focusView = edtSenha;
            cancelado = true;
        }

        if (TextUtils.isEmpty(edtEmail.getText().toString())) {
            edtEmail.setError("E-MAIL: Campo obrigatório");
            if (!cancelado) {
                focusView = edtEmail;
                cancelado = true;
            }
        } else if (!ehEmailValido(edtEmail.getText().toString())) {
            edtEmail.setError("E-mail inválido");
            if (!cancelado) {
                focusView = edtEmail;
                cancelado = true;
            }
        }

        if (cancelado) {
            focusView.requestFocus();
        } else {

            Usuario usuario = UsuarioDao.getInstance(ConnectionFactory.getInstance().abreNovaConexao(this))
                    .selecionaUsuario(edtEmail.getText().toString(), edtSenha.getText().toString());
            if (usuario != null) {
                hideKeyBoard();
                mostraConfiguracoes = cbxConfiguracoes.isChecked();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("email", usuario.getEmail());
                intent.putExtra("nome", usuario.getNome());
                startActivity(intent);
                finish();
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                dialog.setTitle("Informação");
                dialog.setMessage("Usuário não encontrado! "
                        +System.getProperty("line.separator")
                        +System.getProperty("line.separator")
                        +"Por favor, verifique seu E-mail e sua senha, e tente outra vez");
                dialog.setNeutralButton("OK", null);
                dialog.show();
                edtEmail.requestFocus();
            }

        }

    }

    private boolean ehEmailValido(String email) {
        Pattern p = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]{2,7}$");
        Matcher m = p.matcher(email);
        return m.find();
    }

    public void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
