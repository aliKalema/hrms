package com.smartmax.hrms.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
public class LeaveCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private int maxDays;
    private String rotate;
    private String icon;
    public LeaveCategory(int id, String name, int maxDays, String rotate,String icon) {
        this.id = id;
        this.name = name;
        this.maxDays = maxDays;
        this.rotate = rotate;
        this.icon = icon;
    }
    public LeaveCategory(){
        super();
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getMaxDays() {
        return maxDays;
    }
    public void setMaxDays(int maxDays) {
        this.maxDays = maxDays;
    }
    public String getRotate() {
        return rotate;
    }
    public String getIcon(){
        return icon;
    }
    public void setIcon(String icon){
        this.icon =icon;
    }
    public void setRotate(String rotate) {
        this.rotate = rotate;
    }

}