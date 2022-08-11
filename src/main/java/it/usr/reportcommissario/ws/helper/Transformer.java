/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.usr.reportcommissario.ws.helper;

/**
 *
 * @author riccardo.iovenitti
 */
public interface Transformer {
    public <T> T transform(Class<T> type, Object value);
}
