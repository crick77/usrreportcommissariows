/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.usr.reportcommissario.ws.helper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author riccardo.iovenitti
 * @param <T>
 */
public class ReflectiveSetterHelper<T> {
    private final Class<T> clazz;
    private final Map<String, Method> setters;
    private final Map<String, Field> fields;
    
    public ReflectiveSetterHelper(T o) {
        this((Class<T>)o.getClass());
    }
    
    public ReflectiveSetterHelper(Class<T> c) {
        clazz = c;
        setters = new HashMap<>();
        fields = new HashMap<>();
        
        // Estrae tutti i setter
        Method[] ms = clazz.getMethods();
        for(Method m : ms) {
            String mName = m.getName().toLowerCase();
            if(mName.startsWith("set")) {
                setters.put(mName, m);
            }
        }
        
        Field[] fs = clazz.getDeclaredFields();
        for(Field f : fs) {
            String fName = f.getName().toLowerCase();
            fields.put(fName, f);
        }
    }
    
    public void setValue(T instance, String fieldName, String type, Object value) {
        if(value==null) return;
        
        fieldName = fieldName.toLowerCase();
        String sName = "set"+fieldName;
        Method m = setters.get(sName);
        Field f = fields.get(fieldName);
        
        if(m==null) throw new IllegalArgumentException("No setter for ["+fieldName+"]");
                
        try {
            Class cType;
            Transform t = f.getAnnotation(Transform.class);
            if(t!=null) {
                Transformer tx = t.transformer().newInstance();
            
                value = tx.transform(f.getType(), value);
                cType = f.getType();
            }
            else {
                cType = Class.forName(type);
            }
            
            if(cType.isAssignableFrom(value.getClass()) || cType.getClass().equals(value.getClass())) {
                m.invoke(instance, cType.cast(value));
            }
            else {
                Constructor c = cType.getConstructor(value.getClass());
                Object o = c.newInstance(value);
                m.invoke(instance, o);
            }
        } 
        catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            throw new RuntimeException("Cannot set value for setter ["+fieldName+"] of type ["+type+"] because of "+ex, ex);
        }        
    }
}
