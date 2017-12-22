package com.hba.fetokisystems.houseberryapp;


import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hba.fetokisystems.houseberryapp.dao.ConnectionFactory;
import com.hba.fetokisystems.houseberryapp.dao.OpenHelperDao;
import com.hba.fetokisystems.houseberryapp.dao.ServidorConfigDao;
import com.hba.fetokisystems.houseberryapp.model.ServidorConfig;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConfiguracoesFragment extends Fragment implements View.OnClickListener {

    private EditText edtIpServidor;
    private EditText edtPorta;

    private Button btnSalavarConfig;
    private View view;

    public ConfiguracoesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_configuracoes, container, false);
        edtIpServidor = (EditText) view.findViewById(R.id.edtIpServidor);
        edtPorta = (EditText) view.findViewById(R.id.edtPorta);

        edtPorta.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                salvarConfig();
                return true;
            }
        });

        btnSalavarConfig = (Button) view.findViewById(R.id.btnSalvarConfig);
        btnSalavarConfig.setOnClickListener(this);

        buscarConfiguracoes();
        return view;
    }



    @Override
    public void onClick(View view) {salvarConfig();}

    private void salvarConfig() {

        boolean cancelado = false;
        View focusView = null;

        if (TextUtils.isEmpty(edtIpServidor.getText().toString())) {
            edtIpServidor.setError("HOST: Campo obrigatório");
            focusView = edtIpServidor;
            cancelado = true;
        }

        if (TextUtils.isEmpty(edtPorta.getText().toString())) {
            edtPorta.setError("E-MAIL: Campo obrigatório");
            if (!cancelado){
                focusView = edtPorta;
                cancelado = true;
            }
        }

        if (cancelado) {
            focusView.requestFocus();
        } else {
            ServidorConfig servidorConfig = new ServidorConfig();
            servidorConfig.setIpServidor(edtIpServidor.getText().toString());
            servidorConfig.setPorta(Integer.parseInt(edtPorta.getText().toString()));

            ServidorConfigDao.getInstance(ConnectionFactory.getInstance().abreNovaConexao(getActivity()))
                    .removerTodoServidorConfig();

            if (ServidorConfigDao.getInstance(ConnectionFactory.getInstance().abreNovaConexao(getActivity()))
                    .insereServidorConfig(servidorConfig)) {
                hideKeyBoard();
                Snackbar.make(view, "(; Configurações inseridas com sucesso!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("Erro");
                dialog.setMessage("Erro ao inserir usuário");
                dialog.setNeutralButton("OK", null);
                dialog.show();
            }
        }
    }

    public void hideKeyBoard() {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    private void buscarConfiguracoes() {

        ServidorConfig servidorConfig = ServidorConfigDao.getInstance(ConnectionFactory.getInstance().abreNovaConexao(getActivity()))
                .selecionaServidorConfig();
        if (servidorConfig != null) {
            edtIpServidor.setText(servidorConfig.getIpServidor());
            edtPorta.setText(Integer.toString(servidorConfig.getPorta()));
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Informação");
            dialog.setMessage("Configurações não encontradas! "
                    +System.getProperty("line.separator")
                    +System.getProperty("line.separator")
                    +"Favor registrar uma nova configuração");
            dialog.setNeutralButton("OK", null);
            dialog.show();
            edtIpServidor.requestFocus();
        }
    }

}
