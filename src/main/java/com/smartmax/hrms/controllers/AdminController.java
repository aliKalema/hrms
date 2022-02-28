package com.smartmax.hrms.controllers;

import java.util.*;
import java.util.stream.Collectors;

import com.smartmax.hrms.entity.*;
import com.smartmax.hrms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.smartmax.hrms.utils.SystemUtils;

@RestController
public class AdminController {
	@Autowired
	EmployeeRepository employeeRepository;
	
	@Autowired
	DepartmentRepository departmentRepository;
	
	@Autowired 
	CenterRepository centerRepository;
	
	@Autowired
	GradeRepository gradeRepository;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	JobTitleRepository jobTitleRepository;
	
	@Autowired
	TeamRepository teamRepository;
	
	@Autowired
	SectionRepository sectionRepository;
	
	@Autowired
	SessionRegistry sessionRegistry;
	
	@Autowired
	PayrollRepository payrollRepository;
	
	@Autowired
	TaxRepository taxRepository;

	@Autowired
	PayTemplateRepository payTemplateRepository;

	@Autowired
	LeaveCategoryRepository leaveCategoryRepository;

	
	@GetMapping("/api/admin/departments")
	public ResponseEntity<List<Department>>getDepartments(){
		List<Department>departments = new ArrayList<>();
		departmentRepository.findAll().forEach(departments::add);
		for(int i =0;i<departments.size();i++){
			Optional<Employee> employee= employeeRepository.findById(departments.get(i).getHodId());
			if(employee.isPresent()) {
				SystemUtils.getName(employee.get());
			}

		}
		return new ResponseEntity<>(departments,HttpStatus.OK);
	}
	
	@PostMapping("/api/admin/centers")
	public ResponseEntity<Center> addCenter(@RequestParam("location-name")String name){
		Center center = new Center();
		center.setName(name.toUpperCase());
		centerRepository.save(center);
		return new ResponseEntity<>(center,HttpStatus.OK);
	}
	
	@PostMapping("/api/admin/sections")
	public ResponseEntity<Section> addSection(@RequestParam("section-name")String  name){
		Section section  = new Section();
		section.setName(name.toUpperCase());
		sectionRepository.save(section);
		return new ResponseEntity<>(section,HttpStatus.OK);
	}
	
	@PostMapping("/api/admin/departments")
	public ResponseEntity<Department> addDepartment(@RequestParam("department-name")String name){
		Department department  = new Department();
		department.setName(name.toUpperCase());
		departmentRepository.save(department);
		return new ResponseEntity<>(department,HttpStatus.OK);
	}
	
	@PostMapping("/api/admin/jobtitles")
	public ResponseEntity<JobTitle> addJobTitle(@RequestParam("job-title-name") String name){
		JobTitle jobTitle = new JobTitle();
		jobTitle.setName(name.toUpperCase());
		jobTitleRepository.save(jobTitle);
		return new ResponseEntity<>(jobTitle,HttpStatus.OK);
	}
	
