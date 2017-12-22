package com.hba.fetokisystems.houseberryapp;


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
public class THArCondicionadoFragment extends Fragment implements View.OnClickListener {

    private Switch swtArCondicionado;
    private TextView txvTemperatura;
    private TextView txvHumidade;
    private Button btnAtualizaTH;
    private View view;

    public THArCondicionadoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_tharcondicionado, container, false);

        swtArCondicionado = (Switch) view.findViewById(R.id.swtArCondicionado);
        swtArCondicionado.setOnClickListener(this);

        txvTemperatura = (TextView) view.findViewById(R.id.txvTemperatura);
        txvHumidade = (TextView) view.findViewById(R.id.txvHumidade);

        btnAtualizaTH = (Button) view.findViewById(R.id.btnAtualizaTH);
        btnAtualizaTH.setOnClickListener(this);


        String status = Comunicador.getInstance().transmite(getActivity(), "CHECKTH");
        int c = 0;
        //Eg. AR1-T27-H30
        //1=Ar Condicionado; 27=Temperatura; 30=Humidade;
        for (String e : status.split("-")) {
            if (c == 0 && e.equals("1")) {
                swtArCondicionado.setChecked(false);
            } else if (c == 0 && e.equals("0")) {
                swtArCondicionado.setChecked(true);
            } else if(c == 1) {
                txvTemperatura.setText(e);
            } else if(c == 2) {
                txvHumidade.setText(e);
            }
            c++;
        }

        return view;
    }

    @Override
    public void onClick(View view) {

        String inf = "";

        switch (view.getId()) {
            case R.id.swtArCondicionado:
                inf = Comunicador.getInstance().transmite(getActivity(), swtArCondicionado.isChecked() ? "GPIO17V" : "GPIO17F");
                Snackbar.make(view, inf, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                break;
            case R.id.btnAtualizaTH:
                inf = Comunicador.getInstance().transmite(getActivity(), "GPIO26V");
                int c = 0;
                for (String th: inf.split("-")) {
                    if(c == 0) txvTemperatura.setText(th);
                    else if(c == 1) txvHumidade.setText(th);
                    c++;
                }
                inf = "Temperatura e Humidade Atualizadas";
                Snackbar.make(view, inf, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                break;
        }

    }

}
