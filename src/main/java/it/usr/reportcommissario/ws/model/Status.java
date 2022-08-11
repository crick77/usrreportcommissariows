/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.usr.reportcommissario.ws.model;

import java.sql.Timestamp;
import java.util.Date;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbNillable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author riccardo.iovenitti
 */
@XmlRootElement
@JsonbNillable
public class Status {
    private int current;
    private int total;
    @JsonbDateFormat("dd/MM/yyyy HH:mm:ss")
    private Timestamp dt;
    private boolean completed;
    private String error;

    public Status() {
    }

    public Status(int current, int total) {
        this.current = current;
        this.total = total;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }        

    public Date getDt() {
        return dt;
    }

    public void setDt(Timestamp dt) {
        this.dt = dt;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }        
}
