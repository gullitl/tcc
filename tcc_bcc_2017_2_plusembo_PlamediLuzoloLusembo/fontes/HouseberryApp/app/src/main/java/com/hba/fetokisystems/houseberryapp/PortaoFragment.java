package com.hba.fetokisystems.houseberryapp;


import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.hba.fetokisystems.houseberryapp.main.Comunicador;


/**
 * A simple {@link Fragment} subclass.
 */
public class PortaoFragment extends Fragment implements View.OnClickListener {

    private Button btnAbrirPortao;
    private Button btnFecharPortao;
    private TextView txvLegenda;
    private View view;

    public PortaoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_portao, container, false);

        btnAbrirPortao = (Button) view.findViewById(R.id.btnAbrirPortao);
        btnAbrirPortao.setOnClickListener(this);

        btnFecharPortao = (Button) view.findViewById(R.id.btnFecharPortao);
        btnFecharPortao.setOnClickListener(this);

        txvLegenda = (TextView) view.findViewById(R.id.txvLegenda);

        return view;
    }

    @Override
    public void onClick(View view) {

        String inf = "";

        switch (view.getId()) {
            case R.id.btnAbrirPortao:
                inf = Comunicador.getInstance().transmite(getActivity(), "GPIO22V");
                break;

            case R.id.btnFecharPortao:
                inf = Comunicador.getInstance().transmite(getActivity(), "GPIO22F");
                break;
        }

        Snackbar.make(view, inf, Snackbar.LENGTH_LONG).setAction("Action", null).show();

    }

    private void atualizaStatusPortao(boolean e) {
        txvLegenda.setText(e ? "Portão Aberto." : "Portão Fechado.");
        btnAbrirPortao.setEnabled(!e);
        btnFecharPortao.setEnabled(e);
    }

}