	@GetMapping("/api/admin/centers")
	public ResponseEntity<List<Center>>getCenters(){
		List<Center>centers = new ArrayList<>();
		try {
			centerRepository.findAll().forEach(centers::add);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(centers,HttpStatus.OK);
	}
	
	@GetMapping("/api/admin/sections")
	public  ResponseEntity<List<Section>>getSections(){
		List<Section>sections = new ArrayList<>();
		try {
			sectionRepository.findAll().forEach(sections::add);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(sections,HttpStatus.OK);
	}
	
	@GetMapping("/api/admin/teams")
	public ResponseEntity<List<Team>>getTeam(){
		List<Team>teams = new ArrayList<>();
		teamRepository.findAll().forEach(teams::add);
		for(int i =0;i<teams.size();i++) {
			if(teams.get(i).getManagerId()>0) {
				Employee employee= employeeRepository.findById(teams.get(i).getManagerId()).get();
				SystemUtils.getName(employee);
				teams.get(i).setManager(employee);

			}
			else {
				teams.get(i).setManager(null);
			}
		}
		return new ResponseEntity<>(teams,HttpStatus.OK);
	}
	
	@GetMapping("/api/admin/jobtitles")
	public ResponseEntity<List<JobTitle>>getJobTitles(){
		List<JobTitle>titles = new ArrayList<>();
		try {
			jobTitleRepository.findAll().forEach(titles::add);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(titles,HttpStatus.OK);
	}
	
	@PostMapping("/api/admin/grades")
	public  ResponseEntity<Grade>addGrade(@RequestParam("grade-name")String name){
		Grade grade = new Grade();
		grade.setName(name.toUpperCase());
		try {gradeRepository.save(grade);}
			catch(Exception e) {e.printStackTrace();}
		return new ResponseEntity<>(grade, HttpStatus.OK);
	}
	
	@GetMapping("/api/admin/grades")
	public ResponseEntity<List<Grade>>getGrades(){
		List<Grade>grades = new ArrayList<>();
		try {gradeRepository.findAll().forEach(grades::add);}
			catch(Exception e) {e.printStackTrace();}
		return new ResponseEntity<>(grades,HttpStatus.OK);
	}
	@GetMapping("/api/admin/reliefs")
	public Set<Relief> getReliefs(){
		List<Tax> taxes = new ArrayList<>();
		taxRepository.findAll().forEach(taxes::add);
		return taxes.get(0).getReliefs();
	}

	@PostMapping("/api/admin/categories")
	public ResponseEntity<Category> addCategory(@RequestParam("category-name") String name){
		Category category = new Category();
		category.setName(name.toUpperCase());
		categoryRepository.save(category);
		return new ResponseEntity<>(category,HttpStatus.OK);
	}
	
	@GetMapping("/api/admin/categories")
	public ResponseEntity<List<Category>>getCategory(){
		List<Category>categories = new ArrayList<>();
		try {
			categories = categoryRepository.findAll();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(categories,HttpStatus.OK);
	}
	
	@GetMapping("/api/admin/online/users")
	public List<UserDetails> getloggedinUsers(){
		    return sessionRegistry.getAllPrincipals()
		            .stream()
		            .filter(principal -> principal instanceof UserDetails)
		            .map(UserDetails.class::cast)
		            .collect(Collectors.toList());
	}
	
	@PostMapping("/api/admin/payroll/")
	public ResponseEntity<Payroll>getPayRollByMonthAndYear(@RequestParam("month") int month,
														   @RequestParam("year") int year){
		Payroll payroll = payrollRepository.findPayRollByMonthAndYear(month,year).get();
		return new ResponseEntity<>(payroll,HttpStatus.OK);
	}

	@GetMapping("/api/admin/teams/remove/{teamId}/{employeeId}")
	public ResponseEntity<?>removeEmployeeFromTeam(@PathVariable("teamId")String teamId,@PathVariable("employeeId")String employeeId){
		Optional<Team>team= teamRepository.findById(Integer.parseInt(teamId));
		if(team.isEmpty()){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Set<Employee> employees = team.get().getEmployees().stream().filter(employee->employee.getId()==Integer.parseInt(employeeId)).collect(Collectors.toSet());
		team.get().setEmployees(employees);
		teamRepository.save(team.get());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/api/admin/teams/delete/{teamId}")
	public ResponseEntity<?>deleteTeam(@PathVariable("teamId") String teamId){
		teamRepository.deleteById(Integer.parseInt(teamId));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/api/admin/tax")
	public ResponseEntity<Tax>getTax(){
		List<Tax>taxes = new ArrayList<>();
		taxRepository.findAll().forEach(taxes::add);
		Tax tax = taxes.get(0);
		return new ResponseEntity<>(tax,HttpStatus.OK);
	}
	@GetMapping("/api/payroll/check/{id}")
	public String isOnPayRoll(@PathVariable("id")String id) {
		Employee employee = employeeRepository.findById(Integer.parseInt(id)).get();
		if(employee.isPayroll()) {
			return "true";
		}
		else {
			return "false";
		}
	}
	@GetMapping("/api/admin/employee/payroll")
	public ResponseEntity<List<Employee>>getEmployeesOnPayroll(){
		List<Employee>employees = employeeRepository.findByOnPayroll();
		for(Employee employee : employees){
			int templateId = payTemplateRepository.findByEmployeesId(employee.getId()).get();
			PayTemplate template = payTemplateRepository.findById(templateId).get();
			employee.setSalary(template.getGrossSalary());
			SystemUtils.getName(employee);
		}
		return new ResponseEntity<>(employees,HttpStatus.OK);
	}

}