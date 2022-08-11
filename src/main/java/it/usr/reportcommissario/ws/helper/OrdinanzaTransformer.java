/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.usr.reportcommissario.ws.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author riccardo.iovenitti
 */
public class OrdinanzaTransformer implements Transformer {
    private final static Pattern ORDINANZA_PATTERN = Pattern.compile("[0-9]+");
    
    @Override
    public <T> T transform(Class<T> type, Object value) {
        String s = String.valueOf(value);        
        Matcher m = ORDINANZA_PATTERN.matcher(s);
        if(m.find()) {
            return (T)s.substring(m.start(), m.end());
        }
        else {
            throw new IllegalArgumentException("Cannot find matches for ["+value+"] to ["+type+"].");
        }        
    }        
}
