/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.usr.reportcommissario.ws.helper;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

/**
 *
 * @author riccardo.iovenitti
 */
public class SiNoBooleanSerializer implements JsonbSerializer<Boolean> {
    @Override
    public void serialize(Boolean t, JsonGenerator jg, SerializationContext sc) {
        t = (t!=null) ? t : false;
        jg.write(t ? "SI" : "NO");
    }        
}
