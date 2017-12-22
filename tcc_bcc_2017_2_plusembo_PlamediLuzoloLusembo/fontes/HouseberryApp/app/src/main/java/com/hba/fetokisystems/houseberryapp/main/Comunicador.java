package com.hba.fetokisystems.houseberryapp.main;

import com.hba.fetokisystems.houseberryapp.dao.ConnectionFactory;
import com.hba.fetokisystems.houseberryapp.dao.ServidorConfigDao;
import com.hba.fetokisystems.houseberryapp.model.ServidorConfig;
import android.content.Context;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Plamedi L. Lusembo on 10/5/2017.
 */

public class Comunicador {

    private String ipServidor;
    private int porta;
    private static Comunicador uniqueInstance;

    private String cmdEnviado;
    private String cmdRecebido;

    public Comunicador() {
    }

    public static synchronized Comunicador getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new Comunicador();
        }
        return uniqueInstance;
    }

    private void configuraServidor(Context context) {
        ServidorConfig servidorConfig = ServidorConfigDao.getInstance(ConnectionFactory.getInstance().abreNovaConexao(context))
                .selecionaServidorConfig();

        if (servidorConfig == null) return;

        ipServidor = servidorConfig.getIpServidor();
        porta = servidorConfig.getPorta();

    }

    public String transmite(Context context, String cmd) {
        configuraServidor(context);
        cmdEnviado = cmd;
        cmdRecebido = "";

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    final Socket client = new Socket(ipServidor, porta);
                    System.out.println("O cliente se conectou ao servidor!");
                    PrintStream saida = new PrintStream(client.getOutputStream());

                    saida.println(cmdEnviado);
                    cmdRecebido = "";
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Scanner s = new Scanner(client.getInputStream());
                                while (s.hasNextLine()) {
                                    cmdRecebido = s.nextLine();
                                }
                            } catch (IOException ioe) {
                            }
                        }
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();
                    while (cmdRecebido.isEmpty()) {
                        try {
                            Thread.sleep(0);
                        } catch (InterruptedException ex) {
                        }
                    }

                    saida.close();
                    client.close();

                } catch (IOException ioe) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();

        while (cmdRecebido.isEmpty()) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException ex) {
            }
        }

        return cmdRecebido;

    }

}
