package com.smartmax.hrms.service;

import com.smartmax.hrms.entity.*;
import com.smartmax.hrms.repository.EmployeeRepository;
import com.smartmax.hrms.repository.LeaveCategoryRepository;
import com.smartmax.hrms.repository.LeavePeriodRepository;
import com.smartmax.hrms.repository.LeaveRepository;
import com.smartmax.hrms.utils.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class LeaveService {
    @Autowired
    LeaveRepository leaveRepository;

    @Autowired
    LeavePeriodRepository leavePeriodRepository;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    LeaveCategoryRepository leaveCategoryRepository;

    public List<Leavve> getPastLeaves(){
        LocalDate today = LocalDate.now();
        List<Leavve>leaves= new ArrayList<>();
        leaveRepository.findAll().forEach(leaves::add);
        return leaves.stream().filter(leave ->leave.getStartDate().isBefore(LocalDate.now())).collect(Collectors.toList());
    }

    public List<Leavve> getupCommingLeaves(){
        LocalDate today = LocalDate.now();
        List<Leavve>leaves= new ArrayList<>();
        leaveRepository.findAll().forEach(leaves::add);
        List<Leavve>bookedLeaves =  leaves.stream().filter(leave ->leave.getStartDate().isAfter(LocalDate.now())).collect(Collectors.toList());
        bookedLeaves.forEach(lv->{
            LeavePeriod lp =   leavePeriodRepository.findByLeaveId(lv.getId());
            lv.setEmployee(lp.getEmployee());
            lv.setCategoryName(lp.getLeaveCategory().getName());
            employeeService.setStructure(lv.getEmployee());
        });
        return bookedLeaves;
    }

    public List<Leavve> getonLeave(){
        LocalDate today = LocalDate.now();
        List<Leavve>leaves= new ArrayList<>();
        leaveRepository.findAll().forEach(leaves::add);
        return leaves.stream().filter(leave -> LocalDate.now().isBefore(leave.getEndDate()) && LocalDate.now().isAfter(leave.getStartDate())).collect(Collectors.toList());
    }

    public List<LeavePeriod> getEmployeesLeavePeriod(int employeeId){
        return leavePeriodRepository.findByEmployeeId(employeeId);
    }

    public List<LocalDate>getDatesOnLeave(LocalDate start,LocalDate end){
            List<LocalDate>datesOnLeave =  new ArrayList<>();
            List<LocalDate> leaveDays = start.datesUntil(end).collect(Collectors.toList());
            leaveDays.add(end);//datesUntil does not count the end Date
            leaveDays.stream().filter(ld->{
                boolean b = !SystemUtils.isSunday(ld);
                return b;
            }).collect(Collectors.toList());
            datesOnLeave.addAll(leaveDays);
            return datesOnLeave;
    }

    public ResponseEntity<Leavve> addLeave(int employeeId, LocalDate  startDate, LocalDate endDate, String categoryName){
        Leavve postedLeave = new Leavve();
        Optional<Employee> employeeOptional =  employeeRepository.findById(employeeId);
        if(employeeOptional.isEmpty()){return new ResponseEntity<>(new Leavve("Employee Does not Exist"), HttpStatus.CONFLICT);}
        Employee employee =  employeeOptional.get();
        employeeService.setStructure(employee);
        Optional<LeaveCategory> mainCategoryOption = leaveCategoryRepository.findByName(categoryName);
        if(mainCategoryOption.isEmpty()){return new ResponseEntity<>(new Leavve("CategoryName Does Not Exist"), HttpStatus.CONFLICT);}
        LeaveCategory mainCategory = mainCategoryOption.get();
        List<LocalDate> daysOnLeave = getDatesOnLeave(startDate,endDate);
        //get All employee LevePeriod with the same CategoryName
        if(daysOnLeave.size()>mainCategory.getMaxDays()){
            return new ResponseEntity<>(new Leavve(mainCategory.getName() +" has Max days of "+ mainCategory.getMaxDays() +" days.You attempted to book for "+String.valueOf(daysOnLeave.size()) +" days ...Please Reduce" ), HttpStatus.CONFLICT);
        }
        List<LeavePeriod>takenPeriods= leavePeriodRepository.findByEmployeeIdAndLeaveCategoryName(employee.getId(),mainCategory.getId());
        //employee has never taken any leave
        if(takenPeriods.size()<=0 ){
                Leavve firstEverLeave = createLeave(startDate, endDate, categoryName);
                firstEverLeave.setDaysUsed(daysOnLeave.size());
                firstEverLeave.setRemainingDays(mainCategory.getMaxDays() - daysOnLeave.size());
                LeavePeriod leavePeriod = new LeavePeriod();
                leavePeriod.setEmployee(employee);
                leavePeriod.setLeaveCategory(mainCategory);
                leavePeriod.setPeriod(startDate);
                leavePeriod.addLeave(firstEverLeave);
                leavePeriodRepository.save(leavePeriod);
                return new ResponseEntity<>(firstEverLeave, HttpStatus.OK);

        }
        //Sort LeavePeriods From Earliest to Latest using Insertion Sort
        Collections.sort(takenPeriods, (o1, o2) -> o2.getPeriod().compareTo(o1.getPeriod()));
        LeavePeriod latestPeriod = takenPeriods.get(0);
        LocalDate latestPeriodExpiringDate =  latestPeriod.getPeriod().plusDays(SystemUtils.getRotationDays(mainCategory.getRotate()));
        if(latestPeriod.getPeriod().equals(startDate) || latestPeriodExpiringDate.equals(startDate) || (latestPeriod.getPeriod().isAfter(startDate)&& latestPeriodExpiringDate.isBefore(startDate))) {
            //check if startDate lies between  latest LeavePeriod
            if (latestPeriod.usedAllDays()) {
                //used up this Period Leave all in a period does not Qualify a leave Until next rotation
                return new ResponseEntity<>(new Leavve("employee has Used up all leaves in this period"), HttpStatus.CONFLICT);
            }
            else {
                int latestPeriodDaysLeft = latestPeriod.getLeaveCategory().getMaxDays() - latestPeriod.getUsedDays();
                if ((latestPeriodDaysLeft >= daysOnLeave.size())) {
                    // check if leave days within limit
                    int daysUsed = 0;
                    Leavve continuosWithLimit = createLeave(startDate, endDate, categoryName);
                    continuosWithLimit.setDaysUsed(daysOnLeave.size());
                    continuosWithLimit.setRemainingDays(mainCategory.getMaxDays()-daysOnLeave.size());
                    continuosWithLimit.setEmployee(employee);
                    continuosWithLimit.setRemainingDays(latestPeriodDaysLeft);
                    latestPeriod.addLeave(continuosWithLimit);
                    leavePeriodRepository.save(latestPeriod);
                    return new ResponseEntity<>(continuosWithLimit, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(new Leavve("The employee has only " + String.valueOf(latestPeriodDaysLeft) + " left in this Period"), HttpStatus.CONFLICT);
                }
            }
        }
        else{
            LeavePeriod leavePeriod =  new LeavePeriod();
            Leavve continuousLeave = createLeave(startDate,endDate,categoryName);
            continuousLeave.setDaysUsed(daysOnLeave.size());
            continuousLeave.setRemainingDays(mainCategory.getMaxDays()-daysOnLeave.size());
            continuousLeave.setEmployee(employee);
            leavePeriod.setEmployee(employee);
            leavePeriod.setLeaveCategory(mainCategory);
            leavePeriod.setPeriod(continuousLeave.getStartDate());
            leavePeriod.addLeave(continuousLeave);
            leavePeriodRepository.save(leavePeriod);
            return new ResponseEntity<>(continuousLeave,HttpStatus.OK);
        }
    }

    public List<Leavve>getOnLeave(){
        LocalDate today = LocalDate.now();
        List<Leavve> leaves = leaveRepository.findOnLeave(today);
        if(leaves.size()>0){
            for(Leavve lv : leaves){
                LeavePeriod leavePeriod = leavePeriodRepository.findByLeaveId(lv.getId());
                Employee employee = leavePeriod.getEmployee();
                employeeService.setStructure(employee);
                lv.setEmployee(employee);
                lv.setCategoryName(leavePeriod.getLeaveCategory().getName());
                lv.setDaysOnLeave();
            }
        }
        return leaves;
    }

    public Leavve createLeave(LocalDate  startDate, LocalDate endDate, String categoryName){
        Leavve leave = new Leavve();
        leave.setStartDate(startDate);
        leave.setEndDate(endDate);
        leave.setCategoryName(categoryName);
        return leave;
    }

    public List<ViableLeaveCategory>getEmployeeViableLeaves(int employeeId){
        List<ViableLeaveCategory>viableLeaveCategories =  new ArrayList<>();
        List<LeaveCategory>leaveCategories =  new ArrayList<>();
        leaveCategoryRepository.findAll().forEach(leaveCategories :: add);
        System.out.println(leaveCategories.size());
        for(LeaveCategory category: leaveCategories){
            ViableLeaveCategory vlc = null;
            List<LeavePeriod>takenPeriods =  leavePeriodRepository.findByEmployeeIdAndLeaveCategoryName(employeeId,category.getId());
            if(takenPeriods.size()>0){
                LocalDate today =  LocalDate.now();
                Collections.sort(takenPeriods, (o1, o2) -> o2.getPeriod().compareTo(o1.getPeriod()));
                LeavePeriod latestPeriod = takenPeriods.get(0);
                LocalDate latestPeriodExpiringDate =  latestPeriod.getPeriod().plusDays(SystemUtils.getRotationDays(category.getRotate()));
                if(latestPeriod.getPeriod().equals(today) || latestPeriodExpiringDate.equals(today) || (latestPeriod.getPeriod().isAfter(today)&& latestPeriodExpiringDate.isBefore(today))) {
                    //check if startDate lies between  latest LeavePeriod
                    if (latestPeriod.usedAllDays()) {
                        //used up this Period Leave all in a period does not Qualify a leave Until next rotation
                        vlc= new ViableLeaveCategory(category.getName(),category.getName().substring(0,2).toUpperCase(),category.getMaxDays(),category.getRotate());
                        viableLeaveCategories.add(vlc);
                    }
                    else {
                        int latestPeriodDaysLeft = latestPeriod.getLeaveCategory().getMaxDays() - latestPeriod.getUsedDays();
                        List<Leavve>leaves = new ArrayList<>(latestPeriod.getLeaves());
                        int daysUsed = 0;
                        for(int i= 0;i<leaves.size();i++){
                            leaves.get(i).generateDatesOnLeave();
                            daysUsed = daysUsed + leaves.get(i).getDatesOnLeave().size();
                        }
                        vlc = new ViableLeaveCategory(category.getName(),category.getName().substring(0,2).toUpperCase(),latestPeriodDaysLeft,category.getRotate());
                        viableLeaveCategories.add(vlc);
                    }
                }
            }
            else{//not taken any leave
                vlc = new ViableLeaveCategory(category.getName(),category.getName().substring(0,2).toUpperCase(),category.getMaxDays(),category.getRotate());
                viableLeaveCategories.add(vlc);
            }
            if(vlc!=null){
                System.out.println(vlc.toString());
            }
        }
        return viableLeaveCategories;
    }
}
