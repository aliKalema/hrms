package com.smartmax.hrms.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class General {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private  int id;
    private String employerName;
    private String employerCode;
    public General(String employerName,String employerCode){
        this.employerCode = employerCode;
        this.employerName = employerName;
    }
    public General(){
        super();
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getEmployerName() {
        return employerName;
    }
    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }
    public String getEmployerCode() {
        return employerCode;
    }
    public void setEmployerCode(String employerCode) {
        this.employerCode = employerCode;
    }
}
