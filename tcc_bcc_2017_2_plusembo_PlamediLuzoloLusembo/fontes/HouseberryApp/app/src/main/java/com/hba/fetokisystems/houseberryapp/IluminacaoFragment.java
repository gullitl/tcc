package com.hba.fetokisystems.houseberryapp;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.hba.fetokisystems.houseberryapp.main.Comunicador;


/**
 * A simple {@link Fragment} subclass.
 */
public class IluminacaoFragment extends Fragment implements View.OnClickListener {

    private Switch swtLuzSala;
    private Switch swtLuzCozinha;
    private Switch swtLuzQuartoI;
    private Switch swtLuzQuartoII;
    private Switch swtLuzesJardim;
    private Switch swtTodas;

    private View view;

    public IluminacaoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_iluminacao, container, false);

        swtLuzSala = (Switch) view.findViewById(R.id.swtLuzSala);
        swtLuzSala.setOnClickListener(this);

        swtLuzCozinha = (Switch) view.findViewById(R.id.swtLuzCozinha);
        swtLuzCozinha.setOnClickListener(this);

        swtLuzQuartoI = (Switch) view.findViewById(R.id.swtLuzQuartoI);
        swtLuzQuartoI.setOnClickListener(this);

        swtLuzQuartoII = (Switch) view.findViewById(R.id.swtLuzQuartoII);
        swtLuzQuartoII.setOnClickListener(this);

        swtLuzesJardim = (Switch) view.findViewById(R.id.swtLuzesJardim);
        swtLuzesJardim.setOnClickListener(this);

        swtTodas = (Switch) view.findViewById(R.id.swtTodas);
        swtTodas.setOnClickListener(this);

        String status = Comunicador.getInstance().transmite(getActivity(), "CHECKIL");
        int todas = 0;

        for (String e : status.split("-")) {
            switch (e) {
                case "21":
                    swtLuzSala.setChecked(true);
                    todas++;
                    break;

                case "31":
                    swtLuzCozinha.setChecked(true);
                    todas++;
                    break;

                case "41":
                    swtLuzQuartoI.setChecked(true);
                    todas++;
                    break;

                case "51":
                    swtLuzQuartoII.setChecked(true);
                    todas++;
                    break;

                case "61":
                    swtLuzesJardim.setChecked(true);
                    todas++;
                    break;
            }
        }
        swtTodas.setChecked(todas == 5);
        return view;
    }

    public void checkarTodasAsLuzes() {
        if (swtLuzSala.isChecked() && swtLuzCozinha.isChecked()
                && swtLuzQuartoI.isChecked() && swtLuzQuartoII.isChecked()
                && swtLuzesJardim.isChecked()) {
            swtTodas.setChecked(true);
        } else {
            swtTodas.setChecked(false);
        }
    }

    @Override
    public void onClick(View view) {
        String inf = "";
        switch (view.getId()) {
            case R.id.swtLuzSala:
                checkarTodasAsLuzes();
                inf = Comunicador.getInstance().transmite(getActivity(), swtLuzSala.isChecked() ? "GPIO02V" : "GPIO02F");
                break;
            case R.id.swtLuzCozinha:
                checkarTodasAsLuzes();
                inf = Comunicador.getInstance().transmite(getActivity(), swtLuzCozinha.isChecked() ? "GPIO03V" : "GPIO03F");
                break;
            case R.id.swtLuzQuartoI:
                checkarTodasAsLuzes();
                inf = Comunicador.getInstance().transmite(getActivity(), swtLuzQuartoI.isChecked() ? "GPIO04V" : "GPIO04F");
                break;
            case R.id.swtLuzQuartoII:
                checkarTodasAsLuzes();
                inf = Comunicador.getInstance().transmite(getActivity(), swtLuzQuartoII.isChecked() ? "GPIO05V" : "GPIO05F");
                break;
            case R.id.swtLuzesJardim:
                checkarTodasAsLuzes();
                inf = Comunicador.getInstance().transmite(getActivity(), swtLuzesJardim.isChecked() ? "GPIO06V" : "GPIO06F");
                break;
            case R.id.swtTodas:
                swtLuzSala.setChecked(swtTodas.isChecked());
                swtLuzCozinha.setChecked(swtTodas.isChecked());
                swtLuzQuartoI.setChecked(swtTodas.isChecked());
                swtLuzQuartoII.setChecked(swtTodas.isChecked());
                swtLuzesJardim.setChecked(swtTodas.isChecked());
                swtTodas.setChecked(swtTodas.isChecked());
                inf = Comunicador.getInstance().transmite(getActivity(), swtTodas.isChecked() ? "GPIOTLV":"GPIOTLF");
                break;

        }
        Snackbar.make(view, inf, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

}
