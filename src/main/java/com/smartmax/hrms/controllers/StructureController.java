package com.smartmax.hrms.controllers;

import com.smartmax.hrms.entity.*;
import com.smartmax.hrms.repository.*;
import com.smartmax.hrms.service.EmployeeService;
import com.smartmax.hrms.utils.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
public class StructureController {
    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    CenterRepository centerRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    EmployeeService employeeService;

    @GetMapping("/api/admin/department/delete/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable("id")String id){
        departmentRepository.deleteById(Integer.parseInt(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/api/admin/department/edit/{id}")
    public ResponseEntity<Department> editDepartment(@PathVariable("id")String id,
                                                    @RequestParam("name")String name){
        Department department = departmentRepository.findById(Integer.parseInt(id)).get();
        department.setName(name);
        departmentRepository.save(department);
        return new ResponseEntity<>(department,HttpStatus.OK);
    }

    @PostMapping("/api/admin/team")
    public ResponseEntity<Team>addTeam(@RequestParam("managerId")String managerId,
                                       @RequestParam("department")String departmentName,
                                       @RequestParam("role")String role){
        Team team =  new Team();
        Employee manager =employeeRepository.findById(Integer.parseInt(managerId)).get();
        employeeService.setStructure(manager);
        Department department = departmentRepository.findByName(departmentName).get();
        team.setDepartment(department);
        team.setManagerId(manager.getId());
        team.setRole(role);
        team.setManager(manager);
        teamRepository.save(team);
        return new ResponseEntity<>(team,HttpStatus.OK);
    }

    @GetMapping("/api/admin/department/hod/{departmentId}/{employeeId}")
    public ResponseEntity<Department> addHodDepartment(@PathVariable("departmentId")String departmentId,@PathVariable("employeeId")String employeeId){
        Department department = departmentRepository.findById(Integer.parseInt(departmentId)).get();
        int emplId = Integer.parseInt(employeeId);
        Employee employee = employeeRepository.findById(emplId).get();
        employeeService.setStructure(employee);
        if(department.getEmployees().stream().noneMatch(empl -> empl.getId()==emplId)){
            department.addEmployee(employee);
        }
        department.setHodId(emplId);
        departmentRepository.save(department);
        department.setHodImage(employee.getImage().getName());
        department.setHodName(employee.getName());
        return new ResponseEntity<>(department,HttpStatus.OK);
    }

    @GetMapping("/api/admin/department/employees/remove/{depId}/{emplId}")
    public ResponseEntity<?> deleteEmployeeDepartment(@PathVariable("depId")String depId,@PathVariable("emplId")String emplId){
        Department department = departmentRepository.findById(Integer.parseInt(depId)).get();
        Employee employee =  employeeRepository.findById(Integer.parseInt(emplId)).get();
        List<Employee> employees = department.getEmployees();
        employees.removeIf(empl->empl.getId() == employee.getId());
        if(department.getHodId() == employee.getId()){
            department.setHodId(0);
            department.setHodImage("default-avatar.png");
            department.setHodName("");
        }
        departmentRepository.save(department);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/admin/location/delete/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable("id")String id){
        centerRepository.deleteById(Integer.parseInt(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/api/admin/location/edit/{id}")
    public ResponseEntity<Center> editLocation(@PathVariable("id")String id,
                                               @RequestParam("name")String name){
        Center center = centerRepository.findById(Integer.parseInt(id)).get();
        center.setName(name);
        centerRepository.save(center);
        return new ResponseEntity<>(center,HttpStatus.OK);
    }

    @GetMapping("/api/admin/location/employees/remove/{depId}/{emplId}")
    public ResponseEntity<Center> deleteEmployeeLocation(@PathVariable("depId")String depId,@PathVariable("emplId")String emplId){
        Center center = centerRepository.findById(Integer.parseInt(depId)).get();
        Employee employee =  employeeRepository.findById(Integer.parseInt(emplId)).get();
        List<Employee> employees = center.getEmployees();
        employees.removeIf(empl->empl.getId() == employee.getId());
        centerRepository.save(center);
        return new ResponseEntity<>(center,HttpStatus.OK);
    }

    @PostMapping("/api/admin/section/edit/{id}")
    public ResponseEntity<Section> editSection(@PathVariable("id")String id,
                                               @RequestParam("name")String name){
        Section section = sectionRepository.findById(Integer.parseInt(id)).get();
        section.setName(name);
        sectionRepository.save(section);
        return new ResponseEntity<>(section,HttpStatus.OK);
    }

    @GetMapping("/api/admin/section/employees/remove/{depId}/{emplId}")
    public ResponseEntity<Section> deleteEmployeeSection(@PathVariable("depId")String depId,@PathVariable("emplId")String emplId){
        Section section = sectionRepository.findById(Integer.parseInt(depId)).get();
        Employee employee =  employeeRepository.findById(Integer.parseInt(emplId)).get();
        List<Employee> employees = section.getEmployees();
        employees.removeIf(empl->empl.getId() == employee.getId());
        sectionRepository.save(section);
        return new ResponseEntity<>(section,HttpStatus.OK);
    }

    @GetMapping("/api/admin/section/delete/{id}")
    public ResponseEntity<?> deleteSection(@PathVariable("id")String id){
        centerRepository.deleteById(Integer.parseInt(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/admin/teams/edit/manager/{teamId}/{employeeId}")
    public ResponseEntity<Employee>editTeamManger(@PathVariable("teamId")String teamId,@PathVariable("employeeId")String employeeId){
        Optional<Team>team =  teamRepository.findById(Integer.parseInt(teamId));
        Optional<Employee>employee =  employeeRepository.findById(Integer.parseInt(employeeId));
        if(team.isEmpty() || employee.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        team.get().setManagerId(employee.get().getId());
        employeeService.setStructure(employee.get());
        teamRepository.save(team.get());
        return new ResponseEntity<>(employee.get(),HttpStatus.OK);
    }

    @GetMapping("/api/admin/teams/{teamId}/{employeeId}")
    public ResponseEntity<Employee>addEmployeeToTeam(@PathVariable("teamId")String teamId, @PathVariable("employeeId")String employeeId){
        Optional <Team> team =  teamRepository.findById(Integer.parseInt(teamId));
        Optional <Employee> employee =  employeeRepository.findById(Integer.parseInt(employeeId));
        if(team.isEmpty() || employee.isEmpty()){
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        }
        team.get().addEmployee(employee.get());
        teamRepository.save(team.get());
        employeeService.setStructure(employee.get());
        return new ResponseEntity<>(employee.get(),HttpStatus.OK);
    }



}
