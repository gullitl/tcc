package com.hba.fetokisystems.houseberryapp.model;

/**
 * Created by Plamedi L. Lusembo on 10/2/2017.
 */

public class ServidorConfig {

    private int cdServidorConfic;
    private String ipServidor;
    private int porta;

    public ServidorConfig() {}

    public ServidorConfig(String ipServidor, int porta) {
        this.ipServidor = ipServidor;
        this.porta = porta;
    }

    public int getCdServidorConfic() {
        return cdServidorConfic;
    }

    public void setCdServidorConfic(int cdServidorConfic) {
        this.cdServidorConfic = cdServidorConfic;
    }

    public String getIpServidor() {
        return ipServidor;
    }

    public void setIpServidor(String ipServidor) {
        this.ipServidor = ipServidor;
    }

    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }
}
