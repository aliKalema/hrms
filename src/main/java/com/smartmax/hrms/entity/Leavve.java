package com.smartmax.hrms.entity;

import com.smartmax.hrms.utils.SystemUtils;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Leavve {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDate startDate;
    private LocalDate endDate;
    @Transient
    Employee employee;
    @Transient
    private int daysUsed;
    @Transient
    private int remainingDays;
    @Transient
    private int year;
    @Transient
    private List<LocalDate>datesOnLeave;
    @Transient
    String categoryName;
    @Transient
    String categoryRotate;
    @Transient
    String responseText;
    public Leavve(int id, LocalDate startDate,String jobName, LocalDate endDate) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    public Leavve(String responseText){
        this.responseText = responseText;
    }
    public Leavve(){
        super();
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    public Employee getEmployee() {
        return employee;
    }
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public int getDaysUsed() {
        return daysUsed;
    }
    public void setDaysUsed(int daysUsed) {
        this.daysUsed = daysUsed;
    }
    public int getRemainingDays() {
        return remainingDays;
    }
    public void setRemainingDays(int remainingDays) {
        this.remainingDays = remainingDays;
    }
    public void setDaysOnLeave(){
        List<LocalDate> daysOnLeave = new ArrayList<>();
        for(LocalDate localDate : this.getStartDate().datesUntil(this.getEndDate()).collect(Collectors.toList())){
            if(!SystemUtils.isSunday(localDate)){
                daysOnLeave.add(localDate);
            }
        }
        this.daysUsed = daysOnLeave.size();
    }
    public String getResponseText(){
        return responseText;
    }
    public void setResponseText(String responseText){
        this.responseText =  responseText;
    }
    public int getYear(){return year;}
    public void setYear(int year){this.year = year;}
    public String getCategoryRotate(){
        return categoryRotate;
    }
    public void setCategoryRotate(String categoryRotate){
        this.categoryRotate = categoryRotate;
    }
    public List<LocalDate>getDatesOnLeave(){
        return datesOnLeave;
    }
    public void setDatesOnLeave(List<LocalDate>datesOnLeave){
        this.datesOnLeave = datesOnLeave;
    }
    public void generateDatesOnLeave(){
        this.datesOnLeave =  new ArrayList<>();
        List<LocalDate> leaveDays = this.getStartDate().datesUntil(this.getEndDate()).collect(Collectors.toList());
        leaveDays.add(this.getEndDate());//datesUntil does not count the end Date
        leaveDays.stream().filter(ld->{
                                boolean b = !SystemUtils.isSunday(ld);
                                return b;
                            }).collect(Collectors.toList());
        this.datesOnLeave.addAll(leaveDays);
    }
    @Override
    public String toString() {
        return "Leavve{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", employee=" + employee +
                ", daysUsed=" + daysUsed +
                ", remainingDays=" + remainingDays +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }

}
