/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.usr.reportcommissario.ws.model;

import java.sql.Date;

/**
 *
 * @author riccardo.iovenitti
 */
public class Decreto {
    private int idPratica;
    private int idTipoDecreto;
    private int idOrdinanzaRiferimento;
    private Date dataOraProvvedimento;
    private int numeroProvvedimento;
    private boolean ord100;
    private String cup;

    public int getIdPratica() {
        return idPratica;
    }

    public void setIdPratica(int idPratica) {
        this.idPratica = idPratica;
    }

    public int getIdTipoDecreto() {
        return idTipoDecreto;
    }

    public void setIdTipoDecreto(int idTipoDecreto) {
        this.idTipoDecreto = idTipoDecreto;
    }

    public int getIdOrdinanzaRiferimento() {
        return idOrdinanzaRiferimento;
    }

    public void setIdOrdinanzaRiferimento(int idOrdinanzaRiferimento) {
        this.idOrdinanzaRiferimento = idOrdinanzaRiferimento;
    }

    public Date getDataOraProvvedimento() {
        return dataOraProvvedimento;
    }

    public void setDataOraProvvedimento(Date dataOraProvvedimento) {
        this.dataOraProvvedimento = dataOraProvvedimento;
    }

    public int getNumeroProvvedimento() {
        return numeroProvvedimento;
    }

    public void setNumeroProvvedimento(int numeroProvvedimento) {
        this.numeroProvvedimento = numeroProvvedimento;
    }

    public boolean isOrd100() {
        return ord100;
    }

    public void setOrd100(boolean ord100) {
        this.ord100 = ord100;
    }        

    public String getCup() {
        return cup;
    }

    public void setCup(String cup) {
        this.cup = cup;
    }        
}
