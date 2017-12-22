package com.hba.fetokisystems.houseberryapp;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;

import com.hba.fetokisystems.houseberryapp.main.Comunicador;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlarmeFragment extends Fragment implements View.OnClickListener {

    private Switch swtAlarme;
    private ListView listView;
    private Button btnAtualizaHistoricoAlarme;
    private View view;

    public AlarmeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_alarme, container, false);
        swtAlarme = (Switch) view.findViewById(R.id.swtAlarme);
        swtAlarme.setOnClickListener(this);

        btnAtualizaHistoricoAlarme = (Button) view.findViewById(R.id.btnAtualizaHistoricoAlarme);
        btnAtualizaHistoricoAlarme.setOnClickListener(this);


        listView = (ListView) view.findViewById(R.id.listView);

        String statusAlarme = Comunicador.getInstance().transmite(getActivity(), "CHECKAL");
        int c = 0;

        //Alarme
        boolean ad = false;
        boolean aa = false;
        for (String e : statusAlarme.split("-")) {
            if (c == 0) {
                ad = e.equals("1");
            } else if (c == 1) {
                aa = e.equals("1");
            }
            c++;
        }
        if (ad) {
            swtAlarme.setChecked(true);

            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Alerta");
            dialog.setMessage("Detecção de movimento."
                    + System.getProperty("line.separator")
                    + System.getProperty("line.separator")
                    + "Alarme desparado!");
            dialog.setNeutralButton("OK", null);
            dialog.show();
        } else {
            swtAlarme.setChecked(aa);
        }

        //Histórico
        atualizaHistorico(Comunicador.getInstance().transmite(getActivity(), "CHECKHT"));

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String deteccao = String.valueOf(adapterView.getItemAtPosition(i));

                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle("Info");
                        dialog.setMessage("Detecção de movimento:"
                                + System.getProperty("line.separator")
                                + deteccao
                                + System.getProperty("line.separator")
                                + System.getProperty("line.separator")
                                + "Alarme desparado!");
                        dialog.setNeutralButton("OK", null);
                        dialog.show();
                    }
                }
        );


        return view;
    }

    @Override
    public void onClick(View view) {
        String inf = "";
        switch (view.getId()) {
            case R.id.swtAlarme:
                if(swtAlarme.isChecked()) {
                    inf = Comunicador.getInstance().transmite(getActivity(), "GPIO12V");
                } else {
                    inf = Comunicador.getInstance().transmite(getActivity(), "GPIO12F");
                    atualizaHistorico(Comunicador.getInstance().transmite(getActivity(), "CHECKHT"));
                }
                inf = Comunicador.getInstance().transmite(getActivity(), swtAlarme.isChecked() ? "GPIO12V" : "GPIO12F");
                Snackbar.make(view, inf, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                break;
            case R.id.btnAtualizaHistoricoAlarme:
                atualizaHistorico(Comunicador.getInstance().transmite(getActivity(), "CHECKHT"));
                Snackbar.make(view, "Histórico atualizado", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                break;
        }

    }

    private void atualizaHistorico(String historico) {
        int c = 0;
        String ht = historico.replace("HT", "");
        String[] deteccoes = new String[ht.split("_").length];
        for (String e : ht.split("_")) {
            deteccoes[c] = e;
            c++;
        }

        ListAdapter listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, deteccoes);
        listView.setAdapter(listAdapter);
    }

}
