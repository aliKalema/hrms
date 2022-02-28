package com.smartmax.hrms.entity;

import com.smartmax.hrms.utils.SystemUtils;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class LeavePeriod implements Comparable<LeavePeriod>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDate period;
    @OneToOne(cascade= CascadeType.ALL,fetch= FetchType.EAGER)
    private LeaveCategory leaveCategory;
    @OneToOne(cascade= CascadeType.ALL,fetch= FetchType.EAGER)
    private Employee employee;
    @OneToMany(cascade= CascadeType.ALL,fetch= FetchType.EAGER)
    Set<Leavve>leaves;
    public LeavePeriod(int id, LocalDate period, LeaveCategory leaveCategory, Employee employee, Set<Leavve> leaves) {
        this.id = id;
        this.period = period;
        this.leaveCategory = leaveCategory;
        this.employee = employee;
        this.leaves = leaves;
    }
    public LeavePeriod(){
        super();
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public LocalDate getPeriod() {
        return period;
    }
    public void setPeriod(LocalDate period) {
        this.period = period;
    }
    public com.smartmax.hrms.entity.LeaveCategory getLeaveCategory() {
        return leaveCategory;
    }
    public void setLeaveCategory(LeaveCategory leaveCategory) {
        this.leaveCategory = leaveCategory;
    }
    public Employee getEmployee() {
        return employee;
    }
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    public Set<Leavve> getLeaves() {
        return leaves;
    }
    public void setLeaves(Set<Leavve> leaves) {
        this.leaves = leaves;
    }
    public void addLeave(Leavve leave){
        if(this.leaves == null){
           this.setLeaves(new HashSet<>());
        }
        this.leaves.add(leave);
    }
    public boolean usedAllDays(){
        boolean usedDays= true;
        int numberOfDays = this.getUsedDays();
        if(numberOfDays<leaveCategory.getMaxDays()) {
            usedDays = false;
        }
        return usedDays;
    }

    public int getUsedDays(){
        List<LocalDate> dates = new ArrayList<>();
        for (Leavve leave : leaves) {
           leave.generateDatesOnLeave();
           dates.addAll(leave.getDatesOnLeave());
        }
        return dates.size();
    }

    @Override
    public int compareTo(LeavePeriod o) {
        return 0;
    }
}
