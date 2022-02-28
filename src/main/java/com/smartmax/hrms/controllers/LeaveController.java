package com.smartmax.hrms.controllers;

import com.smartmax.hrms.entity.Employee;
import com.smartmax.hrms.entity.LeaveCategory;
import com.smartmax.hrms.entity.LeavePeriod;
import com.smartmax.hrms.entity.Leavve;
import com.smartmax.hrms.repository.EmployeeRepository;
import com.smartmax.hrms.repository.LeaveCategoryRepository;
import com.smartmax.hrms.repository.LeavePeriodRepository;
import com.smartmax.hrms.repository.LeaveRepository;
import com.smartmax.hrms.service.EmployeeService;
import com.smartmax.hrms.service.LeaveService;
import com.smartmax.hrms.utils.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class LeaveController {
    Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(Exception e) {
        logger.warn("Returning HTTP 400 Bad Request", e);
    }
    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    LeaveCategoryRepository leaveCategoryRepository;

    @Autowired
    LeavePeriodRepository leavePeriodRepository;

    @Autowired
    LeaveRepository leaveRepository;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    LeaveService leaveService;

    @PostMapping("/api/admin/leave/category")
    public ResponseEntity<LeaveCategory> addLeaveCategory(@RequestBody LeaveCategory leaveCategory) {
        Optional<LeaveCategory> category = leaveCategoryRepository.findByName(leaveCategory.getName());
        if (category.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.ALREADY_REPORTED);
        }
        leaveCategoryRepository.save(leaveCategory);
        return new ResponseEntity<>(leaveCategory, HttpStatus.OK);
    }

    @GetMapping("/api/admin/leave/category")
    public ResponseEntity<List<LeaveCategory>> getLeaveCategories() {
        List<LeaveCategory> leaveCategories = new ArrayList<>();
        leaveCategoryRepository.findAll().forEach(leaveCategories::add);
        return new ResponseEntity<>(leaveCategories, HttpStatus.OK);
    }

    @GetMapping("/api/admin/leave/category/delete/{id}")
    public ResponseEntity<?> deleteLeaveCategory(@PathVariable("id") String id) {
        leaveCategoryRepository.deleteById(Integer.parseInt(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/api/admin/leave/edit/{id}")
    public ResponseEntity<Leavve>editLeave(@PathVariable("id")String leaveId,
                                          @RequestParam("startDate")String startDate,
                                          @RequestParam("endDate")String endDate) {
        Leavve leave= leaveRepository.findById(Integer.parseInt(leaveId)).get();
        Employee employee = employeeRepository.findByLeaveId(leave.getId()).get();
        leave.setEmployee(employee);
        leave.setStartDate(SystemUtils.convertDateFormat(startDate,"dd-MM-yyyy"));
        leave.setEndDate(SystemUtils.convertDateFormat(endDate,"dd-MM-yyyy"));
        return new ResponseEntity<>(leave,HttpStatus.OK);
    }

    @PostMapping("/api/admin/leave/{id}")
    public ResponseEntity<Leavve>addLeave(@PathVariable("id")String employeeId,
                                          @RequestParam("startDate")String startDate,
                                          @RequestParam("endDate")String endDate,
                                          @RequestParam("categoryName")String categoryName){
        return leaveService.addLeave(Integer.parseInt(employeeId),SystemUtils.convertDateFormat(startDate,"dd-MM-yyyy"),SystemUtils.convertDateFormat(endDate,"dd-MM-yyyy"),categoryName);
    }

    @PostMapping("/api/admin/leave/category/edit/{categoryId}")
    public ResponseEntity<LeaveCategory> addLeaveCategory(@RequestBody LeaveCategory newCategory,@PathVariable("categoryId")String categoryId) {
        Optional<LeaveCategory> category = leaveCategoryRepository.findById(Integer.parseInt(categoryId));
        if(category.isEmpty()){
            return new ResponseEntity(null,HttpStatus.NOT_FOUND);
        }
        newCategory.setId(category.get().getId());
        leaveCategoryRepository.save(newCategory);
        return new ResponseEntity<>(newCategory, HttpStatus.OK);
    }
}
