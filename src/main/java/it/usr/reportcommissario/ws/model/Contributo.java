/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.usr.reportcommissario.ws.model;

import java.math.BigDecimal;

/**
 *
 * @author riccardo.iovenitti
 */
public class Contributo {
    private int idPratica;
    private BigDecimal contributoConcesso;
    private BigDecimal contributoLiquidato;

    public int getIdPratica() {
        return idPratica;
    }

    public void setIdPratica(int idPratica) {
        this.idPratica = idPratica;
    }

    public BigDecimal getContributoConcesso() {
        return contributoConcesso;
    }

    public void setContributoConcesso(BigDecimal contributoConcesso) {
        this.contributoConcesso = contributoConcesso;
    }

    public BigDecimal getContributoLiquidato() {
        return contributoLiquidato;
    }

    public void setContributoLiquidato(BigDecimal contributoLiquidato) {
        this.contributoLiquidato = contributoLiquidato;
    } 
}
