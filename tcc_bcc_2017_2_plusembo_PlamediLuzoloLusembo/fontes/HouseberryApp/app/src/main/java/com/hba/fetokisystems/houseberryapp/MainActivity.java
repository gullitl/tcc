package com.hba.fetokisystems.houseberryapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final ConfiguracoesFragment configuracoesFragment = new ConfiguracoesFragment();
    private final IluminacaoFragment iluminacaoFragment = new IluminacaoFragment();
    private final PortaoFragment portaoFragment = new PortaoFragment();
    private final THArCondicionadoFragment thArCondicionadoFragment = new THArCondicionadoFragment();
    private final AlarmeFragment alarmeFragment = new AlarmeFragment();

    private TextView txvUsuario;
    private TextView txvEmail;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                hideKeyBoard();

            }
        };

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        txvUsuario = (TextView) headerView.findViewById(R.id.txvUsuario);
        txvEmail = (TextView) headerView.findViewById(R.id.txvEmail);

        Bundle bundle = getIntent().getExtras();
        txvEmail.setText(bundle.getString("email"));
        txvUsuario.setText(bundle.getString("nome"));

        if(LoginActivity.mostraConfiguracoes) {
            mostraFragmentoConfiguracoes();
        } else {
            mostraFragmentoIluminacao();
        }
    }

    public void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        hideKeyBoard();
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_configuracoes) {
            mostraFragmentoConfiguracoes();
            return true;
        } else if(id == R.id.action_sobre) {
            mostrarSobre();
        } else if(id == R.id.action_fechar) {
            fecharApp();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_iluminacao) {
            mostraFragmentoIluminacao();
        } else if (id == R.id.nav_portao_piscina) {
            setTitle("Portão");
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFragment, portaoFragment, "portaoFragment");
            fragmentTransaction.commit();
        } else if (id == R.id.nav_temperatura_humidade) {
            setTitle("Ar Condicionado e DHT");
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFragment, thArCondicionadoFragment, "thArCondicionadoFragment");
            fragmentTransaction.commit();
        } else if (id == R.id.nav_alarme) {
            setTitle("Alarme");
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFragment, alarmeFragment, "alarmeFragment");
            fragmentTransaction.commit();
        }else if (id == R.id.nav_configuracoes) {
            mostraFragmentoConfiguracoes();
        }else if (id == R.id.nav_sobre) {
            mostrarSobre();
        }else if (id == R.id.nav_fechar) {
            fecharApp();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void mostraFragmentoIluminacao() {
        setTitle("Iluminação");
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFragment, iluminacaoFragment, "iluminacaoFragment");
        fragmentTransaction.commit();
    }

    private void mostraFragmentoConfiguracoes() {
        setTitle("Configurações");
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFragment, configuracoesFragment, "configuracoesFragment");
        fragmentTransaction.commit();
    }

    private void mostrarSobre() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Sobre");
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View view = inflater.inflate(R.layout.dialog_sobre, null);
        ImageView imvWinberry = (ImageView) view.findViewById(R.id.imvWinberry);
        TextView txvBy = (TextView) view.findViewById(R.id.txvBy);
        dialog.setView(view);
        dialog.setNeutralButton("OK", null);
        dialog.show();
    }

    private void fecharApp() {
        AlertDialog.Builder a_builder = new AlertDialog.Builder(MainActivity.this);
        a_builder.setMessage("Você deseja fechar o Aplicativo ?")
                .setCancelable(false)
                .setPositiveButton("Sim",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("Nao",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }) ;
        AlertDialog alert = a_builder.create();
        alert.setTitle("Fechar o Aplicativo");
        alert.show();
    }
}
